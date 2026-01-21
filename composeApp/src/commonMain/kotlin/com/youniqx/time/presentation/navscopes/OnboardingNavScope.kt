package com.youniqx.time.presentation.navscopes

import com.youniqx.time.presentation.AppRoute
import com.youniqx.time.presentation.onboarding.GitLabSetup
import com.youniqx.time.presentation.onboarding.GitLabSetupRoute
import com.youniqx.time.presentation.onboarding.Welcome
import com.youniqx.time.presentation.onboarding.WelcomeRoute
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
            entry<WelcomeRoute> {
                Welcome(
                    stepFinished = {
                        backStack.removeLastOrNull()
                        backStack += GitLabSetupRoute
                    },
                    hideOnboarding = {
                        backStack.removeLastOrNull()
                        backStack += AppRoute
                    }
                )
            }

            entry<GitLabSetupRoute> {
                GitLabSetup(
                    stepFinished = {
                        backStack.removeLastOrNull()
                        backStack += AppRoute
                    }
                )
            }
        }
}
