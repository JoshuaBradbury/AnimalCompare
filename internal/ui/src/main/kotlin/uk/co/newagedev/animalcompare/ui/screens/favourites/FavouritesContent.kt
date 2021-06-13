package uk.co.newagedev.animalcompare.ui.screens.favourites

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import uk.co.newagedev.animalcompare.domain.room.relations.FavouriteAnimal
import uk.co.newagedev.animalcompare.ui.R
import java.time.format.DateTimeFormatter

@Composable
fun FavouriteProgress(alpha: Float = 1f) {
    // Progress indicator while we wait for the images to load

    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
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