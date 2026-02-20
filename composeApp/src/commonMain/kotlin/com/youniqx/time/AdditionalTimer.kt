package com.youniqx.time

data class AdditionalTimerSupport(
    val isSupported: Boolean,
    val settingsText: String? = null,
)

expect val additionalTimerSupport: AdditionalTimerSupport
