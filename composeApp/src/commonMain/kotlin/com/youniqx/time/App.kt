@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)

package com.youniqx.time

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.window.core.layout.WindowSizeClass
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.api.CacheKey
import com.apollographql.apollo.cache.normalized.api.CacheKeyGenerator
import com.apollographql.apollo.cache.normalized.api.CacheKeyGeneratorContext
import com.apollographql.apollo.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.apollographql.apollo.cache.normalized.normalizedCache
import com.apollographql.apollo.cache.normalized.watch
import com.youniqx.time.animation.FadeInItem
import com.youniqx.time.components.LoadingIssuesList
import com.youniqx.time.components.NoIssuesEmptyState
import com.youniqx.time.components.NoSearchResultsEmptyState
import com.youniqx.time.components.QuickFilter
import com.youniqx.time.components.QuickFilters
import com.youniqx.time.components.SimpleTooltip
import com.youniqx.time.components.SwipeableIssueCard
import com.youniqx.time.components.SwitchTrackingDialog
import com.youniqx.time.components.TimeBadge
import com.youniqx.time.gitlab.models.IssuesQuery
import com.youniqx.time.gitlab.models.IterationCadencesQuery
import com.youniqx.time.gitlab.models.RefreshIssuesQuery
import com.youniqx.time.gitlab.models.TimelogCreateMutation
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.type.TimelogCreateInput
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.history.TimeHistoryScreen
import com.youniqx.time.history.TimeRange
import com.youniqx.time.history.TimelogEntry
import com.youniqx.time.modifier.adaptivePadding
import com.youniqx.time.modifier.clip
import com.youniqx.time.onboarding.OnboardingScreen
import com.youniqx.time.relativetime.RelativeTime
import com.youniqx.time.relativetime.formatDuration
import com.youniqx.time.settings.OpenTracking
import com.youniqx.time.settings.Settings
import com.youniqx.time.settings.SettingsViewModel
import com.youniqx.time.settings.UiState
import com.youniqx.time.theme.AppTheme
import com.youniqx.time.theme.LocalSpacing
import com.youniqx.time.theme.TimerActiveColor
import io.ktor.http.appendPathSegments
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
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

@Composable
fun alwaysShowSearch() = !hasPhysicalOrShowingKeyboard() || currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(
    WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
)

enum class Section {
    Pinned, Open, Closed
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun App(
    focusRequester: FocusRequester = remember { FocusRequester() },
    alwaysShowSearch: Boolean = alwaysShowSearch(),
    setWindowBackground: ((Color) -> Unit)? = null,
    onSettingsStateChange: ((UiState) -> Unit)? = null,
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val settingsViewModel = viewModel<SettingsViewModel>(
        factory = viewModelFactory { initializer { SettingsViewModel(systemInDarkTheme) } }
    )
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    // Report state changes to parent (for menu bar timer)
    LaunchedEffect(settingsUiState) {
        onSettingsStateChange?.invoke(settingsUiState)
    }
    AppTheme(darkTheme = settingsUiState.darkTheme, useHighContrastColors = settingsUiState.highContrastColors) {
        if (setWindowBackground != null) {
            MaterialTheme.colorScheme.surface.let { color ->
                LaunchedEffect(setWindowBackground, color) {
                    setWindowBackground(color)
                }
            }
        }

        // Show onboarding for new users
        if (!settingsUiState.onboardingCompleted) {
            OnboardingScreen(
                instanceUrl = settingsUiState.instanceUrl.orEmpty(),
                onInstanceUrlChange = settingsViewModel::setInstanceUrl,
                token = settingsUiState.token.orEmpty(),
                onTokenChange = settingsViewModel::setToken,
                onComplete = settingsViewModel::completeOnboarding
            )
            return@AppTheme
        }

        var currentUserId: String? by remember { mutableStateOf(null) }
        var issues: List<BareWorkItem>? by remember { mutableStateOf(null) }
        var iterationCadences: List<IterationCadencesQuery.Node>? by remember { mutableStateOf(null) }
        var search: String by remember { mutableStateOf("") }
        var activeFilters by remember { mutableStateOf(emptySet<QuickFilter>()) }
        var loading: Boolean by remember { mutableStateOf(false) }
        var showHistory by remember { mutableStateOf(false) }
        var selectedTimeRange by remember { mutableStateOf(TimeRange.Week) }
        var isRefreshing by remember { mutableStateOf(false) }
        var refreshTrigger by remember { mutableStateOf(0) }
        var disableGlobalSearch by remember { mutableStateOf(false) }
        val disableGlobalSearchIfFocused: Modifier.() -> Modifier = {
            onFocusChanged { disableGlobalSearch = it.hasFocus }
        }
        val isPreview = LocalInspectionMode.current
        val apolloClient = remember(settingsUiState.instanceUrl, settingsUiState.token) {
            val instanceUrl = settingsUiState.instanceUrl ?: return@remember null
            val token = settingsUiState.token ?: return@remember null
            val cacheFactory = MemoryCacheFactory(maxSizeBytes = 30 * 1024 * 1024)
            val cacheKeyGenerator = object : CacheKeyGenerator {
                override fun cacheKeyForObject(obj: Map<String, Any?>, context: CacheKeyGeneratorContext): CacheKey? {
                    // Generate the cache ID based on the object's id field
                    return (obj["id"] as? String)?.let(::CacheKey)
                }
            }
            ApolloClient.Builder()
                .serverUrl(
                    buildUrl {
                        takeFrom(instanceUrl)
                        appendPathSegments("api", "graphql")
                    }.toString()
                )
                .addHttpHeader("Authorization", "Bearer ${token}")
                .normalizedCache(
                    normalizedCacheFactory = cacheFactory,
                    cacheKeyGenerator = cacheKeyGenerator
                )
                .build()
        }
        LaunchedEffect(apolloClient) {
            if (isPreview) {
                iterationCadences = previewIterationCadences
                return@LaunchedEffect
            }
            if (apolloClient == null) return@LaunchedEffect
            val response = apolloClient.query(IterationCadencesQuery()).execute()
            iterationCadences = response.data?.group?.iterationCadences?.nodes?.filterNotNull().orEmpty()
        }
        LaunchedEffect(
            search,
            settingsUiState.iterationCadenceId,
            settingsUiState.pinnedIssues,
            settingsUiState.groupSprintInEpics,
            apolloClient,
            refreshTrigger
        ) {
            if (isPreview) {
                currentUserId = previewUserId
                issues = previewIssues
                return@LaunchedEffect
            }
            if (apolloClient == null) return@LaunchedEffect
            if (!isRefreshing) loading = true
            if (search.isNotEmpty()) delay(300)
            val pinnedPlusOpen = settingsUiState.pinnedIssues +
                    (settingsUiState.openTracking?.let { listOf(it.workItemId) } ?: emptyList())
            val query = IssuesQuery.Builder()
                .iterationCadenceId((settingsUiState.iterationCadenceId?.let { listOf(it) } ?: emptyList()))
                .pinnedIds(pinnedPlusOpen)
                // skip when searching to reduce query complexity
                .doPinnedSearch(pinnedPlusOpen.isNotEmpty() && search.isBlank())
                .search(search)
                .doSearch(search.isNotBlank())
                .build()
            apolloClient.query(query).fetchPolicy(FetchPolicy.NetworkFirst).watch().collect {
                currentUserId = it.data?.currentUser?.id.toString()
                issues = it.extractIssues(
                    groupSprintInEpics = settingsUiState.groupSprintInEpics,
                )
                loading = false
                isRefreshing = false
            }
        }
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
        ) {
            SupportingPaneScaffold(
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                supportingPane = {
                    AnimatedPane(Modifier.clip(align = Alignment.Start, minWidth = 290.dp)) {
                        Settings(
                            viewModel = settingsViewModel,
                            iterationCadences = iterationCadences,
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
                        val filteredIssues = issues.orEmpty().filter { issue ->
                            // Text search filter
                            val matchesSearch = search.isEmpty() ||
                                    issue.title.contains(search, ignoreCase = true) ||
                                    issue.id.toString().contains(search, ignoreCase = true) ||
                                    issue.id == settingsUiState.openTracking?.workItemId ||
                                    issue.iid.contains(search, ignoreCase = true) ||
                                    issue.webUrl.orEmpty().contains(search, ignoreCase = true) ||
                                    issue.assignees?.nodes.orEmpty().filterNotNull().any {
                                        it.name.contains(search, ignoreCase = true) ||
                                                it.username.contains(search, ignoreCase = true)
                                    } ||
                                    issue.labels?.nodes.orEmpty().filterNotNull().any {
                                        it.title.contains(search, ignoreCase = true)
                                    }

                            // Quick filters
                            val matchesQuickFilters = activeFilters.isEmpty() || activeFilters.all { filter ->
                                when (filter) {
                                    QuickFilter.MyIssues -> issue.assignees?.nodes.orEmpty()
                                        .filterNotNull().any { it.id == currentUserId }
                                    QuickFilter.HasTimeLogged -> issue.timelogs.isNotEmpty()
                                    QuickFilter.Pinned -> issue.id in settingsUiState.pinnedIssues
                                    QuickFilter.RecentlyTracked -> issue.timelogs.any {
                                        it.user.id == currentUserId
                                    }
                                }
                            }

                            matchesSearch && matchesQuickFilters
                        }
                        // Aggregate timelogs from all issues for history view
                        // Fetch all timelogs (up to 1 year) - filtering by period is done in TimeHistoryScreen
                        val allTimelogs = remember(issues, currentUserId) {
                            val now = Clock.System.now()
                            val cutoff = now - 365.days // Fetch up to 1 year of history
                            issues.orEmpty()
                                .flatMap { issue ->
                                    issue.timelogs
                                        .filter { timelog -> timelog.user.id == currentUserId }
                                        .mapNotNull { timelog ->
                                            val spentAt = timelog.spentAt?.let { Instant.parseOrNull(it.toString()) }
                                            if (spentAt != null && spentAt >= cutoff) {
                                                TimelogEntry(
                                                    id = timelog.id,
                                                    spentAt = spentAt,
                                                    summary = timelog.summary,
                                                    timeSpent = timelog.timeSpent,
                                                    issueTitle = issue.title,
                                                    issueUrl = issue.webUrl,
                                                    issueIid = issue.iid
                                                )
                                            } else null
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
                                onBack = { showHistory = false }
                            )
                        } else {
                        val lazyListState = rememberLazyListState()
                        // State for tracking switch confirmation dialog
                        var switchTrackingTarget by remember { mutableStateOf<Pair<String, String>?>(null) } // (id, title)

                        // Confirmation dialog for switching tracking
                        switchTrackingTarget?.let { (targetId, targetTitle) ->
                            SwitchTrackingDialog(
                                targetTitle = targetTitle,
                                currentTracking = settingsUiState.openTracking,
                                onKeepTimeAndSwitch = {
                                    settingsUiState.openTracking?.let { currentTracking ->
                                        settingsViewModel.setOpenTracking(
                                            currentTracking.copy(
                                                workItemId = targetId,
                                                workItemTitle = targetTitle
                                            )
                                        )
                                    }
                                    switchTrackingTarget = null
                                },
                                onDiscardAndSwitch = {
                                    settingsViewModel.setOpenTracking(
                                        OpenTracking(
                                            workItemId = targetId,
                                            workItemTitle = targetTitle,
                                            timeOfOpen = Clock.System.now()
                                        )
                                    )
                                    switchTrackingTarget = null
                                },
                                onShowCurrent = {
                                    val currentId = settingsUiState.openTracking?.workItemId
                                    val index = filteredIssues.indexOfFirst { issue -> issue.id == currentId }
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
                                refreshTrigger++
                            },
                            modifier = Modifier.onConsumedWindowInsetsChanged {
                                consumedWindowInsets.insets = it
                            }
                        ) {
                        LazyColumn(
                            state = lazyListState,
                            contentPadding = insets + extraPadding,
                        ) {
                            stickyHeader {
                                Column(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Search(
                                            search = search,
                                            onSearchChange = { search = it },
                                            show = alwaysShowSearch || search.isNotEmpty() || !lazyListState.canScrollBackward,
                                            modifier = Modifier
                                                .weight(1f)
                                                .focusRequester(focusRequester)
                                                .focusProperties { canFocus = !disableGlobalSearch },
                                            onPress = { disableGlobalSearch = false }
                                        )
                                        // Hamburger menu
                                        var menuExpanded by remember { mutableStateOf(false) }
                                        Box {
                                            IconButton(onClick = { menuExpanded = true }) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu,
                                                    contentDescription = "Menu"
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = menuExpanded,
                                                onDismissRequest = { menuExpanded = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Time History") },
                                                    onClick = {
                                                        menuExpanded = false
                                                        showHistory = true
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Default.History,
                                                            contentDescription = null
                                                        )
                                                    }
                                                )
                                                if (navigator.scaffoldValue.secondary == PaneAdaptedValue.Hidden) {
                                                    DropdownMenuItem(
                                                        text = { Text("Settings") },
                                                        onClick = {
                                                            menuExpanded = false
                                                            coroutineScope.launch {
                                                                navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                                            }
                                                        },
                                                        leadingIcon = {
                                                            Icon(
                                                                imageVector = Icons.Outlined.Settings,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
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

                            @Composable
                            operator fun BareWorkItem.invoke() {
                                val isTracking = id == settingsUiState.openTracking?.workItemId
                                val pinned = id in settingsUiState.pinnedIssues
                                val togglePinned = { settingsViewModel.togglePinIssue(id.toString()) }
                                fun startTracking() = settingsViewModel.setOpenTracking(
                                    OpenTracking(
                                        workItemId = id.toString(),
                                        workItemTitle = title,
                                        timeOfOpen = Clock.System.now()
                                    )
                                )
                                SwipeableIssueCard(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    isPinned = pinned,
                                    isTracking = isTracking,
                                    onStartTracking = {
                                        if (settingsUiState.openTracking?.workItemId == null) {
                                            startTracking()
                                        } else {
                                            switchTrackingTarget = id.toString() to title
                                        }
                                    },
                                    onTogglePin = togglePinned
                                ) {
                                    Column {
                                        var commitTimeTrackingEnabled by remember { mutableStateOf(true) }
                                        var commitTimeTrackingErrors by remember {
                                            mutableStateOf<List<String>?>(null)
                                        }
                                        Issue(
                                            this@invoke,
                                            currentUserId = currentUserId,
                                            settingsUiState.showLabelsByDefault,
                                            settingsUiState.useLabelColors,
                                            openTracking = settingsUiState.openTracking,
                                            onOpenTrackingChange = { openTracking ->
                                                settingsViewModel.setOpenTracking(openTracking)
                                            },
                                            pinned = pinned,
                                            togglePinned = togglePinned,
                                            commitTimeTrackingEnabled = commitTimeTrackingEnabled,
                                            commitTimeTracking = commitTimeTracking@{
                                                if (!commitTimeTrackingEnabled) return@commitTimeTracking
                                                if (apolloClient == null) {
                                                    commitTimeTrackingErrors = listOf("Please check your settings.")
                                                    return@commitTimeTracking
                                                }
                                                commitTimeTrackingEnabled = false
                                                commitTimeTrackingErrors = null
                                                coroutineScope.launch {
                                                    settingsUiState.openTracking?.let {
                                                        suspend fun manualRefresh() {
                                                            // https://gitlab.com/gitlab-org/gitlab/-/issues/584627
                                                            val success = "Saved successfully!"
                                                            val ignoredWarning =
                                                                "Only refreshing failed (will be ignored)!"
                                                            val manualRefreshResult =
                                                                apolloClient.query(
                                                                    RefreshIssuesQuery.Builder()
                                                                        .ids(listOf(id)).build()
                                                                ).execute()
                                                            if (manualRefreshResult.exception != null) {
                                                                commitTimeTrackingErrors =
                                                                    listOf(
                                                                        success,
                                                                        ignoredWarning,
                                                                        manualRefreshResult.exception?.message.orEmpty()
                                                                    )
                                                                delay(4.seconds)
                                                                commitTimeTrackingErrors = null
                                                            } else if (manualRefreshResult.hasErrors()) {
                                                                commitTimeTrackingErrors =
                                                                    listOf(success, ignoredWarning) +
                                                                            manualRefreshResult.errors
                                                                                ?.map { it.message }.orEmpty()
                                                                delay(4.seconds)
                                                                commitTimeTrackingErrors = null
                                                            }
                                                            settingsViewModel.setOpenTracking(null)
                                                        }

                                                        val timeSpent = it.customTimeSpent
                                                            ?: (Clock.System.now() - it.timeOfOpen)
                                                                .inWholeMinutes.minutes.toString()
                                                        val result = apolloClient.mutation(
                                                            TimelogCreateMutation(
                                                                workItemId = listOf(id),
                                                                input =
                                                                    TimelogCreateInput.Builder()
                                                                        .issuableId(id)
                                                                        .summary(it.summary.orEmpty())
                                                                        .timeSpent(timeSpent)
                                                                        .build()
                                                            )
                                                        ).execute()

                                                        fun failedBecauseOfEpic() =
                                                            result.errors?.let { errors ->
                                                                errors.isNotEmpty() && errors.all {
                                                                    it.message == "Cannot return null for non-nullable field Timelog.project"
                                                                }
                                                            } == true
                                                        if (result.exception != null) {
                                                            commitTimeTrackingErrors =
                                                                listOf(result.exception?.message.orEmpty())
                                                        } else if (failedBecauseOfEpic()) {
                                                            manualRefresh()
                                                        } else if (result.hasErrors()) {
                                                            commitTimeTrackingErrors = result.errors?.map { it.message }
                                                        } else {
                                                            settingsViewModel.setOpenTracking(null)
                                                        }
                                                    }
                                                    commitTimeTrackingEnabled = true
                                                }
                                            },
                                            disableGlobalSearchIfFocused = disableGlobalSearchIfFocused,
                                            modifier = Modifier.clickable(
                                                enabled = !isTracking,
                                                onClickLabel = "Work on issue",
                                                role = Role.Switch
                                            ) {
                                                if (settingsUiState.openTracking?.workItemId == null) {
                                                    startTracking()
                                                } else {
                                                    switchTrackingTarget = id.toString() to title
                                                }
                                            }
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
                            }

                            val groupedIssues = filteredIssues.groupBy { issue ->
                                when {
                                    issue.id in settingsUiState.pinnedIssues -> Section.Pinned
                                    issue.state == WorkItemState.CLOSED -> Section.Closed
                                    else -> Section.Open
                                }
                            }

                            fun section(section: Section) {
                                val sectionIssues = groupedIssues[section]
                                if (!sectionIssues.isNullOrEmpty()) {
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
                                                count = sectionIssues.size
                                            )
                                        }
                                    }


                                    if (open) items(sectionIssues, key = { issue -> issue.id }) { sectionIssue ->
                                        FadeInItem {
                                            sectionIssue()
                                        }
                                    }
                                }
                            }

                            section(Section.Pinned)
                            section(Section.Open)
                            section(Section.Closed)

                            // Show shimmer loading when loading
                            if (loading && filteredIssues.isEmpty()) {
                                item {
                                    LoadingIssuesList(
                                        count = 5,
                                        modifier = Modifier
                                            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                                            .padding(horizontal = 12.dp)
                                    )
                                }
                            }

                            // Show empty state when no issues
                            if (!loading && filteredIssues.isEmpty()) {
                                item {
                                    if (search.isNotEmpty()) {
                                        NoSearchResultsEmptyState(
                                            modifier = Modifier
                                                .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                                        )
                                    } else {
                                        NoIssuesEmptyState(
                                            modifier = Modifier
                                                .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                                        )
                                    }
                                }
                            }

                            // Small loading indicator when refreshing with existing data
                            if (loading && filteredIssues.isNotEmpty()) item {
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
        // Fake item to ignore focus requests if a issue is open
        Box(modifier = Modifier.focusProperties { canFocus = false }.focusRequester(focusRequester))
    }
}

val BareWorkItem.assignees get() = widgets?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetAssignees != null }
    ?.bareWorkItemWidgets?.onWorkItemWidgetAssignees?.assignees
val BareWorkItem.labels get() = widgets?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetLabels != null }
    ?.bareWorkItemWidgets?.onWorkItemWidgetLabels?.labels
val IssuesQuery.Node.parent get() = this.widgets?.firstOrNull { it.onWorkItemWidgetHierarchy != null }
    ?.onWorkItemWidgetHierarchy?.parent?.bareWorkItem
val BareWorkItem.timelogs get() = widgets?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetTimeTracking != null }
    ?.bareWorkItemWidgets?.onWorkItemWidgetTimeTracking?.timelogs?.nodes?.filterNotNull().orEmpty()

@Suppress("DEPRECATION") // experimental api
private fun ApolloResponse<IssuesQuery.Data>.extractIssues(
    groupSprintInEpics: Boolean,
): List<BareWorkItem> {
    val namespace = data?.namespace
    return buildList {
        addAll(namespace?.sprint?.nodes?.map {
            if (groupSprintInEpics) {
                it?.parent ?: it?.bareWorkItem
            } else {
                it?.bareWorkItem
            }
        }.orEmpty())
        addAll(namespace?.pinned?.nodes?.map { it?.bareWorkItem }.orEmpty())
        addAll(namespace?.search?.nodes?.map { it?.bareWorkItem }.orEmpty())
        addAll(namespace?.searchIid?.nodes?.map { it?.bareWorkItem }.orEmpty())
    }.filterNotNull().distinctBy { it.id }.sortedByDescending { it.state.name }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun Issue(
    issue: BareWorkItem,
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
    additionalContent: (@Composable () -> Unit)? = null
) {
    val uriHandler = LocalUriHandler.current
    val open = openTracking?.workItemId == issue.id
    var showTimelogs by remember { mutableStateOf(false) }
    val allTimelogs = issue.timelogs
    val myTimelogs = allTimelogs.filter { it.user.id == currentUserId }
    val myTotalTime = myTimelogs.fold(0) { acc, timelog -> acc + timelog.timeSpent }
    val totalMinutes = myTotalTime / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val myTotalTimeString = "%d:%02d".format(hours, minutes)
    val spacing = LocalSpacing.current

    // Hover state for visual feedback
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Animated surface color based on state
    val surfaceColor by animateColorAsState(
        targetValue = when {
            open -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            isHovered -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(150),
        label = "issueSurfaceColor"
    )

    Surface(
        modifier = modifier
            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
            .padding(horizontal = 12.dp)
            .then(
                if (open) Modifier.border(
                    width = 2.dp,
                    color = TimerActiveColor,
                    shape = RoundedCornerShape(spacing.cardRadius)
                ) else Modifier
            ),
        color = surfaceColor,
        shape = RoundedCornerShape(spacing.cardRadius),
        shadowElevation = if (open) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .padding(spacing.cardPadding)
        ) {
        val labels = if (showLabelsByDefault) issue.labels?.nodes else null
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = if (labels.isNullOrEmpty()) 48.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            issue.workItemType.name.let { WorkItemTypeIcon(it) }
            Text(
                text = buildAnnotatedString {
                    append(issue.title)
                    if (myTotalTime > 0) {
                        append(" ")
                        appendInlineContent("time", myTotalTimeString)
                    }
                    if (issue.promotedToEpicUrl != null) {
                        append(" ")
                        appendInlineContent("promoted", "(promoted)")
                    } else if (issue.state == WorkItemState.CLOSED) {
                        append(" ")
                        appendInlineContent("closed", "(closed)")
                    }
                },
                inlineContent = mapOf(
                    "time" to InlineTextContent(
                        Placeholder(
                            width = (myTotalTimeString.length * 8 + 14).sp,
                            height = 20.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        TimeBadge(
                            time = myTotalTimeString,
                            onClick = { showTimelogs = !showTimelogs }
                        )
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
                                    issue.promotedToEpicUrl?.let { uri -> uriHandler.openUri(uri) }
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    it.filterNotNull().forEach { label -> Label(label = label, useColors = useLabelColors) }
                }
            }
        }
        AnimatedVisibility(visible = showTimelogs) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                myTimelogs.forEachIndexed { index, timelog ->
                    val isEven = index % 2 == 0
                    val rowBg = if (isEven) {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent
                    }
                    SimpleTooltip(timelog.spentAt?.let { Instant.parseOrNull(it.toString()) }?.let {
                        "${formatDuration(Clock.System.now() - it, RelativeTime.Past)} ago"
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
                                text = "%d:%02d".format(h, m),
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.width(48.dp)
                            )
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
            var timeSinceOpen by remember { mutableStateOf(Duration.ZERO) }
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
                    label = { Text("Time spent")},
                    placeholder = { Text("Example: 1h 30m") },
                    visualTransformation = customTimeSpent?.let { VisualTransformation.None }
                        ?: AddedTextVisualTransformation(
                            addedText = buildAnnotatedString {
                                withStyle(SpanStyle(color = LocalContentColor.current.copy(alpha = 0.5f))) {
                                    append(" ${(timeSinceOpen - timeSinceOpenInWholeMinutes).inWholeSeconds}s")
                                }
                            }
                        ),
                    trailingIcon = {
                        when {
                            customTimeSpent != null -> Row {
                                val customTimeSpentDuration = Duration.parseOrNull(customTimeSpent.trim())
                                customTimeSpentDuration?.let {
                                    SimpleTooltip("Continue timer\n(overwrites $timeSinceOpenString)") {
                                        IconButton(
                                            modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                                            onClick = {
                                                timeSinceOpen = customTimeSpentDuration
                                                onOpenTrackingChange(openTracking.copy(
                                                    timeOfOpen = Clock.System.now() - customTimeSpentDuration,
                                                    customTimeSpent = null,
                                                ))
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
                    isError = customTimeSpent?.let { Duration.parseOrNull(it.trim()) == null } ?: false
                )
                FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    AdditionalActions(issue, pinned, togglePinned)
                    SimpleTooltip("Discard time tracking") {
                        IconButton(onClick = { onOpenTrackingChange(null) }) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Discard time tracking")
                        }
                    }
                    SimpleTooltip("Commit time tracking") {
                        FilledTonalIconButton(
                            modifier = Modifier.padding(start = 4.dp),
                            onClick = commitTimeTracking
                        ) {
                            val x by rememberInfiniteTransition().animateValue(
                                initialValue = if (commitTimeTrackingEnabled) 0.dp else (-35).dp,
                                targetValue = if (commitTimeTrackingEnabled) 0.dp  else 35.dp,
                                typeConverter =  Dp.VectorConverter,
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
            LaunchedEffect(open, openTracking.timeOfOpen) {
                if (!open) return@LaunchedEffect
                focusRequester.requestFocus()
                while (true) {
                    if (!isActive) return@LaunchedEffect
                    timeSinceOpen = (Clock.System.now() - openTracking.timeOfOpen)
                    delay(1.seconds)
                }
            }
        }
        additionalContent?.invoke()
        }
    }
}

@Composable
fun Modifier.changeFocusOnTab(): Modifier {
    val focusManager = LocalFocusManager.current
    return onPreviewKeyEvent {
        if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
            val direction = if (it.isShiftPressed) FocusDirection.Previous else FocusDirection.Next
            focusManager.moveFocus(direction)
            true
        } else {
            false
        }
    }
}

fun Modifier.onCtrlOrMetaEnter(block: () -> Unit) = onPreviewKeyEvent {
    if (it.key == Key.Enter && (it.isMetaPressed || it.isCtrlPressed) && it.type == KeyEventType.KeyUp) {
        block()
        true
    } else {
        false
    }
}

