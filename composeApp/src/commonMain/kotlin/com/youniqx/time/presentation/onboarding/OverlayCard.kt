package com.youniqx.time.presentation.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.window.core.layout.WindowSizeClass
import com.youniqx.time.presentation.LocalSharedTransitionScope
import com.youniqx.time.presentation.theme.LocalSpacing
import com.youniqx.time.systemBarsForVisualComponents

@Composable
fun OverlayCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val spacing = LocalSpacing.current
    val displayAsCard =
        currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
        )

    with(LocalSharedTransitionScope.current) {
        this ?: return@with

        @Composable
        fun MainContent() {
            with(LocalNavAnimatedContentScope.current) {
                Column {
                    val scrollState = rememberScrollState()
                    val alpha by animateFloatAsState(
                        targetValue =
                            when {
                                transition.isRunning && displayAsCard -> 0.3f
                                scrollState.value == 0 -> 0f
                                else -> 0.6f
                            },
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                                    .windowInsetsPadding(WindowInsets.systemBarsForVisualComponents)
                                    .padding(spacing.screenPadding),
                            verticalArrangement = verticalArrangement,
                            horizontalAlignment = horizontalAlignment,
                        ) {
                            content()
                        }
                        header?.let {
                            Box(
                                modifier =
                                    Modifier
                                        .sharedElement(
                                            sharedContentState = rememberSharedContentState("overlayCardHeader"),
                                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        ).then(
                                            if (displayAsCard) {
                                                Modifier.clip(
                                                    MaterialTheme.shapes.medium.copy(
                                                        bottomStart = CornerSize(0f),
                                                        bottomEnd = CornerSize(0f),
                                                    ),
                                                )
                                            } else {
                                                Modifier
                                            },
                                        ).background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = alpha))
                                        .windowInsetsPadding(
                                            WindowInsets.systemBarsForVisualComponents.only(
                                                WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                                            )
                                        )
                                        .padding(spacing.screenPadding),
                            ) {
                                header()
                            }
                        }
                    }
                    footer?.let {
                        Row(
                            modifier =
                                Modifier
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState("overlayCardFooter"),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                    ).then(
                                        if (displayAsCard) {
                                            Modifier.clip(
                                                MaterialTheme.shapes.medium.copy(
                                                    topStart = CornerSize(0f),
                                                    topEnd = CornerSize(0f),
                                                ),
                                            )
                                        } else {
                                            Modifier
                                        },
                                    ).background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .windowInsetsPadding(
                                        WindowInsets.systemBarsForVisualComponents.only(
                                            WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                                        )
                                    )
                                    .padding(horizontal = spacing.screenPadding, vertical = spacing.screenPadding / 2)
                                    .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            footer()
                        }
                    }
                }
            }
        }

        if (displayAsCard) {
            Box(
                modifier =
                    modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBarsForVisualComponents),
                contentAlignment = Alignment.TopCenter,
            ) {
                Card(
                    modifier =
                        Modifier
                            .widthIn(max = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND.dp)
                            .fillMaxSize()
                            .padding(spacing.screenPadding),
                    colors = colors,
                    elevation = elevation,
                ) {
                    MainContent()
                }
            }
        } else {
            MainContent()
        }
    }
}
