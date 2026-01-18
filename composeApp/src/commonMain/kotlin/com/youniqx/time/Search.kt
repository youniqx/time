package com.youniqx.time

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.youniqx.time.presentation.Label
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.presentation.invoke
import com.youniqx.time.presentation.workitems.WorkItemTypeIcon

@Composable
fun Search(
    search: String,
    onSearchChange: (String) -> Unit,
    show: Boolean,
    canFocus: Boolean,
    modifier: Modifier = Modifier,
    onPress: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            if (it is PressInteraction.Press) onPress()
        }
    }
    OutlinedTextField(
        value = search,
        onValueChange = onSearchChange,
        singleLine = true,
        modifier = modifier
            .focusProperties { this.canFocus = canFocus }
            .onPreviewKeyEvent {
                if (
                    !it.isMetaPressed &&
                    !it.isAltPressed &&
                    !it.isCtrlPressed &&
                    !it.isShiftPressed &&
                    it.key == Key.Tab &&
                    it.type == KeyEventType.KeyDown
                ) {
                    focusManager.moveFocus(FocusDirection.Next)
                    true
                } else {
                    false
                }
            }
            .fillMaxWidth()
            .then(
                if (show) {
                    Modifier.padding(vertical = 4.dp)
                } else {
                    Modifier.height(0.dp).alpha(0f)
                }
            ),
        placeholder = { SearchPlaceholder() },
        trailingIcon = if (search.isEmpty()) null else {
            {
                SimpleTooltip("Clear search") {
                    IconButton(
                        modifier = Modifier
                            .focusProperties { this.canFocus = canFocus }
                            .pointerHoverIcon(PointerIcon.Default),
                        onClick = { onSearchChange("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                        )
                    }
                }
            }
        },
        interactionSource = interactionSource
    )
}

@Composable
fun SearchPlaceholder() {
    Text(
        text = buildAnnotatedString {
            append("Search for ")
            appendInlineContent("issues", "issues")
            append(", ")
            appendInlineContent("epics", "epics")
            append(", ")
            appendInlineContent("tasks", "tasks")
            append(", ")
            appendInlineContent("labels", "labels")
            append(", ")
            appendInlineContent("assignees", "assignees")
            append(", IDs, …")
        },
        inlineContent = mapOf(
            "issues" to InlineTextContent(
                Placeholder(
                    width = 16.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                WorkItemTypeIcon("Issue", "Issues")
            },
            "epics" to InlineTextContent(
                Placeholder(
                    width = 16.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                WorkItemTypeIcon("Epic", "Epics")
            },
            "epics" to InlineTextContent(
                Placeholder(
                    width = 16.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                WorkItemTypeIcon("Epic", "Epics")
            },
            "tasks" to InlineTextContent(
                Placeholder(
                    width = 16.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                WorkItemTypeIcon("Task", "Tasks")
            },
            "labels" to InlineTextContent(
                Placeholder(
                    width = 20.sp,
                    height = 20.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                SimpleTooltip("Labels") {
                    Label(__typename = "", id = "", color = "#BC8F8F", title = "L")(
                        useColors = false
                    )
                }
            },
            "assignees" to InlineTextContent(
                Placeholder(
                    width = 16.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                SimpleTooltip("Assignees") {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Assignees",
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
        )
    )
}