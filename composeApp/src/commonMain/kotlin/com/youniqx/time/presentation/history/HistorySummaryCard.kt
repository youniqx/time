package com.youniqx.time.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.isOpenTracking
import com.youniqx.time.presentation.LocalResultStore
import com.youniqx.time.presentation.LocalSharedTransitionScope
import com.youniqx.time.presentation.opentracking.RepresentingIndicator
import com.youniqx.time.presentation.opentracking.representingColors
import com.youniqx.time.presentation.theme.LocalSpacing
import com.youniqx.time.presentation.workitems.ScrollToWorkItem

@Composable
fun HistorySummaryCard(
    timelogs: List<TimelogEntry>,
    openTracking: OpenTracking?,
    visible: Boolean,
    modifier: Modifier = Modifier,
    heading: @Composable () -> Unit = { Text(text = "Total Time") },
    groupedByDay: List<DayGroup>? = null,
) {
    val spacing = LocalSpacing.current
    // Calculate totals
    val totalTime = timelogs.sumOf { it.timeSpent }
    with(LocalSharedTransitionScope.current) {
        if (this == null) return@with
        Card(
            modifier = modifier
                .clip(CardDefaults.shape)
                .sharedElementWithCallerManagedVisibility(
                    sharedContentState = rememberSharedContentState("historySummaryCard"),
                    visible = visible,
                    // renderInOverlayDuringTransition = false,
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.cardPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.labelMedium,
                        LocalContentColor provides MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        content = heading
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatTimeSpent(totalTime),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (timelogs.firstOrNull()?.isOpenTracking ?: false) {
                            Spacer(Modifier.width(spacing.sm))
                            val resultStore = LocalResultStore.current
                            openTracking?.RepresentingIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable(onClickLabel = "Scroll to open tracking") {
                                        resultStore.setResult(result = ScrollToWorkItem(openTracking.workItemId))
                                    },
                                color = openTracking.representingColors.color
                            )
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    groupedByDay?.let {
                        Text(
                            text = "${it.size} ${if (it.size == 1) "day" else "days"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    } ?: Text("")
                    Text(
                        text = "${timelogs.size} ${if (timelogs.size == 1) "entry" else "entries"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}