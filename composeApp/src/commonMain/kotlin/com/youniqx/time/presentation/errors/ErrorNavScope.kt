package com.youniqx.time.presentation.errors

import com.youniqx.time.presentation.navigation.NavScope
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
        { navigator ->
            entry<NotFoundRoute> { _ ->
                NotFound()
            }
        }
}