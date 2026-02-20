package com.youniqx.time.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.api.NormalizedCache
import com.apollographql.apollo.cache.normalized.apolloStore
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.apollographql.apollo.cache.normalized.watch
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.SelectedNamespacesRepository
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.Namespace
import com.youniqx.time.domain.models.SelectedNamespaces
import com.youniqx.time.domain.models.toNamespace
import com.youniqx.time.domain.selectedNamespacesFullPaths
import com.youniqx.time.gitlab.models.SelectedNamespacesQuery
import com.youniqx.time.gitlab.models.fragment.SimpleNamespace
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RemoteSelectedNamespacesRepository(
    private val apolloClientFlow: Flow<ApolloClient?>,
    private val settingsRepository: SettingsRepository,
    dispatchers: IDispatchers,
) : SelectedNamespacesRepository {
    private val scope = CoroutineScope(dispatchers.Default)

    private val _selectedNamespaces = MutableStateFlow(SelectedNamespaces(search = null, iterationCadence = null))
    override val selectedNamespaces = _selectedNamespaces.asStateFlow()

    init {
        scope.launch {
            val selectedNamespacesFullPathsFlow = settingsRepository.settings.mapNotNull {
                it.takeIf { it.source != DataSource.Default }?.selectedNamespacesFullPaths
            }
            apolloClientFlow.filterNotNull().combine(selectedNamespacesFullPathsFlow, ::Pair)
                .collectLatest { (apolloClient, selectedNamespacesFullPaths) ->
                    val query = with(SelectedNamespacesQuery.Builder()) {
                        fetchNamespace(false)
                        fetchIterationCadenceNamespace(false)
                        selectedNamespacesFullPaths.run {
                            search?.let {
                                fetchNamespace(true)
                                namespaceFullPath(it)
                            }
                            iterationCadence?.let {
                                fetchIterationCadenceNamespace(true)
                                iterationCadenceNamespaceFullPath(it)
                            }
                        }
                        build()
                    }
                    apolloClient.query(query).watch().collect { response ->
                        with(response.data ?: return@collect) {
                            _selectedNamespaces.update {
                                SelectedNamespaces(
                                    search = namespace?.simpleNamespace?.toNamespace(),
                                    iterationCadence = iterationCadenceNamespace?.simpleNamespace?.toNamespace()
                                )
                            }
                        }
                    }
            }
        }
    }

    override fun saveSearchNamespace(namespace: Namespace) {
        _selectedNamespaces.update { it.copy(search = namespace) }
        settingsRepository.setNamespaceFullPath(namespace.fullPath)
    }

    override fun saveIterationCadenceNamespace(namespace: Namespace) {
        _selectedNamespaces.update { it.copy(iterationCadence = namespace) }
        // settingsRepository.setIterationCadence(namespace.fullPath) // Todo
    }
}
