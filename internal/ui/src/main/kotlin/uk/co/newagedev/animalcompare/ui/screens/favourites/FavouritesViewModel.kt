package uk.co.newagedev.animalcompare.ui.screens.favourites

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import uk.co.newagedev.animalcompare.data.repositories.ComparisonRepository
import uk.co.newagedev.animalcompare.domain.model.AnimalFilter
import uk.co.newagedev.animalcompare.domain.room.relations.FavouriteAnimal
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val comparisonRepository: ComparisonRepository,
) : ViewModel() {

    fun getFavourites(animalFilter: AnimalFilter): Flow<List<FavouriteAnimal>> {
        return comparisonRepository
            .getFavourites(animalFilter)
            .distinctUntilChanged()
    }
}