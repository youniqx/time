package com.youniqx.time

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        val focusRequester = remember { FocusRequester() }
        App(focusRequester = focusRequester)
        DisposableEffect(focusRequester) {
            val handleKeyDown: (Event) -> Unit = { event ->
                with(event as KeyboardEvent) {
                    if (
                        !metaKey &&
                        !altKey &&
                        !ctrlKey &&
                        !shiftKey &&
                        !(charCode.takeIf { it != 0 } ?: which).toChar().isISOControl()
                    ) {
                        focusRequester.requestFocus()
                    }
                }

            }

            window.addEventListener("keydown", handleKeyDown)
            onDispose {
                window.removeEventListener("keydown", handleKeyDown)
            }
        }
    }
}