package com.youniqx.time.presentation.workitems

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.navigation3.scene.DialogSceneStrategy
import com.youniqx.time.presentation.LocalResultStore
import com.youniqx.time.presentation.history.HistoryRoute
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
class WorkItemsNavScope {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Provides
    @IntoSet
    fun provideNavScope(): NavScope =
        {

            entry<WorkItemsRoute>(
                metadata = AutoFilledSupportingPaneSceneStrategy.mainPane()
            ) {
                val navigator = LocalNavigator.current
                WorkItems(
                    showDaySummary = HistoryRoute !in navigator.state.activeBackStack,
                    showHistory = {
                        navigator.add(HistoryRoute)
                    },
                    showSwitchTracking = { targetId: String, targetTitle: String ->
                        navigator.add(SwitchTrackingRoute(targetId = targetId, targetTitle = targetTitle))
                    }
                )
            }

            entry<SwitchTrackingRoute>(
                metadata = DialogSceneStrategy.dialog()
            ) {
                val navigator = LocalNavigator.current
                val resultStore = LocalResultStore.current
                SwitchTracking(
                    targetId = it.targetId,
                    targetTitle = it.targetTitle,
                    onShowCurrent = { workItemId ->
                        resultStore.setResult(result = ScrollToWorkItem(workItemId = workItemId))
                        navigator.onFinished(route = it)
                    },
                    onDismiss = {
                        navigator.onFinished(route = it)
                    }
                )
            }

        }
}