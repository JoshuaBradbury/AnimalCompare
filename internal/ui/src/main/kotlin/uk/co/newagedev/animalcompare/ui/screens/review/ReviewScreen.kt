package uk.co.newagedev.animalcompare.ui.screens.review

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.request.ImageRequest
import coil.size.Precision
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.launch
import uk.co.newagedev.animalcompare.common.ImageSize
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison
import uk.co.newagedev.animalcompare.ui.R
import uk.co.newagedev.animalcompare.ui.utils.AnimalTab
import uk.co.newagedev.animalcompare.ui.utils.AnimalTabs
import uk.co.newagedev.animalcompare.ui.utils.items
import java.time.format.DateTimeFormatter


@Composable
fun ReviewScreen(viewModel: ReviewViewModel = hiltViewModel()) {
    val (currentTab, updateCurrentTab) = rememberSaveable { mutableStateOf<AnimalTab>(AnimalTab.All) }

    val lazyComparisons = viewModel.getComparisons(currentTab.toFilter()).collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()

    AnimalTabs(
        currentTab = currentTab,
        updateCurrentTab = updateCurrentTab,
        tabs = listOf(
            AnimalTab.All,
            AnimalTab.Dog,
        )
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
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
    ) {
        items(lazyComparisons, key = {
            lazyComparisons.peek(it)!!.id
        }) { comparison ->
            if (comparison == null) {
                ComparisonPlaceholder()
            } else {
                ComparisonCard(tab, deleteComparison, comparison)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ComparisonCard(
    tab: AnimalTab,
    deleteComparison: (Int) -> Unit,
    comparison: AnimalComparison,
) {
    // In theory this is supposed to reset when tab changes, meaning that the expanded or not should
    // be unique to the different tabs, however in practice this doesn't seem to track, whether it
    // is a bug or misunderstanding of the documentation I do not know yet, but it is worth
    // investigating down the line
    val (expanded, updateExpanded) = rememberSaveable(tab) { mutableStateOf(false) }

    val largeImageSize = rememberSaveable { mutableStateOf(0) }

    // Load the image using coil, we don't display a loading screen when the images are still
    // loading, which we can do, but that is down the line work
    val smallPainter = rememberCoilPainter(
        ImageRequest.Builder(LocalContext.current)
            .data(comparison.winner.url)
            .size(ImageSize.SMALL)
            .precision(Precision.EXACT)
            .build(),
        fadeIn = true,
    )

    val mediumPainter = rememberCoilPainter(
        comparison.winner.url,
        fadeIn = true,
    )

    val fadeSmall = remember { Animatable(if (expanded) 0f else 1f) }
    LaunchedEffect(expanded) {
        fadeSmall.animateTo(
            if (expanded) {
                0f
            } else {
                1f
            }
        )
    }

    Column(modifier = Modifier.clickable {
        updateExpanded(!expanded)
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = smallPainter,
                contentDescription = stringResource(id = R.string.swipe_animal_desc),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .requiredSize(80.dp)
                    .alpha(fadeSmall.value),
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

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_dropdown),
                    contentDescription = stringResource(R.string.review_expand_desc),
                    modifier = Modifier
                        .rotate(90f * fadeSmall.value)
                        .offset(12.dp)
                        .padding(end = 24.dp)
                        .size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                )
            }
        }

        AnimatedVisibility(expanded) {
            val one = with(LocalDensity.current) {
                1.dp.toPx()
            }

            Column {
                Image(
                    painter = mediumPainter,
                    contentDescription = stringResource(id = R.string.swipe_animal_desc),
                    modifier = Modifier
                        .fillMaxWidth()
                        .let {
                            if (largeImageSize.value > 0) {
                                it.height(Dp(largeImageSize.value / one))
                            } else {
                                it
                            }
                        }
                        .onGloballyPositioned {
                            largeImageSize.value = it.size.height
                        },
                    contentScale = ContentScale.FillWidth,
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(4.dp),
                    onClick = {
                        deleteComparison(comparison.id)
                    },
                    colors = ButtonDefaults.textButtonColors(),
                ) {
                    Text(
                        text = stringResource(id = R.string.review_delete_swipe),
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonPlaceholder() {
    Row(
        modifier = Modifier
            .height(96.dp)
    ) {

    }
}