package com.youniqx.time.domain.models

import com.youniqx.time.gitlab.models.fragment.SimpleNamespace

sealed interface IterationCadenceMarker {
    data object PlaceHolder : IterationCadenceMarker

    data class Filled(
        val namespaceFullPath: String,
        val title: String,
        val id: String,
    ) : IterationCadenceMarker
}

interface Namespace {
    val name: String?
    val fullPath: String
    val iterationCadences: List<IterationCadenceMarker>?
}

data class NamespaceImpl(
    override val name: String?,
    override val fullPath: String,
    override val iterationCadences: List<IterationCadenceMarker>?,
) : Namespace

fun SimpleNamespace.toNamespace() =
    NamespaceImpl(
        name = this@toNamespace.name,
        fullPath = this@toNamespace.fullPath,
        iterationCadences = null,
    )
