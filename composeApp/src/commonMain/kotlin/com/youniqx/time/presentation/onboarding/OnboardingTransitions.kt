package com.youniqx.time.presentation.onboarding

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay

private const val ONBOARDING_STEP_SPEC = "onboardingStepSpec"

fun onboardingStep(index: Int) = mapOf(ONBOARDING_STEP_SPEC to index)

val onboardingTransitions = NavDisplay.transitionSpec {
    val initialIndex = initialState.entries.last().metadata[ONBOARDING_STEP_SPEC] as? Int
    val targetIndex = targetState.entries.last().metadata[ONBOARDING_STEP_SPEC] as? Int
    when {
        targetIndex == null || initialIndex == null -> EnterTransition.None togetherWith ExitTransition.None
        targetIndex > initialIndex -> (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> -width } + fadeOut())

        targetIndex < initialIndex -> (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> width } + fadeOut())
        else -> EnterTransition.None togetherWith ExitTransition.None
    }
} + NavDisplay.popTransitionSpec {
    // Slide old content down, revealing the new content in place underneath
    EnterTransition.None togetherWith
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(1000)
            )
} + NavDisplay.predictivePopTransitionSpec {
    // Slide old content down, revealing the new content in place underneath
    EnterTransition.None togetherWith
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(1000)
            )
}