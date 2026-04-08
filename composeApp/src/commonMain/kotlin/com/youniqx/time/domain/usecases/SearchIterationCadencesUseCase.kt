package com.youniqx.time.domain.usecases

import androidx.paging.PagingSource
import com.youniqx.time.domain.models.IterationCadenceMarker
import kotlinx.coroutines.flow.Flow

fun interface SearchIterationCadencesUseCase {
    fun search(search: String): Flow<() -> PagingSource<String, IterationCadenceMarker.Filled>>
}
