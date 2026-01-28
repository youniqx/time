@file:OptIn(ExperimentalTime::class)
package com.youniqx.time.domain.models

import androidx.compose.ui.graphics.Color
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets
import com.youniqx.time.presentation.history.TimelogEntry
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

    data class RepresentingColors(
        val color: Color,
        val container: Color,
        val onContainer: Color,
    )
}

val OpenTracking.currentTimeSpentString: String
    get() = customTimeSpent ?: (Clock.System.now() - timeOfOpen).inWholeMinutes.minutes.toString()

fun OpenTracking.toDurationOrNull(): Duration? = Duration.parseOrNull(currentTimeSpentString)

private const val OPEN_TRACKING_TIMELOG_ID = "OPEN_TRACKING_TIMELOG_ID"

val BareWorkItemWidgets.Node2.isOpenTracking: Boolean
    get() = this.id == OPEN_TRACKING_TIMELOG_ID

val TimelogEntry.isOpenTracking: Boolean
    get() = this.id == OPEN_TRACKING_TIMELOG_ID

fun OpenTracking.toTimelog(currentUserId: String): BareWorkItemWidgets.Node2 {
    val timeSpent = toDurationOrNull() ?: Duration.ZERO
    return BareWorkItemWidgets.Node2(
        __typename = "",
        id = OPEN_TRACKING_TIMELOG_ID,
        spentAt = Clock.System.now().toString(),
        summary = summary,
        timeSpent = timeSpent.inWholeSeconds.toInt(),
        user = BareWorkItemWidgets.User(
            __typename = "",
            id = currentUserId
        )
    )
}

fun OpenTracking.toTimelogEntry(): TimelogEntry {
    val timeSpent = toDurationOrNull() ?: Duration.ZERO
    val spentAt = Clock.System.now() - timeSpent
    return TimelogEntry(
        id = OPEN_TRACKING_TIMELOG_ID,
        spentAt = spentAt,
        summary = summary,
        timeSpent = timeSpent.inWholeSeconds.toInt(),
        workItemTitle = workItemTitle,
        workItemUrl = null,
        workItemId = workItemId,
        workItemIid = null,
    )
}
