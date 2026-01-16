package com.youniqx.time.presentation.workitems

import androidx.lifecycle.ViewModel
import com.youniqx.time.domain.WorkItemsRepository
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.usecases.SearchWorkItemsUseCase
import com.youniqx.time.gitlab.models.NamespaceQuery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey

data class UiState(
    val settings: Settings,
    val namespaces: NamespaceQuery.Data?,
)

@Inject
@ViewModelKey(WorkItemsViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class WorkItemsViewModel(
    workItemsRepository: WorkItemsRepository
) : ViewModel(), SearchWorkItemsUseCase by workItemsRepository {
    val uiState = workItemsRepository.workItemsFromCurrentUser
}