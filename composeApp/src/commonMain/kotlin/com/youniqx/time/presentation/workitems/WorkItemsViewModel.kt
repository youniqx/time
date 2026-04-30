package com.youniqx.time.presentation.workitems

import androidx.lifecycle.ViewModel
import com.youniqx.time.domain.CommitTimeTrackingRepository
import com.youniqx.time.domain.WorkItemsRepository
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.models.WorkItemsFromCurrentUser
import com.youniqx.time.domain.usecases.CommitTimeTrackingUseCase
import com.youniqx.time.domain.usecases.SearchWorkItemsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey

typealias UiState = SourceAware<WorkItemsFromCurrentUser?>?

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class WorkItemsViewModel(
    workItemsRepository: WorkItemsRepository,
    commitTimeTrackingRepository: CommitTimeTrackingRepository,
) : ViewModel(),
    SearchWorkItemsUseCase by workItemsRepository,
    CommitTimeTrackingUseCase by commitTimeTrackingRepository {
    val uiState = workItemsRepository.workItemsFromCurrentUser
}
