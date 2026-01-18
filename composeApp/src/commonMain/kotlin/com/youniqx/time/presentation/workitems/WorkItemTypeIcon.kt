package com.youniqx.time.presentation.workitems

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.youniqx.time.presentation.SimpleTooltip

@Composable
fun WorkItemTypeIcon(type: String, text: String = type) {
    SimpleTooltip(text) {
        Icon(
            imageVector = when (type) {
                "Task" -> Icons.Default.Task
                "Epic" -> Icons.Default.Style
                else -> Icons.Default.Sell
            },
            contentDescription = text,
            modifier = Modifier.size(16.dp)
        )
    }
}