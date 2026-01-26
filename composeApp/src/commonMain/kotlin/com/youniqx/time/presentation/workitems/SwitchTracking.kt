package com.youniqx.time.presentation.workitems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.presentation.DialogSurface
import com.youniqx.time.presentation.settings.SettingsViewModel
import com.youniqx.time.presentation.theme.AppTheme
import com.youniqx.time.presentation.theme.LocalSpacing
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class SwitchTrackingRoute(
    val targetId: String,
    val targetTitle: String,
) : NavKey

@Composable
fun SwitchTracking(
    targetId: String,
    targetTitle: String,
    onDismiss: () -> Unit,
    settingsViewModel: SettingsViewModel = metroViewModel()
) {
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val settings = settingsUiState.settings
    SwitchTrackingScreen(
        targetTitle = targetTitle,
        currentTracking = settings.openTracking,
        onKeepTimeAndSwitch = {
            settings.openTracking?.let { currentTracking ->
                settingsViewModel.setOpenTracking(
                    currentTracking.copy(
                        workItemId = targetId,
                        workItemTitle = targetTitle
                    )
                )
            }
            onDismiss()
        },
        onDiscardAndSwitch = {
            settingsViewModel.setOpenTracking(
                OpenTracking(
                    workItemId = targetId,
                    workItemTitle = targetTitle,
                    timeOfOpen = Clock.System.now()
                )
            )
            onDismiss()
        },
        onShowCurrent = {
            // Todo
//            val currentId = settings.openTracking?.workItemId
//            val index = filteredWorkItems.indexOfFirst { workItem -> workItem.id == currentId }
//            if (index >= 0) {
//                coroutineScope.launch {
//                    lazyListState.animateScrollToItem(index + 1, -100)
//                }
//            }
//            switchTrackingTarget = null
        },
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun SwitchTrackingScreen(
    targetTitle: String,
    currentTracking: OpenTracking?,
    onKeepTimeAndSwitch: () -> Unit,
    onDiscardAndSwitch: () -> Unit,
    onShowCurrent: () -> Unit,
    onDismiss: () -> Unit
) {
    val currentTitle = currentTracking?.workItemTitle ?: "another work item"
    val spacing = LocalSpacing.current
    DialogSurface {
        Column(
            modifier = Modifier.padding(spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Text("Switch tracking?", style = MaterialTheme.typography.headlineMedium)
            Text("Currently tracking:")
            Text(
                text = currentTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(spacing.xs))
            Text("Switch to:")
            Text(
                text = targetTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(spacing.sm))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Keep time and switch
                if (currentTracking != null) {
                    Button(
                        onClick = onKeepTimeAndSwitch,
                        modifier = Modifier
                            .widthIn(max = spacing.maxButtonWidth)
                            .fillMaxWidth()
                    ) {
                        Text("Keep time & switch")
                    }
                }
                // Discard and start new
                OutlinedButton(
                    onClick = onDiscardAndSwitch,
                    modifier = Modifier
                        .widthIn(max = spacing.maxButtonWidth)
                        .fillMaxWidth()
                ) {
                    Text("Discard time & switch")
                }
                // Show current work item
                TextButton(
                    onClick = onShowCurrent,
                    modifier = Modifier
                        .widthIn(max = spacing.maxButtonWidth)
                        .fillMaxWidth()
                ) {
                    Text("Show current work item")
                }
                // Cancel
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .widthIn(max = spacing.maxButtonWidth)
                        .fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Preview
@Composable
fun SwitchTrackingPreview() {
    AppTheme {
        SwitchTrackingScreen(
            targetTitle = "The best work item",
            currentTracking = OpenTracking(
                workItemId = "",
                workItemTitle = "Some boring work",
                timeOfOpen = Instant.fromEpochSeconds(0L),
                customTimeSpent = "2h 45m",
            ),
            onKeepTimeAndSwitch = {},
            onDiscardAndSwitch = {},
            onShowCurrent = {},
            onDismiss = {},
        )
    }
}
