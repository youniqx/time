package com.youniqx.time.di

import androidx.compose.runtime.remember
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.api.CacheKey
import com.apollographql.apollo.cache.normalized.api.CacheKeyGenerator
import com.apollographql.apollo.cache.normalized.api.CacheKeyGeneratorContext
import com.apollographql.apollo.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo.cache.normalized.normalizedCache
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.youniqx.time.data.cacheKeyGenerator
import com.youniqx.time.data.toApolloClientOrNull
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import io.ktor.http.appendPathSegments
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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

    @Provides
    fun apolloClientFlow(): Flow<ApolloClient?> = settingsRepository.settings.distinctUntilChanged { old, new ->
        old.data.instanceUrl == new.data.instanceUrl && old.data.token == new.data.token
    }.map { it.toApolloClientOrNull() }
}