package com.youniqx.time.presentation.navscopes

import com.youniqx.time.presentation.AppRoute
import com.youniqx.time.presentation.onboarding.GitLabSetup
import com.youniqx.time.presentation.onboarding.GitLabSetupRoute
import com.youniqx.time.presentation.onboarding.Welcome
import com.youniqx.time.presentation.onboarding.WelcomeRoute
import com.youniqx.time.presentation.onboarding.onboardingStep
import com.youniqx.time.presentation.onboarding.onboardingTransitions
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
            entry<WelcomeRoute>(
                metadata = onboardingStep(0) + onboardingTransitions
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
                metadata = onboardingStep(1) + onboardingTransitions
            ) {
                GitLabSetup(
                    stepFinished = {
                        backStack.clear()
                        backStack += AppRoute
                    }
                )
            }
        }
}
