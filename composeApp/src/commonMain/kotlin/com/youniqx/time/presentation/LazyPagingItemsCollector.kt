package com.youniqx.time.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T : Any> PagingData<T>.collectAsLazyPagingItems(): LazyPagingItems<T> = let {
    remember(it) { flowOf(it) }.collectAsLazyPagingItems()
}