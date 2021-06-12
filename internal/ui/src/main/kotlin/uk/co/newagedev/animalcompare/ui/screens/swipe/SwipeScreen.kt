package uk.co.newagedev.animalcompare.ui.screens.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import coil.size.Precision
import com.google.accompanist.coil.LocalImageLoader
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import uk.co.newagedev.animalcompare.common.ImageSize
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.ui.R
import kotlin.math.absoluteValue

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
    val (choiceMade, updateChoiceMade) = remember { mutableStateOf<Pair<Animal, Animal>?>(null) }

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
        Text(
            stringResource(
                R.string.swipe_which_animal,
                stringResource(animalType.animalName)
            ),
            style = MaterialTheme.typography.h4,
            modifier = Modifier
                .padding(32.dp, 32.dp),
            textAlign = TextAlign.Center
        )

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

                    SwipeController(offsetX) { progress ->
                        // When we reach one end of the animation, be it left or right, we should
                        // submit which animal won the comparison, as the progress maps from -1f to
                        // 1f this is easily achieved below
                        when (progress) {
                            1f -> {
                                updateChoiceMade(animal2 to animal1)
                            }
                            -1f -> {
                                updateChoiceMade(animal1 to animal2)
                            }
                            else -> {
                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                ) {
                                    // Limit the card height to a fixed fraction, rather than
                                    // relying on auto constraints to make it a percentage of the
                                    // remaining screen height, probably a better way but this works
                                    // for now
                                    //
                                    // TODO: use a different dynamic layout to clean this up a bit
                                    val cardHeight = maxHeight * 0.4f

                                    Column(modifier = Modifier.fillMaxSize()) {
                                        CompositionLocalProvider(LocalImageLoader provides swipeViewModel.imageLoader) {
                                            SwipeableCard(
                                                cardHeight = cardHeight,
                                                animal = animal1,
                                                leftAligned = false,
                                                offset = -progress,
                                            )

                                            Spacer(modifier = Modifier.height(32.dp))

                                            SwipeableCard(
                                                cardHeight = cardHeight,
                                                animal = animal2,
                                                leftAligned = true,
                                                offset = progress,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
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
}

@Composable
fun SwipeLoading() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun SwipeableCard(
    cardHeight: Dp,
    animal: Animal,
    offset: Float,
    leftAligned: Boolean
) {
    // Load the image using coil, we don't display a loading screen when the images are still
    // loading, which we can do, but that is down the line work
    val painter = rememberCoilPainter(
        ImageRequest.Builder(LocalContext.current)
            .data(animal.url)
            .size(ImageSize.MEDIUM)
            .precision(Precision.EXACT)
            .build()
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = if (leftAligned) {
            Alignment.CenterStart
        } else {
            Alignment.CenterEnd
        }
    ) {
        val maxWidth = maxWidth

        val leftAlignedAdjustment = if (leftAligned) 1f else -1f

        val position: Dp
        val alpha: Float
        val rotation: Float

        if (offset > 0f) {
            // The animation parameters if this is the one you are selecting. It moves to just off
            // the edge of the screen, it stays visible the entire time, and after the half way
            // point it rotates 90 degrees in total
            position = maxWidth * offset
            alpha = 1f
            rotation = (offset - 0.5f).coerceAtLeast(0f) * 180f
        } else {
            // The animation parameters if this is the one you aren't selecting. It stays still,
            // but fades out, and is invisible by the half way point
            position = 0.dp
            alpha = 1f + offset * 2f
            rotation = 0f
        }

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .offset(position * leftAlignedAdjustment, 0.dp)
                .alpha(alpha)
                .rotate(rotation * leftAlignedAdjustment)
                .requiredHeight(cardHeight)
                .fillMaxWidth(0.6f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )
    }
}

@Composable
fun SwipeController(
    offsetX: Animatable<Float, AnimationVector1D>,
    updateAnimation: @Composable (progress: Float) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                val decay = splineBasedDecay<Float>(this)

                coroutineScope {
                    while (true) {
                        // Detect a touch down event.
                        val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                        val velocityTracker = VelocityTracker()

                        // Intercept an ongoing animation (if there's one).
                        offsetX.stop()
                        awaitPointerEventScope {
                            horizontalDrag(pointerId) { change ->
                                // Update the animation value with touch events.
                                launch {
                                    offsetX.snapTo(
                                        offsetX.value + change.positionChange().x
                                    )
                                }
                                velocityTracker.addPosition(
                                    change.uptimeMillis,
                                    change.position
                                )
                            }
                        }

                        val velocity = velocityTracker.calculateVelocity().x
                        val targetOffsetX = decay.calculateTargetValue(
                            offsetX.value,
                            velocity
                        )

                        // The animation stops when it reaches the bounds.
                        offsetX.updateBounds(
                            lowerBound = -size.width.toFloat(),
                            upperBound = size.width.toFloat()
                        )

                        launch {
                            if (targetOffsetX.absoluteValue <= size.width) {
                                // Not enough velocity; Slide back.
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    initialVelocity = velocity,
                                    animationSpec = tween(durationMillis = 1000)
                                )
                            } else {
                                // The element was swiped away.
                                offsetX.animateDecay(velocity, decay)
                            }
                        }
                    }
                }
            }) {
        // Normalise the offset based on the width of the screen, moving it into the range of -1f
        // to 1f
        val normalisedProgress = offsetX.value / with(LocalDensity.current) {
            maxWidth.toPx()
        }

        updateAnimation(normalisedProgress)
    }
}