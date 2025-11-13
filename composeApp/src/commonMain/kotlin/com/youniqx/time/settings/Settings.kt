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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youniqx.time.SimpleTooltip
import com.youniqx.time.gitlab.models.IterationCadencesQuery
import com.youniqx.time.systemBarsForVisualComponents
import com.youniqx.time.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Settings(
    viewModel: SettingsViewModel,
    iterationCadences: List<IterationCadencesQuery.Node>?,
    disableGlobalSearchIfFocused: Modifier.() -> Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
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
        token = uiState.token,
        onTokenChange = viewModel::setToken,
        iterationCadenceId = uiState.iterationCadenceId,
        iterationCadences = iterationCadences,
        onIterationCadenceChange = viewModel::setIterationCadenceId,
        disableGlobalSearchIfFocused = disableGlobalSearchIfFocused
    )
}

@Composable
fun SettingsScreen(
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
    token: String?,
    onTokenChange: (String) -> Unit,
    iterationCadenceId: String?,
    iterationCadences: List<IterationCadencesQuery.Node>?,
    onIterationCadenceChange: (id: String) -> Unit,
    disableGlobalSearchIfFocused: Modifier.() -> Modifier,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.systemBarsForVisualComponents)
    ) {
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
                SimpleTooltip("Create new GitLab token") {
                    IconButton(onClick = {
                        uriHandler.openUri("https://gitlab.ci.youniqx.com/-/user_settings/personal_access_tokens" +
                                "?name=Time&scopes=api&" +
                                "description=https%3A%2F%2Fgitlab.ci.youniqx.com%2Fyouniqx%2Fmobile_chapter%2Ftime")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Create new GitLab token"
                        )
                    }
                }
            }
        )
        IterationCadenceSelection(
            iterationCadenceId = iterationCadenceId,
            iterationCadences = iterationCadences,
            onIterationCadenceChange = onIterationCadenceChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IterationCadenceSelection(
    iterationCadenceId: String?,
    iterationCadences: List<IterationCadencesQuery.Node>?,
    onIterationCadenceChange: (id: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = iterationCadences?.firstOrNull { it.id == iterationCadenceId }?.title.orEmpty(),
            onValueChange = {},
            readOnly = true,
            maxLines = 1,
            label = { Text("Iteration Cadence") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            iterationCadences?.forEach {
                DropdownMenuItem(
                    text = { Text(it.title) },
                    onClick = {
                        onIterationCadenceChange(it.id.toString())
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            } ?: CircularProgressIndicator()
        }
    }
}

@Preview
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
            token = "𐂂",
            onTokenChange = {},
            iterationCadenceId = null,
            iterationCadences = null,
            onIterationCadenceChange = {},
            disableGlobalSearchIfFocused = { this },
        )
    }
}
