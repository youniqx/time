package com.youniqx.time.presentation.windowsize

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.scene.DialogSceneStrategy
import com.youniqx.time.presentation.LocalResultStore
import com.youniqx.time.presentation.navigation.LocalNavigator
import com.youniqx.time.presentation.navigation.NavScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
class WindowResizerNavScope {
    @Provides
    @IntoSet
    fun provideNavScope(): NavScope =
        {
            entry<WindowResizerRoute>(
                metadata = DialogSceneStrategy.dialog(
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = false, scrimColor = Transparent)
                ),
            ) { route ->
                val navigator = LocalNavigator.current
                val resultStore = LocalResultStore.current
                Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTapGestures(onTap = { navigator.removeLast(route = route) })
                }) {
                    WindowResizeShortcuts()
                }
            }
        }
}

@Composable
@Preview
fun WindowResizeShortcutsP() {
    WindowResizeShortcuts()
}
