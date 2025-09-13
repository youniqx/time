package com.youniqx.time

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.youniqx.time.gitlab.models.CurrentSprintQuery
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(token: String = "") {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var response: ApolloResponse<CurrentSprintQuery.Data>? by remember { mutableStateOf(null) }

        val isPreview = LocalInspectionMode.current
        LaunchedEffect(true) {
            if (isPreview) return@LaunchedEffect
            // Create a client
            val apolloClient = ApolloClient.Builder()
                .serverUrl("https://gitlab.ci.youniqx.com/api/graphql?private_token=$token")
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
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
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