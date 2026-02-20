package com.youniqx.time.presentation.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.youniqx.time.presentation.modifier.changeFocusOnTab

@Composable
fun TokenInput(
    token: String?,
    onTokenChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = token.orEmpty(),
        onValueChange = onTokenChange,
        modifier =
            modifier
                .fillMaxWidth()
                .changeFocusOnTab(),
        visualTransformation = PasswordVisualTransformation(),
        label = { Text("GitLab Token") },
        supportingText = { Text("Needs API read & write access.") },
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Key, contentDescription = null)
        },
        trailingIcon = trailingIcon,
    )
}
