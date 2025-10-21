package com.youniqx.time.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val token: String,
    val darkTheme: Boolean,
    val highContrastColors: Boolean,
)

private enum class SettingKey {
    Token,
    DarkTheme,
    HighContrastColors;
}

@OptIn(ExperimentalSettingsApi::class)
class SettingsViewModel(token: String, systemInDarkTheme: Boolean) : ViewModel() {
    private val _uiState =
        MutableStateFlow(UiState(token = token, darkTheme = systemInDarkTheme, highContrastColors = false))
    val uiState = _uiState.asStateFlow()
    private val settings = Settings().makeObservable().toFlowSettings()

    init {
        settings.getStringFlow(SettingKey.Token.name, token).save { copy(token = it) }
        settings.getBooleanFlow(SettingKey.DarkTheme.name, systemInDarkTheme).save { copy(darkTheme = it) }
        settings.getBooleanFlow(SettingKey.HighContrastColors.name, false).save { copy(highContrastColors = it) }
    }

    fun toggleDarkTheme() {
        viewModelScope.launch {
            settings.putBoolean(SettingKey.DarkTheme.name, !uiState.value.darkTheme)
        }
    }

    fun toggleHighContrastColors() {
        viewModelScope.launch {
            settings.putBoolean(SettingKey.HighContrastColors.name, !uiState.value.highContrastColors)
        }
    }

    fun setToken(token: String) {
        viewModelScope.launch {
            settings.putString(SettingKey.Token.name, token)
        }
    }

    private inline fun <reified T> Flow<T>.save(crossinline updateUiState: UiState.(T) -> UiState) {
        viewModelScope.launch {
            collect { setting ->
                _uiState.update { it.updateUiState(setting) }
            }
        }
    }
}