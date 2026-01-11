package com.youniqx.time.opentracking

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class OpenTracking(
    val workItemId: String,
    val workItemTitle: String? = null, // Todo: do we really want to serialize this?
    val summary: String? = null,
    val timeOfOpen: Instant,
    val customTimeSpent: String? = null,
) {
    val customTimeSpentHasError by lazy {
        customTimeSpent?.let { Duration.parseOrNull(it.trim()) == null } ?: false
    }
}

@OptIn(ExperimentalTime::class)
val OpenTracking.currentTimeSpentString: String
    get() = customTimeSpent ?: (Clock.System.now() - timeOfOpen).inWholeMinutes.minutes.toString()
