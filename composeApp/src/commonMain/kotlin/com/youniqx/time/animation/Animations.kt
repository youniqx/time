package com.youniqx.time.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay

/**
 * Fade in animation with slide from bottom
 */
@Composable
fun FadeIn(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { it / 4 }
                ),
        exit = fadeOut(animationSpec = tween(200)) +
                slideOutVertically(
                    animationSpec = tween(200),
                    targetOffsetY = { -it / 4 }
                ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Scale down slightly when pressed for tactile feedback
 */
fun Modifier.scaleOnPress(
    onClick: () -> Unit = {}
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "scaleOnPress"
    )

    this
        .scale(scale)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

/**
 * Stagger animation delay for list items
 */
@Composable
fun <T> StaggeredListItem(
    item: T,
    index: Int,
    staggerDelay: Long = 50L,
    content: @Composable (T) -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(item) {
        delay(index * staggerDelay)
        visible = true
    }

    FadeIn(visible = visible) {
        content(item)
    }
}

/**
 * Simple delayed visibility animation
 */
@Composable
fun DelayedVisibility(
    delayMillis: Long = 100L,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis)
        visible = true
    }

    FadeIn(visible = visible) {
        content()
    }
}

/**
 * Simple fade in animation for list items (animates immediately on appearance)
 */
@Composable
fun FadeInItem(
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    FadeIn(visible = visible) {
        content()
    }
}
