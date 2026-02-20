package com.youniqx.time.data

import com.youniqx.time.domain.NamespacesRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.gitlab.models.NamespaceQuery
import com.youniqx.time.previewNamespaces
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Todo: figure out how to easily swap these in
// @ContributesBinding(AppScope::class)
// @SingleIn(AppScope::class)
// class PreviewNamespacesRepository : NamespacesRepository {
//    override val namespaces = MutableStateFlow(
//        SourceAware<NamespaceQuery.Data?>(
//            data = previewNamespaces, source = DataSource.Remote
//        )
//    ).asStateFlow()
// }
