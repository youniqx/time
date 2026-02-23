@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)

package com.youniqx.time.presentation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.youniqx.time.domain.SettingsRepository
import com.youniqx.time.presentation.errors.NotFoundRoute
import com.youniqx.time.presentation.history.HistoryRoute
import com.youniqx.time.presentation.navigation.LocalNavigator
import com.youniqx.time.presentation.navigation.NavScope
import com.youniqx.time.presentation.navigation.Navigator
import com.youniqx.time.presentation.navigation.rememberNavEntryProviderDecorator
import com.youniqx.time.presentation.navigation.rememberNavigationState
import com.youniqx.time.presentation.onboarding.GitLabSetupRoute
import com.youniqx.time.presentation.onboarding.NamespacesAndIterationCadenceSetupRoute
import com.youniqx.time.presentation.onboarding.OnboardingRoute
import com.youniqx.time.presentation.onboarding.WelcomeRoute
import com.youniqx.time.presentation.onboarding.onboardingIndex
import com.youniqx.time.presentation.settings.SettingsRoute
import com.youniqx.time.presentation.theme.AppTheme
import com.youniqx.time.presentation.theme.Theme
import com.youniqx.time.presentation.workitems.DisableGlobalSearch
import com.youniqx.time.presentation.workitems.LocalSearchFocusRequester
import com.youniqx.time.presentation.workitems.ScrollToWorkItem
import com.youniqx.time.presentation.workitems.SwitchTrackingRoute
import com.youniqx.time.presentation.workitems.WorkItemsRoute
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun App(
    navScopes: Set<NavScope>,
    settingsRepository: SettingsRepository,
    focusRequester: FocusRequester = remember { FocusRequester() },
    setWindowBackground: ((Color) -> Unit)? = null,
    theme: Theme = com.youniqx.time.presentation.theme.teal.theme,
) {
    AppTheme(settingsRepository = settingsRepository, theme = theme) {
        val resultStore = rememberResultStore(configuration = resultStoreSavedStateConfiguration)
        // Override the defaults so that there isn't a horizontal or vertical space between the panes.
        // See b/444438086
        val windowAdaptiveInfo = currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true)
        // if (forceSinglePane) singlePaneDirective else defaultPaneDirective // Todo
        val directive =
            remember(windowAdaptiveInfo) {
                calculatePaneScaffoldDirective(windowAdaptiveInfo)
                    .copy(horizontalPartitionSpacerSize = 0.dp, verticalPartitionSpacerSize = 0.dp)
            }
        val navigationState =
            rememberNavigationState(
                directive = directive,
                configuration = navBackStackSavedStateConfiguration,
            )
        val navigator = remember(navigationState) { Navigator(navigationState, navScopes) }
        remember { navigator.add(listOf(WorkItemsRoute, HistoryRoute, SettingsRoute), listOf(OnboardingRoute)) }
        val entryDecorators =
            listOf<NavEntryDecorator<NavKey>>(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberNavEntryProviderDecorator(),
            )
        val entryProvider =
            remember {
                entryProvider(fallback = { key ->
                    NavEntry(key) {
                        LaunchedEffect(key) {
                            println("Unknown key: $key")
                            navigator.popUntilLastInclusive()
                            navigator.add(NotFoundRoute)
                        }
                    }
                }) {
                    navScopes.forEach { scope -> scope.entryProvider(this) }
                }
            }
        val entries =
            rememberDecoratedNavEntries(
                backStack = navigationState.activeBackStack,
                entryDecorators = entryDecorators,
                entryProvider = entryProvider,
            )

        if (setWindowBackground != null) {
            MaterialTheme.colorScheme.surface.let { color ->
                LaunchedEffect(setWindowBackground, color) {
                    setWindowBackground(color)
                }
            }
        }

        // Override the defaults so that the supporting pane can be dismissed by pressing back.
        // See b/445826749
        val supportingPaneStrategy =
            rememberSupportingPaneSceneStrategy<NavKey>(
                backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
                directive = directive,
                adaptStrategies =
                    SupportingPaneScaffoldDefaults.adaptStrategies(
                        supportingPaneAdaptStrategy = AdaptStrategy.Hide,
                    ),
            )
        val singlePaneStrategy = remember { SinglePaneSceneStrategy<NavKey>() }
        val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this,
                LocalResultStore provides resultStore,
                LocalSearchFocusRequester provides focusRequester,
                LocalNavigator provides navigator,
            ) {
                Scaffold(
                    floatingActionButton = {
                        if (
                            entries.last().onboardingIndex == null &&
                            SettingsRoute !in navigationState.activeBackStack
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                            ) {
                                val interactionSource = remember { MutableInteractionSource() }
                                val isHovered by interactionSource.collectIsHoveredAsState()
                                SmallFloatingActionButton(
                                    onClick = {
                                        navigator.add(SettingsRoute)
                                    },
                                    interactionSource = interactionSource,
                                ) {
                                    val icon = if (isHovered) Icons.Filled.Settings else Icons.Outlined.Settings
                                    Icon(imageVector = icon, contentDescription = null)
                                }
                            }
                        }
                    },
                ) {
                    NavDisplay(
                        entries = entries,
                        modifier =
                            Modifier
                                // .padding(innerPadding)
                                .fillMaxSize(),
                        sceneStrategy = dialogStrategy then supportingPaneStrategy then singlePaneStrategy,
                        onBack = {
                            navigator.popUntilLastInclusive()
                        },
                    )
                }
            }
        }
        // Todo
        // Fake item to ignore focus requests if we have an open time tracking
        Box(modifier = Modifier.focusProperties { canFocus = false }.focusRequester(focusRequester))
    }
}

private val navBackStackSavedStateConfiguration =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(NotFoundRoute::class, NotFoundRoute.serializer())
                    subclass(OnboardingRoute::class, OnboardingRoute.serializer())
                    subclass(WelcomeRoute::class, WelcomeRoute.serializer())
                    subclass(GitLabSetupRoute::class, GitLabSetupRoute.serializer())
                    subclass(
                        NamespacesAndIterationCadenceSetupRoute::class,
                        NamespacesAndIterationCadenceSetupRoute.serializer(),
                    )
                    subclass(WorkItemsRoute::class, WorkItemsRoute.serializer())
                    subclass(SwitchTrackingRoute::class, SwitchTrackingRoute.serializer())
                    subclass(HistoryRoute::class, HistoryRoute.serializer())
                    subclass(SettingsRoute::class, SettingsRoute.serializer())
                }
            }
    }

private val resultStoreSavedStateConfiguration =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(ResultStoreValue::class) {
                    subclass(ScrollToWorkItem::class, ScrollToWorkItem.serializer())
                    subclass(DisableGlobalSearch::class, DisableGlobalSearch.serializer())
                }
            }
    }
