package uk.co.newagedev.animalcompare.ui.screens.favourites

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.newagedev.animalcompare.domain.room.relations.FavouriteAnimal
import uk.co.newagedev.animalcompare.ui.utils.AnimalTab
import uk.co.newagedev.animalcompare.ui.utils.AnimalTabs

@Composable
fun FavouritesScreen(viewModel: FavouritesViewModel = hiltViewModel()) {
    val (currentTab, updateCurrentTab) = rememberSaveable { mutableStateOf<AnimalTab>(AnimalTab.All) }

    val favourites = viewModel.getFavourites(currentTab.toFilter()).collectAsState(emptyList())

    AnimalTabs(
        currentTab = currentTab,
        updateCurrentTab = updateCurrentTab,
        tabs = AnimalTab.values,
    ) {
        FavouriteList(favourites.value)
    }
}

@Composable
fun FavouriteList(
    favourites: List<FavouriteAnimal>,
) {
    Column {
        favourites.forEach {
            FavouriteAnimal(it)
        }
    }
}

@Composable
fun FavouriteAnimal(
    animal: FavouriteAnimal,
) {
    Text(text = animal.toString())
}