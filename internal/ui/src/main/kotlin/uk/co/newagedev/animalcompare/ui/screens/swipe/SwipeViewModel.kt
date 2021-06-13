package uk.co.newagedev.animalcompare.ui.screens.swipe

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import uk.co.newagedev.animalcompare.data.repositories.AnimalRepository
import uk.co.newagedev.animalcompare.data.repositories.ComparisonRepository
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.domain.model.Comparison
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val comparisonRepository: ComparisonRepository,
) : ViewModel() {

    @OptIn(FlowPreview::class)
    fun getAnimalFlow(animalType: AnimalType): Flow<ComparisonState> {
        return animalRepository
            .getComparisonBacklog(animalType)
            .map {
                ComparisonState.Success(it.first, it.second) as ComparisonState
            }.catch {
                emit(ComparisonState.Error(it))
            }
            // Recommended to reduce unnecessary updates if the data doesn't change
            .distinctUntilChanged()
    }

    suspend fun submitSwipe(winner: Animal, loser: Animal) {
        // Insert with a zero ID, this is the default and will be auto generated
        comparisonRepository.submitComparison(
            Comparison(
                0,
                winner.id,
                loser.id,
                LocalDateTime.now(),
            )
        )
    }
}