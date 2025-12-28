package com.youniqx.time.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.youniqx.time.gitlab.models.NamespaceQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNamespace(namespace: NamespaceQuery.Namespace, state: NamespaceSelectionState) {
    DropdownMenuItem(
        text = {
            Column {
                Text(
                    namespace.fullPath,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalContentColor.current.copy(alpha = 0.7f)
                )
                Text(namespace.name)
            }
        },
        onClick = {
            state.search = namespace.name
            state.expanded = false
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Frequently visited"
            )
        },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
    )
}