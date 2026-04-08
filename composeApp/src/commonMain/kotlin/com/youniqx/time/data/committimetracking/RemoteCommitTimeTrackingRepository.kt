package com.youniqx.time.data.committimetracking

import com.apollographql.apollo.ApolloClient
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.di.IDispatchers
import com.youniqx.time.domain.CommitTimeTrackingRepository
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.currentTimeSpentString
import com.youniqx.time.gitlab.models.RefreshWorkItemsQuery
import com.youniqx.time.gitlab.models.TimelogCreateMutation
import com.youniqx.time.gitlab.models.type.TimelogCreateInput
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSettingsApi::class)
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RemoteCommitTimeTrackingRepository(
    private val apolloClientFlow: Flow<ApolloClient?>,
    private val settingsRepository: SettingsRepository,
    dispatchers: IDispatchers,
) : CommitTimeTrackingRepository {
    private var job: Job? = null
    private val scope = CoroutineScope(dispatchers.Default)

    override suspend fun commitTimeTracking(): List<String>? {
        val settings = settingsRepository.settings.value.data
        val namespaceFullPath = settings.namespaceFullPath ?: return null
        if (job?.isActive == true) return null
        var errors: List<String>? = null
        job =
            scope.launch {
                val apolloClient = apolloClientFlow.firstOrNull()
                if (apolloClient == null) {
                    errors = listOf("Please check your settings.")
                    return@launch
                }
                errors = null
                settings.openTracking?.let { openTracking ->
                    suspend fun manualRefresh() {
                        // https://gitlab.com/gitlab-org/gitlab/-/issues/584627
                        val success = "Saved successfully!"
                        val ignoredWarning =
                            "Only refreshing failed (will be ignored)!"
                        val manualRefreshResult =
                            apolloClient
                                .query(
                                    RefreshWorkItemsQuery
                                        .Builder()
                                        .namespaceFullPath(namespaceFullPath)
                                        .ids(listOf(openTracking.workItemId))
                                        .build(),
                                ).execute()
                        if (manualRefreshResult.exception != null) {
                            errors =
                                listOf(
                                    success,
                                    ignoredWarning,
                                    manualRefreshResult.exception?.message.orEmpty(),
                                )
                            delay(4.seconds)
                            errors = null
                        } else if (manualRefreshResult.hasErrors()) {
                            errors =
                                listOf(success, ignoredWarning) +
                                manualRefreshResult.errors
                                    ?.map { it.message }
                                    .orEmpty()
                            delay(4.seconds)
                            errors = null
                        }
                        settingsRepository.setOpenTracking(null)
                    }

                    val result =
                        apolloClient
                            .mutation(
                                TimelogCreateMutation(
                                    workItemId = listOf(openTracking.workItemId),
                                    input =
                                        TimelogCreateInput
                                            .Builder()
                                            .issuableId(openTracking.workItemId)
                                            .summary(openTracking.summary.orEmpty())
                                            .timeSpent(openTracking.currentTimeSpentString)
                                            .build(),
                                ),
                            ).execute()

                    fun failedBecauseOfEpic() =
                        result.errors?.let { errors ->
                            errors.isNotEmpty() &&
                                errors.all {
                                    it.message == "Cannot return null for non-nullable field Timelog.project"
                                }
                        } == true
                    if (result.exception != null) {
                        errors =
                            listOf(result.exception?.message.orEmpty())
                    } else if (failedBecauseOfEpic()) {
                        manualRefresh()
                    } else if (result.hasErrors()) {
                        errors = result.errors?.map { it.message }
                    } else {
                        settingsRepository.setOpenTracking(null)
                    }
                }
            }
        job?.join()
        return errors
    }
}
