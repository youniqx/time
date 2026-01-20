package com.youniqx.time.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@Composable
fun rememberRotationAnimation(enabled: Boolean, duration: Int = 1_000): RotationAnimationState {
    val coroutineScope = rememberCoroutineScope()
    val state = remember { RotationAnimationState(coroutineScope = coroutineScope, duration = duration) }
    LaunchedEffect(enabled) {
        if (enabled) state.start() else state.stop()
    }
    return state
}

// Source - https://stackoverflow.com/a/78768708
// Posted by Thracian, modified by community. See post 'Timeline' for change history
// Retrieved 2026-01-20, License - CC BY-SA 4.0
class RotationAnimationState(
    val coroutineScope: CoroutineScope,
    val duration: Int
): State<Float> {

    override val value: Float
        get() = animatable.value

    private val animatable = Animatable(0f)
    private val durationPerAngle = duration / 360f

    var rotationStatus: RotationStatus = RotationStatus.Idle

    fun start() {
        if(rotationStatus == RotationStatus.Idle){
            coroutineScope.launch {
                rotationStatus = RotationStatus.Rotating

                while (isActive && rotationStatus == RotationStatus.Rotating) {
                    animatable.animateTo(
                        targetValue = 360f,
                        animationSpec = tween(
                            durationMillis = duration,
                            easing = LinearEasing
                        )
                    )

                    yield()

                    if (rotationStatus == RotationStatus.Rotating) {
                        animatable.snapTo(0f)
                    }
                }
            }
        }
    }

    fun stop() {
        if (rotationStatus == RotationStatus.Rotating){
            coroutineScope.launch {
                rotationStatus = RotationStatus.Stopping
                val currentValue = animatable.value
                // Duration depends on how far current angle is to 360f
                // total duration is duration per angle multiplied with total angles to rotate
                val durationToZero = (durationPerAngle * (360 - currentValue)).toInt()
                animatable.snapTo(currentValue)
                animatable.animateTo(
                    targetValue = 360f,
                    tween(
                        durationMillis = durationToZero,
                        easing = LinearEasing
                    )
                )
                animatable.snapTo(0f)
                rotationStatus = RotationStatus.Idle
            }
        }
    }
}

enum class RotationStatus {
    Idle, Rotating, Stopping
}