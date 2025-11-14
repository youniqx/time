package com.youniqx.time

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
//            val insets = ViewCompat.getRootWindowInsets(LocalView.current)
//            val imeVisible = insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
//            val hasPhysicalKeyboard = LocalConfiguration.current.keyboard != Configuration.KEYBOARD_NOKEYS
//            println("imeVisible: $imeVisible")
//            println("hasPhysicalKeyboard: $hasPhysicalKeyboard")
//            println("ime: ${WindowInsets.isImeVisible}")
            hasPhysicalOrShowingKeyboard()
            Box(Modifier.imePadding()) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}