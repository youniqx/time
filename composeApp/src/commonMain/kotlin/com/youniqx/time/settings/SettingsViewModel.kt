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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class UiState(
    val settingsLoaded: Boolean,
    val instanceUrl: String?,
    val token: String?,
    val iterationCadenceId: String?,
    val darkTheme: Boolean,
    val highContrastColors: Boolean,
    val groupSprintInEpics: Boolean,
    val showLabelsByDefault: Boolean,
    val useLabelColors: Boolean,
    val showMenuBarTimer: Boolean,
    val pinnedIssues: List<String>,
    val openTracking: OpenTracking?,
)

@OptIn(ExperimentalTime::class)
@Serializable
data class OpenTracking(
    val workItemId: String,
    val workItemTitle: String? = null,
    val summary: String? = null,
    val timeOfOpen: Instant,
    val customTimeSpent: String? = null,
)

private enum class SettingKey {
    InstanceUrl,
    Token,
    IterationCadenceId,
    DarkTheme,
    HighContrastColors,
    GroupSprintInEpics,
    ShowLabelsByDefault,
    UseLabelColors,
    ShowMenuBarTimer,
    PinnedIssues,
    OpenTracking,
    OnboardingCompleted,
}

@OptIn(ExperimentalSettingsApi::class)
class SettingsViewModel(systemInDarkTheme: Boolean) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            UiState(
                settingsLoaded = false,
                instanceUrl = null,
                token = null,
                iterationCadenceId = null,
                darkTheme = systemInDarkTheme,
                highContrastColors = false,
                groupSprintInEpics = false,
                showLabelsByDefault = false,
                useLabelColors = false,
                showMenuBarTimer = true,
                pinnedIssues = emptyList(),
                openTracking = null,
            )
        )
    val uiState = _uiState.asStateFlow()
    private val settings = Settings().makeObservable().toFlowSettings()
    private val json = Json { ignoreUnknownKeys = true }

    init {
        settings.getStringOrNullFlow(SettingKey.InstanceUrl.name).loadInto { copy(settingsLoaded = true, instanceUrl = it) }
        settings.getStringOrNullFlow(SettingKey.Token.name).loadInto { copy(token = it) }
        settings.getStringOrNullFlow(SettingKey.IterationCadenceId.name).loadInto { copy(iterationCadenceId = it) }
        settings.getBooleanFlow(SettingKey.DarkTheme.name, systemInDarkTheme).loadInto { copy(darkTheme = it) }
        settings.getBooleanFlow(SettingKey.HighContrastColors.name, false).loadInto { copy(highContrastColors = it) }
        settings.getBooleanFlow(SettingKey.GroupSprintInEpics.name, false).loadInto { copy(groupSprintInEpics = it) }
        settings.getBooleanFlow(SettingKey.ShowLabelsByDefault.name, false).loadInto { copy(showLabelsByDefault = it) }
        settings.getBooleanFlow(SettingKey.UseLabelColors.name, false).loadInto { copy(useLabelColors = it) }
        settings.getBooleanFlow(SettingKey.ShowMenuBarTimer.name, true).loadInto { copy(showMenuBarTimer = it) }
        settings.getStringFlow(
            SettingKey.PinnedIssues.name,
            json.encodeToString(emptyList<String>())
        ).loadInto { copy(pinnedIssues = json.decodeFromString<List<String>>(it).filter(isGlobalId)) }
        settings.getStringOrNullFlow(SettingKey.OpenTracking.name)
            .loadInto { copy(openTracking = it?.let { json.decodeFromString(it) }) }
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

    fun toggleShowMenuBarTimer() {
        viewModelScope.launch {
            settings.putBoolean(SettingKey.ShowMenuBarTimer.name, !uiState.value.showMenuBarTimer)
        }
    }

    fun setInstanceUrl(instanceUrl: String) {
        _uiState.update { it.copy(instanceUrl = instanceUrl) } // optimistic ui
        viewModelScope.launch {
            settings.putString(SettingKey.InstanceUrl.name, instanceUrl)
        }
    }

    fun setToken(token: String) {
        _uiState.update { it.copy(token = token) } // optimistic ui
        viewModelScope.launch {
            settings.putString(SettingKey.Token.name, token)
        }
    }

    fun setIterationCadenceId(id: String) {
        viewModelScope.launch {
            settings.putString(SettingKey.IterationCadenceId.name, id)
        }
    }

    fun togglePinIssue(id: String) {
        viewModelScope.launch {
            val pinned = uiState.value.pinnedIssues.toMutableList()
            if (id in pinned) {
                pinned.remove(id)
            } else {
                pinned.add(id)
            }
            settings.putString(SettingKey.PinnedIssues.name, json.encodeToString(pinned))
        }
    }

    fun setOpenTracking(openTracking: OpenTracking?) {
        viewModelScope.launch {
            _uiState.update { it.copy(openTracking = openTracking) } // optimistic ui
            if (openTracking == null) {
                settings.remove(SettingKey.OpenTracking.name)
            } else {
                settings.putString(SettingKey.OpenTracking.name, json.encodeToString(openTracking))
            }
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

private val isGlobalId = { id: String -> id.startsWith("gid:") }