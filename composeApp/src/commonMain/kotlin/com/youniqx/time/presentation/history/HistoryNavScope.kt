package com.youniqx.time.presentation.history

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import com.youniqx.time.presentation.navigation.AutoFilledSupportingPaneSceneStrategy
import com.youniqx.time.presentation.navigation.LocalNavigator
import com.youniqx.time.presentation.navigation.NavScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
class HistoryNavScope {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Provides
    @IntoSet
    fun provideNavScope(): NavScope =
        {

            entry<HistoryRoute>(
                metadata = AutoFilledSupportingPaneSceneStrategy.supportingPane()
            ) {
                val navigator = LocalNavigator.current
                History(
                    onBack = {
                        navigator.onFinished(route = it)
                    }
                )
            }

        }
}