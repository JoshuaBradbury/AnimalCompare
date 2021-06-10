package uk.co.newagedev.animalcompare.ui.screens.swipe

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import uk.co.newagedev.animalcompare.data.AnimalRepository
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
) : ViewModel() {

    fun getAnimalFlow(animalType: AnimalType): Flow<ComparisonState> {
        return animalRepository
            .getAnimalFlow(animalType)
            .map {
                if (it == null) {
                    ComparisonState.Loading
                } else {
                    ComparisonState.Success(it.first, it.second)
                }
            }.catch {
                emit(ComparisonState.Error(it))
            }
    }

    suspend fun submitSwipe(winner: Animal, loser: Animal) {
        animalRepository.submitWinner(winner)
        animalRepository.submitLoser(loser)
    }
}