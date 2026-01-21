package com.youniqx.time.presentation.navscopes

import com.youniqx.time.presentation.errors.NotFound
import com.youniqx.time.presentation.errors.NotFoundRoute
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

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
