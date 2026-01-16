package com.youniqx.time.domain.models

import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import kotlin.time.Instant

data class WorkItemsFromCurrentUser(
    val currentUserId: String,
    val workItems: List<BareWorkItem>,
    val lastUpdated: Instant,
)