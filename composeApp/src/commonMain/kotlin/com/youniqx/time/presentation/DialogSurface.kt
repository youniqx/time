package com.youniqx.time.presentation

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DialogSurface(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        color = AlertDialogDefaults.containerColor,
        shape = AlertDialogDefaults.shape,
        tonalElevation = AlertDialogDefaults.TonalElevation,
        content = content,
    )
}
