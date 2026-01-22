package com.youniqx.time.presentation.onboarding

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPopTransitionSpec
import androidx.navigation3.ui.defaultPredictivePopTransitionSpec
import androidx.navigation3.ui.defaultTransitionSpec

private const val ONBOARDING_STEP_SPEC = "onboardingStepSpec"

fun onboardingStep(index: Int) = mapOf(ONBOARDING_STEP_SPEC to index)

val NavEntry<*>.onboardingIndex: Int?
    get() = metadata[ONBOARDING_STEP_SPEC] as? Int

val Scene<*>.index: Int?
    get() = entries.last().onboardingIndex

@Suppress("UNCHECKED_CAST")
fun <T : Any> makeUsable(input: (AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform?)) =
    input as AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform?

@Suppress("UNCHECKED_CAST")
fun <T : Any> makeUsable(input: (AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform?)) =
    input as AnimatedContentTransitionScope<Scene<*>>.(Int) -> ContentTransform?

val backwards by lazy {
    (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
            (slideOutHorizontally { width -> width } + fadeOut())
}

val onboardingTransitions = NavDisplay.transitionSpec {
    val initialIndex = initialState.index
    val targetIndex = targetState.index
    val default = makeUsable(defaultTransitionSpec())

    when {
        targetIndex == null || initialIndex == null -> default()
        targetIndex > initialIndex -> (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> -width } + fadeOut())
        targetIndex < initialIndex -> backwards
        else -> default()
    }
} + NavDisplay.popTransitionSpec {
    val initialIndex = initialState.index
    val targetIndex = targetState.index
    val default = makeUsable(defaultPopTransitionSpec())

    when {
        targetIndex == null || initialIndex == null -> default()
        targetIndex < initialIndex -> backwards
        else -> default()
    }
} + NavDisplay.predictivePopTransitionSpec {
    val initialIndex = initialState.index
    val targetIndex = targetState.index
    val default = makeUsable(defaultPredictivePopTransitionSpec())

    when {
        targetIndex == null || initialIndex == null -> default(it)
        targetIndex < initialIndex -> backwards
        else -> default(it)
    }
}