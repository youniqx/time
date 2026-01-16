package com.youniqx.time.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.api.CacheKey
import com.apollographql.apollo.cache.normalized.api.CacheKeyGenerator
import com.apollographql.apollo.cache.normalized.api.CacheKeyGeneratorContext
import com.apollographql.apollo.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo.cache.normalized.normalizedCache
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.Settings
import com.youniqx.time.domain.models.SourceAware
import io.ktor.http.appendPathSegments
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom

fun SourceAware<Settings>.toApolloClientOrNull(): ApolloClient? {
    if (source == DataSource.Default) return null
    val settings = data
    val instanceUrl = settings.instanceUrl ?: return null
    val token = settings.token ?: return null
    val cacheFactory = MemoryCacheFactory(maxSizeBytes = 30 * 1024 * 1024)
    return ApolloClient.Builder()
        .serverUrl(
            buildUrl {
                takeFrom(instanceUrl)
                appendPathSegments("api", "graphql")
            }.toString()
        )
        .addHttpHeader("Authorization", "Bearer $token")
        .normalizedCache(
            normalizedCacheFactory = cacheFactory,
            cacheKeyGenerator = cacheKeyGenerator
        )
        .build()
}

val cacheKeyGenerator = object : CacheKeyGenerator {
    override fun cacheKeyForObject(obj: Map<String, Any?>, context: CacheKeyGeneratorContext): CacheKey? {
        // Generate the cache ID based on the object's id field
        return (obj["id"] as? String)?.let(::CacheKey)
    }
}
