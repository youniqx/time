package com.youniqx.time.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.youniqx.time.settings.OpenTracking
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun SwitchTrackingDialog(
    targetTitle: String,
    currentTracking: OpenTracking?,
    onKeepTimeAndSwitch: () -> Unit,
    onDiscardAndSwitch: () -> Unit,
    onShowCurrent: () -> Unit,
    onDismiss: () -> Unit
) {
    val currentTitle = currentTracking?.workItemTitle ?: "another issue"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Switch tracking?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Currently tracking:")
                Text(
                    text = currentTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(4.dp))
                Text("Switch to:")
                Text(
                    text = targetTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Keep time and switch
                if (currentTracking != null) {
                    Button(
                        onClick = onKeepTimeAndSwitch,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Keep time & switch")
                    }
                }
                // Discard and start new
                OutlinedButton(
                    onClick = onDiscardAndSwitch,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Discard time & switch")
                }
                // Show current issue
                TextButton(
                    onClick = onShowCurrent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show current issue")
                }
                // Cancel
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        },
        dismissButton = {}
    )
}
