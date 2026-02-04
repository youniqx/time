package com.youniqx.time.domain

import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.usecases.LoadTimelogsUseCase
import com.youniqx.time.gitlab.models.TimelogsQuery
import kotlinx.coroutines.flow.StateFlow

interface TimelogsRepository: LoadTimelogsUseCase {
    val timelogs: StateFlow<SourceAware<TimelogsQuery.Data?>?>
}