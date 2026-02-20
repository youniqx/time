package com.youniqx.time.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.apollographql.apollo.cache.normalized.watch
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.WorkItemsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.models.WorkItemsFromCurrentUser
import com.youniqx.time.domain.models.dataIfNotFrom
import com.youniqx.time.gitlab.models.WorkItemsQuery
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RemoteWorkItemsRepository(
    private val apolloClientFlow: Flow<ApolloClient?>,
    private val settingsRepository: SettingsRepository,
    dispatchers: IDispatchers
): WorkItemsRepository {

    private var job: Job? = null
    private val scope = CoroutineScope(dispatchers.Default)

    private val _workItemsFromCurrentUser: MutableStateFlow<SourceAware<WorkItemsFromCurrentUser?>?> =
        MutableStateFlow(null)

    override val workItemsFromCurrentUser = _workItemsFromCurrentUser.asStateFlow()

    init {
        search("")
    }

    override fun search(search: String, setSyncing: Boolean) {
        job?.cancel()
        job = scope.launch {
            delay(300)
            if (setSyncing) _workItemsFromCurrentUser.update { it?.copy(isSyncing = true) }
            val settingsFlow = settingsRepository.settings.map { it.dataIfNotFrom(DataSource.Default) }
                .filterNotNull()
            apolloClientFlow.filterNotNull().combine(settingsFlow, ::Pair)
                .collectLatest { (apolloClient, settings) ->
                val namespaceFullPath = settings.namespaceFullPath ?: return@collectLatest
                val iterationCadenceNamespaceFullPath = settings.iterationCadence?.namespaceFullPath
                    ?: return@collectLatest
                if (search.isNotEmpty()) delay(300)
                val pinnedPlusOpen = settings.pinnedWorkItems +
                        (settings.openTracking?.let { listOf(it.workItemId) } ?: emptyList())
                val query = WorkItemsQuery.Builder()
                    .namespaceFullPath(namespaceFullPath)
                    .iterationCadenceNamespaceFullPath(iterationCadenceNamespaceFullPath)
                    .iterationCadenceId(settings.iterationCadence.id?.let { listOf(it) } ?: emptyList())
                    .pinnedIds(pinnedPlusOpen)
                    // skip when searching to reduce query complexity
                    .doPinnedSearch(pinnedPlusOpen.isNotEmpty() && search.isBlank())
                    .search(search)
                    .doSearch(search.isNotBlank())
                    .build()
                apolloClient.query(query).fetchPolicy(FetchPolicy.NetworkFirst).watch().collect { result ->
                    if (result.errors != null) println(result.errors)
                    if (result.exception != null) println(result.exception)
                    _workItemsFromCurrentUser.update {
                        SourceAware(
                            data = WorkItemsFromCurrentUser(
                                currentUserId = result.data?.currentUser?.id.toString(),
                                workItems = result.extractIssues(
                                    groupSprintInEpics = settings.groupSprintInEpics,
                                ),
                                lastUpdated = Clock.System.now()
                            ),
                            source = DataSource.Remote,
                            isSyncing = false,
                        )
                    }
                }
            }
        }
    }
}

@Suppress("DEPRECATION") // experimental api
private fun ApolloResponse<WorkItemsQuery.Data>.extractIssues(
    groupSprintInEpics: Boolean,
): List<BareWorkItem> {
    val searchNamespace = data?.searchNamespace
    return buildList {
        addAll(data?.iterationCadenceNamespace?.workItems?.nodes?.map {
            if (groupSprintInEpics) {
                it?.parent ?: it?.bareWorkItem
            } else {
                it?.bareWorkItem
            }
        }.orEmpty())
        addAll(searchNamespace?.pinned?.nodes?.map { it?.bareWorkItem }.orEmpty())
        addAll(searchNamespace?.searchIid?.nodes?.map { it?.bareWorkItem }.orEmpty())
        addAll(searchNamespace?.search?.nodes?.map { it?.bareWorkItem }.orEmpty())
    }.filterNotNull().distinctBy { it.id }.sortedByDescending { it.state.name }
}

val WorkItemsQuery.Node.parent get() = this.widgets?.firstOrNull { it.onWorkItemWidgetHierarchy != null }
    ?.onWorkItemWidgetHierarchy?.parent?.bareWorkItem
