package com.youniqx.time.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.youniqx.time.presentation.SimpleTooltip
import com.youniqx.time.presentation.settings.InstanceUrlInput
import com.youniqx.time.presentation.settings.TokenInput
import com.youniqx.time.presentation.settings.createTokenUrl
import com.youniqx.time.presentation.theme.LocalSpacing

@Composable
fun GitLabSetupStep(
    instanceUrl: String?,
    onInstanceUrlChange: (String) -> Unit,
    token: String?,
    onTokenChange: (String) -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.screenPadding)
            .padding(top = 32.dp) // Extra padding for window title bar / traffic lights
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.xxl))

        // Title
        Text(
            text = "Connect to GitLab",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        // Description
        Text(
            text = "Enter your GitLab instance URL and personal access token to start tracking time.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        // GitLab URL input
        InstanceUrlInput(
            instanceUrl = instanceUrl,
            onInstanceUrlChange = onInstanceUrlChange
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
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.padding(end = spacing.sm)
                )
                Text("Create a new access token")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onSkip) {
                Text("Skip for now")
            }

            Button(
                onClick = onComplete,
                enabled = !instanceUrl.isNullOrBlank() && !token.isNullOrBlank()
            ) {
                Text("Continue")
            }
        }
    }
}
