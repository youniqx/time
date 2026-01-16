@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)

package com.youniqx.time

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.youniqx.time.components.LoadingWorkItemList
import com.youniqx.time.components.NoSearchResultsEmptyState
import com.youniqx.time.components.NoWorkItemsEmptyState
import com.youniqx.time.components.QuickFilter
import com.youniqx.time.components.QuickFilters
import com.youniqx.time.components.SimpleTooltip
import com.youniqx.time.components.SwipeableWorkItemCard
import com.youniqx.time.components.SwitchTrackingDialog
import com.youniqx.time.components.TimeBadge
import com.youniqx.time.components.rememberTimeBadgePlaceholder
import com.youniqx.time.data.toApolloClientOrNull
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.currentTimeSpentString
import com.youniqx.time.domain.models.dataIfNotFrom
import com.youniqx.time.domain.models.isOpenTracking
import com.youniqx.time.domain.models.toTimelog
import com.youniqx.time.gitlab.models.RefreshWorkItemsQuery
import com.youniqx.time.gitlab.models.TimelogCreateMutation
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.type.TimelogCreateInput
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.history.HistorySummaryCard
import com.youniqx.time.history.TimeHistoryScreen
import com.youniqx.time.history.TimeRange
import com.youniqx.time.history.toTimelogEntry
import com.youniqx.time.modifier.adaptivePadding
import com.youniqx.time.modifier.changeFocusOnTab
import com.youniqx.time.modifier.clip
import com.youniqx.time.modifier.onCtrlOrMetaEnter
import com.youniqx.time.onboarding.OnboardingScreen
import com.youniqx.time.opentracking.RepresentingIndicator
import com.youniqx.time.opentracking.customTimeSpentHasErrorMessage
import com.youniqx.time.opentracking.representingColors
import com.youniqx.time.presentation.workitems.WorkItemsViewModel
import com.youniqx.time.relativetime.RelativeTime
import com.youniqx.time.relativetime.formatDuration
import com.youniqx.time.settings.Settings
import com.youniqx.time.theme.AppTheme
import com.youniqx.time.theme.LocalSpacing
import com.youniqx.time.theme.Theme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

operator fun PaddingValues.plus(other: PaddingValues): PaddingValues = PaddingValues(
    start = this.calculateStartPadding(LayoutDirection.Ltr) +
            other.calculateStartPadding(LayoutDirection.Ltr),
    top = this.calculateTopPadding() + other.calculateTopPadding(),
    end = this.calculateEndPadding(LayoutDirection.Ltr) +
            other.calculateEndPadding(LayoutDirection.Ltr),
    bottom = this.calculateBottomPadding() + other.calculateBottomPadding(),
)

enum class Section {
    Pinned, Open, Closed
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun App(
    focusRequester: FocusRequester = remember { FocusRequester() },
    setWindowBackground: ((Color) -> Unit)? = null,
    settingsRepository: SettingsRepository,
    theme: Theme = com.youniqx.time.theme.teal.theme,
) {
    val sourceAwareSettings by settingsRepository.settings.collectAsStateWithLifecycle()
    val settings = sourceAwareSettings.data
    val darkTheme = sourceAwareSettings.dataIfNotFrom(excludedSource = DataSource.Default)?.darkTheme
        ?: isSystemInDarkTheme()
    AppTheme(darkTheme = darkTheme, useHighContrastColors = settings.highContrastColors, theme = theme) {
        if (setWindowBackground != null) {
            MaterialTheme.colorScheme.surface.let { color ->
                LaunchedEffect(setWindowBackground, color) {
                    setWindowBackground(color)
                }
            }
        }

        var showOnboarding by remember(sourceAwareSettings.source) {
            mutableStateOf(settings.instanceUrl.isNullOrEmpty() || settings.token.isNullOrEmpty())
        }
        // Show onboarding for new users
        if (showOnboarding) {
            OnboardingScreen(
                loading = sourceAwareSettings.source == DataSource.Default,
                instanceUrl = settings.instanceUrl,
                onInstanceUrlChange = settingsRepository::setInstanceUrl,
                token = settings.token,
                onTokenChange = settingsRepository::setToken,
                onComplete = { showOnboarding = false }
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

        val apolloClient = remember(sourceAwareSettings) { sourceAwareSettings.toApolloClientOrNull() }
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
                                val pinned = id in settings.pinnedWorkItems
                                val togglePinned = { settingsRepository.togglePinWorkItem(id.toString()) }
                                fun startTracking() = settingsRepository.setOpenTracking(
                                    OpenTracking(
                                        workItemId = id.toString(),
                                        workItemTitle = title,
                                        timeOfOpen = Clock.System.now()
                                    )
                                )
                                Column(modifier = modifier) {
                                    var commitTimeTrackingEnabled by remember { mutableStateOf(true) }
                                    var commitTimeTrackingErrors by remember {
                                        mutableStateOf<List<String>?>(null)
                                    }
                                    WorkItem(
                                        this@invoke,
                                        startTracking = {
                                            if (settings.openTracking?.workItemId == null) {
                                                startTracking()
                                            } else {
                                                switchTrackingTarget = id.toString() to title
                                            }
                                        },
                                        currentUserId = currentUserId,
                                        settings.showLabelsByDefault,
                                        settings.useLabelColors,
                                        openTracking = settings.openTracking,
                                        onOpenTrackingChange = { openTracking ->
                                            settingsRepository.setOpenTracking(openTracking)
                                        },
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

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkItem(
    workItem: BareWorkItem,
    startTracking: () -> Unit,
    currentUserId: String?,
    showLabelsByDefault: Boolean,
    useLabelColors: Boolean,
    openTracking: OpenTracking?,
    onOpenTrackingChange: (openTracking: OpenTracking?) -> Unit,
    pinned: Boolean,
    togglePinned: () -> Unit,
    disableGlobalSearchIfFocused: Modifier.() -> Modifier,
    commitTimeTrackingEnabled: Boolean,
    commitTimeTracking: () -> Unit,
    modifier: Modifier = Modifier,
    additionalContent: (@Composable () -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val open = openTracking?.workItemId == workItem.id
    var showTimelogs by remember { mutableStateOf(false) }
    val openTrackingAsTimelog = remember(
        openTracking, currentUserId, open, refresh(every = 1.seconds)
    ) { openTracking.takeIf { open }?.toTimelog(currentUserId = currentUserId.orEmpty()) }
    val allTimelogs = listOfNotNull(openTrackingAsTimelog) + workItem.timelogs
    val myTimelogs = allTimelogs.filter { it.user.id == currentUserId }
    val myTotalTime = myTimelogs.fold(0) { acc, timelog -> acc + timelog.timeSpent }
    val totalMinutes = myTotalTime / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val myTotalTimeString = "$hours:${minutes.toString().padStart(length = 2, padChar = '0')}"
    val spacing = LocalSpacing.current

    SwipeableWorkItemCard(
        modifier = modifier
            .padding(vertical = 4.dp)
            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
            .padding(horizontal = 12.dp),
        isPinned = pinned,
        isTracking = open,
        onStartTracking = startTracking,
        onTogglePin = togglePinned
    ) {
        Surface(
            modifier = Modifier
                .then(
                    if (open) Modifier.border(
                        width = 2.dp,
                        color = openTracking.representingColors.color,
                        shape = RoundedCornerShape(spacing.cardRadius)
                    ) else Modifier
                )
                .clip(RoundedCornerShape(spacing.cardRadius))
                .clickable(
                    enabled = !open,
                    onClickLabel = "Work on work item",
                    role = Role.Switch
                ) {
                    startTracking()
                },
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(spacing.cardRadius),
            shadowElevation = if (open) 4.dp else 0.dp
        ) {
            Column(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .padding(spacing.cardPadding)
            ) {
                val labels = if (showLabelsByDefault) workItem.labels?.nodes else null
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = if (labels.isNullOrEmpty()) 48.dp else 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    workItem.workItemType.name.let { WorkItemTypeIcon(it) }
                    Text(
                        text = buildAnnotatedString {
                            append(workItem.title)
                            if (myTotalTime > 0) {
                                append(" ")
                                appendInlineContent("time", myTotalTimeString)
                            }
                            if (workItem.promotedToEpicUrl != null) {
                                append(" ")
                                appendInlineContent("promoted", "(promoted)")
                            } else if (workItem.state == WorkItemState.CLOSED) {
                                append(" ")
                                appendInlineContent("closed", "(closed)")
                            }
                        },
                        inlineContent = mapOf(
                            "time" to InlineTextContent(
                                placeholder = rememberTimeBadgePlaceholder(
                                    time = myTotalTimeString,
                                    trailingIconSize = if (open) 16.dp else null
                                )
                            ) {
                                SimpleTooltip(
                                    text = "Timelog Sum\nClick to ${if (showTimelogs) "hide" else "see"} details."
                                ) {
                                    openTracking.takeIf { open }?.run {
                                        val representingColors = representingColors
                                        TimeBadge(
                                            time = myTotalTimeString,
                                            backgroundColor = representingColors.container.copy(alpha = 0.7f),
                                            color = representingColors.onContainer,
                                            trailingIcon = {
                                                RepresentingIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = representingColors.onContainer,
                                                )
                                            },
                                            onClick = { showTimelogs = !showTimelogs }
                                        )
                                    } ?: TimeBadge(
                                        time = myTotalTimeString,
                                        backgroundColor =
                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        onClick = { showTimelogs = !showTimelogs }
                                    )
                                }
                            },
                            "closed" to InlineTextContent(
                                Placeholder(
                                    width = 16.sp,
                                    height = 16.sp,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                )
                            ) {
                                SimpleTooltip("closed") {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "closed",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            "promoted" to InlineTextContent(
                                Placeholder(
                                    width = 16.sp,
                                    height = 16.sp,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                )
                            ) {
                                SimpleTooltip("promoted") {
                                    Icon(
                                        imageVector = Icons.Default.ArrowCircleUp,
                                        contentDescription = "promoted",
                                        modifier = Modifier.size(16.dp).clickable {
                                            workItem.promotedToEpicUrl?.let { uri -> uriHandler.openUri(uri) }
                                        }
                                    )
                                }
                            },
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                labels?.let {
                    AnimatedVisibility(visible = labels.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            it.filterNotNull().forEach { label -> Label(label = label, useColors = useLabelColors) }
                        }
                    }
                }
                AnimatedVisibility(visible = showTimelogs) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        myTimelogs.forEachIndexed { index, timelog ->
                            val isEven = index % 2 == 0
                            val rowBg = if (isEven) {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            } else {
                                Transparent
                            }
                            SimpleTooltip(timelog.spentAt?.let { Instant.parseOrNull(it.toString()) }?.let {
                                if (timelog.isOpenTracking) {
                                    "Currently tracking"
                                } else {
                                    "${formatDuration(Clock.System.now() - it, RelativeTime.Past)} ago"
                                }
                            }.orEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val timeMinutes = timelog.timeSpent / 60
                                    val h = timeMinutes / 60
                                    val m = timeMinutes % 60
                                    Text(
                                        text = "$h:${m.toString().padStart(length = 2, padChar = '0')}",
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.width(48.dp - if (timelog.isOpenTracking) 12.dp else 0.dp)
                                    )
                                    if (timelog.isOpenTracking && openTracking != null) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        openTracking.RepresentingIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = openTracking.representingColors.color
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = timelog.summary.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = open) {
                    if (!open) return@AnimatedVisibility
                    val timeSinceOpen = remember(openTracking, refresh(every = 1.seconds)) {
                        Clock.System.now() - openTracking.timeOfOpen
                    }
                    val timeSinceOpenInWholeMinutes = timeSinceOpen.inWholeMinutes.minutes
                    val customTimeSpent = openTracking.customTimeSpent
                    val focusRequester = remember { FocusRequester() }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth()
                                .disableGlobalSearchIfFocused()
                                .focusRequester(focusRequester)
                                .changeFocusOnTab()
                                .onCtrlOrMetaEnter(commitTimeTracking)
                                .onKeyEvent { true }, // https://github.com/JetBrains/compose-multiplatform/issues/4612,
                            value = openTracking.summary.orEmpty(),
                            label = { Text("What have I achieved? (optional)") },
                            onValueChange = { text -> onOpenTrackingChange(openTracking.copy(summary = text)) },
                            leadingIcon = { Icon(Icons.Outlined.Summarize, contentDescription = null) },
                        )
                        val timeSinceOpenString = timeSinceOpenInWholeMinutes.toComponents { hours, minutes, _, _ ->
                            "${hours}h ${minutes}m"
                        }
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth()
                                .disableGlobalSearchIfFocused()
                                .changeFocusOnTab()
                                .onCtrlOrMetaEnter(commitTimeTracking)
                                .onKeyEvent { true }, // https://github.com/JetBrains/compose-multiplatform/issues/4612
                            value = customTimeSpent ?: timeSinceOpenString,
                            onValueChange = { text -> onOpenTrackingChange(openTracking.copy(customTimeSpent = text)) },
                            label = { Text("Time spent") },
                            placeholder = { Text("Example: 1h 30m") },
                            visualTransformation = customTimeSpent?.let { VisualTransformation.None }
                                ?: AddedTextVisualTransformation(
                                    addedText = buildAnnotatedString {
                                        withStyle(SpanStyle(color = LocalContentColor.current.copy(alpha = 0.5f))) {
                                            append(" ${(timeSinceOpen - timeSinceOpenInWholeMinutes).inWholeSeconds}s")
                                        }
                                    }
                                ),
                            leadingIcon = {
                                openTracking.RepresentingIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = openTracking.representingColors.color
                                )
                            },
                            trailingIcon = {
                                when {
                                    customTimeSpent != null -> Row {
                                        val customTimeSpentDuration = Duration.parseOrNull(customTimeSpent.trim())
                                        customTimeSpentDuration?.let {
                                            SimpleTooltip("Continue timer\n(overwrites $timeSinceOpenString)") {
                                                IconButton(
                                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                                                    onClick = {
                                                        onOpenTrackingChange(
                                                            openTracking.copy(
                                                                timeOfOpen = Clock.System.now() - customTimeSpentDuration,
                                                                customTimeSpent = null,
                                                            )
                                                        )
                                                    }
                                                ) {
                                                    Icon(
                                                        Icons.Default.Start,
                                                        contentDescription = "Continue timer from entered time"
                                                    )
                                                }
                                            }
                                        }
                                        SimpleTooltip("Reset to running timer\n($timeSinceOpenString)") {
                                            IconButton(
                                                modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                                                onClick = { onOpenTrackingChange(openTracking.copy(customTimeSpent = null)) }
                                            ) {
                                                Icon(
                                                    Icons.Default.History,
                                                    contentDescription = "Reset to running timer"
                                                )
                                            }
                                        }
                                    }

                                    else -> SimpleTooltip("Restart time spent timer") {
                                        IconButton(
                                            modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                                            onClick = { onOpenTrackingChange(openTracking.copy(timeOfOpen = Clock.System.now())) }
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Restart time spent timer"
                                            )
                                        }
                                    }
                                }
                            },
                            isError = openTracking.customTimeSpentHasError,
                            supportingText = if (openTracking.customTimeSpentHasError) {
                                { Text(customTimeSpentHasErrorMessage) }
                            } else null
                        )
                        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            AdditionalActions(workItem, pinned, togglePinned)
                            SimpleTooltip("Discard time tracking") {
                                IconButton(onClick = { onOpenTrackingChange(null) }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteForever,
                                        contentDescription = "Discard time tracking"
                                    )
                                }
                            }
                            SimpleTooltip("Commit time tracking") {
                                FilledTonalIconButton(
                                    modifier = Modifier.padding(start = 4.dp),
                                    onClick = commitTimeTracking,
                                    enabled = !openTracking.customTimeSpentHasError
                                ) {
                                    val x by rememberInfiniteTransition().animateValue(
                                        initialValue = if (commitTimeTrackingEnabled) 0.dp else (-35).dp,
                                        targetValue = if (commitTimeTrackingEnabled) 0.dp else 35.dp,
                                        typeConverter = Dp.VectorConverter,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1500),
                                            repeatMode = RepeatMode.Restart,
                                            initialStartOffset = StartOffset(
                                                offsetMillis = 750,
                                                offsetType = StartOffsetType.FastForward
                                            )
                                        ),
                                    )
                                    Icon(
                                        modifier = Modifier.offset(x = x, y = -x / 10).rotate(-x.value / 8),
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Commit time tracking"
                                    )
                                }
                            }
                        }
                    }
                    LaunchedEffect(open) {
                        if (!open) return@LaunchedEffect
                        focusRequester.requestFocus()
                    }
                }
                additionalContent?.invoke()
            }
        }
    }
}
