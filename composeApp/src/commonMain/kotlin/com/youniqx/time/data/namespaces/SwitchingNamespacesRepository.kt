package com.youniqx.time.data.namespaces

import androidx.paging.PagingSource
import com.russhwolf.settings.ExperimentalSettingsApi
import com.youniqx.time.data.LocalSettingsRepository
import com.youniqx.time.domain.NamespacesRepository
import com.youniqx.time.domain.demoModeIsActive
import com.youniqx.time.domain.models.NamespaceEntry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
@ContributesBinding(AppScope::class, replaces = [RemoteNamespacesRepository::class, DemoNamespacesRepository::class])
@SingleIn(AppScope::class)
class SwitchingNamespacesRepository(
    private val localSettingsRepository: LocalSettingsRepository,
    private val remoteNamespacesRepository: RemoteNamespacesRepository,
    private val demoNamespacesRepository: DemoNamespacesRepository,
) : NamespacesRepository {
    override fun search(search: String): Flow<() -> PagingSource<String, NamespaceEntry>> =
        localSettingsRepository.settings.value.let {
            val delegate = if (it.demoModeIsActive) demoNamespacesRepository else remoteNamespacesRepository
            delegate.search(search)
        }
}
