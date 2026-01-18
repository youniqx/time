package com.youniqx.time.presentation.workitems

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.theme.LocalSpacing
import kotlin.math.roundToInt

@Composable
fun SwipeableWorkItemCard(
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

    // Hover state for desktop action buttons
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val draggableState = rememberDraggableState { delta ->
        // Limit swipe distance and add resistance
        val newOffset = offsetX + delta * 0.5f
        offsetX = newOffset.coerceIn(-300f, 300f)
    }

    // Background color based on swipe direction
    val backgroundColor by animateColorAsState(
        targetValue = when {
            offsetX > 50f -> MaterialTheme.colorScheme.tertiaryContainer
            offsetX < -50f -> MaterialTheme.colorScheme.primaryContainer
            else -> Color.Transparent
        },
        animationSpec = tween(100),
        label = "swipeBackgroundColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
    ) {
        // Background with action indicators (for swipe)
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
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "Start",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
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
                    enabled = !isTracking,
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

        // Hover action buttons for desktop
        AnimatedVisibility(
            visible = !isTracking && isHovered && offsetX == 0f,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150)),
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
        ) {
            if (isTracking) return@AnimatedVisibility
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Start tracking button
                SimpleTooltip("Start tracking") {
                    IconButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStartTracking()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start tracking",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                // Pin/Unpin button
                SimpleTooltip(if (isPinned) "Unpin" else "Pin") {
                    IconButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onTogglePin()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (isPinned) "Unpin" else "Pin",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
