package com.youniqx.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets

@Composable
fun Label(label: BareWorkItemWidgets.Node, useColors: Boolean) {
    val defaultBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val defaultText = MaterialTheme.colorScheme.onSurfaceVariant

    val (bgColor, textColor) = if (useColors) {
        remember(label.color) {
            val color = try {
                Color(label.color.toColorInt()).copy(alpha = 0.85f)
            } catch (_: Exception) {
                defaultBg
            }
            color to color.contrastingTextColor()
        }
    } else {
        defaultBg to defaultText
    }

    val displayText = label.title.replace("::", " | ")

    Box(
        modifier = Modifier
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            color = textColor,
            fontSize = 11.sp,
            lineHeight = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
