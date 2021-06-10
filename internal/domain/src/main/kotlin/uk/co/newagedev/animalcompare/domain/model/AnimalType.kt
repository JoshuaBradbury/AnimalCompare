package uk.co.newagedev.animalcompare.domain.model

import androidx.annotation.StringRes
import uk.co.newagedev.animalcompare.domain.R

sealed class AnimalType(@StringRes val animalName: Int) {
    object Dog : AnimalType(R.string.animal_dog)
}