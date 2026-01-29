/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youniqx.time.presentation.navigation

import androidx.collection.mutableIntListOf
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole.Extra
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole.Main
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole.Supporting
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldAdaptStrategies
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import com.youniqx.time.presentation.navigation.AutoFilledSupportingPaneSceneStrategy.Companion.extraPane
import com.youniqx.time.presentation.navigation.AutoFilledSupportingPaneSceneStrategy.Companion.mainPane
import com.youniqx.time.presentation.navigation.AutoFilledSupportingPaneSceneStrategy.Companion.supportingPane


/**
 * Creates and remembers a [AutoFilledSupportingPaneSceneStrategy].
 *
 * @param backNavigationBehavior the behavior describing which backstack entries may be skipped
 *   during the back navigation. See [BackNavigationBehavior].
 * @param directive The top-level directives about how the supporting-pane scaffold should arrange
 *   its panes.
 * @param adaptStrategies adaptation strategies of each pane, which denotes how each pane should be
 *   adapted if they can't fit on screen in the [PaneAdaptedValue.Expanded] state. It is recommended
 *   to use [SupportingPaneScaffoldDefaults.adaptStrategies] as a default, but custom
 *   [ThreePaneScaffoldAdaptStrategies] are supported as well.
 */
@ExperimentalMaterial3AdaptiveApi
@Composable
fun <T : Any> rememberAutoFilledSupportingPaneSceneStrategy(
    backNavigationBehavior: BackNavigationBehavior =
        BackNavigationBehavior.PopUntilCurrentDestinationChange,
    directive: PaneScaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()),
    adaptStrategies: ThreePaneScaffoldAdaptStrategies =
        SupportingPaneScaffoldDefaults.adaptStrategies(),
    ghostEntries: List<NavEntry<T>> = emptyList()
): AutoFilledSupportingPaneSceneStrategy<T> {
    remember(backNavigationBehavior) {
        println("backNavigationBehavior")
    }
    remember(directive) {
        println("directive")
    }
    remember(adaptStrategies) {
        println("adaptStrategies")
    }

    return remember(backNavigationBehavior, directive, adaptStrategies) {
        println("new")
        AutoFilledSupportingPaneSceneStrategy(
            backNavigationBehavior = backNavigationBehavior,
            directive = directive,
            adaptStrategies = adaptStrategies,
            ghostEntries = ghostEntries
        )
    }
}

/**
 * A [AutoFilledSupportingPaneSceneStrategy] supports arranging [NavEntry]s into an adaptive
 * [SupportingPaneScaffold]. By using [mainPane], [supportingPane], or [extraPane] in a NavEntry's
 * metadata, entries can be assigned as belonging to a main pane, supporting pane, or extra pane.
 * These panes will be displayed together if the window size is sufficiently large, and will
 * automatically adapt if the window size changes, for example, on a foldable device.
 *
 * @param backNavigationBehavior the behavior describing which backstack entries may be skipped
 *   during the back navigation. See [BackNavigationBehavior].
 * @param directive The top-level directives about how the scaffold should arrange its panes.
 * @param adaptStrategies adaptation strategies of each pane, which denotes how each pane should be
 *   adapted if they can't fit on screen in the [PaneAdaptedValue.Expanded] state. It is recommended
 *   to use [SupportingPaneScaffoldDefaults.adaptStrategies] as a default, but custom
 *   [ThreePaneScaffoldAdaptStrategies] are supported as well.
 */
@ExperimentalMaterial3AdaptiveApi
class AutoFilledSupportingPaneSceneStrategy<T : Any>(
    val backNavigationBehavior: BackNavigationBehavior,
    val directive: PaneScaffoldDirective,
    val adaptStrategies: ThreePaneScaffoldAdaptStrategies,
    val ghostEntries: List<NavEntry<T>>,
) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastPaneMetadata = getPaneMetadata(entries.last()) ?: return null
        val sceneKey = lastPaneMetadata.sceneKey

        val scaffoldEntries = mutableListOf<NavEntry<T>>()
        val scaffoldEntryIndices = mutableIntListOf()
        val entriesAsNavItems = mutableListOf<ThreePaneScaffoldDestinationItem<Any>>()

        var idx = entries.lastIndex
        while (idx >= 0) {
            val entry = entries[idx]
            val paneMetadata = getPaneMetadata(entry) ?: break

            if (paneMetadata.sceneKey == sceneKey) {
                scaffoldEntryIndices.add(0, idx)
                scaffoldEntries.add(0, entry)
                entriesAsNavItems.add(
                    0,
                    ThreePaneScaffoldDestinationItem(
                        pane = paneMetadata.role,
                        contentKey = entry.contentKey,
                    ),
                )
            }
            idx--
        }

        if (scaffoldEntries.isEmpty()) return null

        fun injectGhostEntry(role: ThreePaneScaffoldRole) {
            if (entriesAsNavItems.any { item -> item.pane == role }) return
            val ghostEntry = ghostEntries.findLast { entry -> getPaneMetadata(entry)?.role == role } ?: return
            scaffoldEntryIndices.add(0, -1)
            scaffoldEntries.add(0, ghostEntry)
            entriesAsNavItems.add(
                0,
                ThreePaneScaffoldDestinationItem(
                    pane = role,
                    contentKey = ghostEntry.contentKey,
                ),
            )
        }

        when {
            directive.maxHorizontalPartitions >= 3 -> {
                injectGhostEntry(Supporting)
                injectGhostEntry(Extra)
            }

            directive.maxHorizontalPartitions >= 2 -> {
                injectGhostEntry(Supporting)
            }
        }

        val scene =
            ThreePaneScaffoldScene(
                key = sceneKey,
                onBack = onBack,
                backNavBehavior = backNavigationBehavior,
                directive = directive,
                adaptStrategies = adaptStrategies,
                allEntries = entries,
                scaffoldEntries = scaffoldEntries,
                scaffoldEntryIndices = scaffoldEntryIndices,
                entriesAsNavItems = entriesAsNavItems,
                getPaneRole = { getPaneMetadata(it)?.role },
                scaffoldType = ThreePaneScaffoldType.SupportingPane,
            )

        // TODO(b/417475283): decide if/how we should handle scenes with only a single pane
        if (scene.currentScaffoldValue.paneCount <= 1) {
            return null
        }

        return scene
    }

    internal sealed interface PaneMetadata {
        val sceneKey: Any
        val role: ThreePaneScaffoldRole
    }

    internal class MainMetadata(override val sceneKey: Any) : PaneMetadata {
        override val role: ThreePaneScaffoldRole
            get() = Main
    }

    internal class SupportingMetadata(override val sceneKey: Any) : PaneMetadata {
        override val role: ThreePaneScaffoldRole
            get() = Supporting
    }

    internal class ExtraMetadata(override val sceneKey: Any) : PaneMetadata {
        override val role: ThreePaneScaffoldRole
            get() = Extra
    }

    companion object {
        internal const val AutoFilledSupportingPaneRoleKey: String =
            "androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole"

        /**
         * Constructs metadata to mark a [NavEntry] as belonging to a
         * [main pane][AutoFilledSupportingPaneScaffoldRole.Main] within a [SupportingPaneScaffold].
         *
         * @param sceneKey the key to distinguish the scene of the supporting-pane scaffold, in case
         *   multiple supporting-pane scaffolds are used within the same NavDisplay.
         */
        fun mainPane(sceneKey: Any = Unit): Map<String, Any> =
            mapOf(AutoFilledSupportingPaneRoleKey to MainMetadata(sceneKey))

        /**
         * Constructs metadata to mark a [NavEntry] as belonging to a
         * [supporting pane][AutoFilledSupportingPaneScaffoldRole.Supporting] within a
         * [SupportingPaneScaffold].
         *
         * @param sceneKey the key to distinguish the scene of the supporting-pane scaffold, in case
         *   multiple supporting-pane scaffolds are used within the same NavDisplay.
         */
        fun supportingPane(sceneKey: Any = Unit): Map<String, Any> =
            mapOf(AutoFilledSupportingPaneRoleKey to SupportingMetadata(sceneKey))

        /**
         * Constructs metadata to mark a [NavEntry] as belonging to an
         * [extra pane][AutoFilledSupportingPaneScaffoldRole.Extra] within a [SupportingPaneScaffold].
         *
         * @param sceneKey the key to distinguish the scene of the supporting-pane scaffold, in case
         *   multiple supporting-pane scaffolds are used within the same NavDisplay.
         */
        fun extraPane(sceneKey: Any = Unit): Map<String, Any> =
            mapOf(AutoFilledSupportingPaneRoleKey to ExtraMetadata(sceneKey))

        private fun <T : Any> getPaneMetadata(entry: NavEntry<T>): PaneMetadata? =
            entry.metadata[AutoFilledSupportingPaneRoleKey] as? PaneMetadata
    }
}