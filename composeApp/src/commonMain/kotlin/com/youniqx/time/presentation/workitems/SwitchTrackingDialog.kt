package com.youniqx.time.presentation.workitems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.presentation.settings.SettingsViewModel
import com.youniqx.time.presentation.theme.LocalSpacing
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
    Surface(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        color = AlertDialogDefaults.containerColor,
        shape = AlertDialogDefaults.shape,
        tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
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
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
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
                // Show current work item
                TextButton(
                    onClick = onShowCurrent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show current work item")
                }
                // Cancel
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
