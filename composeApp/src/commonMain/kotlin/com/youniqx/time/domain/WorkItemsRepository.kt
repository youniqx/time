package com.youniqx.time.domain

import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.models.WorkItemsFromCurrentUser
import com.youniqx.time.domain.usecases.SearchWorkItemsUseCase
import kotlinx.coroutines.flow.StateFlow

interface WorkItemsRepository : SearchWorkItemsUseCase {
    val workItemsFromCurrentUser: StateFlow<SourceAware<WorkItemsFromCurrentUser?>?>
}
