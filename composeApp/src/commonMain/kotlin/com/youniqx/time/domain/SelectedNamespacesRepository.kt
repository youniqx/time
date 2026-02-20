package com.youniqx.time.domain

import com.youniqx.time.domain.models.SelectedNamespaces
import com.youniqx.time.domain.usecases.SaveIterationCadenceNamespaceUseCase
import com.youniqx.time.domain.usecases.SaveSearchNamespaceUseCase
import kotlinx.coroutines.flow.StateFlow

interface SelectedNamespacesRepository :
    SaveSearchNamespaceUseCase,
    SaveIterationCadenceNamespaceUseCase {
    val selectedNamespaces: StateFlow<SelectedNamespaces>
}
