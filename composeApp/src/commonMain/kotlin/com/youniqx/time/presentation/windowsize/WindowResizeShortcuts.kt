package com.youniqx.time.presentation.windowsize

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop169
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun WindowResizeShortcuts() {
    SingleChoiceSegmentedButtonRow {
        val colors = SegmentedButtonDefaults.colors(
            inactiveContainerColor = MaterialTheme.colorScheme.inverseSurface,
            inactiveContentColor = MaterialTheme.colorScheme.inverseOnSurface,
        )
        SegmentedButton(
            shape =
                SegmentedButtonDefaults.itemShape(
                    index = 0,
                    count = 3,
                ),
            colors = colors,
            onClick = {  },
            selected = false,
            label = { Icon(Icons.Default.Crop169, "Landscape") },
        )
        SegmentedButton(
            shape =
                SegmentedButtonDefaults.itemShape(
                    index = 1,
                    count = 3,
                ),
            colors = colors,
            onClick = {

            },
            selected = false,
            label = { Icon(Icons.Default.CropSquare, "Square") },
        )
        SegmentedButton(
            shape =
                SegmentedButtonDefaults.itemShape(
                    index = 2,
                    count = 3,
                ),
            colors = colors,
            onClick = {

            },
            selected = false,
            label = {
                Icon(
                    modifier = Modifier.rotate(90.0f),
                    imageVector = Icons.Default.Crop169,
                    contentDescription = "Portrait"
                )
            },
        )
    }
}