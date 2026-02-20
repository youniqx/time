package com.youniqx.time.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.youniqx.time.domain.models.Namespace
import com.youniqx.time.domain.models.NamespaceEntry
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.presentation.errors.ErrorDropdownMenuItem
import com.youniqx.time.presentation.modifier.changeFocusOnTab
import com.youniqx.time.presentation.modifier.disableGlobalSearchIfFocused
import kotlinx.coroutines.flow.drop

class NamespaceSelectionState(
    val textFieldState: TextFieldState,
) {
    var expanded: Boolean by mutableStateOf(false)
    val search: String get() = textFieldState.text.toString()
}

@Composable
fun rememberNamespaceSelectionState(): NamespaceSelectionState {
    val textFieldState = rememberTextFieldState()
    return remember { NamespaceSelectionState(textFieldState) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamespaceSelection(
    selected: @Composable (() -> Unit)? = null,
    namespaces: LazyPagingItems<NamespaceEntry>?,
    onNamespaceChange: (namespace: Namespace) -> Unit,
    state: NamespaceSelectionState = rememberNamespaceSelectionState(),
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = { Text("Type to filter...") },
    supportingText: @Composable (() -> Unit)? = null,
) {
    ExposedDropdownMenuBox(
        modifier = Modifier.padding(vertical = 8.dp),
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .changeFocusOnTab { state.expanded = false }
                    .disableGlobalSearchIfFocused()
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
            modifier =
                Modifier
                    .exposedDropdownSize(true),
            properties = PopupProperties(focusable = false),
            expanded = state.expanded,
            onDismissRequest = {
                state.expanded = false
            },
        ) {
            when (namespaces?.loadState?.refresh) {
                null,
                is LoadState.Error,
                -> {
                    ErrorDropdownMenuItem(onClick = { namespaces?.retry() })
                }

                LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                is LoadState.NotLoading -> {
                    namespaces.itemSnapshotList.forEach {
                        when (it) {
                            is NamespaceEntry.SelectedSearch,
                            is NamespaceEntry.FrecentGroup,
                            is NamespaceEntry.Group,
                            is NamespaceEntry.User,
                            -> {
                                DropdownMenuItem(
                                    text = {
                                        NamespaceItem(fullPath = it.fullPath, name = it.name)
                                    },
                                    onClick = {
                                        onNamespaceChange(it)
                                        state.expanded = false
                                    },
                                    trailingIcon = {
                                        when (it) {
                                            is NamespaceEntry.SelectedSearch -> {
                                                val text = "Set to search scope namespace"
                                                SimpleTooltip(text = text) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = text,
                                                    )
                                                }
                                            }

                                            is NamespaceEntry.FrecentGroup -> {
                                                SimpleTooltip(text = "frequently visited") {
                                                    Icon(
                                                        imageVector = Icons.Default.KeyboardDoubleArrowUp,
                                                        contentDescription = "Frequently visited",
                                                    )
                                                }
                                            }

                                            is NamespaceEntry.Group -> {}

                                            is NamespaceEntry.User -> {
                                                Icon(
                                                    imageVector = Icons.Default.AccountCircle,
                                                    contentDescription = "Account namespace",
                                                )
                                            }
                                        }
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }

                            NamespaceEntry.Separator -> {
                                HorizontalDivider()
                            }

                            null -> {}
                        }
                    }
                    when (val appendLoadingState = namespaces.loadState.append) {
                        is LoadState.Error -> {
                            ErrorDropdownMenuItem(onClick = { namespaces.retry() })
                        }

                        LoadState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )
                        }

                        is LoadState.NotLoading -> {
                            if (!appendLoadingState.endOfPaginationReached) {
                                DropdownMenuItem(
                                    text = { Text("Load more items") },
                                    onClick = {
                                        try {
                                            namespaces[namespaces.itemCount]
                                        } catch (_: Exception) {
                                        }
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NamespaceItem(
    fullPath: String,
    name: String?,
) {
    Column {
        Text(
            fullPath,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        name?.let { Text(name) }
    }
}
