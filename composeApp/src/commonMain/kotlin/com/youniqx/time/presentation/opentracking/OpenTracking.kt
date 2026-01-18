@file:OptIn(ExperimentalTime::class)

package com.youniqx.time.presentation.opentracking

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.domain.models.OpenTracking
import kotlin.time.ExperimentalTime

const val customTimeSpentHasErrorMessage = "Manually entered time is not valid"
private const val customTimeSpentInfoMessage = "Manually entered time.\nTimer not running."

val OpenTracking.representingColors: OpenTracking.RepresentingColors
    @Composable
    get() = when {
        this.customTimeSpent == null -> OpenTracking.RepresentingColors(
            color = MaterialTheme.colorScheme.tertiary,
            container = MaterialTheme.colorScheme.tertiaryContainer,
            onContainer = MaterialTheme.colorScheme.onTertiaryContainer,
        )
        this.customTimeSpentHasError -> OpenTracking.RepresentingColors(
            color = MaterialTheme.colorScheme.error,
            container = MaterialTheme.colorScheme.errorContainer,
            onContainer = MaterialTheme.colorScheme.onErrorContainer,
        )
        else -> OpenTracking.RepresentingColors(
            color = MaterialTheme.colorScheme.secondary,
            container = MaterialTheme.colorScheme.secondaryContainer,
            onContainer = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }

@Composable
fun OpenTracking.RepresentingIndicator(color: Color, modifier: Modifier = Modifier) {
    when {
        this.customTimeSpent == null -> SimpleTooltip("Active timer running") {
            PulsingDot(modifier = modifier, color = color)
        }
        this.customTimeSpentHasError -> SimpleTooltip(customTimeSpentHasErrorMessage) {
            Icon(
                modifier = modifier,
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = customTimeSpentHasErrorMessage,
                tint = color
            )
        }
        else -> {
            SimpleTooltip(customTimeSpentInfoMessage) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Outlined.PauseCircle,
                    contentDescription = customTimeSpentInfoMessage,
                )
            }
        }
    }
}
