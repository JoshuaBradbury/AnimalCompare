package uk.co.newagedev.animalcompare.ui.screens.review

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.request.ImageRequest
import coil.size.Precision
import com.google.accompanist.coil.rememberCoilPainter
import uk.co.newagedev.animalcompare.common.ImageSize
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison
import uk.co.newagedev.animalcompare.ui.R
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ComparisonCard(
    deleteComparison: (Int) -> Unit,
    comparison: AnimalComparison,
) {
    // In theory this is supposed to reset when tab changes, meaning that the expanded or not should
    // be unique to the different tabs, however in practice this doesn't seem to track, whether it
    // is a bug or misunderstanding of the documentation I do not know yet, but it is worth
    // investigating down the line
    val (expanded, updateExpanded) = rememberSaveable { mutableStateOf(false) }

    // Manages the animation of the expansion
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

    // Make it so anywhere tapped inside the cell will expand the view
    Column(modifier = Modifier.clickable {
        updateExpanded(!expanded)
    }) {
        CardSmall(comparison, fadeSmall)
        CardBody(comparison, expanded, deleteComparison)
    }
}

@Composable
private fun CardSmall(
    comparison: AnimalComparison,
    fadeSmall: Animatable<Float, AnimationVector1D>,
) {
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // This is the small image that displays in the unexpanded cell view. We could animate
        // it to the bigger view, however given that the images are completely different
        // resolutions and aspect rations it is cleaner to just fade this out.
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

        // Text showing what date and time the user swiped at
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

        // Indication of whether the cell is expanded or not using a dropdown style arrow
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
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CardBody(
    comparison: AnimalComparison,
    expanded: Boolean,
    deleteComparison: (Int) -> Unit,
) {
    // We need to store the large image size so that when the view recomposes and the image hasn't
    // loaded yet it will not have to resize the view, which would normally lead to views jumping
    val largeImageSize = remember { mutableStateOf(0) }

    // Load the large image using coil
    val largePainter = rememberCoilPainter(
        comparison.winner.url,
        fadeIn = true,
    )

    AnimatedVisibility(expanded) {
        // To convert the height from the pixels of the image itself, and the Dp used to set the
        // height we need a conversion, the best way is usually through some math, although
        // this could potentially be moved into a util function I haven't explored it so, TODO?
        val one = with(LocalDensity.current) {
            1.dp.toPx()
        }

        Column {
            // The larger image, where we also have to store it's height for later compositions
            Image(
                painter = largePainter,
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
