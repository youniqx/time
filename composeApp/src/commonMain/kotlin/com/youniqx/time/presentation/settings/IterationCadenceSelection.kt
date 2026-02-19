package com.youniqx.time.presentation.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.youniqx.time.domain.models.IterationCadence
import com.youniqx.time.domain.models.IterationCadenceMarker
import com.youniqx.time.presentation.errors.ErrorDropdownMenuItem
import com.youniqx.time.presentation.modifier.changeFocusOnTab
import com.youniqx.time.presentation.modifier.disableGlobalSearchIfFocused
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IterationCadenceSelection(
    iterationCadence: IterationCadence?,
    onIterationCadenceChange: (iterationCadence: IterationCadence?) -> Unit,
    iterationCadences: LazyPagingItems<IterationCadenceMarker.Filled>,
    additionalItems: List<IterationCadenceMarker.Filled>,
    iterationCadenceSearcher: (String) -> Unit,
) {
    val state = rememberNamespaceSelectionState()
    LaunchedEffect(state.search) {
        iterationCadenceSearcher(state.search)
    }
    ExposedDropdownMenuBox(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp),
        expanded = state.expanded,
        onExpandedChange = { state.expanded = it },
    ) {
        val allIterationCadences = (iterationCadences.itemSnapshotList + additionalItems).distinct()
        val selected: (@Composable () -> Unit)? = iterationCadence?.let {
            {
                Text(
                    allIterationCadences.firstOrNull { it?.id != iterationCadence.id }?.title
                        ?: iterationCadence.id.orEmpty()
                )
            }
        }
        val showSelected = selected != null && !state.expanded
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
                .changeFocusOnTab { state.expanded = false }
                .disableGlobalSearchIfFocused()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            state = if (showSelected) emptyTextFieldState else state.textFieldState,
            readOnly = showSelected,
            prefix = selected.takeIf { showSelected },
            label = { Text("Iteration Cadence") },
            placeholder = { Text("Type to filter...") },
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
                state.expanded = false
            },
        ) {
            when (iterationCadences.loadState.refresh) {
                is LoadState.Error -> ErrorDropdownMenuItem(onClick = iterationCadences::retry)
                LoadState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                is LoadState.NotLoading -> {
                    allIterationCadences.forEach {
                        when (it) {
                            is IterationCadenceMarker.Filled -> DropdownMenuItem(
                                text = { Text(it.title) },
                                onClick = {
                                    onIterationCadenceChange(
                                        IterationCadence(
                                            namespaceFullPath = it.namespaceFullPath,
                                            id = it.id
                                        )
                                    )
                                    state.expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                            null -> {}
                        }
                    }
                    when (val appendLoadingState = iterationCadences.loadState.append) {
                        is LoadState.Error -> ErrorDropdownMenuItem(onClick = { iterationCadences.retry() })
                        LoadState.Loading -> CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        is LoadState.NotLoading -> if (!appendLoadingState.endOfPaginationReached) DropdownMenuItem(
                            text = { Text("Load more items") },
                            onClick = {
                                try {
                                    iterationCadences[iterationCadences.itemCount]
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