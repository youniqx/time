package com.youniqx.time.presentation.navscopes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

typealias NavScope = EntryProviderScope<NavKey>.(backStack: NavBackStack<NavKey>) -> Unit
