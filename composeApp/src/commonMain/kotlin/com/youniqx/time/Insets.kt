package com.youniqx.time

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable

expect val WindowInsets.Companion.additionalInsets: WindowInsets

val WindowInsets.Companion.systemBarsForVisualComponents: WindowInsets
    @Composable get() = systemBars.union(displayCutout).union(additionalInsets)
