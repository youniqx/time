package com.youniqx.time.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TimeBadge(
    time: String,
    backgroundColor: Color,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
        )
        trailingIcon?.let {
            Spacer(Modifier.width(4.dp))
            trailingIcon()
        }
    }
}

@Composable
fun rememberTimeBadgePlaceholder(time: String, trailingIconSize: Dp? = null): Placeholder {
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.labelSmall
    val result = remember(time, style) {
        textMeasurer.measure(
            text = time,
            style = style.copy(fontFamily = FontFamily.Monospace),
        )
    }
    return with(LocalDensity.current) {
        val trailingIconWidth = trailingIconSize?.let { trailingIconSize + 4.dp } ?: 0.dp
        Placeholder(
            width = (result.size.width + (12.dp + trailingIconWidth).toPx()).toSp(),
            height = (result.size.height
                .coerceAtLeast(trailingIconSize?.roundToPx() ?: 0) + 4.dp.toPx()
                    ).toSp(),
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        )
    }
}
