package uk.co.newagedev.animalcompare.ui.screens

import androidx.annotation.StringRes
import uk.co.newagedev.animalcompare.ui.R


sealed class Screen(val route: String, @StringRes val title: Int) {
    object Dogs : Screen("dog_swipe", R.string.screen_dog)
    object Review : Screen("review", R.string.screen_review)
    object TopAnimals : Screen("top_animals", R.string.screen_top_animals)

    companion object {
        val allScreens = listOf(
            Dogs,
            Review,
            TopAnimals,
        )
    }
}