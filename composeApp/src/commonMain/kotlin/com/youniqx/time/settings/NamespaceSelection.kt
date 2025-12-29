package com.youniqx.time.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.youniqx.time.components.SimpleTooltip
import com.youniqx.time.gitlab.models.NamespaceQuery
import kotlinx.coroutines.flow.drop

class NamespaceSelectionState(val textFieldState: TextFieldState) {
    var expanded: Boolean by mutableStateOf(false)
    val search: String get() = textFieldState.text.toString()
}

@Composable
fun rememberNamespaceSelectionState(): NamespaceSelectionState {
    val textFieldState = rememberTextFieldState()
    return remember { NamespaceSelectionState(textFieldState) }
}

fun NamespaceQuery.Data.getNameByFullPath(fullPath: String?): String? {
    frecentGroups?.forEach {
        it.groupWithIterationCadences.let { namespace -> if (namespace.fullPath == fullPath) return namespace.name }
    }
    currentUser?.namespace?.let { namespace ->
        if (namespace.fullPath == fullPath) return namespace.name
    }
    groups?.nodes?.forEach {
        it?.groupWithIterationCadences?.let { namespace -> if (namespace.fullPath == fullPath) return namespace.name }
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamespaceSelection(
    selected: @Composable (() -> Unit)? = null,
    namespaces: NamespaceQuery.Data?,
    onNamespaceChange: (id: String) -> Unit,
    state: NamespaceSelectionState = rememberNamespaceSelectionState(),
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = { Text("Type to filter...") },
    supportingText: @Composable (() -> Unit)? = null,
    additionalOptions: @Composable (() -> Unit)? = null,
) {
    ExposedDropdownMenuBox(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp),
        expanded = state.expanded,
        onExpandedChange = { state.expanded = it },
    ) {
        val showSelectedNamespace = selected != null && !state.expanded
        val emptyTextFieldState = rememberTextFieldState(" ")
        LaunchedEffect(state.textFieldState) {
            snapshotFlow { state.textFieldState.selection }.drop(1).collect {
                // we just set expanded again to true to prevent the popup from closing
                // when the user actually only wants to change the selection
                state.expanded = true
            }
        }
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field for correctness.
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            state = if (showSelectedNamespace) emptyTextFieldState else state.textFieldState,
            readOnly = showSelectedNamespace,
            prefix = selected.takeIf { showSelectedNamespace },
            label = label,
            placeholder = placeholder,
            supportingText = supportingText,
            lineLimits = TextFieldLineLimits.SingleLine,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        DropdownMenu(
            modifier = Modifier
                .exposedDropdownSize(true),
            properties = PopupProperties(focusable = false),
            expanded = state.expanded,
            onDismissRequest = {
                println("onDismissRequest")
                state.expanded = false
            },
        ) {
            val filteredFrecentGroups = namespaces?.frecentGroups?.mapNotNull {
                it.groupWithIterationCadences.let { group ->
                    group.takeIf {
                        group.fullPath.contains(state.search, ignoreCase = true) ||
                                group.name?.contains(state.search, ignoreCase = true) == true
                    }
                }
            }
            val filteredGroups = namespaces?.groups?.nodes?.mapNotNull {
                it?.groupWithIterationCadences.let { group ->
                    group.takeIf {
                        group?.fullPath?.contains(state.search, ignoreCase = true) == true ||
                                group?.name?.contains(state.search, ignoreCase = true) == true
                    }
                }
            }
            filteredFrecentGroups?.forEach {
                DropdownMenuItem(
                    text = {
                        NamespaceItem(fullPath = it.fullPath, name = it.name)
                    },
                    onClick = onClick@ {
                        onNamespaceChange(it.fullPath)
                        state.expanded = false
                    },
                    trailingIcon = {
                        SimpleTooltip(text = "frequently visited") {
                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowUp,
                                contentDescription = "Frequently visited"
                            )
                        }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
            if (
                !filteredFrecentGroups.isNullOrEmpty() &&
                (additionalOptions != null || !filteredGroups.isNullOrEmpty())
            ) HorizontalDivider()
            additionalOptions?.let {
                additionalOptions()
                if (!filteredGroups.isNullOrEmpty()) HorizontalDivider()
            }
            filteredGroups?.forEach {
                DropdownMenuItem(
                    text = {
                        NamespaceItem(fullPath = it.fullPath, name = it.name)
                    },
                    onClick = {
                        onNamespaceChange(it.fullPath)
                        state.expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
            if (namespaces?.groups?.pageInfo?.hasNextPage == true) {
                DropdownMenuItem(
                    text = { Text("Load more items") },
                    onClick = {

                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
fun NamespaceItem(fullPath: String, name: String?) {
    Column {
        Text(
            fullPath,
            style = MaterialTheme.typography.labelSmall,
            color = LocalContentColor.current.copy(alpha = 0.7f)
        )
        name?.let { Text(name) }
    }
}