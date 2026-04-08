package com.youniqx.time.data.iterationcadences

import androidx.paging.PagingSource
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.data.LocalSettingsRepository
import com.youniqx.time.domain.IterationCadencesRepository
import com.youniqx.time.domain.demoModeIsActive
import com.youniqx.time.domain.models.IterationCadenceMarker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
@ContributesBinding(
    AppScope::class,
    replaces = [RemoteIterationCadencesRepository::class, DemoIterationCadencesRepository::class],
)
@SingleIn(AppScope::class)
class SwitchingIterationCadencesRepository(
    private val localSettingsRepository: LocalSettingsRepository,
    private val remoteIterationCadencesRepository: RemoteIterationCadencesRepository,
    private val demoIterationCadencesRepository: DemoIterationCadencesRepository,
) : IterationCadencesRepository {
    override fun search(search: String): Flow<() -> PagingSource<String, IterationCadenceMarker.Filled>> =
        localSettingsRepository.settings.value.let {
            val delegate =
                if (it.demoModeIsActive) demoIterationCadencesRepository else remoteIterationCadencesRepository
            delegate.search(search)
        }
}
