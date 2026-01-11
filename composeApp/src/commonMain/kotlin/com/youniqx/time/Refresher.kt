package com.youniqx.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun refresh(every: Duration): Instant {
    var output by remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(every) {
        while (isActive) {
            delay(every)
            output = Clock.System.now()
        }
    }
    return output
}
