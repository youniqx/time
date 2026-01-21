package com.youniqx.time.presentation.errors

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.youniqx.time.presentation.theme.AppTheme
import com.youniqx.time.systemBarsForVisualComponents
import kotlinx.serialization.Serializable

@Serializable
object NotFoundRoute: NavKey

@Composable
fun NotFound() {
    NotFoundScreen()
}

@Composable
fun NotFoundScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBarsForVisualComponents)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Requested content was not found.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Preview
@Composable
fun NotFoundPreview() {
    AppTheme {
        NotFoundScreen()
    }
}
