@file:Suppress("ktlint:standard:filename")
@file:OptIn(FlowPreview::class)

package com.youniqx.time

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.youniqx.time.di.JvmAppGraph
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.refreshKey
import com.youniqx.time.domain.models.toDurationOrNull
import com.youniqx.time.presentation.App
import com.youniqx.time.presentation.windowsize.WindowResizer
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import kotlinx.coroutines.FlowPreview
import java.awt.Desktop
import java.awt.Dimension
import java.awt.Font
import java.awt.RenderingHints
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import java.awt.image.BufferedImage
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

val isMacOs = System.getProperty("os.name") == "Mac OS X"

// Cache dark mode detection (checked once at startup)
val isDarkMenuBar: Boolean by lazy {
    if (isMacOs) {
        try {
            val result =
                Runtime
                    .getRuntime()
                    .exec(arrayOf("defaults", "read", "-g", "AppleInterfaceStyle"))
                    .inputStream
                    .bufferedReader()
                    .readText()
                    .trim()
            result.equals("Dark", ignoreCase = true)
        } catch (_: Exception) {
            false // Default to light mode if detection fails
        }
    } else {
        true // Assume dark on Linux
    }
}

@OptIn(ExperimentalTime::class)
fun main() {
    // Hide from Dock on macOS - must be set before AWT initializes
    if (isMacOs) {
        System.setProperty("apple.awt.UIElement", "true")
    }
    val graph = createGraph<JvmAppGraph>()

    application {
        CompositionLocalProvider(LocalMetroViewModelFactory provides graph.metroViewModelFactory) {
            LaunchedEffect(true) {
                println("--------- WE ARE RUNNING ---------")
            }
            var isVisible by remember { mutableStateOf(true) }
            val settingsRepository = graph.settingsRepository
            val windowState = settingsRepository.windowState
            Tray(settingsRepository, windowState, onClick = { isVisible = !isVisible })
            val focusRequester = remember { FocusRequester() }
            Window(
                onCloseRequest = ::exitApplication,
                onPreviewKeyEvent = {
                    if (
                        !it.isMetaPressed &&
                        !it.isAltPressed &&
                        !it.isCtrlPressed &&
                        !it.isShiftPressed &&
                        it.type == KeyEventType.KeyDown &&
                        !it.utf16CodePoint.toChar().isISOControl()
                    ) {
                        focusRequester.requestFocus()
                    }
                    false
                },
                state = windowState,
                visible = isVisible,
                alwaysOnTop = true,
                icon = TrayIcon, // Todo
            ) {
                if (isMacOs) {
                    LaunchedEffect(true) {
                        window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                        window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                        window.rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
                    }
                }
                if (SystemTray.isSupported()) {
                    // Hide window instead of minimizing (tray app behavior)
                    LaunchedEffect(windowState.isMinimized) {
                        if (windowState.isMinimized) {
                            windowState.isMinimized = false
                            isVisible = false
                        }
                    }
                    LaunchedEffect(isVisible) {
                        if (isVisible) {
                            try {
                                Desktop.getDesktop().requestForeground(true)
                            } catch (_: Exception) {
                            }
                        }
                    }
                    DisposableEffect(window) {
                        val listener =
                            object : WindowFocusListener {
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
                }
                App(
                    navScopes = graph.navScopes,
                    settingsRepository = settingsRepository,
                    focusRequester = focusRequester,
                ) {
                    MaterialTheme.colorScheme.surface.let { color ->
                        LaunchedEffect(color) {
                            window.background =
                                java.awt.Color(color.red, color.green, color.blue, color.alpha)
                        }
                    }
                    WindowResizer(windowState = windowState)
                }
            }
        }
    }
}

private val SettingsRepository.windowState: WindowState
    @Composable
    get() {
        val t: Toolkit = Toolkit.getDefaultToolkit()
        val dimensions: Dimension? = t.screenSize
        val height = dimensions?.height?.dp?.div(1.5f) ?: 800.dp
        val width = height * 2
        val size = DpSize(width, height)
//        val windowSize =
//            runBlocking {
//                settings
//                    .map { it.dataIfNotFrom(DataSource.Default) }
//                    .filterNotNull()
//                    .map { it.windowSize }
//                    .firstOrNull() ?: size
//            }
        return rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(alignment = BiasAlignment(horizontalBias = 0f, verticalBias = -0.25f)),
            size = size,
        )
    }

@Composable
private fun ApplicationScope.Tray(
    settingsRepository: SettingsRepository,
    windowState: WindowState,
    onClick: () -> Unit,
) {
    val density = LocalDensity.current
    val sourceAwareSettings by settingsRepository.settings.collectAsState()
    val settings = sourceAwareSettings.data

    // Create dynamic tray icon
    val trayIcon =
        remember(
            settings.showMenuBarTimer,
            settings.openTracking.refreshKey,
        ) {
            val showTimer = settings.showMenuBarTimer
            val openTracking = settings.openTracking

            if (showTimer && openTracking != null) {
                MenuBarTimerIcon(
                    title = openTracking.workItemTitle,
                    elapsed = openTracking.toDurationOrNull() ?: Duration.ZERO,
                )
            } else {
                TrayIcon
            }
        }

    if (SystemTray.isSupported()) {
        Tray(
            icon = trayIcon,
            onClick = { x, y ->
                with(density) {
                    // Todo: check if we need the * 2 only on macOS
                    windowState.position =
                        WindowPosition(x.toDp() * 2 - windowState.size.width / 2, 45.dp)
                }
                // Restore from minimized state if needed
                if (windowState.isMinimized) {
                    windowState.isMinimized = false
                }
                windowState.placement = WindowPlacement.Floating
                onClick()
            },
        )
    }
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFBC8F8F))
    }
}

/**
 * Creates a dynamic menu bar icon with timer text.
 * Format: "Label... HH:MM" or just "HH:MM" if no label
 */
@OptIn(ExperimentalTime::class)
class MenuBarTimerIcon(
    private val title: String?,
    private val elapsed: Duration,
) : Painter() {
    // Menu bar on macOS is 22pt height, we need proper width for text
    private val iconHeight = 22f
    private val maxLabelWidth = 120f // Max pixels for label
    private val timeWidth = 50f // Width for time display (HH:MM)
    private val spacing = 6f
    private val iconWidth = 18f

    override val intrinsicSize: Size
        get() {
            // Calculate total width based on content
            val hasLabel = !title.isNullOrBlank()
            val totalWidth =
                if (hasLabel) {
                    iconWidth + spacing + maxLabelWidth + spacing + timeWidth
                } else {
                    iconWidth + spacing + timeWidth
                }
            return Size(totalWidth, iconHeight)
        }

    override fun DrawScope.onDraw() {
        // This won't be used directly - we override toAwtImage behavior via the Tray composable
        drawOval(Color(0xFFBC8F8F))
    }

    fun createAwtImage(scaleFactor: Int = if (isMacOs) 2 else 1): BufferedImage {
        val hasLabel = !title.isNullOrBlank()

        // Calculate dimensions - macOS menu bar is 22pt
        val height = (22 * scaleFactor)
        val fontSize = (13 * scaleFactor)
        val iconSize = (16 * scaleFactor)
        val padding = (6 * scaleFactor)

        // Create a temporary image to measure text
        val tempImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val tempG2d = tempImage.createGraphics()
        // Use system UI font - SF Pro on macOS
        val font = Font(Font.SANS_SERIF, Font.PLAIN, fontSize)
        tempG2d.font = font
        val fontMetrics = tempG2d.fontMetrics

        // Format time as HH:MM
        val timeString = formatElapsedTime(elapsed)
        val timeTextWidth = fontMetrics.stringWidth(timeString)

        // Calculate label width with truncation (120px max for label)
        val maxLabelPx = (120 * scaleFactor)
        val labelText =
            if (hasLabel) {
                truncateLabel(title!!, fontMetrics, maxLabelPx)
            } else {
                ""
            }
        val labelTextWidth = if (hasLabel) fontMetrics.stringWidth(labelText) else 0

        tempG2d.dispose()

        // Calculate total width with proper padding
        val spacingPx = (spacing * scaleFactor).toInt()
        val totalWidth =
            if (hasLabel) {
                padding + iconSize + spacingPx + labelTextWidth + spacingPx + timeTextWidth + padding
            } else {
                padding + iconSize + spacingPx + timeTextWidth + padding
            }

        // Create the actual image
        val image = BufferedImage(totalWidth, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

        // Draw rounded background
        val bgColor = if (isDarkMenuBar) java.awt.Color(60, 60, 60, 200) else java.awt.Color(240, 240, 240, 200)
        g2d.color = bgColor
        g2d.fillRoundRect(0, 0, totalWidth, height, height / 2, height / 2)

        // Draw icon (simple circle - same as TrayIcon)
        val iconY = (height - iconSize) / 2
        g2d.color = java.awt.Color(0xBC, 0x8F, 0x8F) // Same color as TrayIcon
        g2d.fillOval(padding, iconY, iconSize, iconSize)

        // Set text color based on menu bar appearance
        g2d.color = if (isDarkMenuBar) java.awt.Color.WHITE else java.awt.Color.BLACK
        g2d.font = font

        var xOffset = padding + iconSize + spacingPx

        // Draw label if present
        if (hasLabel && labelText.isNotEmpty()) {
            val textY = (height + fontMetrics.ascent - fontMetrics.descent) / 2
            g2d.drawString(labelText, xOffset, textY)
            xOffset += labelTextWidth + spacingPx
        }

        // Draw time
        val textY = (height + fontMetrics.ascent - fontMetrics.descent) / 2
        g2d.drawString(timeString, xOffset, textY)

        g2d.dispose()
        return image
    }

    private fun truncateLabel(
        text: String,
        fontMetrics: java.awt.FontMetrics,
        maxWidth: Int,
    ): String {
        if (fontMetrics.stringWidth(text) <= maxWidth) {
            return text
        }

        val ellipsis = "..."
        val ellipsisWidth = fontMetrics.stringWidth(ellipsis)

        var truncated = text
        while (truncated.isNotEmpty() && fontMetrics.stringWidth(truncated) + ellipsisWidth > maxWidth) {
            truncated = truncated.dropLast(1)
        }

        return if (truncated.isEmpty()) ellipsis else "$truncated$ellipsis"
    }

    @OptIn(ExperimentalTime::class)
    private fun formatElapsedTime(duration: Duration): String {
        val totalMinutes = duration.inWholeMinutes
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "%02d:%02d".format(hours, minutes)
    }
}
