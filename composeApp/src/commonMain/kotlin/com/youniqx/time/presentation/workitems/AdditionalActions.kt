package com.youniqx.time.presentation.workitems

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import com.youniqx.time.clipEntryOf
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import kotlinx.coroutines.launch

@Composable
fun AdditionalActions(
    workItem: BareWorkItem,
    pinned: Boolean,
    togglePinned: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val text = if (pinned) "Unpin" else "Pin"
    SimpleTooltip(text) {
        IconToggleButton(checked = pinned, onCheckedChange = { togglePinned() }) {
            Icon(
                imageVector = if (pinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                contentDescription = text
            )
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    SimpleTooltip("Copy work item ID\n${workItem.iid}") {
        IconButton(onClick = {
            coroutineScope.launch {
                clipboard.setClipEntry(clipEntryOf(workItem.iid))
            }
        }) {
            Icon(
                imageVector = Icons.Default.Numbers,
                contentDescription = "Copy work item ID\n${workItem.iid}"
            )
        }
    }
    if (workItem.webUrl != null) SimpleTooltip("Copy work item URL") {
        IconButton(onClick = {
            coroutineScope.launch {
                clipboard.setClipEntry(clipEntryOf(workItem.webUrl))
            }
        }) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = "Copy work item URL"
            )
        }
    }
    if (workItem.webUrl != null) SimpleTooltip("Open work item") {
        IconButton(onClick = { uriHandler.openUri(workItem.webUrl) }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open work item")
        }
    }
}