package uk.co.newagedev.animalcompare.domain.model

import androidx.annotation.StringRes
import uk.co.newagedev.animalcompare.domain.R

sealed class AnimalType(@StringRes val animalName: Int, val endpoint: String) {
    object Dog : AnimalType(R.string.animal_dog, "https://random.dog/")
    object Cat : AnimalType(R.string.animal_cat, "https://aws.random.cat/")
    object Fox : AnimalType(R.string.animal_fox, "https://randomfox.ca/")

    override fun toString(): String {
        return when (this) {
            is Dog -> "dog"
            is Cat -> "cat"
            is Fox -> "fox"
        }
    }
}