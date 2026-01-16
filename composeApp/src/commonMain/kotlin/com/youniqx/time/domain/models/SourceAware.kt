package com.youniqx.time.domain.models

data class SourceAware<T>(
    val data: T,
    val source: DataSource,
    val isSyncing: Boolean,
)

fun <T> SourceAware<T>.dataIfNotFrom(excludedSource: DataSource): T? {
    return data.takeIf { source != excludedSource }
}