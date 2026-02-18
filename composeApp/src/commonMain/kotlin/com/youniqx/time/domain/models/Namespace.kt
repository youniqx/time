package com.youniqx.time.domain.models

import com.youniqx.time.gitlab.models.fragment.SimpleNamespace

sealed interface IterationCadenceMarker {
    data object PlaceHolder : IterationCadenceMarker
    data class Filled(
        val namespaceFullPath: String,
        val title: String,
        val id: String,
    ): IterationCadenceMarker
}

interface Namespace {
    val name: String?
    val fullPath: String
    val iterationCadences: List<IterationCadenceMarker>?
}

fun SimpleNamespace.toNamespace() = object : Namespace {
    override val name = this@toNamespace.name
    override val fullPath = this@toNamespace.fullPath
    override val iterationCadences: List<IterationCadenceMarker>? = null
}
