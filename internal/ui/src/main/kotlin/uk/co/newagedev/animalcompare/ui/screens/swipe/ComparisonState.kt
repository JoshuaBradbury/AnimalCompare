package uk.co.newagedev.animalcompare.ui.screens.swipe

import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalInBacklog
import java.lang.Exception

sealed class ComparisonState {
    object Loading: ComparisonState()
    data class Success(val animal1: AnimalInBacklog, val animal2: AnimalInBacklog): ComparisonState()
    data class Error(val exception: Throwable?): ComparisonState()
}
