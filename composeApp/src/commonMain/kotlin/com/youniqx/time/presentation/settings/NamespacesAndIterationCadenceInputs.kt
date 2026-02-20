package com.youniqx.time.presentation.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import com.youniqx.time.domain.models.IterationCadence
import com.youniqx.time.domain.models.IterationCadenceMarker
import com.youniqx.time.domain.models.Namespace
import com.youniqx.time.domain.models.NamespaceEntry
import com.youniqx.time.domain.models.SelectedNamespaces
import com.youniqx.time.presentation.collectAsLazyPagingItems

@Composable
fun NamespacesAndIterationCadenceInputs(
    selectedNamespaces: SelectedNamespaces,
    namespaces: PagingData<NamespaceEntry>,
    namespaceSearcher: (String) -> Unit,
    onSearchNamespaceChange: (Namespace) -> Unit,
    iterationCadencesNamespaces: PagingData<NamespaceEntry>,
    iterationCadenceNamespaceSearcher: (String) -> Unit,
    onIterationCadenceNamespaceChange: (Namespace) -> Unit,
    iterationCadence: IterationCadence?,
    iterationCadences: PagingData<IterationCadenceMarker.Filled>,
    iterationCadenceSearcher: (String) -> Unit,
    onIterationCadenceChange: (iterationCadence: IterationCadence?) -> Unit
) {
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
        iterationCadence = iterationCadence,
        iterationCadences = iterationCadences.collectAsLazyPagingItems(),
        additionalItems = namespaceLazyPagingItems.mapToIterationCadences() +
                iterationCadencesNamespaceLazyPagingItems.mapToIterationCadences(),
        iterationCadenceSearcher = iterationCadenceSearcher,
        onIterationCadenceChange = onIterationCadenceChange,
    )
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