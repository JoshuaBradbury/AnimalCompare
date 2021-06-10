package uk.co.newagedev.animalcompare.ui.screens.swipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.coil.rememberCoilPainter
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.ui.R
import uk.co.newagedev.animalcompare.ui.theme.AnimalCompareTheme

@Composable
fun SwipeScreen(animalType: AnimalType, swipeViewModel: SwipeViewModel = hiltViewModel()) {
    val animalFlow =
        swipeViewModel.getAnimalFlow(animalType).collectAsState(initial = ComparisonState.Loading)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            stringResource(R.string.swipe_which_animal, stringResource(animalType.animalName)),
            style = MaterialTheme.typography.h4,
            modifier = Modifier
                .padding(32.dp, 8.dp),
            textAlign = TextAlign.Center
        )

        when (animalFlow.value) {
            is ComparisonState.Loading -> {
                CircularProgressIndicator()
            }
            is ComparisonState.Success -> {
                SwipeableCard((animalFlow.value as ComparisonState.Success).animal1)
                SwipeableCard((animalFlow.value as ComparisonState.Success).animal2)
            }
            is ComparisonState.Error -> {

            }
        }
    }
}

@Composable
fun SwipeableCard(animal: Animal) {
    val painter = rememberCoilPainter(animal.url)

    Image(
        painter = painter,
        contentDescription = null,
    )
}

@Preview(showBackground = true, name = "Dog Swipe Screen Light Theme")
@Composable
fun DogSwipeScreenLightTheme() {
    AnimalCompareTheme(darkTheme = false) {
        SwipeScreen(animalType = AnimalType.Dog)
    }
}

@Preview(showBackground = true, name = "Dog Swipe Screen Dark Theme")
@Composable
fun DogSwipeScreenDarkTheme() {
    AnimalCompareTheme(darkTheme = true) {
        SwipeScreen(animalType = AnimalType.Dog)
    }
}