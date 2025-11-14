package com.youniqx.time

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets

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