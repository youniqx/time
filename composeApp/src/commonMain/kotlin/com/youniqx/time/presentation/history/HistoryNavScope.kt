package com.youniqx.time.presentation.history

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import com.youniqx.time.presentation.AppRoute
import com.youniqx.time.presentation.navigation.NavScope
import com.youniqx.time.presentation.onboarding.GitLabSetup
import com.youniqx.time.presentation.onboarding.GitLabSetupRoute
import com.youniqx.time.presentation.onboarding.Welcome
import com.youniqx.time.presentation.onboarding.WelcomeRoute
import com.youniqx.time.presentation.onboarding.onboardingStep
import com.youniqx.time.presentation.onboarding.onboardingTransitions
import com.youniqx.time.presentation.settings.Settings
import com.youniqx.time.presentation.settings.SettingsRoute
import com.youniqx.time.presentation.workitems.WorkItems
import com.youniqx.time.presentation.workitems.WorkItemsRoute
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlin.collections.plusAssign

@ContributesTo(AppScope::class)
@BindingContainer
class HistoryNavScope {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Provides
    @IntoSet
    fun provideNavScope(): NavScope =
        { backStack ->

            entry<HistoryRoute>(
                metadata = SupportingPaneSceneStrategy.supportingPane()
            ) {
                History()
            }

        }
}