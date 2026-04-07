package com.youniqx.time.data.workitems

import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.WorkItemsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.domain.models.WorkItemsFromCurrentUser
import com.youniqx.time.previewIssues
import com.youniqx.time.previewUserId
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
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class DemoWorkItemsRepository(
    dispatchers: IDispatchers,
) : WorkItemsRepository {
    private var job: Job? = null
    private val scope = CoroutineScope(dispatchers.Default)

    private val _workItemsFromCurrentUser: MutableStateFlow<SourceAware<WorkItemsFromCurrentUser?>?> =
        MutableStateFlow(null)

    override val workItemsFromCurrentUser = _workItemsFromCurrentUser.asStateFlow()

    init {
        search("")
    }

    override fun search(
        search: String,
        setSyncing: Boolean,
    ) {
        job?.cancel()
        job =
            scope.launch {
                delay(300)
                if (setSyncing) _workItemsFromCurrentUser.update { it?.copy(isSyncing = true) }
                delay(2.seconds)
                val workItems =
                    if (search.isEmpty()) {
                        previewIssues
                    } else {
                        previewIssues.filter {
                            it.title.contains(search, true) ||
                                it.iid.contains(search, true)
                        }
                    }
                _workItemsFromCurrentUser.update {
                    SourceAware(
                        data =
                            WorkItemsFromCurrentUser(
                                currentUserId = previewUserId,
                                workItems = workItems,
                                lastUpdated = Clock.System.now(),
                            ),
                        source = DataSource.Remote,
                        isSyncing = false,
                    )
                }
            }
    }
}
