package com.youniqx.time.domain

import com.apollographql.apollo.api.ApolloResponse
import com.youniqx.time.domain.usecases.SearchNamespacesUseCase
import com.youniqx.time.gitlab.models.NamespaceQuery
import kotlinx.coroutines.flow.StateFlow

interface NamespacesRepository : SearchNamespacesUseCase {
    val namespacesBySearch: StateFlow<Map<String, ApolloResponse<NamespaceQuery.Data>?>>
}