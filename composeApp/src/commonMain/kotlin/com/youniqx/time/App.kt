package com.youniqx.time

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.ApolloClient
import com.youniqx.time.gitlab.models.IssuesQuery
import com.youniqx.time.gitlab.models.fragment.Issues
import com.youniqx.time.gitlab.models.type.IssueState
import com.youniqx.time.theme.AppTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

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
fun App(token: String = "", focusRequester: FocusRequester = remember { FocusRequester() }) {
    val systemInDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemInDarkTheme) }
    var useHighContrastColors by remember { mutableStateOf(false) }
    AppTheme(darkTheme = darkTheme, useHighContrastColors = useHighContrastColors) {
        var issues: List<Issues.Node>? by remember { mutableStateOf(null) }
        var search: String by remember { mutableStateOf("") }
        var loading: Boolean by remember { mutableStateOf(false) }
        val isPreview = LocalInspectionMode.current
        LaunchedEffect(search) {
            if (isPreview) {
                issues = buildList {
                    repeat(20) {
                        val start = Random.nextInt(loremIpsum.lastIndex - 100)
                        add(Issues.Node(
                            id = it.toString(),
                            title = loremIpsum.substring(start, start + Random.nextInt(20, 100)),
                            webUrl = "",
                            state = IssueState.opened,
                            labels = null,
                            assignees = null
                        ))
                    }
                }
                return@LaunchedEffect
            }
            loading = true
            if (search.isNotEmpty()) delay(300)
            val apolloClient = ApolloClient.Builder()
                .serverUrl("https://gitlab.ci.youniqx.com/api/graphql")
                .addHttpHeader("Authorization", "Bearer $token")
                .build()
            val query = IssuesQuery.Builder()
                .pinnedIids(listOf("2780"))
                .search(search)
                .doSearch(search.isNotBlank())
                .build()
            val response = apolloClient.query(query).execute()
            if (response.data != null) with(response.data!!) {
                sprint?.issues?.issues?.nodes.orEmpty()
            }
            response.data?.let {
                issues = buildList {
                    addAll(it.sprint?.issues?.issues?.nodes.orEmpty())
                    addAll(it.pinned?.issues?.issues?.nodes.orEmpty())
                    addAll(it.search?.issues?.issues?.nodes.orEmpty())
                    addAll(it.searchIid?.issues?.issues?.nodes.orEmpty())
                }.filterNotNull().distinctBy { it.id }
            }
            loading = false
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
                item {
                    val focusManager = LocalFocusManager.current
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier
                            .onPreviewKeyEvent {
                                if (
                                    !it.isMetaPressed &&
                                    !it.isAltPressed &&
                                    !it.isCtrlPressed &&
                                    !it.isShiftPressed &&
                                    it.key == Key.Tab &&
                                    it.type == KeyEventType.KeyDown
                                    ) {
                                    focusManager.moveFocus(FocusDirection.Next)
                                    true
                                } else {
                                    false
                                }
                            }
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .then(if (search.isEmpty()) Modifier.height(0.dp).alpha(0f) else Modifier)
                    )
                    LaunchedEffect(darkTheme, useHighContrastColors) {
                        focusRequester.requestFocus()
                    }
                }
                itemsIndexed(issues.orEmpty().filter {
                    it.title.contains(search, ignoreCase = true) ||
                            it.id.contains(search, ignoreCase = true) ||
                            it.assignees?.nodes.orEmpty().filterNotNull().any {
                                it.name.contains(search, ignoreCase = true) ||
                                        it.username.contains(search, ignoreCase = true)
                            } ||
                            it.labels?.nodes.orEmpty().filterNotNull().any {
                                it.title.contains(search, ignoreCase = true)
                            }
                }) { index, issue ->
                    if (index != 0) HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Issue(issue)
                }
                if (loading) item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun Issue(issue: Issues.Node) {
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