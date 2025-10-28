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
import kotlinx.serialization.json.Json

data class UiState(
    val token: String,
    val iterationCadenceId: String?,
    val darkTheme: Boolean,
    val highContrastColors: Boolean,
    val groupSprintInEpics: Boolean,
    val showLabelsByDefault: Boolean,
    val useLabelColors: Boolean,
    val pinnedIssues: List<String>,
)

private enum class SettingKey {
    Token,
    IterationCadenceId,
    DarkTheme,
    HighContrastColors,
    GroupSprintInEpics,
    ShowLabelsByDefault,
    UseLabelColors,
    PinnedIssues,
}

@OptIn(ExperimentalSettingsApi::class)
class SettingsViewModel(token: String, systemInDarkTheme: Boolean) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            UiState(
                token = token,
                iterationCadenceId = null,
                darkTheme = systemInDarkTheme,
                highContrastColors = false,
                groupSprintInEpics = false,
                showLabelsByDefault = false,
                useLabelColors = false,
                pinnedIssues = emptyList()
            )
        )
    val uiState = _uiState.asStateFlow()
    private val settings = Settings().makeObservable().toFlowSettings()

    init {
        settings.getStringFlow(SettingKey.Token.name, token).loadInto { copy(token = it) }
        settings.getStringOrNullFlow(SettingKey.IterationCadenceId.name).loadInto { copy(iterationCadenceId = it) }
        settings.getBooleanFlow(SettingKey.DarkTheme.name, systemInDarkTheme).loadInto { copy(darkTheme = it) }
        settings.getBooleanFlow(SettingKey.HighContrastColors.name, false).loadInto { copy(highContrastColors = it) }
        settings.getBooleanFlow(SettingKey.GroupSprintInEpics.name, false).loadInto { copy(groupSprintInEpics = it) }
        settings.getBooleanFlow(SettingKey.ShowLabelsByDefault.name, false).loadInto { copy(showLabelsByDefault = it) }
        settings.getBooleanFlow(SettingKey.UseLabelColors.name, false).loadInto { copy(useLabelColors = it) }
        settings.getStringFlow(
            SettingKey.PinnedIssues.name,
            Json.encodeToString(emptyList<String>())
        ).loadInto { copy(pinnedIssues = Json.decodeFromString(it)) }
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

    fun toggleGroupSprintInEpics() {
        viewModelScope.launch {
            settings.putBoolean(SettingKey.GroupSprintInEpics.name, !uiState.value.groupSprintInEpics)
        }
    }

    fun toggleShowLabelsByDefault() {
        viewModelScope.launch {
            settings.putBoolean(SettingKey.ShowLabelsByDefault.name, !uiState.value.showLabelsByDefault)
        }
    }

    fun toggleUseLabelColors() {
        viewModelScope.launch {
            settings.putBoolean(SettingKey.UseLabelColors.name, !uiState.value.useLabelColors)
        }
    }

    fun setToken(token: String) {
        viewModelScope.launch {
            settings.putString(SettingKey.Token.name, token)
        }
    }

    fun setIterationCadenceId(id: String) {
        viewModelScope.launch {
            settings.putString(SettingKey.IterationCadenceId.name, id)
        }
    }

    fun togglePinIssue(iid: String) {
        viewModelScope.launch {
            val pinned = uiState.value.pinnedIssues.toMutableList()
            if (iid in pinned) {
                pinned.remove(iid)
            } else {
                pinned.add(iid)
            }
            settings.putString(SettingKey.PinnedIssues.name, Json.encodeToString(pinned))
        }
    }

    private inline fun <reified T> Flow<T>.loadInto(crossinline updateUiState: UiState.(T) -> UiState) {
        viewModelScope.launch {
            collect { setting ->
                _uiState.update { it.updateUiState(setting) }
            }
        }
    }
}