package com.youniqx.time.presentation.errors

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ErrorDropdownMenuItem(onClick: () -> Unit) {
    DropdownMenuItem(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.errorContainer),
        text = { Text("An error occurred", color = MaterialTheme.colorScheme.onErrorContainer) },
        onClick = onClick,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
            )
        },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
    )
}
