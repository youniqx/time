package com.youniqx.time.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

class NavScope(
    val onAdd: (Navigator.(route: NavKey) -> Unit)? = null,
    val onFinished: (Navigator.(route: NavKey) -> Unit)? = null,
    val entryProvider: EntryProviderScope<NavKey>.() -> Unit,
)
