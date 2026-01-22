package com.youniqx.time.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator

class NavEntryProviderDecorator<T : Any> :
    NavEntryDecorator<T>(
        decorate = { entry ->
            CompositionLocalProvider(
                LocalNavEntry provides entry,
                content = entry::Content
            )
        },
    )

@Composable
fun <T : Any> rememberNavEntryProviderDecorator() = remember { NavEntryProviderDecorator<T>() }
