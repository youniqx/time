package com.youniqx.time.data.committimetracking

import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.CommitTimeTrackingRepository
import com.youniqx.time.domain.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class DemoCommitTimeTrackingRepository(
    private val settingsRepository: SettingsRepository,
    dispatchers: IDispatchers,
) : CommitTimeTrackingRepository {
    private var job: Job? = null
    private val scope = CoroutineScope(dispatchers.Default)

    override suspend fun commitTimeTracking(): List<String>? {
        if (job?.isActive == true) return null
        job =
            scope.launch {
                delay(3.seconds)
                settingsRepository.setOpenTracking(null)
            }
        job?.join()
        return emptyList()
    }
}
