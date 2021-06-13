package uk.co.newagedev.animalcompare.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import uk.co.newagedev.animalcompare.data.repositories.ComparisonRepository
import uk.co.newagedev.animalcompare.domain.model.AnimalFilter
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison
import javax.inject.Inject

private const val PAGE_SIZE = 30

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val comparisonRepository: ComparisonRepository,
) : ViewModel() {

    fun getComparisons(animalFilter: AnimalFilter): Flow<PagingData<AnimalComparison>> {
        return comparisonRepository
            .getComparisons(animalFilter, PAGE_SIZE)
            .cachedIn(viewModelScope)
    }
}