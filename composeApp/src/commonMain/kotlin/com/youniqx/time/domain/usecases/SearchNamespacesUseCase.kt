package com.youniqx.time.domain.usecases

import androidx.paging.PagingSource
import com.youniqx.time.domain.models.NamespaceEntry
import kotlinx.coroutines.flow.Flow

fun interface SearchNamespacesUseCase {
    fun search(search: String): Flow<() -> PagingSource<String, NamespaceEntry>>
}
