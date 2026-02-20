package com.youniqx.time.domain.usecases

interface CommitTimeTrackingUseCase {
    suspend fun commitTimeTracking(): List<String>?
}
