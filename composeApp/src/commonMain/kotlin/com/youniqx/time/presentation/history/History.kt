@file:OptIn(ExperimentalTime::class)

package com.youniqx.time.presentation.history

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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.isOpenTracking
import com.youniqx.time.domain.models.toTimelogEntry
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.presentation.navigation.AutoFilledSupportingPaneSceneStrategy
import com.youniqx.time.presentation.navigation.LocalSceneRole
import com.youniqx.time.presentation.opentracking.RepresentingIndicator
import com.youniqx.time.presentation.opentracking.representingColors
import com.youniqx.time.presentation.relativetime.RelativeTime
import com.youniqx.time.presentation.relativetime.formatDuration
import com.youniqx.time.presentation.settings.SettingsViewModel
import com.youniqx.time.presentation.theme.LocalSpacing
import com.youniqx.time.refresh
import com.youniqx.time.systemBarsForVisualComponents
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Serializable
object HistoryRoute: NavKey

@Composable
fun History(
    viewModel: HistoryViewModel = metroViewModel(),
    settingsViewModel: SettingsViewModel = metroViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val settings = settingsUiState.settings
    val openTrackingAsTimelogEntry = remember(settings.openTracking, refresh(every = 1.seconds)) {
        settings.openTracking?.toTimelogEntry()
    }

    var range by remember { mutableStateOf(TimeRange.Today) }
    HistoryScreen(
        timelogs = listOfNotNull(openTrackingAsTimelogEntry) + uiState.timelogs,
        isLoading = uiState.loading,
        selectedRange = range,
        onRangeChange = { range = it },
        onBack = onBack,
        openTracking = settings.openTracking
    )
}

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

data class DayGroup(
    val dayLabel: String,
    val entries: List<TimelogEntry>,
    val totalSeconds: Int
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HistoryScreen(
    timelogs: List<TimelogEntry>,
    isLoading: Boolean,
    selectedRange: TimeRange,
    onRangeChange: (TimeRange) -> Unit,
    onBack: () -> Unit,
    openTracking: OpenTracking?,
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
        topBar = if (LocalSceneRole.current == AutoFilledSupportingPaneSceneStrategy.Role.Supporting) {
            {}
        } else {
            {
                TopAppBar(
                    title = { Text("Time History") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    windowInsets = WindowInsets.systemBarsForVisualComponents
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBarsForVisualComponents,
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
                openTracking = openTracking,
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
                            text = "Track time on work items to see your history here",
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
                                totalSeconds = dayGroup.totalSeconds,
                                openTrackingRepresentation = openTracking.takeIf {
                                    dayGroup.entries.firstOrNull()?.isOpenTracking == true
                                }?.run {
                                    {
                                        RepresentingIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = representingColors.color
                                        )
                                    }
                                }
                            )
                        }

                        // Entries for this day
                        items(dayGroup.entries, key = { it.id }) { entry ->
                            TimelogCard(
                                entry = entry,
                                openTrackingRepresentation = openTracking.takeIf { entry.isOpenTracking }?.run {
                                    {
                                        RepresentingIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = representingColors.color
                                        )
                                    }
                                }
                            )
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
    modifier: Modifier = Modifier,
    openTrackingRepresentation: (@Composable () -> Unit)? = null,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                openTrackingRepresentation?.let {
                    Spacer(Modifier.width(spacing.xs))
                    openTrackingRepresentation()
                }
                Text(
                    text = formatTimeSpent(totalSeconds),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TimelogCard(
    entry: TimelogEntry,
    modifier: Modifier = Modifier,
    openTrackingRepresentation: (@Composable () -> Unit)? = null,
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
                    if (!entry.workItemTitle.isNullOrEmpty()) {
                        Text(
                            text = entry.workItemTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = if (expanded) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Issue ID
                    if (!entry.workItemIid.isNullOrEmpty()) {
                        Text(
                            text = "#${entry.workItemIid}",
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        openTrackingRepresentation?.let {
                            Spacer(Modifier.width(spacing.xs))
                            openTrackingRepresentation()
                        }
                        Text(
                            text = formatTimeSpent(entry.timeSpent),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
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

                    if (!entry.workItemUrl.isNullOrEmpty()) {
                        SimpleTooltip("Open in GitLab") {
                            IconButton(
                                onClick = { uriHandler.openUri(entry.workItemUrl) },
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
