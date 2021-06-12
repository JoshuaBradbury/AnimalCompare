package uk.co.newagedev.animalcompare.domain.model

import androidx.annotation.StringRes
import uk.co.newagedev.animalcompare.domain.R

sealed class AnimalType(@StringRes val animalName: Int, val endpoint: String) {
    object Dog : AnimalType(R.string.animal_dog, "https://random.dog/")

    override fun toString(): String {
        return when (this) {
            is Dog -> "dog"
        }
    }
}