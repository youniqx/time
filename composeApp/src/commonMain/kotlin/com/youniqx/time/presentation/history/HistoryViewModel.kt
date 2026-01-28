package com.youniqx.time.presentation.history

import androidx.lifecycle.ViewModel
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.TimelogsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.gitlab.models.TimelogsQuery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Instant

data class UiState(
    val loading: Boolean,
    val timelogs: List<TimelogEntry>,
)

@ViewModelKey(HistoryViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class HistoryViewModel(
    timelogsRepository: TimelogsRepository,
    dispatchers: IDispatchers,
) : ViewModel() {
    val uiState = timelogsRepository.timelogs
        .map { it.toUiState() }
        .stateIn(
            scope = CoroutineScope(dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = timelogsRepository.timelogs.value.toUiState()
        )

    private fun SourceAware<TimelogsQuery.Data?>?.toUiState(): UiState {
        return UiState(
            loading = this?.source != DataSource.Remote,
            timelogs = this?.data?.currentUser?.timelogs?.nodes.orEmpty()
                        .mapNotNull { timelog -> timelog?.toTimelogEntry() },
        )
    }
}

data class TimelogEntry(
    val id: String,
    val spentAt: Instant,
    val summary: String?,
    val timeSpent: Int, // in seconds
    val workItemTitle: String?,
    val workItemUrl: String?,
    val workItemId: String?,
    val workItemIid: String?,
)

fun TimelogsQuery.Node.toTimelogEntry(): TimelogEntry? {
    val spentAt = spentAt?.let { Instant.parseOrNull(it.toString()) } ?: return null
    return TimelogEntry(
        id = id,
        spentAt = spentAt,
        summary = summary,
        timeSpent = timeSpent,
        workItemTitle = issue?.title ?: mergeRequest?.title,
        workItemUrl = issue?.webUrl ?: mergeRequest?.webUrl,
        workItemId = issue?.id ?: mergeRequest?.id,
        workItemIid = issue?.iid ?: mergeRequest?.iid,
    )
}