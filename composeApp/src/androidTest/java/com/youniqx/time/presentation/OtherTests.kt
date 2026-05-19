package com.youniqx.time.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.youniqx.time.data.defaultSettings
import com.youniqx.time.di.AndroidAppGraph
import com.youniqx.time.presentation.onboarding.OnboardingViewModel
import com.youniqx.time.presentation.onboarding.UiState
import com.youniqx.time.testutils.ViewModelBindings
import com.youniqx.time.testutils.vm
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.createDynamicGraphFactory
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test

class OtherTests {
    @BindingContainer
    private class TestSpecificBindings

    private val graph =
        createDynamicGraphFactory<AndroidAppGraph.Factory>(ViewModelBindings(), TestSpecificBindings())
            .create(getApplicationContext())

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenAnotherTestThenIssueArises() =
        runTest {
            every { graph.vm<OnboardingViewModel>().uiState } returns
                MutableStateFlow(
                    UiState(
                        loading = true,
                        settings = defaultSettings,
                        showOnboarding = true,
                    ),
                )
            composeTestRule.setContent {
                CompositionLocalProvider(LocalMetroViewModelFactory provides graph.metroViewModelFactory) {
                    App(navScopes = graph.navScopes, settingsRepository = graph.settingsRepository)
                }
            }
            composeTestRule.awaitIdle()
        }
}
