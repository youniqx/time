package com.youniqx.time.presentation.cornersash

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.demoModeIsActive
import com.youniqx.time.presentation.SimpleTooltip
import kotlinx.coroutines.flow.map

@Composable
fun DemoModeCornerSash(
    settingsRepository: SettingsRepository,
    onClick: () -> Unit,
) {
    val demoModeIsActive by
        settingsRepository.settings.map { it.demoModeIsActive }.collectAsStateWithLifecycle(false)
    if (!demoModeIsActive) return
    val label = "Enter instance Url to get real content"
    CornerSash(modifier = Modifier.clickable(onClickLabel = label, onClick = onClick)) {
        SimpleTooltip(label) {
            Text(text = "DEMO MODE", fontWeight = FontWeight.Bold)
        }
    }
}
