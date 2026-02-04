package com.youniqx.time.presentation.onboarding

import com.youniqx.time.presentation.navigation.LocalNavigator
import com.youniqx.time.presentation.navigation.NavScope
import com.youniqx.time.presentation.workitems.WorkItemsRoute
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
class OnboardingNavScope {
    @Provides
    @IntoSet
    fun provideNavScope(): NavScope =
        {

            var stepCount = 0
            val onboardingSteps = iterator {
                while (true) yield(onboardingStep(stepCount++))
            }

            entry<WelcomeRoute>(
                metadata = onboardingSteps.next() + onboardingTransitions
            ) {
                val navigator = LocalNavigator.current
                Welcome(
                    stepFinished = {
                        navigator.onFinished(route = it)
                    },
                    hideOnboarding = {
                        navigator.onFinished(route = GitLabSetupRoute)
                    }
                )
            }

            entry<GitLabSetupRoute>(
                metadata = onboardingSteps.next() + onboardingTransitions
            ) {
                val navigator = LocalNavigator.current
                GitLabSetup(
                    stepCount = stepCount,
                    stepFinished = {
                        navigator.onFinished(route = it)
                    },
                )
            }
        }
}