package com.youniqx.time

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

actual val WindowInsets.Companion.additionalInsets: WindowInsets
    // + custom inset for the transparent macos system bar
    @Composable get() = if (isMacOs) WindowInsets(top = 28.dp) else WindowInsets(0)
