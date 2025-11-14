package com.youniqx.time

import android.content.res.Configuration
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalLayoutApi::class)
@Composable
actual fun hasPhysicalOrShowingKeyboard(): Boolean {
    val imeVisible = WindowInsets.isImeVisible
    val hasPhysicalKeyboard = LocalConfiguration.current.keyboard != Configuration.KEYBOARD_NOKEYS
    println("imeVisible: ${WindowInsets.isImeVisible}")
    println("hasPhysicalKeyboard: $hasPhysicalKeyboard")
    return hasPhysicalKeyboard || imeVisible
}