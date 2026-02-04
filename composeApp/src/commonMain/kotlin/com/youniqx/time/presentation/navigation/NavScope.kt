package com.youniqx.time.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

typealias NavScope = EntryProviderScope<NavKey>.() -> Unit
