package com.youniqx.time.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class IterationCadence(
    val namespaceFullPath: String,
    val id: String? = null,
)
