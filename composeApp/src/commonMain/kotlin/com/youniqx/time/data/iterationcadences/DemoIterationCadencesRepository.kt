package com.youniqx.time.data.iterationcadences

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.domain.IterationCadencesRepository
import com.youniqx.time.domain.models.IterationCadenceMarker
import com.youniqx.time.domain.models.IterationCadenceMarker.Filled
import com.youniqx.time.previewIterationCadences
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
class DemoIterationCadencesPagingSource(
    @Assisted val query: String?,
) : PagingSource<String, Filled>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Filled> {
        delay(2.seconds) // artificial loading
        val loadResult = previewIterationCadences.toLoadResult()
        return if (query != null && loadResult is LoadResult.Page) {
            loadResult.copy(
                data =
                    loadResult.data.filter {
                        it.title.contains(query, ignoreCase = true) ||
                            it.namespaceFullPath.contains(query, ignoreCase = true)
                    },
            )
        } else {
            loadResult
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
        ): DemoIterationCadencesPagingSource
    }
}

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class DemoIterationCadencesRepository(
    private val iterationCadencesPagingSourceFactory: DemoIterationCadencesPagingSource.Factory,
) : IterationCadencesRepository {
    override fun search(search: String): Flow<() -> PagingSource<String, Filled>> =
        flowOf(value = { iterationCadencesPagingSourceFactory.create(query = search) })
}
