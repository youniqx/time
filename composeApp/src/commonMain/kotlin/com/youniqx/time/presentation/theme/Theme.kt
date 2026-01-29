package com.youniqx.time.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.dataIfNotFrom
import com.youniqx.time.presentation.theme.rosybrown.theme

data class Theme(
    val lightScheme: ColorScheme,
    val mediumContrastLightColorScheme: ColorScheme,
    val highContrastLightColorScheme: ColorScheme,
    val darkScheme: ColorScheme,
    val mediumContrastDarkColorScheme: ColorScheme,
    val highContrastDarkColorScheme: ColorScheme,
)

val themes = listOf(
    theme,
    com.youniqx.time.presentation.theme.teal.theme,
)

@Composable
fun AppTheme(
    settingsRepository: SettingsRepository,
    theme: Theme = com.youniqx.time.presentation.theme.teal.theme,
    content: @Composable () -> Unit
) {
    val sourceAwareSettings by settingsRepository.settings.collectAsStateWithLifecycle()
    val settings = sourceAwareSettings.data
    val darkTheme = sourceAwareSettings.dataIfNotFrom(excludedSource = DataSource.Default)?.darkTheme
        ?: isSystemInDarkTheme()
    AppTheme(
        darkTheme = darkTheme,
        useHighContrastColors = settings.highContrastColors,
        theme = theme,
        content = content
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useHighContrastColors: Boolean = false,
    theme: Theme = com.youniqx.time.presentation.theme.teal.theme,
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

