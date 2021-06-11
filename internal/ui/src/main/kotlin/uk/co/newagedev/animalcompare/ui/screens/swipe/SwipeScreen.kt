package uk.co.newagedev.animalcompare.ui.screens.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import uk.co.newagedev.animalcompare.domain.fake.FakeAnimal
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.ui.R
import uk.co.newagedev.animalcompare.ui.theme.AnimalCompareTheme
import kotlin.math.absoluteValue

@Composable
fun SwipeScreen(animalType: AnimalType, swipeViewModel: SwipeViewModel = hiltViewModel()) {
    val animalFlow =
        swipeViewModel.getAnimalFlow(animalType).collectAsState(initial = ComparisonState.Loading)

    val composableScope = rememberCoroutineScope()

    when (animalFlow.value) {
        is ComparisonState.Loading -> {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        is ComparisonState.Success -> {
            Comparison(
                animalType = animalType,
                animal1 = (animalFlow.value as ComparisonState.Success).animal1,
                animal2 = (animalFlow.value as ComparisonState.Success).animal2
            ) { winner, loser ->
                composableScope.launch {
                    swipeViewModel.submitSwipe(winner, loser)
                }
            }
        }
        is ComparisonState.Error -> {

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Comparison(
    animalType: AnimalType,
    animal1: Animal,
    animal2: Animal,
    onComparisonMade: (winner: Animal, loser: Animal) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            stringResource(R.string.swipe_which_animal, stringResource(animalType.animalName)),
            style = MaterialTheme.typography.h4,
            modifier = Modifier
                .padding(32.dp, 32.dp),
            textAlign = TextAlign.Center
        )

        SwipeController { progress ->
            if (progress == 1f) {
                onComparisonMade(animal2, animal1)
            } else if (progress == -1f) {
                onComparisonMade(animal1, animal2)
            }

            Column {
                SwipeableCard(animal = animal1, leftAligned = false, offset = -progress)
                Spacer(modifier = Modifier.height(32.dp))
                SwipeableCard(animal = animal2, leftAligned = true, offset = progress)
            }
        }
    }
}

@Composable
fun SwipeController(useTransitionData: @Composable (progress: Float) -> Unit) {
    val offsetX = remember { Animatable(0f) }

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
        val normalizedOffset = offsetX.value / with(LocalDensity.current) {
            maxWidth.toPx()
        }

        useTransitionData(normalizedOffset)
    }
}

@Composable
fun SwipeableCard(animal: Animal, offset: Float, leftAligned: Boolean) {
    val painter = rememberCoilPainter(animal.url)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        val maxWidth = maxWidth

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = if (leftAligned) {
                Arrangement.Start
            } else {
                Arrangement.End
            }
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .offset(
                        (if (offset > 0f) maxWidth * offset else 0.dp) * (if (leftAligned) 1f else -1f),
                        0.dp
                    )
                    .alpha(if (offset < 0f) 1f + offset * 2f else 1f)
                    .rotate(if (offset > 0f) (offset - 0.5f).coerceAtLeast(0f) * 180f * (if (leftAligned) 1f else -1f) else 0f)
                    .fillMaxWidth(0.6f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Dog Swipe Screen Light Theme")
@Composable
fun DogSwipeScreenLightTheme() {
    AnimalCompareTheme(darkTheme = false) {
        Comparison(
            animalType = AnimalType.Dog,
            animal1 = FakeAnimal.getFakeAnimal(AnimalType.Dog),
            animal2 = FakeAnimal.getFakeAnimal(AnimalType.Dog),
        ) { _, _ -> }
    }
}

@Preview(showBackground = true, name = "Dog Swipe Screen Dark Theme")
@Composable
fun DogSwipeScreenDarkTheme() {
    AnimalCompareTheme(darkTheme = true) {
        Comparison(
            animalType = AnimalType.Dog,
            animal1 = FakeAnimal.getFakeAnimal(AnimalType.Dog),
            animal2 = FakeAnimal.getFakeAnimal(AnimalType.Dog),
        ) { _, _ -> }
    }
}