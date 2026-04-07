package com.youniqx.time.data.workitems

import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.data.LocalSettingsRepository
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.WorkItemsRepository
import com.youniqx.time.domain.demoModeIsActive
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.models.WorkItemsFromCurrentUser
import com.youniqx.time.gitlab.models.WorkItemsQuery
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
    replaces = [RemoteWorkItemsRepository::class, DemoWorkItemsRepository::class],
)
@SingleIn(AppScope::class)
class SwitchingWorkItemsRepository(
    localSettingsRepository: LocalSettingsRepository,
    private val remoteWorkItemsRepository: RemoteWorkItemsRepository,
    private val demoWorkItemsRepository: DemoWorkItemsRepository,
    dispatchers: IDispatchers,
) : WorkItemsRepository {
    private val scope = CoroutineScope(dispatchers.Default)

    private val _workItemsFromCurrentUser: MutableStateFlow<SourceAware<WorkItemsFromCurrentUser?>?> =
        MutableStateFlow(null)

    private val delegate =
        localSettingsRepository.settings
            .mapLatest {
                val delegate =
                    if (it.demoModeIsActive) demoWorkItemsRepository else remoteWorkItemsRepository
                delegate
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = remoteWorkItemsRepository,
            )

    override val workItemsFromCurrentUser = _workItemsFromCurrentUser.asStateFlow()

    init {
        scope.launch {
            delegate.collectLatest {
                it.workItemsFromCurrentUser.collectLatest { workItemsFromCurrentUser ->
                    _workItemsFromCurrentUser.value = workItemsFromCurrentUser
                }
            }
        }
    }

    override fun search(
        search: String,
        setSyncing: Boolean,
    ) {
        delegate.value.search(search, setSyncing)
    }
}
