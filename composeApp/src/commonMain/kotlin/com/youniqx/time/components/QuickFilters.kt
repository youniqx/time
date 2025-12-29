package com.youniqx.time.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.youniqx.time.theme.LocalSpacing

enum class QuickFilter {
    MyIssues,
    HasTimeLogged,
    Pinned,
    RecentlyTracked
}

@Composable
fun QuickFilters(
    activeFilters: Set<QuickFilter>,
    onFilterToggle: (QuickFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        QuickFilterChip(
            label = "My Issues",
            icon = Icons.Default.Person,
            selected = QuickFilter.MyIssues in activeFilters,
            onClick = { onFilterToggle(QuickFilter.MyIssues) }
        )

        QuickFilterChip(
            label = "Has Time",
            icon = Icons.Default.AccessTime,
            selected = QuickFilter.HasTimeLogged in activeFilters,
            onClick = { onFilterToggle(QuickFilter.HasTimeLogged) }
        )

        QuickFilterChip(
            label = "Pinned",
            icon = Icons.Default.PushPin,
            selected = QuickFilter.Pinned in activeFilters,
            onClick = { onFilterToggle(QuickFilter.Pinned) }
        )

        QuickFilterChip(
            label = "Recent",
            icon = Icons.Default.Star,
            selected = QuickFilter.RecentlyTracked in activeFilters,
            onClick = { onFilterToggle(QuickFilter.RecentlyTracked) }
        )
    }
}

@Composable
private fun QuickFilterChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "filterChipColor"
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = containerColor
        )
    )
}
