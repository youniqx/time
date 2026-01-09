package com.youniqx.time

actual val additionalTimerSupport = AdditionalTimerSupport(
    isSupported = isTraySupported,
    settingsText = "Show timer in menu bar"
)
