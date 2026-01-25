package com.youniqx.time.presentation.workitems

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import com.youniqx.time.presentation.history.HistoryRoute
import com.youniqx.time.presentation.navigation.AutoFilledSupportingPaneSceneStrategy
import com.youniqx.time.presentation.navigation.NavScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
class WorkItemsNavScope {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Provides
    @IntoSet
    fun provideNavScope(): NavScope =
        { backStack ->

            entry<WorkItemsRoute>(
                metadata = AutoFilledSupportingPaneSceneStrategy.mainPane()
            ) {
                WorkItems(
                    showHistory = {
                        if (HistoryRoute !in backStack) backStack += HistoryRoute
                    }
                )
            }

        }
}