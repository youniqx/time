package com.youniqx.time.timer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.youniqx.time.theme.TimerActiveColor
import com.youniqx.time.theme.TimerIdleColor
import com.youniqx.time.theme.TimerTextStyle
import kotlin.time.Duration

@Composable
fun ProgressRing(
    progress: Float, // 0f to 1f for one hour cycle
    elapsedTime: Duration,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 12.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    // Pulsing animation when running
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val ringColor by animateColorAsState(
        targetValue = if (isRunning) TimerActiveColor else TimerIdleColor,
        animationSpec = tween(300),
        label = "ringColor"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Background ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = ringColor.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        // Progress ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        // Time display
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = elapsedTime.formatTime(),
                style = TimerTextStyle,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isRunning) {
                Text(
                    text = "Tracking",
                    style = MaterialTheme.typography.labelMedium,
                    color = TimerActiveColor
                )
            }
        }
    }
}

/**
 * Format duration as HH:MM:SS
 */
fun Duration.formatTime(): String {
    val totalSeconds = inWholeSeconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

/**
 * Format duration in compact form (e.g., "1h 23m" or "45m" or "30s")
 */
fun Duration.formatCompact(): String {
    val totalSeconds = inWholeSeconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}
