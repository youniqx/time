@file:Suppress("ktlint:standard:filename")

package com.youniqx.time.di

import com.apollographql.apollo.ApolloClient
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.youniqx.time.data.toApolloClientOrNull
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.presentation.navigation.NavScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsApi::class)
interface AppGraph : ViewModelGraph {
    val settingsRepository: SettingsRepository

    @Multibinds
    val navScopes: Set<NavScope>

    @Provides
    @SingleIn(AppScope::class)
    fun provideDispatchers(): IDispatchers = Dispatchers

    @Provides
    @SingleIn(AppScope::class)
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @SingleIn(AppScope::class)
    fun provideFlowSettings(): FlowSettings = Settings().makeObservable().toFlowSettings()

    @Provides
    @SingleIn(AppScope::class)
    fun apolloClientFlow(dispatchers: IDispatchers): Flow<ApolloClient?> =
        settingsRepository.settings
            .distinctUntilChanged { old, new ->
                old.data.instanceUrl == new.data.instanceUrl && old.data.token == new.data.token
            }.map { it.toApolloClientOrNull() }
            .stateIn(
                scope = CoroutineScope(dispatchers.Default),
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
}
