package uk.co.newagedev.animalcompare.ui.screens.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.request.ImageRequest
import coil.size.Precision
import com.google.accompanist.coil.rememberCoilPainter
import uk.co.newagedev.animalcompare.common.ImageSize
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison
import uk.co.newagedev.animalcompare.ui.R
import uk.co.newagedev.animalcompare.ui.utils.AnimalTab
import uk.co.newagedev.animalcompare.ui.utils.AnimalTabs
import java.time.format.DateTimeFormatter


@Composable
fun ReviewScreen(viewModel: ReviewViewModel = hiltViewModel()) {
    val (currentTab, updateCurrentTab) = rememberSaveable { mutableStateOf<AnimalTab>(AnimalTab.All) }

    val lazyComparisons = viewModel.getComparisons(currentTab.toFilter()).collectAsLazyPagingItems()

    AnimalTabs(
        currentTab = currentTab,
        updateCurrentTab = updateCurrentTab,
        tabs = listOf(
            AnimalTab.All,
            AnimalTab.Dog,
        )
    ) {
        ReviewList(lazyComparisons)
    }
}

@Composable
private fun ReviewList(
    lazyComparisons: LazyPagingItems<AnimalComparison>,
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
    ) {
        items(lazyComparisons) { comparison ->
            if (comparison == null) {
                ComparisonPlaceholder()
            } else {
                ComparisonCard(comparison)
            }
        }
    }
}

@Composable
fun ComparisonCard(
    comparison: AnimalComparison,
) {
    val (expanded, updateExpanded) = rememberSaveable { mutableStateOf(false) }

    // Load the image using coil, we don't display a loading screen when the images are still
    // loading, which we can do, but that is down the line work
    val painter = rememberCoilPainter(
        ImageRequest.Builder(LocalContext.current)
            .data(comparison.winner.url)
            .size(ImageSize.SMALL)
            .precision(Precision.EXACT)
            .build()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable {
                updateExpanded(!expanded)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(id = R.string.swipe_animal_desc),
            modifier = Modifier
                .background(if (expanded) Color.Red else Color.Blue)
                .padding(horizontal = 8.dp)
                .requiredSize(80.dp),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )

        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = stringResource(
                R.string.review_chose_on,
                comparison.dateCompared.format(DateTimeFormatter.ISO_LOCAL_DATE),
                comparison.dateCompared.format(DateTimeFormatter.ISO_LOCAL_TIME),
            ),
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
fun ComparisonPlaceholder() {
    Row(
        modifier = Modifier
            .height(48.dp)
            .background(Color.Red)
    ) {

    }
}