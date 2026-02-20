package com.youniqx.time

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.youniqx.time.di.IosAppGraph
import com.youniqx.time.presentation.App
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import platform.UIKit.UIViewController

@Suppress("ktlint:standard:function-naming")
fun MainViewController(): UIViewController {
    val graph = createGraph<IosAppGraph>()
    return ComposeUIViewController {
        CompositionLocalProvider(LocalMetroViewModelFactory provides graph.metroViewModelFactory) {
            App(navScopes = graph.navScopes, settingsRepository = graph.settingsRepository)
        }
    }
}
