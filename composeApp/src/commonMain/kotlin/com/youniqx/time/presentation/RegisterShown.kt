package com.youniqx.time.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
inline fun <reified T : ResultStoreValue> ResultStore.Register(shown: T) {
    DisposableEffect(this, shown) {
        setResult(result = shown)
        onDispose { removeResult<T>() }
    }
}
