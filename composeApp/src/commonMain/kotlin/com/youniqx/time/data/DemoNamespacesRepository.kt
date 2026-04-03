package com.youniqx.time.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.domain.NamespacesRepository
import com.youniqx.time.domain.models.IterationCadenceMarker
import com.youniqx.time.domain.models.NamespaceEntry
import com.youniqx.time.previewNamespaces
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.seconds

@AssistedInject
class DemoNamespacesPagingSource(
    @Assisted val query: String?,
) : PagingSource<String, NamespaceEntry>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, NamespaceEntry> {
        delay(2.seconds) // artificial loading
        return with(previewNamespaces) {
            val namespaceEntries =
                buildList<NamespaceEntry> {
                    addAll(
                        frecentGroups
                            ?.map {
                                it.groupWithIterationCadences.let { group ->
                                    NamespaceEntry.FrecentGroup(
                                        name = group.name,
                                        fullPath = group.fullPath,
                                        iterationCadences =
                                            group.iterationCadences?.nodes?.map { iterationCadence ->
                                                if (iterationCadence?.id != null) {
                                                    IterationCadenceMarker.Filled(
                                                        id = iterationCadence.id.toString(),
                                                        title = iterationCadence.title.orEmpty(),
                                                        namespaceFullPath = group.fullPath,
                                                    )
                                                } else {
                                                    IterationCadenceMarker.PlaceHolder
                                                }
                                            },
                                    )
                                }
                            }.orEmpty(),
                    )
                    currentUser?.namespace?.simpleNamespace?.let {
                        add(
                            NamespaceEntry.User(
                                name = it.name,
                                fullPath = it.fullPath,
                                iterationCadences = null,
                            ),
                        )
                    }
                    addAll(
                        groups
                            ?.nodes
                            ?.mapNotNull {
                                it?.groupWithIterationCadences?.let { group ->
                                    NamespaceEntry.Group(
                                        name = group.name,
                                        fullPath = group.fullPath,
                                        iterationCadences =
                                            group.iterationCadences?.nodes?.map { iterationCadence ->
                                                if (iterationCadence?.id != null) {
                                                    IterationCadenceMarker.Filled(
                                                        id = iterationCadence.id.toString(),
                                                        title = iterationCadence.title.orEmpty(),
                                                        namespaceFullPath = group.fullPath,
                                                    )
                                                } else {
                                                    IterationCadenceMarker.PlaceHolder
                                                }
                                            },
                                    )
                                }
                            }.orEmpty(),
                    )
                }
            LoadResult.Page(
                data = namespaceEntries,
                prevKey = groups?.pageInfo?.run { startCursor.takeIf { hasPreviousPage } },
                nextKey = groups?.pageInfo?.run { endCursor.takeIf { hasNextPage } },
            )
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
        ): DemoNamespacesPagingSource
    }
}

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class DemoNamespacesRepository(
    private val namespacesPagingSourceFactory: DemoNamespacesPagingSource.Factory,
) : NamespacesRepository {
    override fun search(search: String): Flow<() -> PagingSource<String, NamespaceEntry>> =
        flowOf(value = { namespacesPagingSourceFactory.create(query = search) })
}
