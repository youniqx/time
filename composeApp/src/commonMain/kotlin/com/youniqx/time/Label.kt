package com.youniqx.time

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets

@Composable
fun Label(label: BareWorkItemWidgets.Node, useColors: Boolean) {
    val default = SuggestionChipDefaults.suggestionChipColors()
        .copy(
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledLabelColor = LocalContentColor.current,
        )
    val colors = if (useColors) remember(label.color) {
        val color = try {
            Color(label.color.toColorInt()).copy(alpha = 0.75f)
        } catch (_: Exception) {
            default.disabledContainerColor
        }
        default.copy(
            disabledContainerColor = color,
            disabledLabelColor = color.contrastingTextColor()
        )
    } else {
        default
    }
    val titleParts = label.title.split("::")
    SuggestionChip(
        onClick = { },
        enabled = false,
        colors = colors,
        label = {
            Text(
                text = buildAnnotatedString {
                    titleParts.forEachIndexed { i, titlePart ->
                        append(titlePart)
                        if (i != titleParts.lastIndex) {
                            append(" ")
                            appendInlineContent("divider", "|")
                            append(" ")
                        }
                    }
                },
                inlineContent = mapOf(
                    "divider" to InlineTextContent(
                        Placeholder(
                            width = 1.sp,
                            height = 18.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        VerticalDivider(
                            thickness = 1.dp,
                            color = colors.disabledLabelColor.copy(alpha = 0.6f)
                        )
                    },
                )
            )
        }
    )
}