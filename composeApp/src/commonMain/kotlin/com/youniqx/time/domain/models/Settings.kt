package com.youniqx.time.domain.models

data class Settings(
    val instanceUrl: String?,
    val token: String?,
    val namespaceFullPath: String?,
    val iterationCadence: IterationCadence?,
    val darkTheme: Boolean,
    val highContrastColors: Boolean,
    val groupSprintInEpics: Boolean,
    val showLabelsByDefault: Boolean,
    val useLabelColors: Boolean,
    val showMenuBarTimer: Boolean,
    val pinnedIssues: List<String>,
    val openTracking: OpenTracking?,
)
