package com.youniqx.time.presentation.onboarding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.paging.PagingData
import androidx.savedstate.serialization.SavedStateConfiguration
import com.youniqx.time.domain.models.IterationCadence
import com.youniqx.time.domain.models.IterationCadenceMarker
import com.youniqx.time.domain.models.Namespace
import com.youniqx.time.domain.models.NamespaceEntry
import com.youniqx.time.domain.models.SelectedNamespaces
import com.youniqx.time.presentation.LocalResultStore
import com.youniqx.time.presentation.rememberResultStore
import com.youniqx.time.presentation.settings.NamespacesAndIterationCadenceInputs
import com.youniqx.time.presentation.settings.SettingsViewModel
import com.youniqx.time.presentation.settings.emptyPagingData
import com.youniqx.time.presentation.theme.AppTheme
import com.youniqx.time.presentation.theme.LocalSpacing
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable

@Serializable
object NamespacesAndIterationCadenceSetupRoute : NavKey

@Composable
fun NamespacesAndIterationCadenceSetup(
    stepCount: Int,
    stepFinished: () -> Unit,
    viewModel: SettingsViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NamespacesAndIterationCadenceSetupScreen(
        stepCount = stepCount,
        namespaces = uiState.namespaces,
        selectedNamespaces = uiState.selectedNamespaces,
        namespaceSearcher = viewModel::searchNamespace,
        onSearchNamespaceChange = viewModel::saveSearchNamespace,
        iterationCadencesNamespaces = uiState.iterationCadenceNamespaces,
        iterationCadenceNamespaceSearcher = viewModel::searchIterationCadenceNamespace,
        onIterationCadenceNamespaceChange = viewModel::saveIterationCadenceNamespace,
        iterationCadence = uiState.settings.iterationCadence,
        iterationCadences = uiState.iterationCadences,
        iterationCadenceSearcher = viewModel::searchIterationCadence,
        onIterationCadenceChange = viewModel::setIterationCadence,
        onComplete = stepFinished,
        onSkip = stepFinished,
    )
}

@Composable
fun NamespacesAndIterationCadenceSetupScreen(
    stepCount: Int,
    selectedNamespaces: SelectedNamespaces,
    namespaces: PagingData<NamespaceEntry>,
    namespaceSearcher: (String) -> Unit,
    onSearchNamespaceChange: (Namespace) -> Unit,
    iterationCadencesNamespaces: PagingData<NamespaceEntry>,
    iterationCadenceNamespaceSearcher: (String) -> Unit,
    onIterationCadenceNamespaceChange: (Namespace) -> Unit,
    iterationCadence: IterationCadence?,
    iterationCadences: PagingData<IterationCadenceMarker.Filled>,
    iterationCadenceSearcher: (String) -> Unit,
    onIterationCadenceChange: (iterationCadence: IterationCadence?) -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    OverlayCard(
        modifier = modifier,
        header = {
            OnboardingProgressIndicator(stepCount)
        },
        footer = {
            TextButton(onClick = onSkip) {
                Text("Skip for now")
            }

            Button(
                onClick = onComplete,
                enabled = selectedNamespaces.search != null,
            ) {
                Text("Continue")
            }
        },
    ) {
        Spacer(modifier = Modifier.height(spacing.xxl * 1.5f))

        // Title
        Text(
            text = "Select your scopes",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        // Description
        Text(
            text = "Select your search scope and your iteration cadence",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        NamespacesAndIterationCadenceInputs(
            selectedNamespaces = selectedNamespaces,
            namespaces = namespaces,
            namespaceSearcher = namespaceSearcher,
            onSearchNamespaceChange = onSearchNamespaceChange,
            iterationCadencesNamespaces = iterationCadencesNamespaces,
            iterationCadenceNamespaceSearcher = iterationCadenceNamespaceSearcher,
            onIterationCadenceNamespaceChange = onIterationCadenceNamespaceChange,
            iterationCadence = iterationCadence,
            iterationCadences = iterationCadences,
            iterationCadenceSearcher = iterationCadenceSearcher,
            onIterationCadenceChange = onIterationCadenceChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NamespacesAndIterationCadenceSetupPreview() {
    AppTheme {
        val resultStore = rememberResultStore(SavedStateConfiguration {})
        CompositionLocalProvider(LocalResultStore provides resultStore) {
            NamespacesAndIterationCadenceSetupScreen(
                stepCount = 3,
                namespaceSearcher = {},
                namespaces = emptyPagingData(),
                selectedNamespaces = SelectedNamespaces(search = null, iterationCadence = null),
                onSearchNamespaceChange = {},
                iterationCadencesNamespaces = emptyPagingData(),
                iterationCadenceNamespaceSearcher = {},
                onIterationCadenceNamespaceChange = {},
                iterationCadence = null,
                iterationCadences = emptyPagingData(),
                iterationCadenceSearcher = {},
                onIterationCadenceChange = {},
                onComplete = {},
                onSkip = {},
            )
        }
    }
}
