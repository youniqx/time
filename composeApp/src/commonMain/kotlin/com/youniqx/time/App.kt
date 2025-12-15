@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)

package com.youniqx.time

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.luminance
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
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
import com.youniqx.time.gitlab.models.IssuesQuery
import com.youniqx.time.gitlab.models.IterationCadencesQuery
import com.youniqx.time.gitlab.models.TimelogCreateMutation
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.type.TimelogCreateInput
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.modifier.adaptivePadding
import com.youniqx.time.modifier.clip
import com.youniqx.time.relativetime.RelativeTime
import com.youniqx.time.relativetime.formatDuration
import com.youniqx.time.settings.OpenTracking
import com.youniqx.time.settings.Settings
import com.youniqx.time.settings.SettingsViewModel
import com.youniqx.time.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.text.Typography.nbsp
import kotlin.time.Clock
import kotlin.time.Duration
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
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val settingsViewModel = viewModel<SettingsViewModel>(
        factory = viewModelFactory { initializer { SettingsViewModel(systemInDarkTheme) } }
    )
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    AppTheme(darkTheme = settingsUiState.darkTheme, useHighContrastColors = settingsUiState.highContrastColors) {
        if (setWindowBackground != null) {
            MaterialTheme.colorScheme.surface.let { color ->
                LaunchedEffect(setWindowBackground, color) {
                    setWindowBackground(color)
                }
            }
        }
        var currentUserId: String? by remember { mutableStateOf(null) }
        var issues: List<BareWorkItem>? by remember { mutableStateOf(null) }
        var iterationCadences: List<IterationCadencesQuery.Node>? by remember { mutableStateOf(null) }
        var search: String by remember { mutableStateOf("") }
        var loading: Boolean by remember { mutableStateOf(false) }
        var disableGlobalSearch by remember { mutableStateOf(false) }
        val disableGlobalSearchIfFocused: Modifier.() -> Modifier = {
            onFocusChanged { disableGlobalSearch = it.hasFocus }
        }
        val isPreview = LocalInspectionMode.current
        val apolloClient = remember(settingsUiState.token) {
            val cacheFactory = MemoryCacheFactory(maxSizeBytes = 30 * 1024 * 1024)
            val cacheKeyGenerator = object : CacheKeyGenerator {
                override fun cacheKeyForObject(obj: Map<String, Any?>, context: CacheKeyGeneratorContext): CacheKey? {
                    // Generate the cache ID based on the object's id field
                    return (obj["id"] as? String)?.let(::CacheKey)
                }
            }
            ApolloClient.Builder()
                .serverUrl("https://gitlab.ci.youniqx.com/api/graphql")
                .addHttpHeader("Authorization", "Bearer ${settingsUiState.token}")
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
            val response = apolloClient.query(IterationCadencesQuery()).execute()
            iterationCadences = response.data?.group?.iterationCadences?.nodes?.filterNotNull().orEmpty()
        }
        LaunchedEffect(
            search,
            settingsUiState.iterationCadenceId,
            settingsUiState.pinnedIssues,
            settingsUiState.groupSprintInEpics,
            apolloClient
        ) {
            if (isPreview) {
                currentUserId = previewUserId
                issues = previewIssues
                return@LaunchedEffect
            }
            loading = true
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
            floatingActionButton = {
                if (navigator.scaffoldValue.secondary == PaneAdaptedValue.Hidden ||
                    navigator.scaffoldValue.primary == PaneAdaptedValue.Hidden) CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isHovered by interactionSource.collectIsHoveredAsState()
                    FloatingActionButton(
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
                            viewModel = settingsViewModel,
                            iterationCadences = iterationCadences,
                            disableGlobalSearchIfFocused = disableGlobalSearchIfFocused
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
                        val filteredIssues = issues.orEmpty().filter {
                            it.title.contains(search, ignoreCase = true) ||
                                    it.id.toString().contains(search, ignoreCase = true) ||
                                    it.id == settingsUiState.openTracking?.workItemId ||
                                    it.iid.contains(search, ignoreCase = true) ||
                                    it.webUrl.orEmpty().contains(search, ignoreCase = true) ||
                                    it.assignees?.nodes.orEmpty().filterNotNull().any {
                                        it.name.contains(search, ignoreCase = true) ||
                                                it.username.contains(search, ignoreCase = true)
                                    } ||
                                    it.labels?.nodes.orEmpty().filterNotNull().any {
                                        it.title.contains(search, ignoreCase = true)
                                    }
                        }
                        val lazyListState = rememberLazyListState()
                        var openTrackingWarningOn by remember { mutableStateOf<String?>(null) }
                        val openSections = remember { mutableStateListOf(Section.Pinned, Section.Open) }
                        LazyColumn(
                            modifier =
                                Modifier
                                    .onConsumedWindowInsetsChanged {
                                        consumedWindowInsets.insets = it
                                    },
                            state = lazyListState,
                            contentPadding = insets + extraPadding,
                        ) {
                            stickyHeader {
                                Search(
                                    search = search,
                                    onSearchChange = { search = it },
                                    show = (alwaysShowSearch || search.isNotEmpty()) && !lazyListState.canScrollBackward,
                                    modifier = Modifier
                                        .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
                                        .focusRequester(focusRequester)
                                        .focusProperties { canFocus = !disableGlobalSearch },
                                    onPress = { disableGlobalSearch = false }
                                )
                            }

                            @Composable
                            operator fun BareWorkItem.invoke() {
                                val showOpenTrackingWarning = openTrackingWarningOn == id
                                val open = id == settingsUiState.openTracking?.workItemId
                                fun startTracking() = settingsViewModel.setOpenTracking(
                                    OpenTracking(
                                        workItemId = id.toString(),
                                        timeOfOpen = Clock.System.now()
                                    )
                                )
                                Column {
                                    val pinned = id in settingsUiState.pinnedIssues
                                    val togglePinned = { settingsViewModel.togglePinIssue(id.toString()) }
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
                                        commitTimeTracking = commitTimeTracking@ {
                                            if (!commitTimeTrackingEnabled) return@commitTimeTracking
                                            commitTimeTrackingEnabled = false
                                            commitTimeTrackingErrors = null
                                            coroutineScope.launch {
                                                settingsUiState.openTracking?.let {
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
                                                    if (result.exception != null) {
                                                        commitTimeTrackingErrors =
                                                            listOf(result.exception?.message.orEmpty())
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
                                            enabled = !open,
                                            onClickLabel = "Work on issue",
                                            role = Role.Switch
                                        ) {
                                            if (settingsUiState.openTracking?.workItemId == null) {
                                                startTracking()
                                            } else {
                                                openTrackingWarningOn =
                                                    if (showOpenTrackingWarning) null else id.toString()
                                            }
                                        }
                                    ) {
                                        AnimatedVisibility(visible = showOpenTrackingWarning) {
                                            settingsUiState.openTracking?.let {
                                                Column {
                                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing * 1.5f))
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Warning,
                                                            modifier = Modifier.size(ButtonDefaults.IconSize),
                                                            contentDescription = null
                                                        )
                                                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                                        Text(text = "You are currently tracking another issue")
                                                    }
                                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                                    FlowRow(
                                                        horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
                                                    ) {
                                                        TextButton(
                                                            onClick = {
                                                                val index = filteredIssues.indexOfFirst {
                                                                        issue -> issue.id == it.workItemId
                                                                }
                                                                coroutineScope.launch {
                                                                    lazyListState.animateScrollToItem(index + 1, -100)
                                                                }
                                                            },
                                                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                                                        ) {
                                                            Icon(
                                                                Icons.Filled.Visibility,
                                                                contentDescription = "Show",
                                                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                                            )
                                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                                            Text("Show")
                                                        }
                                                        TextButton(
                                                            onClick = {
                                                                openTrackingWarningOn = null
                                                                startTracking()
                                                            },
                                                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                                                        ) {
                                                            Icon(
                                                                Icons.Filled.DeleteForever,
                                                                contentDescription = "Discard",
                                                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                                            )
                                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                                            Text("Discard")
                                                        }
                                                        TextButton(
                                                            onClick = {
                                                                openTrackingWarningOn = null
                                                                settingsViewModel.setOpenTracking(
                                                                    openTracking = it.copy(workItemId = id.toString())
                                                                )
                                                            },
                                                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                                                        ) {
                                                            Icon(
                                                                Icons.Filled.ContentCopy,
                                                                contentDescription = "Copy over",
                                                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                                            )
                                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                                            Text("Copy over")
                                                        }
                                                    }
                                                    FlowRow(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.End
                                                    ) {
                                                        AdditionalActions(this@invoke, pinned, togglePinned)
                                                    }
                                                }
                                            }
                                        }
                                    }
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
                                        sectionIssue()
                                    }
                                }
                            }

                            section(Section.Pinned)
                            section(Section.Open)
                            section(Section.Closed)

                            if (loading) item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
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
    val myTotalTimeString = myTotalTime.seconds.inWholeMinutes.minutes.toString()
    Column(
        modifier = modifier
            .heightIn(min = 48.dp)
            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
            .padding(horizontal = 12.dp)
            .padding(vertical = 8.dp)
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
                        append(" - ")
                        val linkStyle = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                        )
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "Tracking",
                                styles = TextLinkStyles(
                                    style = SpanStyle(color = LocalContentColor.current),
                                    focusedStyle = linkStyle,
                                    hoveredStyle = linkStyle,
                                    pressedStyle = linkStyle,
                        ),
                                linkInteractionListener = {
                                    showTimelogs = !showTimelogs
                                },

                            )
                        ) {
                            append(myTotalTimeString.replace(' ', nbsp))
                        }
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy((-4).dp, Alignment.CenterVertically)
                ) {
                    it.filterNotNull().onEach { label -> Label(label = label, useColors = useLabelColors) }
                }
            }
        }
        AnimatedVisibility(visible = showTimelogs) {
            Column {
                myTimelogs.forEach {
                    SimpleTooltip(it.spentAt?.let { Instant.parseOrNull(it.toString()) }?.let {
                        "${formatDuration(Clock.System.now() - it, RelativeTime.Past)} ago"
                    }.orEmpty()) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = it.timeSpent.seconds.inWholeMinutes.minutes.toString().replace(' ', nbsp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            VerticalDivider(thickness = 0.5.dp, modifier = Modifier.size(width = 0.5.dp, height = 20.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                modifier = Modifier.weight(4f),
                                text = it.summary.orEmpty(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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

fun String.toColorInt(): Int {
    if (this[0] == '#') {
        var color = substring(1).toLong(16)
        if (length == 7) {
            color = color or 0x00000000ff000000L
        } else if (length != 9) {
            throw IllegalArgumentException("Unknown color")
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}

/**
 * Returns either Black or White as a high-contrast text color
 * for this background color.
 *
 * @param threshold The luminance value to check against.
 * WCAG suggests a threshold of 0.179 for true sRGB.
 * You can adjust this value to fine-tune the results.
 * @return `Color.Black` if the background is light, or `Color.White` if it's dark.
 */
fun Color.contrastingTextColor(threshold: Double = 0.25): Color {
    return if (luminance() > threshold) Color.Black else Color.White
}

@Composable
fun SimpleTooltip(text: String, content: @Composable () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip { Text(text) }
        },
        state = rememberTooltipState(),
        content = content
    )
}
