package com.youniqx.time.presentation.onboarding

import com.youniqx.time.presentation.AppRoute
import com.youniqx.time.presentation.navigation.NavScope
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
        { backStack ->

            var stepCount = 0
            val onboardingSteps = iterator {
                while (true) yield(onboardingStep(stepCount++))
            }

            entry<WelcomeRoute>(
                metadata = onboardingSteps.next() + onboardingTransitions
            ) {
                Welcome(
                    stepFinished = {
                        backStack += GitLabSetupRoute
                    },
                    hideOnboarding = {
                        backStack.removeLastOrNull()
                        backStack += AppRoute
                    }
                )
            }

            entry<GitLabSetupRoute>(
                metadata = onboardingSteps.next() + onboardingTransitions
            ) {
                GitLabSetup(
                    stepCount = stepCount,
                    stepFinished = {
                        backStack.clear()
                        backStack += AppRoute
                    },
                )
            }
        }
}