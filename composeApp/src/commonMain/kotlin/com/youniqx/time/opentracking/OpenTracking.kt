package com.youniqx.time.opentracking

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.youniqx.time.components.SimpleTooltip
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class OpenTracking(
    val workItemId: String,
    val workItemTitle: String? = null, // Todo: do we really want to serialize this?
    val summary: String? = null,
    val timeOfOpen: Instant,
    val customTimeSpent: String? = null,
) {
    val customTimeSpentHasError by lazy {
        customTimeSpent?.let { Duration.parseOrNull(it.trim()) == null } ?: false
    }

    data class RepresentingColors(
        val color: Color,
        val container: Color,
        val onContainer: Color,
    )
}

@OptIn(ExperimentalTime::class)
val OpenTracking.currentTimeSpentString: String
    get() = customTimeSpent ?: (Clock.System.now() - timeOfOpen).inWholeMinutes.minutes.toString()

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
            color = MaterialTheme.colorScheme.primary,
            container = MaterialTheme.colorScheme.primaryContainer,
            onContainer = MaterialTheme.colorScheme.onPrimaryContainer,
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
