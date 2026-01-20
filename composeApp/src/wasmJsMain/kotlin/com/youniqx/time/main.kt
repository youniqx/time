package com.youniqx.time

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.window.ComposeViewport
import com.youniqx.time.di.WasmAppGraph
import com.youniqx.time.presentation.App
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import time.composeapp.generated.resources.NotoColorEmoji
import time.composeapp.generated.resources.Res

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    val graph = createGraph<WasmAppGraph>()
    ComposeViewport(document.body!!) {
        CompositionLocalProvider(LocalMetroViewModelFactory provides graph.metroViewModelFactory) {
            val focusRequester = remember { FocusRequester() }
            val emojiFont: Font? by preloadFont(Res.font.NotoColorEmoji)
            val fontFamilyResolver = LocalFontFamilyResolver.current

            LaunchedEffect(emojiFont) {
                emojiFont?.let { fontFamilyResolver.preload(it.toFontFamily()) }
            }
            App(
                navScopes = graph.navScopes,
                settingsRepository = graph.settingsRepository,
                focusRequester = focusRequester
            )
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
}