package com.youniqx.time

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.youniqx.time.gitlab.models.CurrentSprintQuery
import org.jetbrains.compose.ui.tooling.preview.Preview

val WindowInsets.Companion.systemBarsForVisualComponents: WindowInsets
    // + custom inset for the transparent macos system bar; todo: move
    @Composable get() = systemBars.union(displayCutout).add(WindowInsets(top = 16.dp))

operator fun PaddingValues.plus(other: PaddingValues): PaddingValues = PaddingValues(
    start = this.calculateStartPadding(LayoutDirection.Ltr) +
            other.calculateStartPadding(LayoutDirection.Ltr),
    top = this.calculateTopPadding() + other.calculateTopPadding(),
    end = this.calculateEndPadding(LayoutDirection.Ltr) +
            other.calculateEndPadding(LayoutDirection.Ltr),
    bottom = this.calculateBottomPadding() + other.calculateBottomPadding(),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun App(token: String = "") {
    MaterialTheme {
        var response: ApolloResponse<CurrentSprintQuery.Data>? by remember { mutableStateOf(null) }

        val isPreview = LocalInspectionMode.current
        LaunchedEffect(true) {
            if (isPreview) return@LaunchedEffect
            // Create a client
            val apolloClient = ApolloClient.Builder()
                .serverUrl("https://gitlab.ci.youniqx.com/api/graphql")
                .addHttpHeader("Authorization", "Bearer $token")
                .build()
            response = apolloClient.query(CurrentSprintQuery()).execute()
        }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // https://kotlinlang.slack.com/archives/CJLTWPH7S/p1731631796638429?thread_ts=1731631796.638429
            val consumedWindowInsets = remember { MutableWindowInsets() }
            val insets =
                WindowInsets.systemBarsForVisualComponents
                    .exclude(consumedWindowInsets)
                    .asPaddingValues()

            LazyColumn(
                modifier =
                    Modifier
                        .onConsumedWindowInsetsChanged {
                            consumedWindowInsets.insets = it
                        },
                contentPadding = insets + PaddingValues(20.dp),
            ) {
                val issues = response?.data?.group?.issues?.nodes.orEmpty().filterNotNull().sortedBy {
                    it.state
                }
                itemsIndexed(issues) { index, issue ->
                    if (index != 0) HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Issue(issue)
                }
            }
        }
    }
}

@Composable
fun Issue(issue: CurrentSprintQuery.Node) {
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = Modifier
            .clickable { uriHandler.openUri(issue.webUrl) }
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(issue.title)
    }
}