package com.youniqx.time

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ThemeToggle(
    darkTheme: Boolean,
    useHighContrast: Boolean,
    toggleDarkTheme: () -> Unit,
    toggleHighContrast: () -> Unit
) {
    Row {
        var offerHighContrastToggle by remember { mutableStateOf(false) }
        LaunchedEffect(offerHighContrastToggle) {
            if (offerHighContrastToggle) {
                delay(5.seconds)
                offerHighContrastToggle = false
            }
        }
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()
            OutlinedIconButton(
                onClick = {
                    offerHighContrastToggle = true
                    toggleDarkTheme()
                },
                interactionSource = interactionSource,
            ) {
                val icon =
                    if (darkTheme) {
                        if (isHovered) Icons.Filled.LightMode else Icons.Outlined.LightMode
                    } else {
                        if (isHovered) Icons.Filled.DarkMode else Icons.Outlined.DarkMode
                    }
                Icon(imageVector = icon, contentDescription = null)
            }
        }

        DropdownMenu(
            expanded = offerHighContrastToggle,
            onDismissRequest = { offerHighContrastToggle = false },
        ) {
            val text = if (useHighContrast) "Disable high contrast" else "Enable high contrast"
            DropdownMenuItem(
                text = { Text(text) },
                onClick = { toggleHighContrast() }
            )
        }
    }
}