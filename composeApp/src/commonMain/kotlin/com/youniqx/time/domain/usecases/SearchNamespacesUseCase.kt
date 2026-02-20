package com.youniqx.time.domain.usecases

import androidx.paging.PagingSource
import com.youniqx.time.domain.models.NamespaceEntry

fun interface SearchNamespacesUseCase {
    fun search(search: String): PagingSource<String, NamespaceEntry>
}
