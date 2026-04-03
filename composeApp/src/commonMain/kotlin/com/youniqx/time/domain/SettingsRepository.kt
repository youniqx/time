package com.youniqx.time.domain

import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.SelectedNamespacesFullPaths
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.models.dataIfNotFrom
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository : UpdateSettingsUseCase {
    val settings: StateFlow<SourceAware<Settings>>
}

val SourceAware<Settings>.selectedNamespacesFullPaths
    get() =
        SelectedNamespacesFullPaths(
            search = data.namespaceFullPath,
            iterationCadence = data.iterationCadence?.namespaceFullPath,
        )

val SourceAware<Settings>.demoModeIsActive: Boolean
    get() {
        val settings = data.takeIf { source == DataSource.Local } ?: return false
        return settings.instanceUrl.isNullOrBlank()
    }
