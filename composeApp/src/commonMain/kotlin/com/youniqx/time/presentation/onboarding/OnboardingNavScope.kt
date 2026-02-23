package com.youniqx.time.presentation.onboarding

import androidx.navigation3.runtime.NavKey
import com.youniqx.time.presentation.navigation.LocalNavigator
import com.youniqx.time.presentation.navigation.NavScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute : NavKey

@ContributesTo(AppScope::class)
@BindingContainer
class OnboardingNavScope {
    @Provides
    @IntoSet
    fun provideNavScope(): NavScope =
        NavScope(
            onAdd = {
                when (it) {
                    OnboardingRoute -> {
                        popUntilLastInclusive(route = it)
                        add(WelcomeRoute)
                    }
                }
            },
            onFinished = {
                when (it) {
                    WelcomeRoute -> {
                        add(GitLabSetupRoute)
                    }

                    GitLabSetupRoute -> {
                        add(NamespacesAndIterationCadenceSetupRoute)
                    }

                    NamespacesAndIterationCadenceSetupRoute -> {
                        onFinished(OnboardingRoute)
                    }

                    OnboardingRoute -> {
                        popUntilLastInclusive(route = WelcomeRoute)
                    }
                }
            },
        ) {
            var stepCount = 0
            val onboardingSteps =
                iterator {
                    while (true) yield(onboardingStep(stepCount++))
                }

            entry<WelcomeRoute>(
                metadata = onboardingSteps.next() + onboardingTransitions,
            ) {
                val navigator = LocalNavigator.current
                Welcome(
                    stepFinished = {
                        navigator.onFinished(route = it)
                    },
                    hideOnboarding = {
                        navigator.onFinished(route = OnboardingRoute)
                    },
                )
            }

            entry<GitLabSetupRoute>(
                metadata = onboardingSteps.next() + onboardingTransitions,
            ) {
                val navigator = LocalNavigator.current
                GitLabSetup(
                    stepCount = stepCount,
                    stepFinished = {
                        navigator.onFinished(route = it)
                    },
                )
            }

            entry<NamespacesAndIterationCadenceSetupRoute>(
                metadata = onboardingSteps.next() + onboardingTransitions,
            ) {
                val navigator = LocalNavigator.current
                NamespacesAndIterationCadenceSetup(
                    stepCount = stepCount,
                    stepFinished = {
                        navigator.onFinished(route = it)
                    },
                )
            }
        }
}
