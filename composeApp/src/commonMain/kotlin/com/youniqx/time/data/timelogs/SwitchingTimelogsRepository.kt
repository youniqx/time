package com.youniqx.time.data.timelogs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.data.LocalSettingsRepository
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.TimelogsRepository
import com.youniqx.time.domain.demoModeIsActive
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.gitlab.models.TimelogsQuery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
@ContributesBinding(
    AppScope::class,
    replaces = [RemoteTimelogsRepository::class, DemoTimelogsRepository::class],
)
@SingleIn(AppScope::class)
class SwitchingTimelogsRepository(
    localSettingsRepository: LocalSettingsRepository,
    private val remoteTimelogsRepository: RemoteTimelogsRepository,
    private val demoTimelogsRepository: DemoTimelogsRepository,
    dispatchers: IDispatchers,
) : TimelogsRepository {
    private val scope = CoroutineScope(dispatchers.Default)

    private val _timelogs: MutableStateFlow<SourceAware<TimelogsQuery.Data?>?> =
        MutableStateFlow(null)

    private val delegate =
        localSettingsRepository.settings
            .mapLatest {
                val delegate =
                    if (it.demoModeIsActive) demoTimelogsRepository else remoteTimelogsRepository
                delegate
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = remoteTimelogsRepository,
            )

    override val timelogs = _timelogs.asStateFlow()

    init {
        scope.launch {
            delegate.collectLatest {
                it.timelogs.collectLatest { timelogs ->
                    _timelogs.value = timelogs
                }
            }
        }
    }

    override fun refresh() {
        delegate.value.refresh()
    }
}
