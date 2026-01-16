package com.youniqx.time.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.NamespacesRepository
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.gitlab.models.NamespaceQuery
import com.youniqx.time.previewNamespaces
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RemoteNamespacesRepository(
    private val apolloClientFlow: Flow<ApolloClient?>,
    dispatchers: IDispatchers
): NamespacesRepository {

    private val scope = CoroutineScope(dispatchers.IO)

    private val _namespaces: MutableStateFlow<SourceAware<NamespaceQuery.Data?>?> =
        MutableStateFlow(null)

    override val namespaces = _namespaces.asStateFlow()

    init {
        scope.launch {
            apolloClientFlow.filterNotNull().collect { apolloClient ->
                val response = apolloClient.query(NamespaceQuery()).execute()
                _namespaces.update { SourceAware(source = DataSource.Remote, data = response.data, isSyncing = false) }
            }
        }
    }
}
