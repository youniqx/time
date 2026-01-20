package com.youniqx.time.presentation.navscopes

import androidx.navigation3.runtime.NavKey
import com.youniqx.time.presentation.errors.NotFound
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.serialization.Serializable

@Serializable
object NotFoundRoute: NavKey

@ContributesTo(AppScope::class)
@BindingContainer
object ErrorNavScope {
    @Provides
    @IntoSet
    fun provideNavScope(): NavScope =
        { backStack ->
            entry<NotFoundRoute> { _ ->
                NotFound()
            }
        }
}
