package uk.co.newagedev.animalcompare.ui.screens.review

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison
import uk.co.newagedev.animalcompare.ui.utils.AnimalTab
import uk.co.newagedev.animalcompare.ui.utils.AnimalTabs
import uk.co.newagedev.animalcompare.ui.utils.items


@Composable
fun ReviewScreen(viewModel: ReviewViewModel = hiltViewModel()) {
    val (currentTab, updateCurrentTab) = rememberSaveable { mutableStateOf<AnimalTab>(AnimalTab.All) }

    val lazyComparisons = viewModel.getComparisons(currentTab.toFilter()).collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()

    // Custom animal tabs composable to cleanup the root review screen composable
    AnimalTabs(
        currentTab = currentTab,
        updateCurrentTab = updateCurrentTab,
        tabs = AnimalTab.values,
    ) {
        ReviewList(it, {
            coroutineScope.launch {
                viewModel.deleteComparison(it)
            }
        }, lazyComparisons)
    }
}

@Composable
private fun ReviewList(
    tab: AnimalTab,
    deleteComparison: (Int) -> Unit,
    lazyComparisons: LazyPagingItems<AnimalComparison>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        // We use a custom items function here so we can set the key properly. For some reason the
        // paging library for compose is yet to add this, but it's important so that the list
        // doesn't assume that a removal of a comparison is us just updating an existing cell,
        // because the key is normally based on the position, so here we need to base it on the id.
        // This can potentially crash if the data has yet to load, but it should have by this point
        // being based on a room DB locally, if there was a network mediator in the mix it might not
        // be possible to do this as cleanly
        items(lazyComparisons, key = {
            lazyComparisons.peek(it)!!.id
        }) { comparison ->
            if (comparison == null) {
                ComparisonPlaceholder()
            } else {
                key(comparison.id, tab) {
                    ComparisonCard(deleteComparison, comparison)
                }
            }
        }
    }
}

@Composable
fun ComparisonPlaceholder() {
    // Placeholder cell, we can display some mock data or greyed out versions of the view, but for
    // now as long as the height is right it won't cause any jumping
    Row(
        modifier = Modifier
            .height(96.dp)
    ) {
    }
}