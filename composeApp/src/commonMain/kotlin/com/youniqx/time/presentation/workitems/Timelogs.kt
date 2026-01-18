package com.youniqx.time.presentation.workitems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.youniqx.time.components.SimpleTooltip
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.isOpenTracking
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets
import com.youniqx.time.presentation.opentracking.RepresentingIndicator
import com.youniqx.time.presentation.opentracking.representingColors
import com.youniqx.time.relativetime.RelativeTime
import com.youniqx.time.relativetime.formatDuration
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
operator fun List<BareWorkItemWidgets.Node2>.invoke(
    openTracking: OpenTracking?
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        forEachIndexed { index, timelog ->
            val isEven = index % 2 == 0
            val rowBg = if (isEven) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            } else {
                Transparent
            }
            SimpleTooltip(timelog.spentAt?.let { Instant.parseOrNull(it.toString()) }?.let {
                if (timelog.isOpenTracking) {
                    "Currently tracking"
                } else {
                    "${formatDuration(Clock.System.now() - it, RelativeTime.Past)} ago"
                }
            }.orEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBg)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val timeMinutes = timelog.timeSpent / 60
                    val h = timeMinutes / 60
                    val m = timeMinutes % 60
                    Text(
                        text = "$h:${m.toString().padStart(length = 2, padChar = '0')}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(48.dp - if (timelog.isOpenTracking) 12.dp else 0.dp)
                    )
                    if (timelog.isOpenTracking && openTracking != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        openTracking.RepresentingIndicator(
                            modifier = Modifier.size(16.dp),
                            color = openTracking.representingColors.color
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = timelog.summary.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
