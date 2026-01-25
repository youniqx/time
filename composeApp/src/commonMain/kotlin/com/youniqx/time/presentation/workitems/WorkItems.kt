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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.models.toTimelog
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.presentation.Section
import com.youniqx.time.presentation.history.HistorySummaryCard
import com.youniqx.time.presentation.history.toTimelogEntry
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
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Serializable
object WorkItemsRoute : NavKey

@Composable
fun WorkItems(
    showHistory: () -> Unit,
    viewModel: WorkItemsViewModel = metroViewModel(),
    settingsViewModel: SettingsViewModel = metroViewModel(),
) {
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle() // Todo

    WorkItemsScreen(
        settings = settingsUiState.settings,
        settingsUpdater = settingsViewModel, // Todo
        showHistory = showHistory
    )
}

enum class Section {
    Pinned, Open, Closed
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun WorkItemsScreen(
    settings: Settings,
    settingsUpdater: UpdateSettingsUseCase,
    showHistory: () -> Unit,
) {
    var search: String by remember { mutableStateOf("") }
    var activeFilters by remember { mutableStateOf(emptySet<QuickFilter>()) }
    var disableGlobalSearch by remember { mutableStateOf(false) }
    val disableGlobalSearchIfFocused: Modifier.() -> Modifier = {
        onFocusChanged { disableGlobalSearch = it.hasFocus }
    }

    val viewModel: WorkItemsViewModel = metroViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var loading: Boolean = uiState?.isSyncing ?: true
    var isRefreshing by remember(uiState) { mutableStateOf(false) }
    val workItems = uiState?.data?.workItems
    val currentUserId = uiState?.data?.currentUserId

    val coroutineScope = rememberCoroutineScope()


    // AnimatedPane(Modifier.clip(align = Alignment.End, minWidth = 290.dp)) {
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
    val filteredWorkItems = workItems.orEmpty().filter { workItem ->
        // Text search filter
        val matchesSearch = search.isEmpty() ||
                workItem.title.contains(search, ignoreCase = true) ||
                workItem.id.toString().contains(search, ignoreCase = true) ||
                workItem.id == settings.openTracking?.workItemId ||
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
    val openTrackingAsTimelog = remember(
        settings.openTracking, currentUserId, refresh(every = 1.seconds)
    ) { settings.openTracking?.toTimelog(currentUserId = currentUserId.orEmpty()) }
    val openTrackingWorkItem = remember(workItems, settings.openTracking) {
        settings.openTracking?.let { openTracking ->
            workItems?.firstOrNull { it.id == openTracking.workItemId }
        }
    }
    // Aggregate timelogs from all work items for history view
    // Fetch all timelogs (up to 1 year) - filtering by period is done in TimeHistoryScreen
    val allTimelogs = remember(openTrackingAsTimelog, workItems, currentUserId) {
        val now = Clock.System.now()
        val cutoff = now - 365.days // Fetch up to 1 year of history
        val openTrackingAsEntry =
            openTrackingAsTimelog?.toTimelogEntry(workItem = openTrackingWorkItem, cutoff = cutoff)
        listOfNotNull(openTrackingAsEntry) + workItems.orEmpty()
            .flatMap { workItem ->
                workItem.timelogs
                    .filter { timelog -> timelog.user.id == currentUserId }
                    .mapNotNull { timelog ->
                        timelog.toTimelogEntry(workItem = workItem, cutoff = cutoff)
                    }
            }
            .sortedByDescending { it.spentAt }
    }
    val lazyListState = rememberLazyListState()
    // State for tracking switch confirmation dialog
    var switchTrackingTarget by remember { mutableStateOf<Pair<String, String>?>(null) } // (id, title)

    // Confirmation dialog for switching tracking
    switchTrackingTarget?.let { (targetId, targetTitle) ->
        SwitchTrackingDialog(
            targetTitle = targetTitle,
            currentTracking = settings.openTracking,
            onKeepTimeAndSwitch = {
                settings.openTracking?.let { currentTracking ->
                    settingsUpdater.setOpenTracking(
                        currentTracking.copy(
                            workItemId = targetId,
                            workItemTitle = targetTitle
                        )
                    )
                }
                switchTrackingTarget = null
            },
            onDiscardAndSwitch = {
                settingsUpdater.setOpenTracking(
                    OpenTracking(
                        workItemId = targetId,
                        workItemTitle = targetTitle,
                        timeOfOpen = Clock.System.now()
                    )
                )
                switchTrackingTarget = null
            },
            onShowCurrent = {
                val currentId = settings.openTracking?.workItemId
                val index = filteredWorkItems.indexOfFirst { workItem -> workItem.id == currentId }
                if (index >= 0) {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(index + 1, -100)
                    }
                }
                switchTrackingTarget = null
            },
            onDismiss = { switchTrackingTarget = null }
        )
    }

    val openSections = remember { mutableStateListOf(Section.Pinned, Section.Open) }
    val sceneRole = LocalSceneRole.current
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.search(search, setSyncing = false)
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
                            viewModel.search(it)
                        },
                        loading = loading,
                        refresh = {
                            loading = true
                            viewModel.search("")
                        },
                        show = true, // (alwaysShowSearch || search.isNotEmpty()) && !lazyListState.canScrollBackward,
                        canFocus = !disableGlobalSearch,
                        modifier = Modifier
                            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        // .focusRequester(focusRequester), // Todo
                        onPress = { disableGlobalSearch = false }
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

            if (sceneRole != AutoFilledSupportingPaneSceneStrategy.Role.Main) item(key = "daySummary") {
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
                    timelogs = remember(allTimelogs) {
                        allTimelogs.filter {
                            val now = Clock.System.now()
                            val timeZone = TimeZone.currentSystemDefault()
                            val startOfDay = now.toLocalDateTime(timeZone).date.atStartOfDayIn(timeZone)
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
                                switchTrackingTarget = id.toString() to title
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
                                commitTimeTrackingErrors = viewModel.commitTimeTracking()
                                commitTimeTrackingEnabled = true
                            }
                        },
                        disableGlobalSearchIfFocused = disableGlobalSearchIfFocused,
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

            val groupedWorkItems = filteredWorkItems.groupBy { workItem ->
                when {
                    workItem.id in settings.pinnedWorkItems -> Section.Pinned
                    workItem.state == WorkItemState.CLOSED -> Section.Closed
                    else -> Section.Open
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

            section(Section.Pinned)
            section(Section.Open)
            section(Section.Closed)

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
    // Todo
    // Fake item to ignore focus requests if we have an open time tracking
    // Box(modifier = Modifier.focusProperties { canFocus = false }.focusRequester(focusRequester))
}
