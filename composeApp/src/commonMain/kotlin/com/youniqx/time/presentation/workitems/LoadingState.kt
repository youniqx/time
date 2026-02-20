package com.youniqx.time.presentation.workitems

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.youniqx.time.presentation.theme.LocalSpacing

@Composable
fun ShimmerWorkItemCard(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current

    val shimmerColors =
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmerTranslate",
    )

    val brush =
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 200, translateAnim - 200),
            end = Offset(translateAnim, translateAnim),
        )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = CardDefaults.cardColors().containerColor.copy(alpha = 0.7f),
            ),
        shape = RoundedCornerShape(spacing.cardRadius),
    ) {
        Column(modifier = Modifier.padding(spacing.cardPadding)) {
            // Title placeholder
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush),
            )

            Spacer(modifier = Modifier.height(spacing.md))

            // Labels placeholder
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                repeat(2) {
                    Box(
                        modifier =
                            Modifier
                                .width(60.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush),
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Bottom row placeholder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier =
                        Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush),
                )
                Box(
                    modifier =
                        Modifier
                            .width(32.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(brush),
                )
            }
        }
    }
}

@Composable
fun LoadingWorkItemList(
    count: Int = 5,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        repeat(count) {
            ShimmerWorkItemCard()
        }
    }
}
