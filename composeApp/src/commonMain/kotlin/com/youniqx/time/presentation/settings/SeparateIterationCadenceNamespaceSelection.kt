package com.youniqx.time.presentation.settings

import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.youniqx.time.domain.models.IterationCadence
import com.youniqx.time.domain.models.Namespace
import com.youniqx.time.domain.models.NamespaceEntry
import com.youniqx.time.domain.models.SelectedNamespaces

@Composable
fun SeparateIterationCadenceNamespaceSelection(
    iterationCadence: IterationCadence?,
    selectedNamespaces: SelectedNamespaces,
    namespaces: LazyPagingItems<NamespaceEntry>?,
    namespaceSearcher: (String) -> Unit,
    onNamespaceChange: (namespace: Namespace) -> Unit,
) {
    var showSelection by remember(iterationCadence) {
        mutableStateOf(
            iterationCadence != null && iterationCadence.namespaceFullPath != selectedNamespaces.search?.fullPath
        )
    }
    if (showSelection) {
        val namespaceSelectionState = rememberNamespaceSelectionState()
        LaunchedEffect(namespaceSelectionState.search) {
            namespaceSearcher(namespaceSelectionState.search)
        }
        NamespaceSelection(
            selected = selectedNamespaces.iterationCadence?.let {
                { NamespaceItem(fullPath = it.fullPath, name = it.name) }
            },
            namespaces = namespaces,
            onNamespaceChange = onNamespaceChange,
            state = namespaceSelectionState,
            label = { Text("Iteration Cadence Namespace") },
            supportingText = { Text("Exact namespace of the iteration cadence.") },
        )
        return
    }
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        modifier = Modifier
            .padding(
                horizontal = TextFieldDefaults.contentPaddingWithLabel()
                    .calculateStartPadding(LocalLayoutDirection.current)
            )
            .padding(bottom = 8.dp)
            .padding(horizontal = 12.dp),
        text = buildAnnotatedString {
            withLink(
                LinkAnnotation.Clickable(
                    tag = "IterationInDifferentNamespace",
                    styles = TextLinkStyles(
                        style = SpanStyle(color = OutlinedTextFieldDefaults.colors().unfocusedLabelColor),
                        focusedStyle = linkStyle,
                        hoveredStyle = linkStyle,
                        pressedStyle = linkStyle,
                    ),
                    linkInteractionListener = {
                        showSelection = true
                    }
                )
            ) {
                append("Click if iteration cadence is not in this exact namespace.")
            }
        },
        style = MaterialTheme.typography.bodySmall
    )
}