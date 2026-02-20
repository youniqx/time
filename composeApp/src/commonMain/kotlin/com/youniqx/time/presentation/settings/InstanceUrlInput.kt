package com.youniqx.time.presentation.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.youniqx.time.presentation.modifier.changeFocusOnTab
import io.ktor.http.Url

@Composable
fun InstanceUrlInput(
    instanceUrl: String?,
    onInstanceUrlChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val parsedInstanceUrl = instanceUrl?.let { Url(instanceUrl) }
    OutlinedTextField(
        value = instanceUrl.orEmpty(),
        onValueChange = onInstanceUrlChange,
        modifier =
            modifier
                .fillMaxWidth()
                .changeFocusOnTab(),
        isError = !instanceUrl.isNullOrEmpty() && parsedInstanceUrl == null,
        label = { Text("GitLab Instance Url") },
        placeholder = { Text("https://gitlab.com") },
        supportingText = { Text("Requires GitLab version 18.6 or higher.") },
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Language, contentDescription = null)
        },
    )
}
