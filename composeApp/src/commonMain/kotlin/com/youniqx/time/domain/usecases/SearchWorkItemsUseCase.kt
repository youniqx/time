package com.youniqx.time.domain.usecases

interface SearchWorkItemsUseCase {
    fun search(search: String, setSyncing: Boolean = true)
}