package com.youniqx.time.settings

import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.youniqx.time.gitlab.models.NamespaceQuery

@Composable
fun SeparateIterationCadenceNamespaceSelection(
    iterationCadence: IterationCadence?,
    searchScopeNamespaceFullPath: String?,
    searchScopeNamespaceName: String?,
    namespaces: NamespaceQuery.Data?,
    onIterationCadenceChange: (IterationCadence?) -> Unit,
) {
    var showSelection by remember(iterationCadence) {
        mutableStateOf(iterationCadence != null && iterationCadence.namespaceFullPath != searchScopeNamespaceFullPath)
    }
    if (showSelection) {
        val namespaceSelectionState = rememberNamespaceSelectionState()
        val namespaceFullPath = iterationCadence?.namespaceFullPath
        val onNamespaceChange = { fullPath: String ->
            if (namespaceFullPath != fullPath) {
                onIterationCadenceChange(IterationCadence(namespaceFullPath = fullPath))
            }
        }
        val namespaceName = namespaceFullPath?.let {
            namespaces?.getNameByFullPath(fullPath = it)
        }
        NamespaceSelection(
            selected = namespaceName?.let {
                { NamespaceItem(fullPath = namespaceFullPath, name = it) }
            },
            namespaces = namespaces,
            onNamespaceChange = onNamespaceChange,
            state = namespaceSelectionState,
            label = { Text("Iteration Cadence Namespace") },
            supportingText = { Text("Exact namespace of the iteration cadence.") },
            additionalOptions = searchScopeNamespaceFullPath?.let {
                {
                    SearchScopeNamespace(
                        namespaceFullPath = searchScopeNamespaceFullPath,
                        namespaceName = searchScopeNamespaceName.orEmpty(),
                        state = namespaceSelectionState,
                        onNamespaceChange = onNamespaceChange
                    )
                }
            },
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