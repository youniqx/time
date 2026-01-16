package com.youniqx.time.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.youniqx.time.domain.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsApi::class)
interface AppGraph : ViewModelGraph {

    val settingsRepository: SettingsRepository

    @Provides
    @SingleIn(AppScope::class)
    fun provideDispatchers(): IDispatchers = Dispatchers

    @Provides
    @SingleIn(AppScope::class)
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @SingleIn(AppScope::class)
    fun provideFlowSettings(): FlowSettings = Settings().makeObservable().toFlowSettings()
}