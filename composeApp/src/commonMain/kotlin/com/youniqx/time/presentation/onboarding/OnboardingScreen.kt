package com.youniqx.time.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

enum class OnboardingStep {
    Welcome,
    GitLabSetup
}

@Composable
fun OnboardingScreen(
    loading: Boolean,
    instanceUrl: String?,
    onInstanceUrlChange: (String) -> Unit,
    token: String?,
    onTokenChange: (String) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentStep by remember { mutableStateOf(OnboardingStep.Welcome) }

    AnimatedContent(
        targetState = currentStep,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                // Going forward
                (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> -width } + fadeOut())
            } else {
                // Going backward
                (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> width } + fadeOut())
            }
        },
        label = "onboardingTransition"
    ) { step ->
        when (step) {
            OnboardingStep.Welcome -> {
                WelcomeStep(
                    loading = loading,
                    onNext = { currentStep = OnboardingStep.GitLabSetup }
                )
            }

            OnboardingStep.GitLabSetup -> {
                GitLabSetupStep(
                    instanceUrl = instanceUrl,
                    onInstanceUrlChange = onInstanceUrlChange,
                    token = token,
                    onTokenChange = onTokenChange,
                    onComplete = onComplete,
                    onSkip = onComplete
                )
            }
        }
    }
}
