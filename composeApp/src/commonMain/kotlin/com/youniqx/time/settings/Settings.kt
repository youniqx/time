package com.youniqx.time.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youniqx.time.components.SimpleTooltip
import com.youniqx.time.gitlab.models.NamespaceQuery
import com.youniqx.time.systemBarsForVisualComponents
import com.youniqx.time.theme.AppTheme
import com.youniqx.time.theme.LocalSpacing
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Settings(
    viewModel: SettingsViewModel,
    namespaces: NamespaceQuery.Data?,
    disableGlobalSearchIfFocused: Modifier.() -> Modifier,
    onBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        onBack = onBack,
        darkTheme = uiState.darkTheme,
        toggleDarkTheme = viewModel::toggleDarkTheme,
        highContrastColors = uiState.highContrastColors,
        toggleHighContrastColors = viewModel::toggleHighContrastColors,
        groupSprintInEpics = uiState.groupSprintInEpics,
        toggleGroupSprintInEpics = viewModel::toggleGroupSprintInEpics,
        showLabelsByDefault = uiState.showLabelsByDefault,
        toggleShowLabelsByDefault = viewModel::toggleShowLabelsByDefault,
        useLabelColors = uiState.useLabelColors,
        toggleUseLabelColors = viewModel::toggleUseLabelColors,
        showMenuBarTimer = uiState.showMenuBarTimer,
        toggleShowMenuBarTimer = viewModel::toggleShowMenuBarTimer,
        instanceUrl = uiState.instanceUrl,
        onInstanceUrlChange = viewModel::setInstanceUrl,
        token = uiState.token,
        onTokenChange = viewModel::setToken,
        iterationCadence = uiState.iterationCadence,
        namespaces = namespaces,
        namespaceFullPath = uiState.namespaceFullPath,
        onNamespaceChange = viewModel::setNamespaceFullPath,
        onIterationCadenceChange = viewModel::setIterationCadence,
        disableGlobalSearchIfFocused = disableGlobalSearchIfFocused
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: (() -> Unit)? = null,
    darkTheme: Boolean,
    toggleDarkTheme: () -> Unit,
    highContrastColors: Boolean,
    toggleHighContrastColors: () -> Unit,
    groupSprintInEpics: Boolean,
    toggleGroupSprintInEpics: () -> Unit,
    showLabelsByDefault: Boolean,
    toggleShowLabelsByDefault: () -> Unit,
    useLabelColors: Boolean,
    toggleUseLabelColors: () -> Unit,
    showMenuBarTimer: Boolean,
    toggleShowMenuBarTimer: () -> Unit,
    instanceUrl: String?,
    onInstanceUrlChange: (String) -> Unit,
    token: String?,
    onTokenChange: (String) -> Unit,
    iterationCadence: IterationCadence?,
    namespaces: NamespaceQuery.Data?,
    namespaceFullPath: String?,
    onNamespaceChange: (id: String) -> Unit,
    onIterationCadenceChange: (iterationCadence: IterationCadence?) -> Unit,
    disableGlobalSearchIfFocused: Modifier.() -> Modifier,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.systemBarsForVisualComponents)
    ) {
        // Header with back button
        if (onBack != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        val lightInteractionSource = remember { MutableInteractionSource() }
        val lightIsHovered by lightInteractionSource.collectIsHoveredAsState()
        val darkInteractionSource = remember { MutableInteractionSource() }
        val darkIsHovered by darkInteractionSource.collectIsHoveredAsState()
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                // enabled = !lightIsHovered && !darkIsHovered,
                onClickLabel = if (darkTheme) "Enable light theme" else "Enable dark theme",
                role = Role.Switch,
                onClick = toggleDarkTheme
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
                    onClick = { if (darkTheme) toggleDarkTheme() },
                    selected = !darkTheme,
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
                    onClick = { if (!darkTheme) toggleDarkTheme() },
                    selected = darkTheme,
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
                onClickLabel = if (highContrastColors) "Disable high contrast colors" else "Enable high contrast colors",
                role = Role.Switch,
                onClick = toggleHighContrastColors
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Use high contrast colors")
            Switch(checked = highContrastColors, onCheckedChange = { toggleHighContrastColors() })
        }
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                onClickLabel = if (groupSprintInEpics) {
                    "Show individual issues of sprint even if they have parent epics"
                } else {
                    "Show parent epics instead of individual issues if available"
                },
                role = Role.Switch,
                onClick = toggleGroupSprintInEpics
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Group sprint in epics")
            Switch(checked = groupSprintInEpics, onCheckedChange = { toggleGroupSprintInEpics() })
        }
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                onClickLabel = if (showLabelsByDefault) "Hide labels by default" else "Show labels by default",
                role = Role.Switch,
                onClick = toggleShowLabelsByDefault
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show labels by default")
            Switch(checked = showLabelsByDefault, onCheckedChange = { toggleShowLabelsByDefault() })
        }
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                onClickLabel = if (useLabelColors) "Don't color labels" else "Use label colors",
                role = Role.Switch,
                onClick = toggleUseLabelColors
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Use label colors")
            Switch(checked = useLabelColors, onCheckedChange = { toggleUseLabelColors() })
        }
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                onClickLabel = if (showMenuBarTimer) "Hide timer in menu bar" else "Show timer in menu bar",
                role = Role.Switch,
                onClick = toggleShowMenuBarTimer
            ).padding(horizontal = 12.dp).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show timer in menu bar")
            Switch(checked = showMenuBarTimer, onCheckedChange = { toggleShowMenuBarTimer() })
        }
        val parsedInstanceUrl = instanceUrl?.let { Url(instanceUrl) }
        OutlinedTextField(
            value = instanceUrl.orEmpty(),
            onValueChange = onInstanceUrlChange,
            modifier = Modifier
                .fillMaxWidth()
                .disableGlobalSearchIfFocused()
                .padding(vertical = 8.dp)
                .padding(horizontal = 12.dp),
            isError = !instanceUrl.isNullOrEmpty() && parsedInstanceUrl == null,
            label = { Text("GitLab Instance Url") },
            placeholder = { Text("https://gitlab.com") },
            supportingText = { Text("Requires GitLab version 18.6 or higher.") },
        )
        val uriHandler = LocalUriHandler.current
        OutlinedTextField(
            value = token.orEmpty(),
            onValueChange = onTokenChange,
            modifier = Modifier
                .fillMaxWidth()
                .disableGlobalSearchIfFocused()
                .padding(vertical = 8.dp)
                .padding(horizontal = 12.dp),
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("GitLab Token") },
            supportingText = { Text("Needs API read & write access.") },
            trailingIcon = {
                SimpleTooltip("Create new GitLab token" + if (instanceUrl.isNullOrEmpty()) "\nPlease enter Instance Url first." else "") {
                    IconButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                        enabled = !instanceUrl.isNullOrEmpty(),
                        onClick = {
                            parsedInstanceUrl?.let {
                                val tokenUrl = buildUrl {
                                    takeFrom(parsedInstanceUrl)
                                    appendPathSegments("-", "user_settings", "personal_access_tokens")
                                    parameters.append("name", "Time")
                                    parameters.append("scopes", "api")
                                    parameters.append(
                                        "description",
                                        "Token used by the Time app to help you track time on GitLab."
                                    )
                                }
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
        val filteredUserNamespace = namespaces?.currentUser?.namespace?.takeIf {
            it.fullPath.contains(namespaceSelectionState.search, ignoreCase = true) ||
                    it.name.contains(namespaceSelectionState.search, ignoreCase = true)
        }
        val namespaceName = namespaceFullPath?.let {
            namespaces?.getNameByFullPath(fullPath = namespaceFullPath)
        }
        NamespaceSelection(
            selected = namespaceName?.let {
                { NamespaceItem(fullPath = namespaceFullPath, name = it) }
            },
            namespaces = namespaces,
            onNamespaceChange = onNamespaceChange,
            state = namespaceSelectionState,
            label = { Text("Namespace") },
            supportingText = { Text("Search scope (decedent namespaces included).") },
            additionalOptions = filteredUserNamespace?.let {
                {
                    UserNamespace(
                        namespace = it,
                        state = namespaceSelectionState,
                        onNamespaceChange = onNamespaceChange
                    )
                }
            },
        )
        if (namespaceSelectionState.search.isEmpty() && !(namespaces?.groups?.pageInfo?.containsAllResults ?: false)) {
            SeparateIterationCadenceNamespaceSelection(
                searchScopeNamespaceFullPath = namespaceFullPath,
                searchScopeNamespaceName = namespaceName,
                namespaces = namespaces
            )
        }
        IterationCadenceSelection(
            iterationCadence = iterationCadence,
            namespaces = namespaces,
            onIterationCadenceChange = onIterationCadenceChange
        )
    }
}

val NamespaceQuery.PageInfo.containsAllResults get() = !hasPreviousPage && !hasNextPage

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    AppTheme {
        SettingsScreen(
            darkTheme = true,
            toggleDarkTheme = {},
            highContrastColors = false,
            toggleHighContrastColors = {},
            groupSprintInEpics = false,
            toggleGroupSprintInEpics = {},
            showLabelsByDefault = true,
            toggleShowLabelsByDefault = {},
            useLabelColors = true,
            toggleUseLabelColors = {},
            showMenuBarTimer = true,
            toggleShowMenuBarTimer = {},
            instanceUrl = null,
            onInstanceUrlChange = {},
            token = "𐂂",
            onTokenChange = {},
            namespaces = null,
            namespaceFullPath = null,
            onNamespaceChange = {},
            iterationCadence = null,
            onIterationCadenceChange = {},
            disableGlobalSearchIfFocused = { this },
        )
    }
}
