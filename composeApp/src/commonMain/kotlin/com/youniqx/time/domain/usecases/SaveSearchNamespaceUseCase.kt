package com.youniqx.time.domain.usecases

import com.youniqx.time.domain.models.Namespace

fun interface SaveSearchNamespaceUseCase {
    fun saveSearchNamespace(namespace: Namespace)
}
