package com.youniqx.time.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.youniqx.time.additionalTimerSupport
import com.youniqx.time.domain.models.IterationCadence
import com.youniqx.time.domain.models.IterationCadenceMarker
import com.youniqx.time.domain.models.Namespace
import com.youniqx.time.domain.models.NamespaceEntry
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.SelectedNamespaces
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import com.youniqx.time.presentation.Label
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.presentation.invoke
import com.youniqx.time.presentation.modifier.disableGlobalSearchIfFocused
import com.youniqx.time.presentation.theme.AppTheme
import com.youniqx.time.systemBarsForVisualComponents
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable
import kotlin.let

@Serializable
object SettingsRoute: NavKey

@Composable
fun Settings(
    viewModel: SettingsViewModel = metroViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        settings = uiState.settings,
        updater = viewModel,
        namespaceSearcher = viewModel::searchNamespace,
        onBack = onBack,
        namespaces = uiState.namespaces,
        selectedNamespaces = uiState.selectedNamespaces,
        onSearchNamespaceChange = viewModel::saveSearchNamespace,
        iterationCadencesNamespaces = uiState.iterationCadenceNamespaces,
        iterationCadenceNamespaceSearcher = viewModel::searchIterationCadenceNamespace,
        onIterationCadenceNamespaceChange = viewModel::saveIterationCadenceNamespace,
        iterationCadences = uiState.iterationCadences,
        iterationCadenceSearcher = viewModel::searchIterationCadence,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: Settings,
    updater: UpdateSettingsUseCase,
    namespaceSearcher: (String) -> Unit,
    onBack: () -> Unit,
    namespaces: PagingData<NamespaceEntry>,
    selectedNamespaces: SelectedNamespaces,
    onSearchNamespaceChange: (Namespace) -> Unit,
    iterationCadencesNamespaces: PagingData<NamespaceEntry>,
    iterationCadenceNamespaceSearcher: (String) -> Unit,
    onIterationCadenceNamespaceChange: (Namespace) -> Unit,
    iterationCadences: PagingData<IterationCadenceMarker.Filled>,
    iterationCadenceSearcher: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.systemBarsForVisualComponents)
    ) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            windowInsets = WindowInsets.systemBarsForVisualComponents
        )
        val lightInteractionSource = remember { MutableInteractionSource() }
        val lightIsHovered by lightInteractionSource.collectIsHoveredAsState()
        val darkInteractionSource = remember { MutableInteractionSource() }
        val darkIsHovered by darkInteractionSource.collectIsHoveredAsState()
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                // enabled = !lightIsHovered && !darkIsHovered,
                onClickLabel = if (settings.darkTheme) "Enable light theme" else "Enable dark theme",
                role = Role.Switch,
                onClick = updater::toggleDarkTheme
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Theme")
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 0, count = 2
                    ),
                    onClick = { if (settings.darkTheme) updater.toggleDarkTheme() },
                    selected = !settings.darkTheme,
                    interactionSource = lightInteractionSource,
                    icon = {
                        Icon(
                            imageVector = if (lightIsHovered) Icons.Filled.LightMode else Icons.Outlined.LightMode,
                            contentDescription = null
                        )
                    },
                    label = { Text("Light") })
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1, count = 2
                    ),
                    onClick = { if (!settings.darkTheme) updater.toggleDarkTheme() },
                    selected = settings.darkTheme,
                    interactionSource = darkInteractionSource,
                    icon = {
                        Icon(
                            imageVector = if (darkIsHovered) Icons.Filled.DarkMode else Icons.Outlined.DarkMode,
                            contentDescription = null
                        )
                    },
                    label = { Text("Dark") })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                onClickLabel = if (settings.highContrastColors) "Disable high contrast colors" else "Enable high contrast colors",
                role = Role.Switch,
                onClick = updater::toggleHighContrastColors
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Use high contrast colors")
            Switch(checked = settings.highContrastColors, onCheckedChange = { updater.toggleHighContrastColors() })
        }
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                onClickLabel = if (settings.groupSprintInEpics) {
                    "Show individual work items of sprint even if they have parents"
                } else {
                    "Show parent instead of individual work items if available"
                },
                role = Role.Switch,
                onClick = updater::toggleGroupSprintInEpics
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Group sprint in epics")
            Switch(checked = settings.groupSprintInEpics, onCheckedChange = { updater.toggleGroupSprintInEpics() })
        }
        FlowRow (
            modifier = Modifier.fillMaxWidth().clickable(
                onClickLabel = if (settings.showLabelsByDefault) "Hide labels" else "Show labels",
                role = Role.Switch,
                onClick = updater::toggleShowLabelsByDefault
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            Text("Labels")
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 0, count = 3
                    ),
                    onClick = { if(settings.showLabelsByDefault) updater.toggleShowLabelsByDefault() },
                    selected = !settings.showLabelsByDefault,
                    label = { Text("None", style = MaterialTheme.typography.labelSmall) }
                )
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1, count = 3
                    ),
                    onClick = {
                        if (!settings.showLabelsByDefault) updater.toggleShowLabelsByDefault()
                        if (settings.useLabelColors) updater.toggleUseLabelColors()
                    },
                    selected = settings.showLabelsByDefault && !settings.useLabelColors,
                    label = { Label(__typename = "", id = "", color = "#a4c639", title = "Simple")(useColors = false) }
                )
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 2, count = 3
                    ),
                    onClick = {
                        if (!settings.showLabelsByDefault) updater.toggleShowLabelsByDefault()
                        if (!settings.useLabelColors) updater.toggleUseLabelColors()
                    },
                    selected = settings.showLabelsByDefault && settings.useLabelColors,
                    label = { Label(__typename = "", id = "", color = "#a4c639", title = "Color")(useColors = true) }
                )
            }
        }
        if (additionalTimerSupport.isSupported) Row(
            modifier = Modifier.fillMaxWidth().clickable(
                onClickLabel = if (settings.showMenuBarTimer) "Hide timer in menu bar" else "Show timer in menu bar",
                role = Role.Switch,
                onClick = updater::toggleShowMenuBarTimer
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(additionalTimerSupport.settingsText.orEmpty())
            Switch(checked = settings.showMenuBarTimer, onCheckedChange = { updater.toggleShowMenuBarTimer() })
        }
        InstanceUrlInput(
            modifier = Modifier
                .disableGlobalSearchIfFocused()
                .padding(vertical = 8.dp)
                .padding(horizontal = 12.dp),
            instanceUrl = settings.instanceUrl,
            onInstanceUrlChange = updater::setInstanceUrl
        )
        val uriHandler = LocalUriHandler.current
        TokenInput(
            modifier = Modifier
                .disableGlobalSearchIfFocused()
                .padding(vertical = 8.dp)
                .padding(horizontal = 12.dp),
            token = settings.token,
            onTokenChange = updater::setToken,
            trailingIcon = {
                SimpleTooltip("Create new GitLab token" + if (settings.instanceUrl.isNullOrEmpty()) "\nPlease enter Instance Url first." else "") {
                    IconButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                        enabled = !settings.instanceUrl.isNullOrEmpty(),
                        onClick = {
                            settings.instanceUrl?.let {
                                val tokenUrl = createTokenUrl(fromInstanceUrl = settings.instanceUrl)
                                uriHandler.openUri(tokenUrl.toString())
                            }
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Create new GitLab token"
                        )
                    }
                }
            }
        )
        val namespaceSelectionState = rememberNamespaceSelectionState()
        LaunchedEffect(namespaceSelectionState.search) {
            namespaceSearcher(namespaceSelectionState.search)
        }
        val namespaceLazyPagingItems = namespaces.collectAsLazyPagingItems()
        val iterationCadencesNamespaceLazyPagingItems = iterationCadencesNamespaces.collectAsLazyPagingItems()
        NamespaceSelection(
            selected = selectedNamespaces.search?.let {
                { NamespaceItem(fullPath = it.fullPath, name = it.name) }
            },
            namespaces = namespaceLazyPagingItems,
            onNamespaceChange = onSearchNamespaceChange,
            state = namespaceSelectionState,
            label = { Text("Namespace") },
            supportingText = { Text("Search scope (decedent namespaces included).") },
        )
        val hasAllResults = namespaceLazyPagingItems.hasAllResults()
        if (namespaceSelectionState.search.isEmpty() && !hasAllResults) {
            SeparateIterationCadenceNamespaceSelection(
                selectedNamespaces = selectedNamespaces,
                namespaces = iterationCadencesNamespaceLazyPagingItems,
                namespaceSearcher = iterationCadenceNamespaceSearcher,
                onNamespaceChange = onIterationCadenceNamespaceChange,
            )
        }
        IterationCadenceSelection(
            iterationCadence = settings.iterationCadence,
            iterationCadences = iterationCadences.collectAsLazyPagingItems(),
            additionalItems = namespaceLazyPagingItems.mapToIterationCadences() +
                    iterationCadencesNamespaceLazyPagingItems.mapToIterationCadences(),
            iterationCadenceSearcher = iterationCadenceSearcher,
            onIterationCadenceChange = updater::setIterationCadence,
        )
    }
}

@Composable
private fun LazyPagingItems<*>.hasAllResults() = loadState.run {
    prepend is LoadState.NotLoading &&
            prepend.endOfPaginationReached &&
            append is LoadState.NotLoading &&
            append.endOfPaginationReached
}

@Composable
private fun LazyPagingItems<NamespaceEntry>.mapToIterationCadences() =
    takeIf { it.hasAllResults() }?.itemSnapshotList?.flatMap {
        (it as? Namespace)?.iterationCadences?.filterIsInstance<IterationCadenceMarker.Filled>()
            .orEmpty()
    }.orEmpty()

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    AppTheme {
        SettingsScreen(
            settings = Settings(
                darkTheme = true,
                highContrastColors = false,
                groupSprintInEpics = false,
                showLabelsByDefault = true,
                useLabelColors = true,
                showMenuBarTimer = true,
                instanceUrl = null,
                token = "𐂂",
                namespaceFullPath = null,
                iterationCadence = null,
                pinnedWorkItems = emptyList(),
                openTracking = null,
            ),
            updater = object : UpdateSettingsUseCase {
                override fun toggleDarkTheme() {}
                override fun toggleHighContrastColors() {}
                override fun toggleGroupSprintInEpics() {}
                override fun toggleShowLabelsByDefault() {}
                override fun toggleUseLabelColors() {}
                override fun toggleShowMenuBarTimer() {}
                override fun setInstanceUrl(instanceUrl: String) {}
                override fun setToken(token: String) {}
                override fun setNamespaceFullPath(fullPath: String) {}
                override fun setIterationCadence(iterationCadence: IterationCadence?) {}
                override fun togglePinWorkItem(id: String) {}
                override fun setOpenTracking(openTracking: OpenTracking?) {}
            },
            namespaceSearcher = {},
            onBack = {},
            namespaces = emptyPagingData(),
            selectedNamespaces = SelectedNamespaces(search = null, iterationCadence = null),
            onSearchNamespaceChange = {},
            iterationCadencesNamespaces = emptyPagingData(),
            iterationCadenceNamespaceSearcher = {},
            onIterationCadenceNamespaceChange = {},
            iterationCadences = emptyPagingData(),
            iterationCadenceSearcher = {},
        )
    }
}

@Composable
fun <T : Any> PagingData<T>.collectAsLazyPagingItems(): LazyPagingItems<T> = let {
    remember(it) { flowOf(it) }.collectAsLazyPagingItems()
}
