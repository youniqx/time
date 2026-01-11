package com.youniqx.time.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

@Composable
fun TimeBadge(
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
fun rememberTimeBadgePlaceholder(time: String): Placeholder {
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.labelSmall
    val result = remember(time, style) {
        textMeasurer.measure(
            text = time,
            style = style.copy(fontFamily = FontFamily.Monospace),
        )
    }
    return with(LocalDensity.current) {
        Placeholder(
            width = (result.size.width + 12.dp.toPx()).toSp(),
            height = (result.size.height + 4.dp.toPx()).toSp(),
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        )
    }
}
