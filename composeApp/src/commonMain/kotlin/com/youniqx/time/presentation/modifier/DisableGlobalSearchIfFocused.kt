package com.youniqx.time.presentation.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import com.youniqx.time.presentation.LocalResultStore
import com.youniqx.time.presentation.workitems.DisableGlobalSearch

@Composable
fun Modifier.disableGlobalSearchIfFocused(): Modifier {
    val resultStore = LocalResultStore.current
    return onFocusChanged {
        if (it.hasFocus) {
            resultStore.setResult(result = DisableGlobalSearch)
        } else {
            resultStore.removeResult<DisableGlobalSearch>()
        }
    }
}
