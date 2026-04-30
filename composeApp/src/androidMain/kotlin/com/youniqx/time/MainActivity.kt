package com.youniqx.time

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.dataIfNotFrom
import com.youniqx.time.presentation.App
import com.youniqx.time.presentation.navigation.NavScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey
class MainActivity(
    private val metroVmf: MetroViewModelFactory,
    private val navScopes: Set<NavScope>,
    private val settingsRepository: SettingsRepository,
) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(LocalMetroViewModelFactory provides metroVmf) {
                val sourceAwareSettings by settingsRepository.settings.collectAsStateWithLifecycle()
                val isDarkTheme =
                    sourceAwareSettings.dataIfNotFrom(excludedSource = DataSource.Default)?.darkTheme
                        ?: isSystemInDarkTheme()

                // Update status bar icons based on theme
                DisposableEffect(isDarkTheme) {
                    enableEdgeToEdge(
                        statusBarStyle =
                            if (isDarkTheme) {
                                SystemBarStyle.dark(Color.Transparent.toArgb())
                            } else {
                                SystemBarStyle.light(Color.Transparent.toArgb(), Color.Black.toArgb())
                            },
                        navigationBarStyle =
                            if (isDarkTheme) {
                                SystemBarStyle.dark(Color.Transparent.toArgb())
                            } else {
                                SystemBarStyle.light(Color.Transparent.toArgb(), Color.Black.toArgb())
                            },
                    )
                    onDispose {}
                }

                App(navScopes = navScopes, settingsRepository = settingsRepository)
            }
        }
    }
}
