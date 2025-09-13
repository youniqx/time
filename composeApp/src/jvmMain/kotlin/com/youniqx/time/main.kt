package com.youniqx.time

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Desktop
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

val isMacOs = System.getProperty("os.name") == "Mac OS X"

fun main() = application {
    var isOpen by remember { mutableStateOf(true) }
    var isVisible by remember { mutableStateOf(false) }
    val windowState = rememberWindowState(
        position = WindowPosition(0.dp, 0.dp),
        size = DpSize(400.dp, 800.dp)
    )
    val density = LocalDensity.current
    if (isOpen) {
        Tray(
            icon = TrayIcon,
            onClick = { x, y ->
                with(density) {
                    println("$x $y")
                    windowState.position = WindowPosition(x.toDp() * 2 - windowState.size.width / 2, 45.dp)
                }
                isVisible = true
            }
        )
        Window(
            onCloseRequest = {
                isOpen = false
            },
            state = windowState,
            visible = isVisible,
            alwaysOnTop = true,
            icon = TrayIcon, // Todo
        ) {
            if (isMacOs) LaunchedEffect(true) {
                window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                window.rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
            }
            LaunchedEffect(isVisible) {
                if (isVisible) Desktop.getDesktop().requestForeground(true)
            }
            DisposableEffect(window) {
                val listener = object : WindowFocusListener {
                    override fun windowGainedFocus(p0: WindowEvent?) {
                    }

                    override fun windowLostFocus(p0: WindowEvent?) {
                        isVisible = false
                    }

                }
                window.addWindowFocusListener(listener)
                onDispose {
                    window.removeWindowFocusListener(listener)
                }
            }
            App(token = System.getenv("youniqxGitlabPackageRegistryToken"))
        }
    }
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFBC8F8F))
    }
}