package com.youniqx.time.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.usecases.UpdateSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val loading: Boolean,
    val settings: Settings,
    val showOnboarding: Boolean,
)

fun SourceAware<Settings>.toInitialUiState() =
    UiState(
        loading = source == DataSource.Default,
        settings = data,
        showOnboarding = data.instanceUrl.isNullOrEmpty() || data.token.isNullOrEmpty(),
    )

@ViewModelKey(OnboardingViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class OnboardingViewModel(
    settingsRepository: SettingsRepository,
    dispatchers: IDispatchers,
) : ViewModel(),
    UpdateSettingsUseCase by settingsRepository {
    private val initialSettings = settingsRepository.settings.value
    private val _uiState = MutableStateFlow(initialSettings.toInitialUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // specifying the context here is a quickfix for an issue in wasmJs
        viewModelScope.launch(dispatchers.Default) {
            var lastDataSource: DataSource? = null
            settingsRepository.settings.collect { newSettings ->
                _uiState.update {
                    it.copy(
                        loading = newSettings.source == DataSource.Default,
                        settings = newSettings.data,
                        showOnboarding =
                            if (newSettings.source != lastDataSource) {
                                newSettings.data.instanceUrl.isNullOrEmpty() || newSettings.data.token.isNullOrEmpty()
                            } else {
                                it.showOnboarding
                            },
                    )
                }
                lastDataSource = newSettings.source
            }
        }
    }
}
