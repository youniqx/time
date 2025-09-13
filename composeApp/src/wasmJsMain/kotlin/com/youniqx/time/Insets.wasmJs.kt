package com.youniqx.time

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

actual val WindowInsets.Companion.additionalInsets: WindowInsets
    // + custom inset for the transparent macos system bar
    @Composable get() = WindowInsets(0)
