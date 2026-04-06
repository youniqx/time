package com.youniqx.time.data.timelogs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.TimelogsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.gitlab.models.TimelogsQuery
import com.youniqx.time.previewTimelogs
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class DemoTimelogsRepository(
    dispatchers: IDispatchers,
) : TimelogsRepository {
    private var job: Job? = null
    private val scope = CoroutineScope(dispatchers.Default)

    private val _timelogs: MutableStateFlow<SourceAware<TimelogsQuery.Data?>?> =
        MutableStateFlow(null)

    override val timelogs = _timelogs.asStateFlow()

    init {
        refresh()
    }

    override fun refresh() {
        job?.cancel()
        job =
            scope.launch {
                delay(2.seconds)
                _timelogs.update {
                    SourceAware(
                        source = DataSource.Remote,
                        data = previewTimelogs,
                        isSyncing = false,
                    )
                }
            }
    }
}
