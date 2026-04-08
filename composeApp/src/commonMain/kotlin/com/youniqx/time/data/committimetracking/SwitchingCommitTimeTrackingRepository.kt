package com.youniqx.time.data.committimetracking

import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.data.LocalSettingsRepository
import com.youniqx.time.domain.CommitTimeTrackingRepository
import com.youniqx.time.domain.demoModeIsActive
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
@ContributesBinding(
    AppScope::class,
    replaces = [RemoteCommitTimeTrackingRepository::class, DemoCommitTimeTrackingRepository::class],
)
@SingleIn(AppScope::class)
class SwitchingCommitTimeTrackingRepository(
    private val localSettingsRepository: LocalSettingsRepository,
    private val remoteCommitTimeTrackingRepository: RemoteCommitTimeTrackingRepository,
    private val demoCommitTimeTrackingRepository: DemoCommitTimeTrackingRepository,
) : CommitTimeTrackingRepository {
    override suspend fun commitTimeTracking(): List<String>? =
        localSettingsRepository.settings.value.let {
            val delegate =
                if (it.demoModeIsActive) demoCommitTimeTrackingRepository else remoteCommitTimeTrackingRepository
            delegate.commitTimeTracking()
        }
}
