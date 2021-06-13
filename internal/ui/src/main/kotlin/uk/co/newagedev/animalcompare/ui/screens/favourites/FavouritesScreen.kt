package uk.co.newagedev.animalcompare.ui.screens.favourites

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import uk.co.newagedev.animalcompare.domain.room.relations.FavouriteAnimal
import uk.co.newagedev.animalcompare.ui.R
import uk.co.newagedev.animalcompare.ui.utils.AnimalTab
import uk.co.newagedev.animalcompare.ui.utils.AnimalTabs
import java.time.format.DateTimeFormatter

private const val MIN_FAVOURITES = 10
private const val FAVOURITE_THRESHOLD = 2

@Composable
fun FavouritesScreen(viewModel: FavouritesViewModel = hiltViewModel()) {
    val (currentTab, updateCurrentTab) = rememberSaveable { mutableStateOf<AnimalTab>(AnimalTab.All) }

    val favourites = viewModel.getFavourites(currentTab.toFilter()).collectAsState(null)

    // Custom animal tabs composable to cleanup the root favourites screen composable
    AnimalTabs(
        currentTab = currentTab,
        updateCurrentTab = updateCurrentTab,
        tabs = AnimalTab.values,
    ) {
        // If we haven't finished loading any favourites yet, we should show a progress indicator
        // rather than the not enough swipes screen, in case the user has swiped enough
        if (favourites.value == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        } else if (favourites.value!!.size < MIN_FAVOURITES || favourites.value!!.any { it.count < FAVOURITE_THRESHOLD }) {
            // Checks if the user has enough animals that meet the count and vote thresholds, if not
            // we should let them know they need to keep swiping
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.favourite_not_enough),
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(32.dp),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            FavouriteList(favourites.value!!)
        }
    }
}

@Composable
fun FavouriteList(
    favourites: List<FavouriteAnimal>,
) {
    var currentRank = 0
    var currentRankValue = Integer.MAX_VALUE

    val scrollState = rememberScrollState()

    // Count how many images have successfully loaded, this way we can show the UI in one go,
    // without fear of the views jumping
    var loadCount by rememberSaveable(favourites) { mutableStateOf(0) }

    // Tracks when the content should be shown and animates it for a cleaner look and feel
    val (showContent, updateShowContent) = rememberSaveable(favourites) { mutableStateOf(loadCount >= favourites.size) }
    val loadedAnim = remember(favourites) { Animatable(if (showContent) 1f else 0f) }
    LaunchedEffect(showContent) {
        loadedAnim.animateTo(if (showContent) 1f else 0f)
    }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .alpha(loadedAnim.value),
    ) {
        favourites.forEachIndexed { index, animal ->
            // The ranks should be the same if they share the same value, being the highest rank possible,
            // but once there is one with a lower value it should be at it's current indexes rank, rather
            // than an index below it
            if (animal.count < currentRankValue) {
                currentRank = index + 1
                currentRankValue = animal.count
            }

            key(animal.animal.id) {
                FavouriteAnimal(currentRank, animal) {
                    loadCount += 1
                    if (loadCount >= favourites.size) {
                        updateShowContent(true)
                    }
                }
            }
        }
    }

    // Progress indicator while we wait for the images to load
    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(1f - loadedAnim.value),
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun FavouriteAnimal(
    rank: Int,
    favourite: FavouriteAnimal,
    loaded: () -> Unit,
) {
    // Load the large image using coil
    val largePainter = rememberCoilPainter(
        favourite.animal.url,
        fadeIn = true,
    )

    // When the image has loaded we should propagate it up to the parent, so that the views can be
    // displayed uniformly
    LaunchedEffect(largePainter.loadState) {
        if (largePainter.loadState is ImageLoadState.Success) {
            loaded()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Rank title
        Text(
            text = stringResource(
                R.string.favourite_rank,
                rank,
                favourite.lastSwiped.format(DateTimeFormatter.ISO_LOCAL_DATE),
                favourite.lastSwiped.format(DateTimeFormatter.ISO_LOCAL_TIME),
            ),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(8.dp)
        )

        Image(
            painter = largePainter,
            contentDescription = stringResource(id = R.string.swipe_animal_desc),
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    }
}