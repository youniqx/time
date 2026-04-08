package com.youniqx.time.data.workitems

import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.data.LocalSettingsRepository
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.WorkItemsRepository
import com.youniqx.time.domain.demoModeIsActive
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
@ContributesBinding(
    AppScope::class,
    replaces = [RemoteWorkItemsRepository::class, DemoWorkItemsRepository::class],
)
@SingleIn(AppScope::class)
class SwitchingWorkItemsRepository(
    private val localSettingsRepository: LocalSettingsRepository,
    private val remoteWorkItemsRepository: RemoteWorkItemsRepository,
    private val demoWorkItemsRepository: DemoWorkItemsRepository,
    dispatchers: IDispatchers,
) : WorkItemsRepository {
    private val scope = CoroutineScope(dispatchers.Default)

    override val workItemsFromCurrentUser =
        localSettingsRepository.settings
            .flatMapLatest {
                val delegate =
                    if (it.demoModeIsActive) demoWorkItemsRepository else remoteWorkItemsRepository
                delegate.workItemsFromCurrentUser
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = null,
            )

    override fun search(
        search: String,
        setSyncing: Boolean,
    ) {
        localSettingsRepository.settings.value.let {
            val delegate =
                if (it.demoModeIsActive) demoWorkItemsRepository else remoteWorkItemsRepository
            delegate.search(search, setSyncing)
        }
    }
}
