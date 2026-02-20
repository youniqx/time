package com.youniqx.time.domain.usecases

import com.youniqx.time.domain.models.Namespace

fun interface SaveIterationCadenceNamespaceUseCase {
    fun saveIterationCadenceNamespace(namespace: Namespace)
}
