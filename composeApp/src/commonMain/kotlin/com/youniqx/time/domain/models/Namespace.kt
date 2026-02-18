package com.youniqx.time.domain.models

import com.youniqx.time.gitlab.models.fragment.SimpleNamespace

interface Namespace {
    val name: String?
    val fullPath: String
    val iterationCadencesCount: Int?
}

fun SimpleNamespace.toNamespace() = object : Namespace {
    override val name = this@toNamespace.name
    override val fullPath = this@toNamespace.fullPath
    override val iterationCadencesCount = null
}
