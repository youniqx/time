package com.youniqx.time.data

import com.apollographql.apollo.ApolloClient
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.TimelogsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.SourceAware
import com.youniqx.time.gitlab.models.TimelogsQuery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RemoteTimelogsRepository(
    private val apolloClientFlow: Flow<ApolloClient?>,
    dispatchers: IDispatchers
): TimelogsRepository {

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
        job = scope.launch {
            apolloClientFlow.filterNotNull().collectLatest { apolloClient ->
                val now = Clock.System.now()
                val query = TimelogsQuery.Builder()
                    .startDate((now - 365.days).toString())
                    .endDate((now + 1.days).toString())
                    .build()
                val response = apolloClient.query(query).execute()
                _timelogs.update { SourceAware(source = DataSource.Remote, data = response.data, isSyncing = false) }
            }
        }
    }
}
