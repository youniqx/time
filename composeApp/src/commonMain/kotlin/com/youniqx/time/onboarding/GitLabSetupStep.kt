package com.youniqx.time.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.youniqx.time.settings.InstanceUrlInput
import com.youniqx.time.theme.LocalSpacing

@Composable
fun GitLabSetupStep(
    instanceUrl: String,
    onInstanceUrlChange: (String) -> Unit,
    token: String,
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
        OutlinedTextField(
            value = token,
            onValueChange = onTokenChange,
            label = { Text("Personal Access Token") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Key, contentDescription = null)
            }
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        // Token creation helper
        TextButton(
            onClick = {
                val url = if (instanceUrl.isNotBlank()) {
                    "${instanceUrl.trimEnd('/')}/-/user_settings/personal_access_tokens"
                } else {
                    "https://gitlab.com/-/user_settings/personal_access_tokens"
                }
                uriHandler.openUri(url)
            }
        ) {
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.padding(end = spacing.sm)
            )
            Text("Create a new access token")
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
                enabled = instanceUrl.isNotBlank() && token.isNotBlank()
            ) {
                Text("Continue")
            }
        }
    }
}
