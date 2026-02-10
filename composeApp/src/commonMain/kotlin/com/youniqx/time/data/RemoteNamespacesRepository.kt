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
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RemoteNamespacesRepository(
    private val apolloClientFlow: Flow<ApolloClient?>,
    dispatchers: IDispatchers
): NamespacesRepository {

    private var job: Job? = null
    private val scope = CoroutineScope(dispatchers.Default)

    private val _namespacesBySearch = MutableStateFlow(emptyMap<String, ApolloResponse<NamespaceQuery.Data>?>())

    override val namespacesBySearch = _namespacesBySearch.asStateFlow()

    init {
        search("")
    }

    override fun search(search: String) {
        job?.cancel()
        job = scope.launch {
            _namespacesBySearch.update {
                if (search !in it.keys) it + (search to null) else it
            }
            delay(300)
            apolloClientFlow.filterNotNull().collect { apolloClient ->
                val query = NamespaceQuery.Builder()
                    .initial(search.isEmpty())
                    .search(search.takeUnless { it.isEmpty() })
                    .build()
                val response: ApolloResponse<NamespaceQuery.Data> = apolloClient.query(query).execute()
                _namespacesBySearch.update {
                    it + (search to response)
                }
            }
        }
    }
}
