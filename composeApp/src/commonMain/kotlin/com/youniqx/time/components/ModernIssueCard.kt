@file:OptIn(kotlin.time.ExperimentalTime::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.youniqx.time.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.youniqx.time.Label
import com.youniqx.time.WorkItemTypeIcon
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets
import com.youniqx.time.settings.OpenTracking
import com.youniqx.time.theme.LocalSpacing
import com.youniqx.time.theme.custom.TimerActiveColor
import com.youniqx.time.timer.formatCompact
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun ModernIssueCard(
    issueId: String,
    issueTitle: String,
    issueTypeName: String,
    labels: List<BareWorkItemWidgets.Node>?,
    totalTimeLoggedSeconds: Int,
    openTracking: OpenTracking?,
    isPinned: Boolean,
    useLabelColors: Boolean,
    onStartTracking: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val isTracking = openTracking?.workItemId == issueId
    var elapsedTime by remember { mutableStateOf(Duration.ZERO) }

    val totalTimeLogged = totalTimeLoggedSeconds.seconds

    // Update elapsed time when tracking
    LaunchedEffect(openTracking?.timeOfOpen, isTracking) {
        if (isTracking) {
            while (isActive) {
                elapsedTime = Clock.System.now() - openTracking!!.timeOfOpen
                delay(1.seconds)
            }
        } else {
            elapsedTime = Duration.ZERO
        }
    }

    // Animate card color when tracking
    val cardColor by animateColorAsState(
        targetValue = if (isTracking)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "cardColor"
    )

    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTracking) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(spacing.cardRadius)
    ) {
        Column(
            modifier = Modifier.padding(spacing.cardPadding)
        ) {
            // Top row: Type icon + Title + Pin indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                WorkItemTypeIcon(issueTypeName)

                Spacer(modifier = Modifier.width(spacing.sm))

                Text(
                    text = issueTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (isPinned) {
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Icon(
                        Icons.Filled.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Labels (if present) - flow layout with proper spacing
            if (!labels.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(spacing.sm))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    labels.forEach { label ->
                        Label(label = label, useColors = useLabelColors)
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Bottom row: Time info + Quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isTracking) {
                        // Active timer badge
                        ActiveTimerBadge(elapsedTime = elapsedTime)
                    } else if (totalTimeLogged > Duration.ZERO) {
                        // Total logged time
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(spacing.xs))
                        Text(
                            text = totalTimeLogged.formatCompact(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Quick action button
                if (!isTracking) {
                    FilledTonalIconButton(
                        onClick = onStartTracking,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Start tracking",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveTimerBadge(elapsedTime: Duration) {
    val spacing = LocalSpacing.current

    Surface(
        color = TimerActiveColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulsingDot(color = TimerActiveColor)
            Spacer(Modifier.width(6.dp))
            Text(
                text = elapsedTime.formatCompact(),
                style = MaterialTheme.typography.labelMedium,
                color = TimerActiveColor,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun PulsingDot(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}
