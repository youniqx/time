package com.youniqx.time.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.youniqx.time.components.SimpleTooltip
import com.youniqx.time.gitlab.models.NamespaceQuery

class NamespaceSelectionState {
    var expanded: Boolean by mutableStateOf(false)
    var search: String by mutableStateOf("")
}

@Composable
fun rememberNamespaceSelectionState() = remember { NamespaceSelectionState() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamespaceSelection(
    namespaceId: String?,
    namespaces: NamespaceQuery.Data?,
    onNamespaceChange: (id: String) -> Unit,
    state: NamespaceSelectionState = rememberNamespaceSelectionState(),
    label: @Composable (() -> Unit)? = null,
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
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field for correctness.
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            value = state.search,
            onValueChange = {
                state.search = it
                state.expanded = true
            },
            label = label,
            placeholder = placeholder,
            supportingText = supportingText,
            maxLines = 1,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        DropdownMenu(
            modifier = Modifier
                .exposedDropdownSize(true),
            properties = PopupProperties(focusable = false),
            expanded = state.expanded,
            onDismissRequest = { state.expanded = false },
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
                        Column {
                            Text(
                                it.fullPath,
                                style = MaterialTheme.typography.labelSmall,
                                color = LocalContentColor.current.copy(alpha = 0.7f)
                            )
                            it.name?.let { name -> Text(name) }
                        }
                    },
                    onClick = {
                        state.search = it.name.orEmpty()
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
                        Column {
                            Text(
                                it.fullPath,
                                style = MaterialTheme.typography.labelSmall,
                                color = LocalContentColor.current.copy(alpha = 0.7f)
                            )
                            it.name?.let { name -> Text(name) }
                        }
                    },
                    onClick = {
                        state.search = it.name.orEmpty()
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