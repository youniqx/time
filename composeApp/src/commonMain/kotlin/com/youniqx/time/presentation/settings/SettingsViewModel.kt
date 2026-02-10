package com.youniqx.time.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youniqx.time.domain.NamespacesRepository
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.usecases.SearchNamespacesUseCase
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import com.youniqx.time.gitlab.models.NamespaceQuery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

data class UiState(
    val settings: Settings,
    val namespaces: NamespaceQuery.Data?,
)

@ViewModelKey(SettingsViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class SettingsViewModel(
    settingsRepository: SettingsRepository,
    private val namespacesRepository: NamespacesRepository
) : ViewModel(), UpdateSettingsUseCase by settingsRepository, SearchNamespacesUseCase {
    private val initialSettings = settingsRepository.settings.value.data
    private var lastSuccessfulSearchTerm = ""
    private var searchTerm = ""
    val uiState = settingsRepository.settings.combine(namespacesRepository.namespacesBySearch, ::Pair)
        .map { (settings, namespacesBySearch) ->
            UiState(
                settings = settings.data,
                namespaces = namespacesBySearch[searchTerm]?.data.also {
                    if (it != null) lastSuccessfulSearchTerm = searchTerm
                } ?: namespacesBySearch[lastSuccessfulSearchTerm]?.data
            )
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = UiState(
                settings = initialSettings,
                namespaces = null
            )
    )

    override fun search(search: String) {
        searchTerm = search
        namespacesRepository.search(search)
    }
}