@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.youniqx.time.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.flatMap
import androidx.paging.insertSeparators
import com.youniqx.time.domain.NamespacesRepository
import com.youniqx.time.domain.SelectedNamespacesRepository
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.IterationCadence
import com.youniqx.time.domain.models.Namespace
import com.youniqx.time.domain.models.NamespaceEntry
import com.youniqx.time.domain.models.SelectedNamespaces
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.usecases.SaveIterationCadenceNamespaceUseCase
import com.youniqx.time.domain.usecases.SaveSearchNamespaceUseCase
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

data class UiState(
    val settings: Settings,
    val namespaces: PagingData<NamespaceEntry>,
    val iterationCadenceNamespaces: PagingData<NamespaceEntry>,
    val selectedNamespaces: SelectedNamespaces,
)

@ViewModelKey(SettingsViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class SettingsViewModel(
    settingsRepository: SettingsRepository,
    private val namespacesRepository: NamespacesRepository,
    private val selectedNamespacesRepository: SelectedNamespacesRepository,
) : ViewModel(),
    UpdateSettingsUseCase by settingsRepository,
    SaveSearchNamespaceUseCase,
    SaveIterationCadenceNamespaceUseCase {

    private val initialSettings = settingsRepository.settings.value.data
    private val initialSelectedNamespaces = selectedNamespacesRepository.selectedNamespaces.value
    private var namespaceSearchTerm = MutableStateFlow("")
    private var iterationCadenceNamespaceSearchTerm = MutableStateFlow("")
    val uiState = combine(
        namespaceSearchTerm.asPagingData(),
        iterationCadenceNamespaceSearchTerm.asPagingData(),
        settingsRepository.settings.map { it.data },
        selectedNamespacesRepository.selectedNamespaces
    ) { namespaceSearchPagingData, iterationCadenceNamespaceSearchPagingData, settings, selectedNamespaces ->
        UiState(
            settings = settings,
            namespaces = namespaceSearchPagingData.insertSeparators(),
            iterationCadenceNamespaces = iterationCadenceNamespaceSearchPagingData.flatMap {
                if (it is NamespaceEntry.User) {
                    val selectedSearchNamespace = selectedNamespaces.search
                    if (selectedSearchNamespace != null) listOf(
                        NamespaceEntry.SelectedSearch(
                            name = selectedSearchNamespace.name,
                            fullPath = selectedSearchNamespace.fullPath,
                            iterationCadences = selectedSearchNamespace.iterationCadences,
                        )
                    ) else emptyList()
                } else listOf(it)
            }.insertSeparators(),
            selectedNamespaces = selectedNamespaces,
        )
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = UiState(
                settings = initialSettings,
                namespaces = PagingData.empty(
                    sourceLoadStates = LoadStates(
                        LoadState.Loading,
                        NotLoading(endOfPaginationReached = false),
                        NotLoading(endOfPaginationReached = false),
                    )
                ),
                iterationCadenceNamespaces = PagingData.empty(
                    sourceLoadStates = LoadStates(
                        LoadState.Loading,
                        NotLoading(endOfPaginationReached = false),
                        NotLoading(endOfPaginationReached = false),
                    )
                ),
                selectedNamespaces = initialSelectedNamespaces
            )
    )

    private fun Flow<String>.asPagingData(): Flow<PagingData<NamespaceEntry>> = debounce(timeoutMillis = 300)
        .distinctUntilChanged()
        .flatMapLatest { searchTerm ->
            Pager(
                config = PagingConfig(pageSize = 3),
                pagingSourceFactory = { namespacesRepository.search(search = searchTerm) }
            ).flow.cachedIn(viewModelScope)
        }

    fun searchNamespace(search: String) {
        namespaceSearchTerm.value = search
    }

    fun searchIterationCadenceNamespace(search: String) {
        iterationCadenceNamespaceSearchTerm.value = search
    }

    override fun saveSearchNamespace(namespace: Namespace) {
        selectedNamespacesRepository.saveSearchNamespace(namespace = namespace)
        this.setNamespaceFullPath(namespace.fullPath)
    }

    override fun saveIterationCadenceNamespace(namespace: Namespace) {
        val namespaceFullPath = uiState.value.selectedNamespaces.iterationCadence?.fullPath
        if (namespaceFullPath != namespace.fullPath) {
            selectedNamespacesRepository.saveIterationCadenceNamespace(namespace = namespace)
            this.setIterationCadence(IterationCadence(namespaceFullPath = namespace.fullPath))
        }
    }
}

private fun PagingData<NamespaceEntry>.insertSeparators(): PagingData<NamespaceEntry> =
    insertSeparators { entry, entry1 ->
        if (entry != null && entry1 != null && !entry.isOfSameType(entry1)) {
            NamespaceEntry.Separator
        } else null
    }
