package com.youniqx.time.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

private object Uninitialized

@Composable
fun <T> rememberSyncedSource(
    from: T,
    save: (T) -> Unit,
): MutableState<T> {
    // 1. Keep track of the last value we intentionally sent to the repository
    val lastSaved = remember { mutableStateOf<Any?>(Uninitialized) }

    // 2. Local UI state (the immediate source of truth)
    val localState = remember { mutableStateOf(from) }

    // 3. Handle external updates (from the 'from' parameter)
    LaunchedEffect(from) {
        // Only override local state if 'from' is different from what we
        // currently show AND different from the last thing we saved.
        if (from == localState.value) {
            lastSaved.value = Uninitialized
        } else if (from != lastSaved.value) {
            localState.value = from
        }
    }

    // 4. Wrap the state to intercept writes
    return remember(localState) {
        object : MutableState<T> by localState {
            override var value: T
                get() = localState.value
                set(newValue) {
                    if (localState.value != newValue) {
                        localState.value = newValue
                        lastSaved.value = newValue // Mark this as an "expected echo"
                        save(newValue)
                    }
                }
        }
    }
}
