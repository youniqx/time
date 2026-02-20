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

import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration

private const val MAX_AVAILABLE_PANES = 3

/**
 * Create a navigation state that persists config changes and process death.
 */
@Composable
fun rememberNavigationState(
    directive: PaneScaffoldDirective,
    configuration: SavedStateConfiguration,
    vararg elements: NavKey,
): NavigationState {
    val backStacks = List(size = MAX_AVAILABLE_PANES) { rememberNavBackStack(configuration, *elements) }

    return remember(directive) {
        NavigationState(
            backStacks = backStacks,
            directive = directive,
        )
    }
}

/**
 * State holder for navigation state.
 *
 * @param backStacks - the back stacks for each available pane count
 */
class NavigationState(
    val backStacks: List<NavBackStack<NavKey>>,
    directive: PaneScaffoldDirective,
) {
    val activeBackStack = activeBackStackFor(directive = directive)
}

/**
 * Convert NavigationState into NavEntries.
 */
fun NavigationState.activeBackStackFor(directive: PaneScaffoldDirective): NavBackStack<NavKey> =
    backStacks[directive.maxHorizontalPartitions - 1]
