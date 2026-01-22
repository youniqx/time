package com.youniqx.time.presentation.onboarding

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.youniqx.time.presentation.LocalSharedTransitionScope
import com.youniqx.time.presentation.navigation.LocalNavEntry
import kotlin.math.sign

@Composable
fun OnboardingProgressIndicator(stepCount: Int) {
    val navEnty = LocalNavEntry.current
    val myOnboardingIndex = remember(navEnty) { navEnty?.onBoardingIndex ?: 0 }
    with(LocalSharedTransitionScope.current) {
        if (this == null) return@with
        val transitionOffset by with(LocalNavAnimatedContentScope.current) {
            val currentOnboardingIndex = ((transition.parentTransition?.currentState as? Scene<*>?)?.index ?: 0)
            val direction = ((myOnboardingIndex - currentOnboardingIndex).sign.takeIf { it != 0 } ?: -1)
            transition.animateFloat {
                when (it) {
                    EnterExitState.PreEnter -> -1f / stepCount
                    EnterExitState.Visible -> 0f
                    EnterExitState.PostExit -> 1f / stepCount
                } * direction
            }
        }
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .sharedElement(
                    sharedContentState = rememberSharedContentState("onboardingProgress"),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                ),
            progress = {
                // we do not subtract 1 from the stepCount as the imaginary final step is the app
                myOnboardingIndex.toFloat() / stepCount + transitionOffset
            },
        )
    }
}