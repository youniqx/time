package com.youniqx.time.presentation.workitems

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.youniqx.time.Label
import com.youniqx.time.invoke

@Composable
operator fun List<Label?>?.invoke(
    useLabelColors: Boolean
) {
    this?.let {
        AnimatedVisibility(visible = this.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                it.filterNotNull().forEach { label -> label.invoke(useColors = useLabelColors) }
            }
        }
    }
}