/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youniqx.time.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events by updating the navigation state.
 */
class Navigator(
    val state: NavigationState,
    private val navScopes: Set<NavScope>,
) {
    init {
        state.backStacks.firstOrNull()?.toList()?.forEach { navKey ->
            navScopes.forEach { navScope -> navScope.onAdd?.invoke(this, navKey) }
        }
    }

    fun onFinished(route: NavKey) {
        navScopes.forEach { navScope -> navScope.onFinished?.invoke(this, route) }
    }

    fun add(vararg toAdd: NavKey) {
        toAdd.forEach { navKey ->
            state.backStacks.forEach { backstack ->
                backstack += navKey
            }
            navScopes.forEach { navScope -> navScope.onAdd?.invoke(this, navKey) }
        }
    }

    fun add(vararg toAdd: List<NavKey>) {
        toAdd.forEach {
            it.forEachIndexed { paneIndex, navKey ->
                state.backStacks.forEachIndexed { availablePanesIndex, backstack ->
                    if (availablePanesIndex >= paneIndex) backstack += navKey
                }
                navScopes.forEach { navScope -> navScope.onAdd?.invoke(this, navKey) }
            }
        }
    }

    fun popUntilLastInclusive(route: NavKey? = null) {
        val fromBackStack = state.activeBackStack
        popUntilLastInclusive(fromBackStack = fromBackStack, route = route ?: fromBackStack.lastOrNull())
    }

    fun popUntilLastInclusive(
        fromBackStack: NavBackStack<NavKey>,
        route: NavKey? = fromBackStack.lastOrNull(),
    ) {
        route ?: return
        if (fromBackStack.size == 1) return
        state.backStacks.forEach { backStack ->
            val index = backStack.asReversed().indexOf(route)
            if (index == -1 || index == backStack.lastIndex) return@forEach
            (backStack.lastIndex downTo (backStack.lastIndex - index)).forEach {
                backStack.removeAt(index = it)
            }
        }
    }
}

private val PrivateLocalNavigator = compositionLocalOf<Navigator?> { null }

object LocalNavigator {
    interface Accessor {
        val current: Navigator
            @Composable
            get() = error("Do not access the navigator outside of NavScopes.")
    }

    /**
     * Provides a [Navigator] to the composition
     */
    infix fun provides(navigator: Navigator): ProvidedValue<Navigator?> = PrivateLocalNavigator.provides(navigator)
}

/**
 * The current [Navigator]
 * Prevent nested composables to access the navigator directly
 */
val EntryProviderScope<NavKey>.LocalNavigator: LocalNavigator.Accessor get() =
    object : LocalNavigator.Accessor {
        /**
         * The current [Navigator]
         */
        override val current: Navigator
            @Composable
            get() = PrivateLocalNavigator.current ?: error("No Navigator has been provided")
    }
