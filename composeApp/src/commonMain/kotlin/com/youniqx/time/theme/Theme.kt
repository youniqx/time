package com.youniqx.time.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

data class Theme(
    val lightScheme: ColorScheme,
    val mediumContrastLightColorScheme: ColorScheme,
    val highContrastLightColorScheme: ColorScheme,
    val darkScheme: ColorScheme,
    val mediumContrastDarkColorScheme: ColorScheme,
    val highContrastDarkColorScheme: ColorScheme,
)

val themes = listOf(
    com.youniqx.time.theme.rosybrown.theme,
    com.youniqx.time.theme.teal.theme,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useHighContrastColors: Boolean = false,
    theme: Theme = com.youniqx.time.theme.teal.theme,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        if (useHighContrastColors) theme.highContrastDarkColorScheme else theme.darkScheme
    } else {
        if (useHighContrastColors) theme.highContrastLightColorScheme else theme.lightScheme
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

