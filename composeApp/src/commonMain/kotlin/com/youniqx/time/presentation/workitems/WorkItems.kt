@file:OptIn(ExperimentalLayoutApi::class)

package com.youniqx.time.presentation.workitems

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.models.toTimelogEntry
import com.youniqx.time.domain.usecases.CommitTimeTrackingUseCase
import com.youniqx.time.domain.usecases.SearchWorkItemsUseCase
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.presentation.LocalResultStore
import com.youniqx.time.presentation.ResultStoreValue
import com.youniqx.time.presentation.Section
import com.youniqx.time.presentation.history.HistorySummaryCard
import com.youniqx.time.presentation.history.HistoryViewModel
import com.youniqx.time.presentation.history.TimelogEntry
import com.youniqx.time.presentation.modifier.adaptivePadding
import com.youniqx.time.presentation.navigation.AutoFilledSupportingPaneSceneStrategy
import com.youniqx.time.presentation.navigation.LocalSceneRole
import com.youniqx.time.presentation.plus
import com.youniqx.time.presentation.rememberSyncedSource
import com.youniqx.time.presentation.settings.SettingsViewModel
import com.youniqx.time.presentation.stickyHeader
import com.youniqx.time.refresh
import com.youniqx.time.systemBarsForVisualComponents
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

@Serializable
object WorkItemsRoute : NavKey

@Serializable
data class ScrollToWorkItem(
    val workItemId: String
): ResultStoreValue

@Serializable
object DisableGlobalSearch : ResultStoreValue

@Composable
fun WorkItems(
    showHistory: () -> Unit,
    viewModel: WorkItemsViewModel = metroViewModel(),
    settingsViewModel: SettingsViewModel = metroViewModel(),
    historyViewModel: HistoryViewModel = metroViewModel(),
    showSwitchTracking: (targetId: String, targetTitle: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle() // Todo
    val historyUiState by historyViewModel.uiState.collectAsStateWithLifecycle() // Todo

    WorkItemsScreen(
        uiState = uiState,
        searcher = viewModel,
        timeTrackingCommiter = viewModel,
        settings = settingsUiState.settings,
        settingsUpdater = settingsViewModel, // Todo
        timelogs = historyUiState.timelogs,
        showHistory = showHistory,
        showSwitchTracking = showSwitchTracking,
    )
}

enum class Section {
    Pinned, Open, Closed
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun WorkItemsScreen(
    uiState: UiState,
    searcher: SearchWorkItemsUseCase,
    timeTrackingCommiter: CommitTimeTrackingUseCase,
    settings: Settings,
    settingsUpdater: UpdateSettingsUseCase,
    timelogs: List<TimelogEntry>,
    showHistory: () -> Unit,
    showSwitchTracking: (targetId: String, targetTitle: String) -> Unit,
) {
    var search: String by remember { mutableStateOf("") }
    var activeFilters by remember { mutableStateOf(emptySet<QuickFilter>()) }

    var loading: Boolean = uiState?.isSyncing ?: true
    var isRefreshing by remember(uiState) { mutableStateOf(false) }
    val workItems = uiState?.data?.workItems
    val currentUserId = uiState?.data?.currentUserId

    // https://kotlinlang.slack.com/archives/CJLTWPH7S/p1731631796638429?thread_ts=1731631796.638429
    val consumedWindowInsets = remember { MutableWindowInsets() }
    val insets =
        WindowInsets.systemBarsForVisualComponents
            .exclude(consumedWindowInsets)
            .asPaddingValues()
    val extraPadding = if (currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        )
    ) PaddingValues(vertical = 20.dp) else PaddingValues()

    val filteredWorkItems = rememberFilteredWorkItems(workItems, search, settings, activeFilters, currentUserId)

    val groupedWorkItems = remember(filteredWorkItems, settings.pinnedWorkItems) {
        filteredWorkItems.groupBy { workItem ->
            when {
                workItem.id in settings.pinnedWorkItems -> Section.Pinned
                workItem.state == WorkItemState.CLOSED -> Section.Closed
                else -> Section.Open
            }
        }
    }

    val lazyListState = rememberLazyListState()

    val openSections = remember { mutableStateListOf(Section.Pinned, Section.Open) }

    val sceneRole = LocalSceneRole.current
    val showDaySummary = sceneRole != AutoFilledSupportingPaneSceneStrategy.Role.Main
    val resultStore = LocalResultStore.current
    val scrollToWorkItem = resultStore.getResultState<ScrollToWorkItem?>()
    LaunchedEffect(groupedWorkItems, scrollToWorkItem) {
        if (scrollToWorkItem == null || filteredWorkItems.isEmpty()) return@LaunchedEffect
        var scrollToIndex = 1 // offset for search and quick filter area
        if (showDaySummary) scrollToIndex++
        Section.entries.forEach {
            scrollToIndex++ // offset for section header
            val grouped = groupedWorkItems[it].orEmpty()
            val indexInSection = grouped.indexOfFirst {
                workItem -> workItem.id == scrollToWorkItem.workItemId
            }
            if (indexInSection != -1) {
                scrollToIndex += indexInSection
                try {
                    if (it !in openSections) {
                        openSections += it
                        // start scrolling immediately and scroll to the exact index after we open the section
                        lazyListState.animateScrollToItem(index = scrollToIndex, scrollOffset = 100)
                        delay(timeMillis = 500)
                    }
                } finally {
                    withContext(NonCancellable) {
                        resultStore.removeResult<ScrollToWorkItem>()
                        lazyListState.animateScrollToItem(index = scrollToIndex, scrollOffset = 100)
                    }
                }
                return@LaunchedEffect
            } else if (it in openSections) {
                scrollToIndex += grouped.size
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            searcher.search(search, setSyncing = false)
        },
        modifier = Modifier.onConsumedWindowInsetsChanged {
            consumedWindowInsets.insets = it
        }
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = insets + extraPadding,
        ) {
            stickyHeader(listState = lazyListState) { _, _ ->
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)

                ) {
                    Search(
                        search = search,
                        onSearchChange = {
                            search = it
                            loading = true
                            searcher.search(it)
                        },
                        loading = loading,
                        refresh = {
                            loading = true
                            searcher.search("")
                        },
                        show = true, // (alwaysShowSearch || search.isNotEmpty()) && !lazyListState.canScrollBackward,
                        canFocus = resultStore.getResultState<DisableGlobalSearch?>() == null,
                        modifier = Modifier
                            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .then(LocalSearchFocusRequester.current?.let {
                                Modifier.focusRequester(it)
                            } ?: Modifier),
                        onPress = { resultStore.removeResult<DisableGlobalSearch>() }
                    )
                    QuickFilters(
                        activeFilters = activeFilters,
                        onFilterToggle = { filter ->
                            activeFilters = if (filter in activeFilters) {
                                activeFilters - filter
                            } else {
                                activeFilters + filter
                            }
                        },
                    )
                }
            }

            if (showDaySummary) item(key = "daySummary") {
                HistorySummaryCard(
                    modifier = Modifier
                        .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 4.dp)
                        .clickable(
                            onClickLabel = "Show history",
                            onClick = {
                                showHistory()
                            }),
                    heading = { Text(text = "Today") },
                    openTracking = settings.openTracking,
                    timelogs = remember(settings.openTracking, refresh(every = 1.seconds)) {
                        listOfNotNull(settings.openTracking?.toTimelogEntry())
                    } + remember(timelogs) {
                        val now = Clock.System.now()
                        val timeZone = TimeZone.currentSystemDefault()
                        val startOfDay = now.toLocalDateTime(timeZone).date.atStartOfDayIn(timeZone)
                        timelogs.filter {
                            it.spentAt in startOfDay..now
                        }
                    }
                )
            }

            @Composable
            operator fun BareWorkItem.invoke(modifier: Modifier = Modifier) {
                val workItem = this
                val pinned = id in settings.pinnedWorkItems
                val togglePinned = { settingsUpdater.togglePinWorkItem(id.toString()) }
                var openTracking by rememberSyncedSource(
                    from = settings.openTracking,
                    save = settingsUpdater::setOpenTracking
                )

                fun startTracking() {
                    openTracking = OpenTracking(
                        workItemId = id.toString(),
                        workItemTitle = title,
                        timeOfOpen = Clock.System.now()
                    )
                }

                val coroutineScope = rememberCoroutineScope()
                Column(modifier = modifier) {
                    var commitTimeTrackingEnabled by remember { mutableStateOf(true) }
                    var commitTimeTrackingErrors by remember {
                        mutableStateOf<List<String>?>(null)
                    }
                    workItem(
                        startTracking = {
                            if (openTracking?.workItemId == null) {
                                startTracking()
                            } else {
                                showSwitchTracking(
                                    id.toString(),
                                    title
                                )
                            }
                        },
                        currentUserId = currentUserId,
                        settings.showLabelsByDefault,
                        settings.useLabelColors,
                        openTracking = openTracking,
                        onOpenTrackingChange = { new -> openTracking = new },
                        pinned = pinned,
                        togglePinned = togglePinned,
                        commitTimeTrackingEnabled = commitTimeTrackingEnabled,
                        commitTimeTracking = commitTimeTracking@{
                            if (!commitTimeTrackingEnabled) return@commitTimeTracking
                            commitTimeTrackingEnabled = false
                            coroutineScope.launch {
                                commitTimeTrackingErrors = timeTrackingCommiter.commitTimeTracking()
                                commitTimeTrackingEnabled = true
                            }
                        },
                    )
                    AnimatedVisibility(visible = !commitTimeTrackingErrors.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier
                                .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(vertical = 8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(8.dp)
                        ) {
                            commitTimeTrackingErrors?.forEach {
                                Text(text = it, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            fun section(section: Section) {
                val sectionWorkItems = groupedWorkItems[section]
                if (!sectionWorkItems.isNullOrEmpty()) {
                    val open = section in openSections
                    stickyHeader(listState = lazyListState) { _, isSticky ->
                        val corner = if (isSticky) 50 else 0
                        Surface(
                            modifier = Modifier
                                .then(if (isSticky) Modifier else Modifier.fillMaxWidth())
                                .clip(RoundedCornerShape(topEndPercent = corner, bottomEndPercent = corner))
                                .clickable {
                                    if (open) openSections -= section else openSections += section
                                },
                            color = if (isSticky) MaterialTheme.colorScheme.surfaceContainer else Transparent,
                            shadowElevation = if (isSticky) 5.dp else 0.dp
                        ) {
                            Section(
                                title = { Text(section.name) },
                                open = open,
                                count = sectionWorkItems.size
                            )
                        }
                    }


                    if (open) items(
                        sectionWorkItems,
                        key = { workItem -> workItem.id }
                    ) { sectionWorkItem ->
                        sectionWorkItem(modifier = Modifier.animateItem())
                    }
                }
            }

            Section.entries.forEach { section(it) }

            // Show shimmer loading when loading
            if (loading && filteredWorkItems.isEmpty()) {
                item {
                    LoadingWorkItemList(
                        count = 5,
                        modifier = Modifier
                            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                            .padding(horizontal = 12.dp)
                    )
                }
            }

            // Show empty state when no work items
            if (!loading && filteredWorkItems.isEmpty()) {
                item {
                    if (search.isNotEmpty()) {
                        NoSearchResultsEmptyState(
                            modifier = Modifier
                                .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                        )
                    } else {
                        NoWorkItemsEmptyState(
                            modifier = Modifier
                                .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                        )
                    }
                }
            }

            // Small loading indicator when refreshing with existing data
            if (loading && filteredWorkItems.isNotEmpty()) item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun rememberFilteredWorkItems(
    workItems: List<BareWorkItem>?,
    search: String,
    settings: Settings,
    activeFilters: Set<QuickFilter>,
    currentUserId: String?
): List<BareWorkItem> {
    val openTrackingWorkItemId = settings.openTracking?.workItemId
    return remember(workItems, search, openTrackingWorkItemId, activeFilters, currentUserId) {
        workItems.orEmpty().filter { workItem ->
            // Text search filter
            val matchesSearch = search.isEmpty() ||
                    workItem.title.contains(search, ignoreCase = true) ||
                    workItem.id.toString().contains(search, ignoreCase = true) ||
                    workItem.id == openTrackingWorkItemId ||
                    workItem.iid.contains(search, ignoreCase = true) ||
                    workItem.webUrl.orEmpty().contains(search, ignoreCase = true) ||
                    workItem.assignees?.nodes.orEmpty().filterNotNull().any {
                        it.name.contains(search, ignoreCase = true) ||
                                it.username.contains(search, ignoreCase = true)
                    } ||
                    workItem.labels?.nodes.orEmpty().filterNotNull().any {
                        it.title.contains(search, ignoreCase = true)
                    }

            // Quick filters
            val matchesQuickFilters = activeFilters.isEmpty() || activeFilters.all { filter ->
                when (filter) {
                    QuickFilter.Assigend -> workItem.assignees?.nodes.orEmpty()
                        .filterNotNull().any { it.id == currentUserId }

                    QuickFilter.HasTimeLogged -> workItem.timelogs.isNotEmpty()
                    QuickFilter.Pinned -> workItem.id in settings.pinnedWorkItems
                    QuickFilter.RecentlyTracked -> workItem.timelogs.any {
                        it.user.id == currentUserId
                    }
                }
            }

            matchesSearch && matchesQuickFilters
        }
    }
}
