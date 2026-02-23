package com.youniqx.time.domain.usecases

import androidx.compose.ui.unit.DpSize
import com.youniqx.time.domain.models.IterationCadence
import com.youniqx.time.domain.models.OpenTracking

interface UpdateSettingsUseCase {
    fun toggleDarkTheme()

    fun toggleHighContrastColors()

    fun toggleGroupSprintInEpics()

    fun toggleShowLabelsByDefault()

    fun toggleUseLabelColors()

    fun toggleShowMenuBarTimer()

    fun setInstanceUrl(instanceUrl: String)

    fun setToken(token: String)

    fun setNamespaceFullPath(fullPath: String)

    fun setIterationCadence(iterationCadence: IterationCadence?)

    fun togglePinWorkItem(id: String)

    fun setOpenTracking(openTracking: OpenTracking?)

    fun setWindowSize(size: DpSize?)
}
