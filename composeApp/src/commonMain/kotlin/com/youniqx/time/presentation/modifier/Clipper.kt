package com.youniqx.time.presentation.modifier

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

fun Modifier.clip(
    align: Alignment.Horizontal = Alignment.Start,
    minWidth: Dp,
) = layout { measurable, constraints ->
    val width = minWidth.roundToPx()
    val placeable =
        measurable.measure(
            if (constraints.maxWidth > width) {
                constraints
            } else {
                constraints.copy(minWidth = width, maxWidth = width)
            },
        )

    val wrapperWidth = placeable.width.coerceIn(constraints.minWidth, constraints.maxWidth)
    val wrapperHeight = placeable.height.coerceIn(constraints.minHeight, constraints.maxHeight)
    layout(wrapperWidth, wrapperHeight) {
        placeable.place(align.align(0, wrapperWidth - placeable.width, layoutDirection), 0)
    }
}
