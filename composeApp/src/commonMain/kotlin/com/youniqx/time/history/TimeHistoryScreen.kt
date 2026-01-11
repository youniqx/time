@file:OptIn(ExperimentalTime::class)

package com.youniqx.time.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.youniqx.time.components.SimpleTooltip
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets
import com.youniqx.time.relativetime.RelativeTime
import com.youniqx.time.relativetime.formatDuration
import com.youniqx.time.systemBarsForVisualComponents
import com.youniqx.time.theme.LocalSpacing
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

enum class TimeRange(val label: String, val daysBack: Int) {
    Today("Today", 1),
    Week("Week", 7),
    Month("Month", 30);

    fun getLabel(offset: Int): String = when {
        offset == 0 -> label
        this == Today -> "$offset days ago"
        this == Week -> if (offset == 1) "Last week" else "$offset weeks ago"
        this == Month -> if (offset == 1) "Last month" else "$offset months ago"
        else -> label
    }
}

data class TimelogEntry(
    val id: String,
    val spentAt: Instant,
    val summary: String?,
    val timeSpent: Int, // in seconds
    val issueTitle: String?,
    val issueUrl: String?,
    val issueIid: String?
)

fun BareWorkItemWidgets.Node2.toTimelogEntry(workItem: BareWorkItem?, cutoff: Instant): TimelogEntry? {
    val spentAt = spentAt?.let { Instant.parseOrNull(it.toString()) }
    if (spentAt == null || spentAt < cutoff) return null
    return TimelogEntry(
        id = id,
        spentAt = spentAt,
        summary = summary,
        timeSpent = timeSpent,
        issueTitle = workItem?.title,
        issueUrl = workItem?.webUrl,
        issueIid = workItem?.iid
    )
}

data class DayGroup(
    val dayLabel: String,
    val entries: List<TimelogEntry>,
    val totalSeconds: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeHistoryScreen(
    timelogs: List<TimelogEntry>,
    isLoading: Boolean,
    selectedRange: TimeRange,
    onRangeChange: (TimeRange) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val insets = WindowInsets.systemBarsForVisualComponents.asPaddingValues()

    // Period offset (0 = current, 1 = previous, 2 = 2 periods ago, etc.)
    var periodOffset by remember(selectedRange) { mutableStateOf(0) }

    // Filter timelogs based on selected range and offset
    val filteredTimelogs = remember(timelogs, selectedRange, periodOffset) {
        val now = Clock.System.now()
        val periodDays = selectedRange.daysBack
        val startOffset = periodOffset * periodDays
        val endOffset = (periodOffset + 1) * periodDays

        val periodStart = now - endOffset.days
        val periodEnd = now - startOffset.days

        timelogs.filter { entry ->
            entry.spentAt in periodStart..<periodEnd
        }
    }

    // Group timelogs by day
    val groupedByDay = remember(filteredTimelogs) {
        val now = Clock.System.now()
        filteredTimelogs
            .groupBy { entry ->
                val age = now - entry.spentAt
                when {
                    age < 1.days -> "Today"
                    age < 2.days -> "Yesterday"
                    age < 7.days -> "${age.inWholeDays.toInt()} days ago"
                    else -> "${(age.inWholeDays / 7).toInt()} weeks ago"
                }
            }
            .map { (label, entries) ->
                DayGroup(
                    dayLabel = label,
                    entries = entries.sortedByDescending { it.spentAt },
                    totalSeconds = entries.sumOf { it.timeSpent }
                )
            }
            .sortedBy { group ->
                // Sort by the first entry's time (most recent first)
                group.entries.firstOrNull()?.spentAt?.let { now - it }?.inWholeMilliseconds ?: Long.MAX_VALUE
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Time History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBarsForVisualComponents),
                windowInsets = WindowInsets(0)
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Time range selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenPadding)
                    .padding(bottom = spacing.sm),
                horizontalArrangement = Arrangement.Center
            ) {
                SingleChoiceSegmentedButtonRow {
                    TimeRange.entries.forEachIndexed { index, range ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = TimeRange.entries.size
                            ),
                            onClick = { onRangeChange(range) },
                            selected = selectedRange == range
                        ) {
                            Text(range.label)
                        }
                    }
                }
            }

            // Period navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenPadding)
                    .padding(bottom = spacing.md),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { periodOffset++ }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous period")
                }
                Text(
                    text = selectedRange.getLabel(periodOffset),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = spacing.md)
                )
                IconButton(
                    onClick = { if (periodOffset > 0) periodOffset-- },
                    enabled = periodOffset > 0
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next period")
                }
            }

            // Summary card
            HistorySummaryCard(
                modifier = Modifier
                    .padding(horizontal = spacing.screenPadding)
                    .padding(bottom = spacing.lg),
                groupedByDay = groupedByDay,
                timelogs = filteredTimelogs
            )

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredTimelogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.md)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "No time entries",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Track time on issues to see your history here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = spacing.screenPadding,
                        end = spacing.screenPadding,
                        bottom = spacing.screenPadding + insets.calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    groupedByDay.forEach { dayGroup ->
                        // Day header
                        item(key = "header-${dayGroup.dayLabel}") {
                            DayHeader(
                                dayLabel = dayGroup.dayLabel,
                                totalSeconds = dayGroup.totalSeconds
                            )
                        }

                        // Entries for this day
                        items(dayGroup.entries, key = { it.id }) { entry ->
                            TimelogCard(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(
    dayLabel: String,
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = dayLabel,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = formatTimeSpent(totalSeconds),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun TimelogCard(
    entry: TimelogEntry,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val uriHandler = LocalUriHandler.current
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Issue title
                    if (!entry.issueTitle.isNullOrEmpty()) {
                        Text(
                            text = entry.issueTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = if (expanded) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Issue ID
                    if (!entry.issueIid.isNullOrEmpty()) {
                        Text(
                            text = "#${entry.issueIid}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(spacing.md))

                // Time spent
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = formatTimeSpent(entry.timeSpent),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Summary (if present)
            AnimatedVisibility(visible = !entry.summary.isNullOrEmpty()) {
                Text(
                    text = entry.summary.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = spacing.sm)
                )
            }

            // Time stamp and actions
            AnimatedVisibility(visible = expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDuration(Clock.System.now() - entry.spentAt, RelativeTime.Past) + " ago",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    if (!entry.issueUrl.isNullOrEmpty()) {
                        SimpleTooltip("Open in GitLab") {
                            IconButton(
                                onClick = { uriHandler.openUri(entry.issueUrl) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open in GitLab",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTimeSpent(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}
