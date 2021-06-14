package uk.co.newagedev.animalcompare.ui.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import uk.co.newagedev.animalcompare.ui.R


sealed class Screen(
    val route: String,
    @StringRes val title: Int,
    val getIcon: @Composable () -> ImageVector,
) {
    object Dogs : Screen(
        "dog_swipe",
        R.string.tab_dogs,
        { ImageVector.vectorResource(R.drawable.ic_dog) },
    )

    object Cats : Screen(
        "cat_swipe",
        R.string.tab_cats,
        { ImageVector.vectorResource(R.drawable.ic_cat) },
    )

    object Foxes : Screen(
        "fox_swipe",
        R.string.tab_foxes,
        { ImageVector.vectorResource(R.drawable.ic_fox) },
    )

    object Review : Screen(
        "review",
        R.string.screen_review,
        { ImageVector.vectorResource(R.drawable.ic_review) },
    )

    object TopAnimals : Screen(
        "top_animals",
        R.string.screen_top_animals,
        { Icons.Filled.Favorite },
    )

    companion object {
        val allScreens = listOf(
            Dogs,
            Cats,
            Foxes,
            Review,
            TopAnimals,
        )
    }
}