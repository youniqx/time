package com.youniqx.time.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.youniqx.time.theme.LocalSpacing
import com.youniqx.time.theme.TimerActiveColor
import kotlin.math.roundToInt

@Composable
fun SwipeableIssueCard(
    isPinned: Boolean,
    isTracking: Boolean,
    onStartTracking: () -> Unit,
    onTogglePin: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    val hapticFeedback = LocalHapticFeedback.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 150f

    val draggableState = rememberDraggableState { delta ->
        // Limit swipe distance and add resistance
        val newOffset = offsetX + delta * 0.5f
        offsetX = newOffset.coerceIn(-300f, 300f)
    }

    // Background color based on swipe direction
    val backgroundColor by animateColorAsState(
        targetValue = when {
            offsetX > 50f -> TimerActiveColor.copy(alpha = 0.2f)
            offsetX < -50f -> MaterialTheme.colorScheme.primaryContainer
            else -> Color.Transparent
        },
        animationSpec = tween(100),
        label = "swipeBackgroundColor"
    )

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Background with action indicators
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(spacing.cardRadius))
                .background(backgroundColor)
                .padding(horizontal = 24.dp),
            contentAlignment = when {
                offsetX > 0 -> Alignment.CenterStart
                offsetX < 0 -> Alignment.CenterEnd
                else -> Alignment.Center
            }
        ) {
            when {
                offsetX > 50f && !isTracking -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start tracking",
                            modifier = Modifier.size(24.dp),
                            tint = TimerActiveColor
                        )
                        Text(
                            text = "Start",
                            style = MaterialTheme.typography.labelLarge,
                            color = TimerActiveColor
                        )
                    }
                }
                offsetX < -50f -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Text(
                            text = if (isPinned) "Unpin" else "Pin",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = if (isPinned) "Unpin" else "Pin",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Foreground content
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        when {
                            offsetX > swipeThreshold && !isTracking -> {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onStartTracking()
                            }
                            offsetX < -swipeThreshold -> {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTogglePin()
                            }
                        }
                        offsetX = 0f
                    }
                )
        ) {
            content()
        }
    }
}
