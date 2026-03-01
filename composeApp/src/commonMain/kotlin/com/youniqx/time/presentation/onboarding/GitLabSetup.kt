package com.youniqx.time.presentation.onboarding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.presentation.settings.InstanceUrlInput
import com.youniqx.time.presentation.settings.TokenInput
import com.youniqx.time.presentation.settings.createTokenUrl
import com.youniqx.time.presentation.theme.AppTheme
import com.youniqx.time.presentation.theme.LocalSpacing
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable

@Serializable
object GitLabSetupRoute : NavKey

@Composable
fun GitLabSetup(
    stepCount: Int,
    stepFinished: () -> Unit,
    viewModel: OnboardingViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GitLabSetupScreen(
        stepCount = stepCount,
        instanceUrl = uiState.settings.instanceUrl,
        onInstanceUrlChange = viewModel::setInstanceUrl,
        token = uiState.settings.token,
        onTokenChange = viewModel::setToken,
        onComplete = stepFinished,
        onSkip = stepFinished,
    )
}

@Composable
fun GitLabSetupScreen(
    stepCount: Int,
    instanceUrl: String?,
    onInstanceUrlChange: (String) -> Unit,
    token: String?,
    onTokenChange: (String) -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val uriHandler = LocalUriHandler.current

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
                enabled = !instanceUrl.isNullOrBlank() && !token.isNullOrBlank(),
            ) {
                Text("Continue")
            }
        },
    ) {
        Spacer(modifier = Modifier.height(spacing.xxl * 1.5f))

        // Title
        Text(
            text = "Connect to GitLab",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        // Description
        Text(
            text = "Enter your GitLab instance URL and personal access token to start tracking time.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        // GitLab URL input
        InstanceUrlInput(
            instanceUrl = instanceUrl,
            onInstanceUrlChange = onInstanceUrlChange,
        )

        Spacer(modifier = Modifier.height(spacing.lg))

        // Token input
        TokenInput(
            token = token,
            onTokenChange = onTokenChange,
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        SimpleTooltip("Open browser" + if (instanceUrl.isNullOrEmpty()) "\nPlease enter Instance Url first." else "") {
            TextButton(
                enabled = !instanceUrl.isNullOrEmpty(),
                onClick = {
                    instanceUrl?.let {
                        val tokenUrl = createTokenUrl(fromInstanceUrl = instanceUrl)
                        uriHandler.openUri(tokenUrl.toString())
                    }
                },
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.padding(end = spacing.sm),
                )
                Text("Create a new access token")
            }
        }
    }
}

@Preview
@Composable
fun GitLabSetupPreview() {
    AppTheme {
        GitLabSetupScreen(
            stepCount = 3,
            instanceUrl = "",
            onInstanceUrlChange = {},
            token = "",
            onTokenChange = {},
            onComplete = {},
            onSkip = {},
        )
    }
}
