package uk.co.newagedev.animalcompare.ui.screens.swipe

import uk.co.newagedev.animalcompare.domain.model.Animal
import java.lang.Exception

sealed class ComparisonState {
    object Loading: ComparisonState()
    data class Success(val animal1: Animal, val animal2: Animal): ComparisonState()
    data class Error(val exception: Throwable?): ComparisonState()
}
