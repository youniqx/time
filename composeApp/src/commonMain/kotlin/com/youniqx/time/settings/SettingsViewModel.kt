package com.youniqx.time.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youniqx.time.domain.NamespacesRepository
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import com.youniqx.time.gitlab.models.NamespaceQuery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
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

@Inject
@ViewModelKey(SettingsViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class SettingsViewModel(
    settingsRepository: SettingsRepository,
    namespacesRepository: NamespacesRepository
) : ViewModel(), UpdateSettingsUseCase by settingsRepository {
    private val initialSettings = settingsRepository.settings.value.data
    val uiState = settingsRepository.settings.combine(namespacesRepository.namespaces, ::Pair)
        .map { (settings, namespaces) ->
            UiState(
                settings = settings.data,
                namespaces = namespaces?.data
            )
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = UiState(
                settings = initialSettings,
                namespaces = null
            )
    )
}