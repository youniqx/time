package com.youniqx.time.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.youniqx.time.presentation.SimpleTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScopeNamespace(
    namespaceFullPath: String,
    namespaceName: String,
    state: NamespaceSelectionState,
    onNamespaceChange: (String) -> Unit
) {
    DropdownMenuItem(
        text = {
            Column {
                NamespaceItem(fullPath = namespaceFullPath, name = namespaceName)
            }
        },
        onClick = {
            onNamespaceChange(namespaceFullPath)
            state.expanded = false
        },
        trailingIcon = {
            val text = "Set to search scope namespace"
            SimpleTooltip(text = text) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = text
                )
            }
        },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
    )
}