@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)

package com.youniqx.time

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.youniqx.time.gitlab.models.IssuesQuery
import com.youniqx.time.gitlab.models.IterationCadencesQuery
import com.youniqx.time.gitlab.models.TimelogCreateMutation
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets
import com.youniqx.time.gitlab.models.type.TimelogCreateInput
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.settings.Settings
import com.youniqx.time.settings.SettingsViewModel
import com.youniqx.time.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

operator fun PaddingValues.plus(other: PaddingValues): PaddingValues = PaddingValues(
    start = this.calculateStartPadding(LayoutDirection.Ltr) +
            other.calculateStartPadding(LayoutDirection.Ltr),
    top = this.calculateTopPadding() + other.calculateTopPadding(),
    end = this.calculateEndPadding(LayoutDirection.Ltr) +
            other.calculateEndPadding(LayoutDirection.Ltr),
    bottom = this.calculateBottomPadding() + other.calculateBottomPadding(),
)

val loremIpsum = """
    Aut aut minima quidem occaecati ea consequuntur est. Iure velit minus enim id sit explicabo nulla dolorem. Alias officiis quia et exercitationem.
    Doloribus adipisci fugit molestias illum. Quos assumenda minus et consequatur officia reprehenderit. Atque quia est et. Minima aut labore nostrum. Omnis voluptates occaecati molestias assumenda. Dolorum quia at soluta sequi vero saepe.
    Non distinctio qui placeat dolores ab voluptatum ea. Et corporis veniam labore quia in ut velit qui. Laudantium quo repudiandae quam quae saepe esse voluptatum consequuntur. Qui numquam optio commodi.
""".trimIndent()

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun App(token: String = "", focusRequester: FocusRequester = remember { FocusRequester() }) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val settingsViewModel = viewModel<SettingsViewModel>(
        factory = viewModelFactory { initializer { SettingsViewModel(token, systemInDarkTheme) } }
    )
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    AppTheme(darkTheme = settingsUiState.darkTheme, useHighContrastColors = settingsUiState.highContrastColors) {
        var issues: List<BareWorkItem>? by remember { mutableStateOf(null) }
        var iterationCadences: List<IterationCadencesQuery.Node>? by remember { mutableStateOf(null) }
        var search: String by remember { mutableStateOf("") }
        var loading: Boolean by remember { mutableStateOf(false) }
        var openIssue by remember { mutableStateOf<BareWorkItem?>(null) }
        var disableGlobalSearch by remember { mutableStateOf(false) }
        val disableGlobalSearchIfFocused: Modifier.() -> Modifier = {
            onFocusChanged { disableGlobalSearch = it.hasFocus }
        }
        val isPreview = LocalInspectionMode.current
        val apolloClient = remember(settingsUiState.token) {
            ApolloClient.Builder()
                .serverUrl("https://gitlab.ci.youniqx.com/api/graphql")
                .addHttpHeader("Authorization", "Bearer ${settingsUiState.token}")
                .build()
        }
        LaunchedEffect(apolloClient) {
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
                issues = previewIssues
                return@LaunchedEffect
            }
            loading = true
            if (search.isNotEmpty()) delay(300)
            val pinnedPlusOpen = settingsUiState.pinnedIssues + (openIssue?.let { listOf(it.iid) } ?: emptyList())
            val query = IssuesQuery.Builder()
                .iterationCadenceId((settingsUiState.iterationCadenceId?.let { listOf(it) } ?: emptyList()))
                .pinnedIids(pinnedPlusOpen)
                .doPinnedSearch(pinnedPlusOpen.isNotEmpty())
                .search(search)
                .doSearch(search.isNotBlank())
                .build()
            val response = apolloClient.query(query).execute()
            issues = response.extractIssues(settingsUiState.groupSprintInEpics)
            loading = false
        }
        val navigator = rememberSupportingPaneScaffoldNavigator()
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
                    AnimatedPane {
                        Settings(
                            viewModel = settingsViewModel,
                            iterationCadences = iterationCadences,
                            disableGlobalSearchIfFocused = disableGlobalSearchIfFocused
                        )
                    }
                }, mainPane = {
                    AnimatedPane {
                        // https://kotlinlang.slack.com/archives/CJLTWPH7S/p1731631796638429?thread_ts=1731631796.638429
                        val consumedWindowInsets = remember { MutableWindowInsets() }
                        val insets =
                            WindowInsets.systemBarsForVisualComponents
                                .exclude(consumedWindowInsets)
                                .asPaddingValues()
                        val lazyListState = rememberLazyListState()
                        LazyColumn(
                            modifier =
                                Modifier
                                    .onConsumedWindowInsetsChanged {
                                        consumedWindowInsets.insets = it
                                    },
                            state = lazyListState,
                            contentPadding = insets,
                        ) {
                            stickyHeader {
                                Search(
                                    search = search,
                                    onSearchChange = { search = it },
                                    show = search.isNotEmpty() && !lazyListState.canScrollBackward,
                                    modifier = Modifier
                                        .focusRequester(focusRequester)
                                        .focusProperties { canFocus = !disableGlobalSearch },
                                    onPress = { disableGlobalSearch = false }
                                )
                                LaunchedEffect(true) {
                                    focusRequester.requestFocus()
                                }
                            }
                            itemsIndexed(issues.orEmpty().filter {
                                it.title.contains(search, ignoreCase = true) ||
                                        it.id.toString().contains(search, ignoreCase = true) ||
                                        it.id == openIssue?.id ||
                                        it.iid.contains(search, ignoreCase = true) ||
                                        it.webUrl.orEmpty().contains(search, ignoreCase = true) ||
                                        it.assignees?.nodes.orEmpty().filterNotNull().any {
                                            it.name.contains(search, ignoreCase = true) ||
                                                    it.username.contains(search, ignoreCase = true)
                                        } ||
                                        it.labels?.nodes.orEmpty().filterNotNull().any {
                                            it.title.contains(search, ignoreCase = true)
                                        }
                            }, key = { _, issue -> issue.id }) { index, issue ->
                                // if (index != 0) HorizontalDivider(thickness = 0.5.dp)
                                val open = issue.id == openIssue?.id
                                Issue(
                                    issue,
                                    settingsUiState.showLabelsByDefault,
                                    settingsUiState.useLabelColors,
                                    open = open,
                                    onClick = {
                                        openIssue = if (open) null else issue
                                    },
                                    pinned = issue.iid in settingsUiState.pinnedIssues,
                                    togglePinned = { settingsViewModel.togglePinIssue(issue.iid) },
                                    apolloClient = apolloClient,
                                    disableGlobalSearchIfFocused = disableGlobalSearchIfFocused
                                )
                            }
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
                paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
                paneExpansionDragHandle = { state ->
                    val interactionSource =
                        remember { MutableInteractionSource() }
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

private val previewIssues: List<BareWorkItem> = buildList {
    repeat(20) {
        val start = Random.nextInt(loremIpsum.lastIndex - 100)
        add(
            BareWorkItem(
                id = it.toString(),
                iid = it.toString(),
                title = loremIpsum.substring(start, start + Random.nextInt(20, 100)).trim(),
                webUrl = "",
                state = WorkItemState.OPEN,
                workItemType = BareWorkItem.WorkItemType(id = "", name = "team::shrews"),
                promotedToEpicUrl = null,
                widgets = listOf(
                    BareWorkItem.Widget(
                        __typename = "WorkItemWidgetLabels",
                        bareWorkItemWidgets = BareWorkItemWidgets(
                            __typename = "WorkItemWidgetLabels",
                            onWorkItemWidgetLabels = BareWorkItemWidgets.OnWorkItemWidgetLabels(
                                labels = null
                            ),
                            onWorkItemWidgetAssignees = BareWorkItemWidgets.OnWorkItemWidgetAssignees(
                                assignees = null
                            )
                        )
                    )
                )
            )
        )
    }
}

val BareWorkItem.assignees get() = widgets?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetAssignees != null }
    ?.bareWorkItemWidgets?.onWorkItemWidgetAssignees?.assignees
val BareWorkItem.labels get() = widgets?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetLabels != null }
    ?.bareWorkItemWidgets?.onWorkItemWidgetLabels?.labels
val IssuesQuery.Node.parent get() = this.widgets?.firstOrNull { it.onWorkItemWidgetHierarchy != null }
    ?.onWorkItemWidgetHierarchy?.parent?.bareWorkItem

@Suppress("DEPRECATION") // experimental api
private fun ApolloResponse<IssuesQuery.Data>.extractIssues(groupSprintInEpics: Boolean): List<BareWorkItem>? {
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

@Composable
fun Search(
    search: String,
    onSearchChange: (String) -> Unit,
    show: Boolean,
    modifier: Modifier = Modifier,
    onPress: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            if (it is PressInteraction.Press) onPress()
        }
    }
    OutlinedTextField(
        value = search,
        onValueChange = onSearchChange,
        modifier = modifier
            .onPreviewKeyEvent {
                if (
                    !it.isMetaPressed &&
                    !it.isAltPressed &&
                    !it.isCtrlPressed &&
                    !it.isShiftPressed &&
                    it.key == Key.Tab &&
                    it.type == KeyEventType.KeyDown
                ) {
                    focusManager.moveFocus(FocusDirection.Next)
                    true
                } else {
                    false
                }
            }
            .fillMaxWidth()
            .then(
                if (show) {
                    Modifier.padding(horizontal = 12.dp).padding(vertical = 4.dp)
                } else {
                    Modifier.height(0.dp).alpha(0f)
                }
            ),
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun Issue(
    issue: BareWorkItem,
    showLabelsByDefault: Boolean,
    useLabelColors: Boolean,
    open: Boolean,
    onClick: () -> Unit,
    pinned: Boolean,
    togglePinned: () -> Unit,
    apolloClient: ApolloClient,
    disableGlobalSearchIfFocused: Modifier.() -> Modifier
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .clickable(
                onClickLabel = if (open) "Collapse issue and reset current timer" else "Work on issue",
                role = Role.Switch
            ) { onClick() }
            .heightIn(min = 48.dp)
            .padding(horizontal = 12.dp)
            .padding(vertical = 8.dp)
    ) {
        val labels = if (showLabelsByDefault) issue.labels?.nodes else null
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = if (labels.isNullOrEmpty()) 48.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            issue.workItemType.name.let {
                SimpleTooltip(it) {
                    Icon(
                        imageVector = when (it) {
                            "Task" -> Icons.Default.Task
                            "Epic" -> Icons.Default.Style
                            else -> Icons.Default.Sell
                        },
                        contentDescription = it,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = buildAnnotatedString {
                    append(issue.title)
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
                    it.filterNotNull().onEach { label ->
                        val default = SuggestionChipDefaults.suggestionChipColors()
                            .copy(
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                disabledLabelColor = LocalContentColor.current,
                            )
                        val colors = if (useLabelColors) remember(label.color) {
                            val color = try {
                                Color(label.color.toColorInt()).copy(alpha = 0.75f)
                            } catch (_: Exception) {
                                default.disabledContainerColor
                            }
                            default.copy(
                                disabledContainerColor = color,
                                disabledLabelColor = color.contrastingTextColor()
                            )
                        } else {
                            default
                        }
                        val titleParts = label.title.split("::")
                        SuggestionChip(
                            onClick = { },
                            enabled = false,
                            colors = colors,
                            label = {
                                Text(
                                    text = buildAnnotatedString {
                                        titleParts.forEachIndexed { i, titlePart ->
                                            append(titlePart)
                                            if (i != titleParts.lastIndex) {
                                                append(" ")
                                                appendInlineContent("divider", "|")
                                                append(" ")
                                            }
                                        }
                                    },
                                    inlineContent = mapOf(
                                        "divider" to InlineTextContent(
                                            Placeholder(
                                                width = 1.sp,
                                                height = 18.sp,
                                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                            )
                                        ) {
                                            VerticalDivider(
                                                thickness = 1.dp,
                                                color = colors.disabledLabelColor.copy(alpha = 0.6f)
                                            )
                                        },
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
        AnimatedVisibility(visible = open) {
            var timeOfOpen by remember { mutableStateOf(Clock.System.now()) }
            var timeSinceOpen by remember { mutableStateOf(Duration.ZERO) }
            val timeSinceOpenInWholeMinutes = timeSinceOpen.inWholeMinutes.minutes
            var customTimeSpent by remember { mutableStateOf<String?>(null) }
            var summary by remember { mutableStateOf("") }
            val focusRequester = remember { FocusRequester() }
            val coroutineScope = rememberCoroutineScope()
            fun commitTimeTracking() {
                coroutineScope.launch {
                    apolloClient.mutation(
                        TimelogCreateMutation(
                            input =
                                TimelogCreateInput.Builder()
                                    .issuableId(issue.id)
                                    .summary(summary)
                                    .timeSpent(customTimeSpent ?: timeSinceOpenInWholeMinutes.toString())
                                    .build()
                        )
                    ).execute()
                    onClick()
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth()
                        .disableGlobalSearchIfFocused()
                        .focusRequester(focusRequester)
                        .changeFocusOnTab()
                        .onCtrlOrMetaEnter(::commitTimeTracking)
                        .onKeyEvent { true }, // https://github.com/JetBrains/compose-multiplatform/issues/4612,
                    value = summary,
                    label = { Text("What have I achieved? (optional)") },
                    onValueChange = { summary = it },
                )
                val timeSinceOpenString = timeSinceOpenInWholeMinutes.toComponents { hours, minutes, _, _ ->
                    "${hours}h ${minutes}m"
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth()
                        .disableGlobalSearchIfFocused()
                        .changeFocusOnTab()
                        .onCtrlOrMetaEnter(::commitTimeTracking)
                        .onKeyEvent { true }, // https://github.com/JetBrains/compose-multiplatform/issues/4612
                    value = customTimeSpent ?: timeSinceOpenString,
                    onValueChange = { customTimeSpent = it },
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
                                val customTimeSpentDuration = customTimeSpent?.let { Duration.parseOrNull(it.trim()) }
                                customTimeSpentDuration?.let {
                                    SimpleTooltip("Continue timer\n(overwrites $timeSinceOpenString)") {
                                        IconButton(
                                            modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                                            onClick = {
                                                timeOfOpen = Clock.System.now() - customTimeSpentDuration
                                                timeSinceOpen = customTimeSpentDuration
                                                customTimeSpent = null
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
                                        onClick = { customTimeSpent = null }
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
                                    onClick = { timeOfOpen = Clock.System.now() }
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    val text = if (pinned) "Unpin issue" else "Pin issue"
                    SimpleTooltip(text) {
                        IconToggleButton(checked = pinned, onCheckedChange = { togglePinned() }) {
                            Icon(
                                imageVector = if (pinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = text
                            )
                        }
                    }
                    if (issue.webUrl != null) SimpleTooltip("Open issue") {
                        IconButton(onClick = { uriHandler.openUri(issue.webUrl) }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open issue")
                        }
                    }
                    SimpleTooltip("Commit time tracking") {
                        FilledTonalIconButton(modifier = Modifier.padding(start = 4.dp), onClick = ::commitTimeTracking) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Commit time tracking")
                        }
                    }
                }
            }
            LaunchedEffect(open, timeOfOpen) {
                if (!open) return@LaunchedEffect
                focusRequester.requestFocus()
                while (true) {
                    if (!isActive) return@LaunchedEffect
                    timeSinceOpen = (Clock.System.now() - timeOfOpen)
                    delay(1.seconds)
                }
            }
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
