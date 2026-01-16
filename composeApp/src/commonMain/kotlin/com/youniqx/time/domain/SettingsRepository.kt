package com.youniqx.time.domain

import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository: UpdateSettingsUseCase {
    val settings: StateFlow<SourceAware<Settings>>
}