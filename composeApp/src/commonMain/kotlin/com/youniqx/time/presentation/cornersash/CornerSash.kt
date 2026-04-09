package com.youniqx.time.presentation.cornersash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.youniqx.time.presentation.theme.AppTheme
import kotlin.math.sqrt

@Composable
fun CornerSash(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        Surface(
            modifier =
                Modifier
                    .graphicsLayer {
                        val offset = size.width / (2 * sqrt(2f)) + size.height / (2 * sqrt(2f)) - size.height
                        translationX = (-size.width / 2) + offset
                        translationY = (size.height / 2) - offset
                        rotationZ = 45f
                        alpha = 0.8f
                    }.then(modifier),
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.inverseSurface,
        ) {
            Box(modifier = Modifier.padding(horizontal = 40.dp)) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun CornerSashPreview() {
    AppTheme(darkTheme = true) {
        Surface(modifier = Modifier.size(300.dp)) {
            CornerSash {
                Text("DEMO MODE", fontWeight = FontWeight.Bold)
            }
        }
    }
}
