package com.youniqx.time.presentation.windowsize

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

val LocalWindowResizer = compositionLocalOf<WindowResizer?> { null }

@Serializable
data object WindowResizerRoute : NavKey

class WindowResizer {

}