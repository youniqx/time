@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.youniqx.time.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.youniqx.time.settings.OpenTracking
import com.youniqx.time.theme.LocalSpacing
import com.youniqx.time.theme.custom.TimerActiveColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun VisualTimer(
    openTracking: OpenTracking?,
    issueTitle: String?,
    onDiscard: () -> Unit,
    onCommit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val isRunning = openTracking != null
    var elapsedTime by remember { mutableStateOf(Duration.ZERO) }

    // Update elapsed time every second
    LaunchedEffect(openTracking?.timeOfOpen) {
        if (openTracking != null) {
            while (isActive) {
                elapsedTime = Clock.System.now() - openTracking.timeOfOpen
                delay(1.seconds)
            }
        } else {
            elapsedTime = Duration.ZERO
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(spacing.cardRadius)
    ) {
        Column(
            modifier = Modifier.padding(spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress ring with timer
            ProgressRing(
                progress = (elapsedTime.inWholeMinutes % 60) / 60f,
                elapsedTime = elapsedTime,
                isRunning = isRunning,
                modifier = Modifier.size(200.dp)
            )

            // Issue title if available
            if (issueTitle != null) {
                Spacer(modifier = Modifier.height(spacing.lg))
                Text(
                    text = issueTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(spacing.xl))

            // Controls
            if (isRunning) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Discard button
                    FilledTonalIconButton(
                        onClick = onDiscard,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Discard",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Commit button
                    FilledIconButton(
                        onClick = onCommit,
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = TimerActiveColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Commit Time",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactTimer(
    openTracking: OpenTracking?,
    modifier: Modifier = Modifier
) {
    val isRunning = openTracking != null
    var elapsedTime by remember { mutableStateOf(Duration.ZERO) }

    LaunchedEffect(openTracking?.timeOfOpen) {
        if (openTracking != null) {
            while (isActive) {
                elapsedTime = Clock.System.now() - openTracking.timeOfOpen
                delay(1.seconds)
            }
        } else {
            elapsedTime = Duration.ZERO
        }
    }

    if (isRunning) {
        ProgressRing(
            progress = (elapsedTime.inWholeMinutes % 60) / 60f,
            elapsedTime = elapsedTime,
            isRunning = true,
            modifier = modifier,
            size = 80.dp,
            strokeWidth = 6.dp
        )
    }
}
