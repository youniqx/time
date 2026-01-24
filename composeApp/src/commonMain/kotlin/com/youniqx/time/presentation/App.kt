@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)

package com.youniqx.time.presentation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.domain.models.DataSource
import com.youniqx.time.domain.models.dataIfNotFrom
import com.youniqx.time.presentation.errors.NotFoundRoute
import com.youniqx.time.presentation.history.HistoryRoute
import com.youniqx.time.presentation.navigation.NavScope
import com.youniqx.time.presentation.navigation.rememberNavEntryProviderDecorator
import com.youniqx.time.presentation.onboarding.GitLabSetupRoute
import com.youniqx.time.presentation.onboarding.WelcomeRoute
import com.youniqx.time.presentation.onboarding.onboardingIndex
import com.youniqx.time.presentation.settings.SettingsRoute
import com.youniqx.time.presentation.theme.AppTheme
import com.youniqx.time.presentation.theme.Theme
import com.youniqx.time.presentation.workitems.WorkItemsRoute
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.time.ExperimentalTime

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(NotFoundRoute::class, NotFoundRoute.serializer())
            subclass(WelcomeRoute::class, WelcomeRoute.serializer())
            subclass(GitLabSetupRoute::class, GitLabSetupRoute.serializer())
            subclass(WorkItemsRoute::class, WorkItemsRoute.serializer())
            subclass(HistoryRoute::class, HistoryRoute.serializer())
            subclass(SettingsRoute::class, SettingsRoute.serializer())
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun App(
    navScopes: Set<NavScope>,
    settingsRepository: SettingsRepository,
    focusRequester: FocusRequester = remember { FocusRequester() },
    setWindowBackground: ((Color) -> Unit)? = null,
    theme: Theme = com.youniqx.time.presentation.theme.teal.theme,
) {
    val sourceAwareSettings by settingsRepository.settings.collectAsStateWithLifecycle()
    val settings = sourceAwareSettings.data
    val darkTheme = sourceAwareSettings.dataIfNotFrom(excludedSource = DataSource.Default)?.darkTheme
        ?: isSystemInDarkTheme()
    val backStack = rememberNavBackStack(configuration = config, WelcomeRoute)
    val entries =
        rememberDecoratedNavEntries(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberNavEntryProviderDecorator(),
            ),
            entryProvider =
                entryProvider(fallback = { key ->
                    NavEntry(key) {
                        LaunchedEffect(key) {
                            println("Unknown key: $key")
                            backStack.removeLastOrNull()
                            backStack.add(NotFoundRoute)
                        }
                    }
                }) {
                    navScopes.forEach { scope ->
                        scope(backStack)
                    }
                },
        )

    AppTheme(darkTheme = darkTheme, useHighContrastColors = settings.highContrastColors, theme = theme) {
        if (setWindowBackground != null) {
            MaterialTheme.colorScheme.surface.let { color ->
                LaunchedEffect(setWindowBackground, color) {
                    setWindowBackground(color)
                }
            }
        }
        // Override the defaults so that there isn't a horizontal or vertical space between the panes.
        // See b/444438086
        val windowAdaptiveInfo = currentWindowAdaptiveInfo()
        // if (forceSinglePane) singlePaneDirective else defaultPaneDirective // Todo
        val directive = remember(windowAdaptiveInfo) {
            calculatePaneScaffoldDirective(windowAdaptiveInfo)
                .copy(horizontalPartitionSpacerSize = 0.dp, verticalPartitionSpacerSize = 0.dp)
        }

        // Override the defaults so that the supporting pane can be dismissed by pressing back.
        // See b/445826749
        val supportingPaneStrategy = rememberSupportingPaneSceneStrategy<NavKey>(
            backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
            directive = directive,
            adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(
                supportingPaneAdaptStrategy = AdaptStrategy.Hide
            )
        )
        val singlePaneStrategy: SinglePaneSceneStrategy<NavKey> = remember { SinglePaneSceneStrategy() }
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this,
            ) {
                Scaffold(
                    floatingActionButton = {
                        if (entries.last().onboardingIndex == null) CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isHovered by interactionSource.collectIsHoveredAsState()
                            SmallFloatingActionButton(
                                onClick = {
                                    backStack += SettingsRoute
                                },
                                interactionSource = interactionSource,
                            ) {
                                val icon = if (isHovered) Icons.Filled.Settings else Icons.Outlined.Settings
                                Icon(imageVector = icon, contentDescription = null)
                            }
                        }
                    }
                ) {
                    NavDisplay(
                        entries = entries,
                        modifier =
                            Modifier
                                // .padding(innerPadding)
                                .fillMaxSize(),
                        sceneStrategy = supportingPaneStrategy then singlePaneStrategy,
                        onBack = { backStack.removeLastOrNull() },
                        sharedTransitionScope = this,
                    )
                }
            }
        }
    }
}