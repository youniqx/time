package com.youniqx.time

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.ApolloClient
import com.youniqx.time.gitlab.models.CurrentSprintQuery
import com.youniqx.time.gitlab.models.type.IssueState
import com.youniqx.time.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

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

val loremIpsum = """
    Aut aut minima quidem occaecati ea consequuntur est. Iure velit minus enim id sit explicabo nulla dolorem. Alias officiis quia et exercitationem.
    Doloribus adipisci fugit molestias illum. Quos assumenda minus et consequatur officia reprehenderit. Atque quia est et. Minima aut labore nostrum. Omnis voluptates occaecati molestias assumenda. Dolorum quia at soluta sequi vero saepe.
    Non distinctio qui placeat dolores ab voluptatum ea. Et corporis veniam labore quia in ut velit qui. Laudantium quo repudiandae quam quae saepe esse voluptatum consequuntur. Qui numquam optio commodi.
""".trimIndent()

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun App(token: String = "") {
    val systemInDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemInDarkTheme) }
    var useHighContrastColors by remember { mutableStateOf(false) }
    AppTheme(darkTheme = darkTheme, useHighContrastColors = useHighContrastColors) {
        var issues: List<CurrentSprintQuery.Node>? by remember { mutableStateOf(null) }
        val isPreview = LocalInspectionMode.current
        LaunchedEffect(true) {
            if (isPreview) {
                issues = buildList {
                    repeat(20) {
                        val start = Random.nextInt(loremIpsum.lastIndex - 100)
                        add(CurrentSprintQuery.Node(
                            id = it.toString(),
                            title = loremIpsum.substring(start, start + Random.nextInt(20, 100)),
                            webUrl = "",
                            state = IssueState.opened,
                            assignees = null
                        ))
                    }
                }
                return@LaunchedEffect
            }
            // Create a client
            val apolloClient = ApolloClient.Builder()
                .serverUrl("https://gitlab.ci.youniqx.com/api/graphql")
                .addHttpHeader("Authorization", "Bearer $token")
                .build()
            val response = apolloClient.query(CurrentSprintQuery()).execute()
            issues = response.data?.group?.issues?.nodes.orEmpty().filterNotNull().sortedBy {
                it.state
            }
        }
        Scaffold(
            floatingActionButton = {
                ThemeToggle(
                    darkTheme = darkTheme,
                    useHighContrast = useHighContrastColors,
                    toggleDarkTheme = { darkTheme = !darkTheme },
                    toggleHighContrast = { useHighContrastColors = !useHighContrastColors }
                )
            }
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
                itemsIndexed(issues.orEmpty()) { index, issue ->
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