package com.youniqx.time.presentation.windowsize

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.withTimeoutOrNull
import java.awt.MouseInfo
import java.awt.Point
import javax.swing.SwingUtilities
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Composable
fun FrameWindowScope.WindowResizer(windowState: WindowState) {
    var location by remember { mutableStateOf<Point?>(null) }
    var isHovered by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        snapshotFlow { windowState.size }
            .debounce(16.milliseconds)
            .drop(1)
            .transformLatest {
                withTimeoutOrNull(500) {
                    while (!isHovered && windowState.placement == WindowPlacement.Floating) {
                        val loc = MouseInfo.getPointerInfo().location
                        SwingUtilities.convertPointFromScreen(loc, window)
                        emit(loc)
                        delay(16.milliseconds)
                    }
                    if (windowState.placement != WindowPlacement.Floating) {
                        isHovered = false
                        location = null
                    }
                }
            }.onEach {
                location = it
            }.launchIn(this)
    }
    var undoState by remember { mutableStateOf(windowState.toUndoState()) }
    LaunchedEffect(true) {
        val threshold = 30.dp.value
        combine(
            snapshotFlow { windowState.size },
            snapshotFlow { windowState.position },
            snapshotFlow { windowState.placement },
        ) { size, position, placement ->
            UndoState(size = size, position = position, placement = placement)
        }.debounce(1.seconds)
            .runningFold(windowState.toUndoState()) { prev, new ->
                val diff = prev.size - new.size
                val xDiff = prev.position.x - new.position.x
                val yDiff = prev.position.y - new.position.y
                if (
                    abs(diff.width.value) < threshold &&
                    abs(diff.height.value) < threshold &&
                    abs(xDiff.value) < threshold &&
                    abs(yDiff.value) < threshold &&
                    prev.placement == new.placement
                ) {
                    prev
                } else {
                    undoState = prev
                    new
                }
            }.launchIn(this)
    }
    LaunchedEffect(location, isHovered) {
        if (location != null && !isHovered) {
            delay(1.5.seconds)
            location = null
        }
    }
    WindowResizeShortcuts(
        modifier =
            Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        val padding = 16.dp.roundToPx()
                        location?.let {
                            val maxX =
                                (constraints.maxWidth - placeable.width - padding)
                                    .coerceAtLeast(padding)
                            val x =
                                it.x.dp
                                    .roundToPx()
                                    .minus(placeable.width / 2)
                                    .coerceIn(range = padding..maxX)
                            val maxY =
                                (constraints.maxHeight - placeable.height - padding)
                                    .coerceAtLeast(padding)
                            val y =
                                it.y.dp
                                    .roundToPx()
                                    .minus(placeable.height / 2)
                                    .coerceIn(range = padding..maxY)
                            placeable.place(x = x, y = y)
                        }
                    }
                }.pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Enter -> {
                                    isHovered = true
                                }

                                PointerEventType.Exit -> {
                                    isHovered = false
                                }
                            }
                        }
                    }
                },
        onLandscape = {
            windowState.size = windowState.size.run { copy(width = height * 2).forceMinWidth() }
        },
        onUndo = {
            windowState.placement = undoState.placement
            windowState.size = undoState.size
            windowState.position = undoState.position
        },
        onPortrait = {
            windowState.size = windowState.size.run { copy(width = height / 2).forceMinWidth() }
        },
    )
}

private fun DpSize.forceMinWidth() = copy(width = width.coerceAtLeast(400.dp))

private data class UndoState(
    val size: DpSize,
    val position: WindowPosition,
    val placement: WindowPlacement,
)

private fun WindowState.toUndoState() = UndoState(size = size, position = position, placement = placement)
