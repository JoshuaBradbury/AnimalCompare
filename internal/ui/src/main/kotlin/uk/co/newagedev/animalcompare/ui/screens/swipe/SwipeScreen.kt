package uk.co.newagedev.animalcompare.ui.screens.swipe

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import coil.size.Precision
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import uk.co.newagedev.animalcompare.common.ImageSize
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalInBacklog
import uk.co.newagedev.animalcompare.ui.utils.decayBasedSwipeAnimation

@Composable
fun SwipeScreen(
    animalType: AnimalType,
    swipeViewModel: SwipeViewModel = hiltViewModel()
) {
    // The Flow that holds the state of the current comparison to display
    val animalFlow = swipeViewModel
        .getAnimalFlow(animalType)
        .collectAsState(initial = ComparisonState.Loading)

    // The progress of the animation, it is hoisted here to be better shared between the
    // SwipeController and also so that we can reset it when a new comparison has loaded
    val offsetX = remember { Animatable(0f) }

    // Hold the state of whether a choice has been made or not, so that we can hide the swipe view
    // and reset it off screen, removing any jumps or other visual side effects
    val (choiceMade, updateChoiceMade) = remember {
        mutableStateOf<Pair<AnimalInBacklog, AnimalInBacklog>?>(
            null
        )
    }

    // Every time the choiceMade variable updates, we should check if it has a value, without
    // blocking the UI, and if so we should submit it and reset the swipe view
    LaunchedEffect(choiceMade) {
        if (choiceMade != null) {
            swipeViewModel.submitSwipe(choiceMade.first, choiceMade.second)
            offsetX.stop()
            offsetX.snapTo(0f)
        }
    }

    // Every time we receive a new value in the flow, we should display it. A success will only be
    // received when the comparison has already been submitted, so that it will have the correct
    // latest value, without having to check here
    LaunchedEffect(animalFlow.value) {
        updateChoiceMade(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SwipeScreenTop(animalType)

        SwipeScreenBody(offsetX, animalFlow, choiceMade, updateChoiceMade)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SwipeScreenBody(
    offsetX: Animatable<Float, AnimationVector1D>,
    animalFlow: State<ComparisonState>,
    choiceMade: Pair<AnimalInBacklog, AnimalInBacklog>?,
    updateChoiceMade: (Pair<AnimalInBacklog, AnimalInBacklog>?) -> Unit,
) {
    // Track number of swipes completed so that we can eventually hide the swipe arrows
    val swipeCount = rememberSaveable { mutableStateOf(0) }
    // Increment swipe count when a choice has been made
    LaunchedEffect(choiceMade) {
        if (choiceMade != null) {
            swipeCount.value += 1
        }
    }

    // If a choice has been made lets just display a simple progress icon so that the user knows
    // their choice is being submitted, with some near immediate feedback
    if (choiceMade != null) {
        SwipeLoading()
    } else {
        when (animalFlow.value) {
            is ComparisonState.Loading -> {
                SwipeLoading()
            }
            is ComparisonState.Success -> {
                val (animal1, animal2) = animalFlow.value as ComparisonState.Success

                SwipeAnimals(
                    animal1,
                    animal2,
                    offsetX,
                    updateChoiceMade,
                    swipeCount.value,
                )
            }
            is ComparisonState.Error -> {
                // The most common error will be a no such element, meaning the list is empty
                // so we should let the user know we are loading more
                Text(
                    "Loading more...",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                )

                SwipeLoading()
            }
        }
    }
}

@Composable
fun SwipeAnimals(
    animal1: AnimalInBacklog,
    animal2: AnimalInBacklog,
    offsetX: Animatable<Float, AnimationVector1D>,
    updateChoiceMade: (Pair<AnimalInBacklog, AnimalInBacklog>?) -> Unit,
    swipeCount: Int,
) {
    // Load the images using coil
    val animal1Painter = rememberCoilPainter(
        ImageRequest.Builder(LocalContext.current)
            .data(animal1.animal.url)
            .size(ImageSize.MEDIUM)
            .precision(Precision.EXACT)
            .build()
    )

    val animal2Painter = rememberCoilPainter(
        ImageRequest.Builder(LocalContext.current)
            .data(animal2.animal.url)
            .size(ImageSize.MEDIUM)
            .precision(Precision.EXACT)
            .build()
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .let {
                if (animal1Painter.loadState is ImageLoadState.Success && animal2Painter.loadState is ImageLoadState.Success) {
                    it.decayBasedSwipeAnimation(offsetX)
                } else {
                    it
                }
            }
    ) {
        // Normalise the offset based on the width of the screen, moving it into the range of -1f
        // to 1f
        val normalisedProgress = offsetX.value / with(LocalDensity.current) {
            maxWidth.toPx()
        }

        // When we reach one end of the animation, be it left or right, we should
        // submit which animal won the comparison, as the progress maps from -1f to
        // 1f this is easily achieved below
        when (normalisedProgress) {
            1f -> {
                updateChoiceMade(animal2 to animal1)
            }
            -1f -> {
                updateChoiceMade(animal1 to animal2)
            }
            else -> {
                if (animal1Painter.loadState is ImageLoadState.Success && animal2Painter.loadState is ImageLoadState.Success) {
                    SwipeOptions(
                        animal1Painter,
                        animal2Painter,
                        normalisedProgress,
                        swipeCount,
                    )
                } else {
                    SwipeLoading()
                }
            }
        }
    }
}