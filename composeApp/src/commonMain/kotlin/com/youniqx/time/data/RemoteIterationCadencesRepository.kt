package com.youniqx.time.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.IterationCadencesRepository
import com.youniqx.time.domain.SelectedNamespacesRepository
import com.youniqx.time.domain.models.IterationCadenceMarker
import com.youniqx.time.gitlab.models.IterationCadencesQuery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@AssistedInject
class IterationCadencesPagingSource(
    private val apolloClientFlow: Flow<ApolloClient?>,
    selectedNamespacesRepository: SelectedNamespacesRepository,
    dispatchers: IDispatchers,
    @Assisted val query: String?,
) : PagingSource<String, IterationCadenceMarker.Filled>() {

    private val scope = CoroutineScope(dispatchers.Default)
    private val selectedNamespaceFullPath = selectedNamespacesRepository.selectedNamespaces.map {
        (it.iterationCadence ?: it.search)?.fullPath
    }.distinctUntilChanged()

    init {
        scope.launch {
            val initialNamespaceFullPaths = selectedNamespaceFullPath.firstOrNull()
            selectedNamespaceFullPath.filterNot { it == initialNamespaceFullPaths }.collect {
                invalidate()
            }
        }
    }

    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, IterationCadenceMarker.Filled> {
        try {
            val namespaceFullPath = selectedNamespaceFullPath.firstOrNull() ?: return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
            val apolloClient = apolloClientFlow.filterNotNull().first()
            val query = with(IterationCadencesQuery.Builder()) {
                // This is just set to true here because we need the variable in the fragment
                // which is also used in another query
                initial(true)
                namespaceFullPath(namespaceFullPath)
                iterationCadencesSearch(query.takeUnless { it.isNullOrEmpty() })
                iterationCadencesFirst(params.loadSize)
                when (params) {
                    is LoadParams.Append<*> -> iterationCadencesAfter(params.key)
                    is LoadParams.Prepend<*> -> iterationCadencesBefore(params.key)
                    is LoadParams.Refresh<*> -> {
                        params.key?.let { iterationCadencesAfter(it) }
                    }
                }
                build()
            }
            val response = apolloClient.query(query).execute()
            if (response.errors != null) println(response.errors)
            if (response.exception != null) println(response.exception)
            val group = response.data?.group?.groupWithIterationCadences
            return with(
                group?.iterationCadences
                    ?: return LoadResult.Error(Throwable())
            ) {
                LoadResult.Page(
                    data = nodes.orEmpty().mapNotNull {
                        it?.run {
                            IterationCadenceMarker.Filled(
                                namespaceFullPath = group.fullPath,
                                title = title.orEmpty(),
                                id = id.toString()
                            )
                        }
                    },
                    prevKey = pageInfo.run { startCursor.takeIf { hasPreviousPage } },
                    nextKey = pageInfo.run { endCursor.takeIf { hasNextPage } },
                )
            }
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error for
            // expected errors (such as a network failure).
            println(e.message)
            return LoadResult.Error(Throwable())
        }
    }

    override fun getRefreshKey(state: PagingState<String, IterationCadenceMarker.Filled>): String? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey // Todo: probably not correct
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(
            @Assisted query: String?,
        ): IterationCadencesPagingSource
    }
}

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RemoteIterationCadencesRepository(
    private val iterationCadencesPagingSourceFactory: IterationCadencesPagingSource.Factory,
): IterationCadencesRepository {
    override fun search(search: String) =
        iterationCadencesPagingSourceFactory.create(query = search)
}
