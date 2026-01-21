@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)

package com.youniqx.time.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.window.core.layout.WindowSizeClass
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.dataIfNotFrom
import com.youniqx.time.domain.models.toTimelog
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.onSet
import com.youniqx.time.presentation.errors.NotFoundRoute
import com.youniqx.time.presentation.history.HistorySummaryCard
import com.youniqx.time.presentation.history.TimeHistoryScreen
import com.youniqx.time.presentation.history.TimeRange
import com.youniqx.time.presentation.history.toTimelogEntry
import com.youniqx.time.presentation.modifier.adaptivePadding
import com.youniqx.time.presentation.modifier.clip
import com.youniqx.time.presentation.navscopes.NavScope
import com.youniqx.time.presentation.onboarding.GitLabSetupRoute
import com.youniqx.time.presentation.onboarding.WelcomeRoute
import com.youniqx.time.presentation.settings.Settings
import com.youniqx.time.presentation.theme.AppTheme
import com.youniqx.time.presentation.theme.Theme
import com.youniqx.time.presentation.workitems.LoadingWorkItemList
import com.youniqx.time.presentation.workitems.NoSearchResultsEmptyState
import com.youniqx.time.presentation.workitems.NoWorkItemsEmptyState
import com.youniqx.time.presentation.workitems.QuickFilter
import com.youniqx.time.presentation.workitems.QuickFilters
import com.youniqx.time.presentation.workitems.Search
import com.youniqx.time.presentation.workitems.SwitchTrackingDialog
import com.youniqx.time.presentation.workitems.WorkItemsViewModel
import com.youniqx.time.presentation.workitems.invoke
import com.youniqx.time.refresh
import com.youniqx.time.systemBarsForVisualComponents
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

enum class Section {
    Pinned, Open, Closed
}

// Fallback route to just render the rest of the app which is not migrated to nav3 yet.
@Serializable
object AppRoute: NavKey

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(NotFoundRoute::class, NotFoundRoute.serializer())
            subclass(WelcomeRoute::class, WelcomeRoute.serializer())
            subclass(GitLabSetupRoute::class, GitLabSetupRoute.serializer())
            subclass(AppRoute::class, AppRoute.serializer())
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun App(
    navScopes: Set<NavScope>,
    settingsRepository: SettingsRepository,
    focusRequester: FocusRequester = remember { FocusRequester() },
    setWindowBackground: ((Color) -> Unit)? = null,
    theme: Theme = com.youniqx.time.presentation.theme.teal.theme,
) {
    val sourceAwareSettings by settingsRepository.settings.collectAsStateWithLifecycle()
    val settings = sourceAwareSettings.data
    val darkTheme = sourceAwareSettings.dataIfNotFrom(excludedSource = DataSource.Default)?.darkTheme
        ?: isSystemInDarkTheme()
    val backStack = rememberNavBackStack(configuration = config, WelcomeRoute)

    AppTheme(darkTheme = darkTheme, useHighContrastColors = settings.highContrastColors, theme = theme) {
        if (setWindowBackground != null) {
            MaterialTheme.colorScheme.surface.let { color ->
                LaunchedEffect(setWindowBackground, color) {
                    setWindowBackground(color)
                }
            }
        }

        if (backStack.last() != AppRoute) {
            NavDisplay(
                backStack = backStack,
                modifier =
                    Modifier
                        // .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize(),
                onBack = { backStack.removeLastOrNull() },
                entryProvider =
                    entryProvider(fallback = { key ->
                        NavEntry(key) {
                            LaunchedEffect(key) {
                                println("Unknown key: $key")
                                backStack.removeLastOrNull()
                                backStack.add(NotFoundRoute)
                            }
                        }
                    }) {
                        navScopes.forEach { scope ->
                            scope(backStack)
                        }
                    },
            )
            return@AppTheme
        }

        var search: String by remember { mutableStateOf("") }
        var activeFilters by remember { mutableStateOf(emptySet<QuickFilter>()) }
        var showHistory by remember { mutableStateOf(false) }
        var selectedTimeRange by remember { mutableStateOf(TimeRange.Today) }
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

        val singlePaneDirective = remember { PaneScaffoldDirective.Default }
        val defaultPaneDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo())
        var anchorRefresher by remember { mutableStateOf(false) }
        var forceSinglePane by remember { mutableStateOf(false) } onSet { current, new ->
            // only refresh anchors when we go back to default mode,
            // this way we have no visual artifacts when entering single pane mode
            if (current && !new) anchorRefresher = !anchorRefresher
        }

        val navigator = rememberSupportingPaneScaffoldNavigator(
            scaffoldDirective = if (forceSinglePane) singlePaneDirective else defaultPaneDirective,
            adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(
                supportingPaneAdaptStrategy = AdaptStrategy.Hide
            )
        )
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            floatingActionButton = {
                if (navigator.scaffoldValue.secondary == PaneAdaptedValue.Hidden ||
                    navigator.scaffoldValue.primary == PaneAdaptedValue.Hidden) CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isHovered by interactionSource.collectIsHoveredAsState()
                    SmallFloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                if (forceSinglePane) {
                                    forceSinglePane = false
                                    return@launch
                                }
                                if (navigator.scaffoldValue.primary == PaneAdaptedValue.Hidden) {
                                    navigator.navigateTo(SupportingPaneScaffoldRole.Main)
                                } else {
                                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                }
                            }
                        },
                        interactionSource = interactionSource,
                    ) {
                        val icon =
                            if (navigator.scaffoldValue.primary == PaneAdaptedValue.Hidden) {
                                if (isHovered) Icons.Filled.Home else Icons.Outlined.Home
                            } else {
                                if (isHovered) Icons.Filled.Settings else Icons.Outlined.Settings
                            }
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
            }
        ) {
            SupportingPaneScaffold(
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                supportingPane = {
                    AnimatedPane(Modifier.clip(align = Alignment.Start, minWidth = 290.dp)) {
                        Settings(
                            disableGlobalSearchIfFocused = disableGlobalSearchIfFocused,
                            onBack = if (navigator.scaffoldValue.primary == PaneAdaptedValue.Hidden) {
                                {
                                    coroutineScope.launch {
                                        if (forceSinglePane) {
                                            forceSinglePane = false
                                        } else {
                                            navigator.navigateTo(SupportingPaneScaffoldRole.Main)
                                        }
                                    }
                                }
                            } else null
                        )
                    }
                }, mainPane = {
                    AnimatedPane(Modifier.clip(align = Alignment.End, minWidth = 290.dp)) {
                        // https://kotlinlang.slack.com/archives/CJLTWPH7S/p1731631796638429?thread_ts=1731631796.638429
                        val consumedWindowInsets = remember { MutableWindowInsets() }
                        val insets =
                            WindowInsets.systemBarsForVisualComponents
                                .exclude(consumedWindowInsets)
                                .asPaddingValues()
                        val extraPadding = if (currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(
                                WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
                            )) PaddingValues(vertical = 20.dp) else PaddingValues()
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

                        if (showHistory) {
                            TimeHistoryScreen(
                                timelogs = allTimelogs,
                                isLoading = loading,
                                selectedRange = selectedTimeRange,
                                onRangeChange = { selectedTimeRange = it },
                                onBack = { showHistory = false },
                                openTracking = settings.openTracking
                            )
                        } else {
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
                                        settingsRepository.setOpenTracking(
                                            currentTracking.copy(
                                                workItemId = targetId,
                                                workItemTitle = targetTitle
                                            )
                                        )
                                    }
                                    switchTrackingTarget = null
                                },
                                onDiscardAndSwitch = {
                                    settingsRepository.setOpenTracking(
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
                                            .padding(horizontal = 12.dp)
                                            .focusRequester(focusRequester),
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

                            item(key = "daySummary") {
                                HistorySummaryCard(
                                    modifier = Modifier
                                        .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                                        .padding(horizontal = 12.dp)
                                        .padding(bottom = 4.dp)
                                        .clickable(
                                            onClickLabel = "Show history",
                                            onClick = {
                                                selectedTimeRange = TimeRange.Today
                                                showHistory = true
                                            }),
                                    heading = { Text(text = "Today") },
                                    openTracking = settings.openTracking,
                                    timelogs = remember(allTimelogs) { allTimelogs.filter {
                                        val now = Clock.System.now()
                                        val timeZone = TimeZone.currentSystemDefault()
                                        val startOfDay = now.toLocalDateTime(timeZone).date.atStartOfDayIn(timeZone)
                                        it.spentAt in startOfDay..now
                                    } }
                                )
                            }

                            @Composable
                            operator fun BareWorkItem.invoke(modifier: Modifier = Modifier) {
                                val workItem = this
                                val pinned = id in settings.pinnedWorkItems
                                val togglePinned = { settingsRepository.togglePinWorkItem(id.toString()) }
                                var openTracking by rememberSyncedSource(
                                    from = settings.openTracking,
                                    save = settingsRepository::setOpenTracking
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
                                        Surface(modifier = Modifier
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
                    }
                }
                },
                paneExpansionState = key(anchorRefresher) {
                    rememberPaneExpansionState(
                        keyProvider = navigator.scaffoldValue,
                        anchors = listOf(
                            PaneExpansionAnchor.Proportion(0f),
                            PaneExpansionAnchor.Offset.fromStart(300.dp),
                            PaneExpansionAnchor.Proportion(0.5f),
                            PaneExpansionAnchor.Offset.fromEnd(300.dp),
                            PaneExpansionAnchor.Proportion(1f)
                        ),
                        // initialAnchoredIndex = if (forceSinglePane) 0 else -1,
                    )
                },
                paneExpansionDragHandle = { state ->
                    val interactionSource =
                        remember { MutableInteractionSource() }
                    LaunchedEffect(state.currentAnchor) {
                        when (val anchor = state.currentAnchor) {
                            is PaneExpansionAnchor.Offset -> {} // no-op
                            is PaneExpansionAnchor.Proportion -> when (anchor.proportion) {
                                1f -> {
                                    navigator.navigateTo(SupportingPaneScaffoldRole.Main)
                                    forceSinglePane = true
                                }
                                0f -> {
                                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                    forceSinglePane = true
                                }
                            }
                            null -> {} // no-op
                        }
                    }
                    VerticalDragHandle(
                        modifier =
                            Modifier.paneExpansionDraggable(
                                state,
                                LocalMinimumInteractiveComponentSize.current,
                                interactionSource
                            ), interactionSource = interactionSource
                    )
                }
            )
        }
        // Fake item to ignore focus requests if we have an open time tracking
        Box(modifier = Modifier.focusProperties { canFocus = false }.focusRequester(focusRequester))
    }
}

val BareWorkItem.assignees get() = widgets?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetAssignees != null }
    ?.bareWorkItemWidgets?.onWorkItemWidgetAssignees?.assignees
val BareWorkItem.labels get() = widgets?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetLabels != null }
    ?.bareWorkItemWidgets?.onWorkItemWidgetLabels?.labels
val BareWorkItem.timelogs get() = widgets?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetTimeTracking != null }
    ?.bareWorkItemWidgets?.onWorkItemWidgetTimeTracking?.timelogs?.nodes?.filterNotNull().orEmpty()
