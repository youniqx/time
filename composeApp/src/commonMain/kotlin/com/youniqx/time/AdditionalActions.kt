package com.youniqx.time

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
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import kotlinx.coroutines.launch

@Composable
fun AdditionalActions(
    issue: BareWorkItem,
    pinned: Boolean,
    togglePinned: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val text = if (pinned) "Unpin issue" else "Pin issue"
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
    SimpleTooltip("Copy issue ID\n${issue.iid}") {
        IconButton(onClick = {
            coroutineScope.launch {
                clipboard.setClipEntry(clipEntryOf(issue.iid))
            }
        }) {
            Icon(
                imageVector = Icons.Default.Numbers,
                contentDescription = "Copy issue ID\n${issue.iid}"
            )
        }
    }
    if (issue.webUrl != null) SimpleTooltip("Copy issue URL") {
        IconButton(onClick = {
            coroutineScope.launch {
                clipboard.setClipEntry(clipEntryOf(issue.webUrl))
            }
        }) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = "Copy issue URL"
            )
        }
    }
    if (issue.webUrl != null) SimpleTooltip("Open issue") {
        IconButton(onClick = { uriHandler.openUri(issue.webUrl) }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open issue")
        }
    }
}