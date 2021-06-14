package uk.co.newagedev.animalcompare.ui.utils

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems


public fun <T : Any> LazyListScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount, key) { index ->
        itemContent(lazyPagingItems.getAsState(index).value)
    }
}