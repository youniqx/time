package com.youniqx.time.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.youniqx.time.presentation.theme.AppTheme

@Composable
fun Section(
    title: @Composable () -> Unit,
    open: Boolean,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.titleSmall)
    CompositionLocalProvider(
        LocalTextStyle provides mergedStyle,
    ) {
        Row(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (open) OpenSectionIcon() else ClosedSectionIcon()
            title()
            Badge(
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) { Text(count.toString()) }
        }
    }
}

@Composable
fun OpenSectionIcon() = Icon(Icons.Default.ArrowDropDown, contentDescription = "Open section")

@Composable
fun ClosedSectionIcon() = Icon(Icons.AutoMirrored.Default.ArrowRight, contentDescription = "Closed section")

@Preview
@Composable
fun OpenSectionPreview() {
    AppTheme {
        Section(
            title = { Text("Doing") },
            open = true,
            count = 63,
        )
    }
}

@Preview
@Composable
fun ClosedSectionPreview() {
    AppTheme {
        Section(
            title = { Text("Doing") },
            open = false,
            count = 6,
        )
    }
}
