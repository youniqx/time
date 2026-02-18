package com.youniqx.time.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.domain.NamespacesRepository
import com.youniqx.time.domain.models.NamespaceEntry
import com.youniqx.time.gitlab.models.NamespaceQuery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@AssistedInject
class NamespacesPagingSource(
    private val apolloClientFlow: Flow<ApolloClient?>,
    @Assisted val query: String?,
) : PagingSource<String, NamespaceEntry>() {

    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, NamespaceEntry> {
        try {
            val apolloClient = apolloClientFlow.filterNotNull().first()
            val query = with(NamespaceQuery.Builder()) {
                initial(false)
                search(query.takeUnless { it.isNullOrEmpty() })
                first(params.loadSize)
                when (params) {
                    is LoadParams.Append<*> -> after(params.key)
                    is LoadParams.Prepend<*> -> before(params.key)
                    is LoadParams.Refresh<*> -> {
                        initial(query.isNullOrEmpty())
                        params.key?.let { after(it) }
                    }
                }
                build()
            }
            val response: ApolloResponse<NamespaceQuery.Data> = apolloClient.query(query).execute()
            return with(response.data ?: return LoadResult.Error(Throwable())) {
                val namespaceEntries = buildList<NamespaceEntry> {
                    addAll(
                        frecentGroups?.map {
                            it.groupWithIterationCadences.let { group ->
                                NamespaceEntry.FrecentGroup(
                                    name = group.name,
                                    fullPath = group.fullPath,
                                    iterationCadencesCount = group.iterationCadences?.nodes?.size
                                )
                            }
                        }.orEmpty()
                    )
                    currentUser?.namespace?.simpleNamespace?.let {
                        add(
                            NamespaceEntry.User(
                                name = it.name,
                                fullPath = it.fullPath,
                                iterationCadencesCount = null
                            )
                        )
                    }
                    addAll(
                        groups?.nodes?.mapNotNull {
                            it?.groupWithIterationCadences?.let { group ->
                                NamespaceEntry.Group(
                                    name = group.name,
                                    fullPath = group.fullPath,
                                    iterationCadencesCount = group.iterationCadences?.nodes?.size
                                )
                            }
                        }.orEmpty()
                    )
                }
                LoadResult.Page(
                    data = namespaceEntries,
                    prevKey = groups?.pageInfo?.run { startCursor.takeIf { hasPreviousPage } },
                    nextKey = groups?.pageInfo?.run { endCursor.takeIf { hasNextPage } },
                )
            }
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error for
            // expected errors (such as a network failure).
            return LoadResult.Error(Throwable())
        }
    }

    override fun getRefreshKey(state: PagingState<String, NamespaceEntry>): String? {
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
        ): NamespacesPagingSource
    }
}

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RemoteNamespacesRepository(
    private val namespacesPagingSourceFactory: NamespacesPagingSource.Factory,
): NamespacesRepository {
    override fun search(search: String) =
        namespacesPagingSourceFactory.create(query = search)
}
