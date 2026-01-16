package com.youniqx.time.domain

import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.gitlab.models.NamespaceQuery
import kotlinx.coroutines.flow.StateFlow

interface NamespacesRepository {
    val namespaces: StateFlow<SourceAware<NamespaceQuery.Data?>?>
}