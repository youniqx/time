package com.youniqx.time.presentation.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun Modifier.changeFocusOnTab(afterTab: (() -> Unit)? = null): Modifier {
    val focusManager = LocalFocusManager.current
    return onPreviewKeyEvent {
        if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
            val direction = if (it.isShiftPressed) FocusDirection.Previous else FocusDirection.Next
            focusManager.moveFocus(direction)
            afterTab?.invoke()
            true
        } else {
            false
        }
    }
}

fun Modifier.onCtrlOrMetaEnter(block: () -> Unit) =
    onPreviewKeyEvent {
        if (it.key == Key.Enter && (it.isMetaPressed || it.isCtrlPressed) && it.type == KeyEventType.KeyUp) {
            block()
            true
        } else {
            false
        }
    }
