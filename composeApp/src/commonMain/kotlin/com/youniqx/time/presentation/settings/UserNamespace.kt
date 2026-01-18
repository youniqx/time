package com.youniqx.time.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.youniqx.time.gitlab.models.NamespaceQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNamespace(
    namespace: NamespaceQuery.Namespace,
    state: NamespaceSelectionState,
    onNamespaceChange: (String) -> Unit
) {
    DropdownMenuItem(
        text = {
            Column {
                NamespaceItem(fullPath = namespace.fullPath, name = namespace.name)
            }
        },
        onClick = {
            onNamespaceChange(namespace.fullPath)
            state.expanded = false
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account namespace"
            )
        },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
    )
}