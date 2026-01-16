package com.youniqx.time.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.IterationCadence
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.models.SourceAware
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

val defaultSettings = Settings(
    instanceUrl = null,
    token = null,
    namespaceFullPath = null,
    iterationCadence = null,
    darkTheme = true,
    highContrastColors = false,
    groupSprintInEpics = false,
    showLabelsByDefault = false,
    useLabelColors = false,
    showMenuBarTimer = true,
    pinnedIssues = emptyList(),
    openTracking = null,
)

@OptIn(ExperimentalSettingsApi::class)
@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class LocalSettingsRepository(
    private val flowSettings: FlowSettings,
    private val json: Json,
    dispatchers: IDispatchers,
): SettingsRepository {

    private val _settings =
        MutableStateFlow(
            SourceAware(
                data = defaultSettings,
                source = DataSource.Default
            )
        )
    override val settings = _settings.asStateFlow()

    val scope = CoroutineScope(dispatchers.IO)

    init {
        flowSettings.getStringOrNullFlow(SettingKey.InstanceUrl.name).loadInto { copy(instanceUrl = it) }
        flowSettings.getStringOrNullFlow(SettingKey.Token.name).loadInto { copy(token = it) }
        flowSettings.getStringOrNullFlow(SettingKey.NamespaceFullPath.name).loadInto { copy(namespaceFullPath = it) }
        flowSettings.getStringOrNullFlow(SettingKey.IterationCadence.name)
            .loadInto { copy(iterationCadence = it?.let { json.decodeFromString(it) }) }
        flowSettings.getBooleanFlow(SettingKey.DarkTheme.name, true).loadInto { copy(darkTheme = it) }
        flowSettings.getBooleanFlow(SettingKey.HighContrastColors.name, false).loadInto { copy(highContrastColors = it) }
        flowSettings.getBooleanFlow(SettingKey.GroupSprintInEpics.name, false).loadInto { copy(groupSprintInEpics = it) }
        flowSettings.getBooleanFlow(SettingKey.ShowLabelsByDefault.name, false).loadInto { copy(showLabelsByDefault = it) }
        flowSettings.getBooleanFlow(SettingKey.UseLabelColors.name, false).loadInto { copy(useLabelColors = it) }
        flowSettings.getBooleanFlow(SettingKey.ShowMenuBarTimer.name, true).loadInto { copy(showMenuBarTimer = it) }
        flowSettings.getStringFlow(
            SettingKey.PinnedIssues.name,
            json.encodeToString(emptyList<String>())
        ).loadInto { copy(pinnedIssues = json.decodeFromString<List<String>>(it).filter(isGlobalId)) }
        flowSettings.getStringOrNullFlow(SettingKey.OpenTracking.name)
            .loadInto { copy(openTracking = it?.let { json.decodeFromString(it) }) }
    }

    override fun toggleDarkTheme() {
        val newValue = !settings.value.data.darkTheme
        updateSettingsData { copy(darkTheme = newValue) } // optimistic ui
        scope.launch {
            flowSettings.putBoolean(SettingKey.DarkTheme.name, newValue)
        }
    }

    override fun toggleHighContrastColors() {
        val newValue = !settings.value.data.highContrastColors
        updateSettingsData { copy(highContrastColors = newValue) } // optimistic ui
        scope.launch {
            flowSettings.putBoolean(SettingKey.HighContrastColors.name, newValue)
        }
    }

    override fun toggleGroupSprintInEpics() {
        val newValue = !settings.value.data.groupSprintInEpics
        updateSettingsData { copy(groupSprintInEpics = newValue) } // optimistic ui
        scope.launch {
            flowSettings.putBoolean(SettingKey.GroupSprintInEpics.name, newValue)
        }
    }

    override fun toggleShowLabelsByDefault() {
        val newValue = !settings.value.data.showLabelsByDefault
        updateSettingsData { copy(showLabelsByDefault = newValue) } // optimistic ui
        scope.launch {
            flowSettings.putBoolean(SettingKey.ShowLabelsByDefault.name, newValue)
        }
    }

    override fun toggleUseLabelColors() {
        val newValue = !settings.value.data.useLabelColors
        updateSettingsData { copy(useLabelColors = newValue) } // optimistic ui
        scope.launch {
            flowSettings.putBoolean(SettingKey.UseLabelColors.name, newValue)
        }
    }

    override fun toggleShowMenuBarTimer() {
        scope.launch {
            flowSettings.putBoolean(SettingKey.ShowMenuBarTimer.name, !settings.value.data.showMenuBarTimer)
        }
    }

    override fun setInstanceUrl(instanceUrl: String) {
        updateSettingsData { copy(instanceUrl = instanceUrl) } // optimistic ui
        scope.launch {
            flowSettings.putString(SettingKey.InstanceUrl.name, instanceUrl)
        }
    }

    override fun setToken(token: String) {
        updateSettingsData { copy(token = token) } // optimistic ui
        scope.launch {
            flowSettings.putString(SettingKey.Token.name, token)
        }
    }

    override fun setNamespaceFullPath(fullPath: String) {
        updateSettingsData { copy(namespaceFullPath = fullPath) } // optimistic ui
        scope.launch {
            flowSettings.putString(SettingKey.NamespaceFullPath.name, fullPath)
        }
    }

    override fun setIterationCadence(iterationCadence: IterationCadence?) {
        updateSettingsData { copy(iterationCadence = iterationCadence) } // optimistic ui
        scope.launch {
            if (iterationCadence == null) {
                flowSettings.remove(SettingKey.IterationCadence.name)
            } else {
                flowSettings.putString(SettingKey.IterationCadence.name, json.encodeToString(iterationCadence))
            }
        }
    }

    override fun togglePinIssue(id: String) {
        scope.launch {
            val pinned = settings.value.data.pinnedIssues.toMutableList()
            if (id in pinned) {
                pinned.remove(id)
            } else {
                pinned.add(id)
            }
            flowSettings.putString(SettingKey.PinnedIssues.name, json.encodeToString(pinned))
        }
    }

    override fun setOpenTracking(openTracking: OpenTracking?) {
        scope.launch {
            updateSettingsData { copy(openTracking = openTracking) } // optimistic ui
            if (openTracking == null) {
                flowSettings.remove(SettingKey.OpenTracking.name)
            } else {
                flowSettings.putString(SettingKey.OpenTracking.name, json.encodeToString(openTracking))
            }
        }
    }
    
    private inline fun updateSettingsData(update: Settings.() -> Settings) {
        _settings.update { it.copy(data = it.data.update()) }
    }

    private inline fun <reified T> Flow<T>.loadInto(crossinline updateSettings: Settings.(T) -> Settings) {
        scope.launch {
            collect { setting ->
                _settings.update { it.copy(data = it.data.updateSettings(setting), source = DataSource.Local) }
            }
        }
    }
}

private val isGlobalId = { id: String -> id.startsWith("gid:") }
