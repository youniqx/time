package com.youniqx.time.presentation.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

/**
 * Applies horizontal padding only if the available width is greater than or equal to [minWidth].
 */
fun Modifier.adaptivePadding(
    minWidth: Dp,
    horizontalPadding: Dp
) = this.layout { measurable, constraints ->
    val paddingPx = horizontalPadding.roundToPx()
    if (constraints.maxWidth >= minWidth.roundToPx()) {
        val contentWidth = constraints.maxWidth - (paddingPx * 2)
        val newConstraints = constraints.takeIf { !it.hasBoundedWidth } ?:
            constraints.copy(maxWidth = contentWidth.coerceAtLeast(constraints.minWidth))
        val placeable = measurable.measure(newConstraints)
        layout(placeable.width + 2 * paddingPx, placeable.height) {
            placeable.placeRelative(x = paddingPx, y = 0)
        }
    } else {
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
}