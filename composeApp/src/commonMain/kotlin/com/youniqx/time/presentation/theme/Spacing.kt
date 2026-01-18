package com.youniqx.time.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,

    // Component-specific
    val cardPadding: Dp = 16.dp,
    val cardGap: Dp = 12.dp,
    val sectionGap: Dp = 24.dp,
    val screenPadding: Dp = 20.dp,
    val cardRadius: Dp = 12.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
