package com.youniqx.time

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex

private data class StickyType(val contentType: Any?)

private fun LazyListState.stickyInfoFor(index: Int): State<Int> {
    return derivedStateOf {
        val upcomingStickyHeaders = layoutInfo.visibleItemsInfo
            .take(10)
            .filter { it.contentType is StickyType }
            .iterator()
        upcomingStickyHeaders.forEach { firstSticky ->
            if (firstSticky.index == index) {
                val offset = (-firstSticky.offset).coerceAtLeast(0)
                return@derivedStateOf if (upcomingStickyHeaders.hasNext()) {
                    val secondSticky = upcomingStickyHeaders.next()
                    offset - (firstSticky.size - secondSticky.offset).coerceAtLeast(0)
                } else offset
            }
        }
        0
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.stickyHeader(
    listState: LazyListState,
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.(index: Int, isSticky: Boolean) -> Unit
) {
    stickyHeader(
        key = key,
        contentType = StickyType(contentType),
        content = { index ->
            val stickyInfo by remember(listState) { listState.stickyInfoFor(index) }
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = 0, y = stickyInfo) }
                    .zIndex(10f)
            ) {
                content(index, stickyInfo > 0)
            }
        }
    )
}
