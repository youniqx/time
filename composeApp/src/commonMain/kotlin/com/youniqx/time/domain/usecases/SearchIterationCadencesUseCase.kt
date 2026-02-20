package com.youniqx.time.domain.usecases

import androidx.paging.PagingSource
import com.youniqx.time.domain.models.IterationCadenceMarker
import com.youniqx.time.domain.models.NamespaceEntry

fun interface SearchIterationCadencesUseCase {
    fun search(search: String): PagingSource<String, IterationCadenceMarker.Filled>
}
