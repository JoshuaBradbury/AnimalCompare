package uk.co.newagedev.animalcompare.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import uk.co.newagedev.animalcompare.data.room.AppDatabase
import uk.co.newagedev.animalcompare.domain.model.AnimalFilter
import uk.co.newagedev.animalcompare.domain.model.Comparison
import uk.co.newagedev.animalcompare.domain.model.ComparisonBacklog
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComparisonRepository @Inject constructor(
    private val db: AppDatabase,
) {
    suspend fun submitComparison(comparison: Comparison) {
        db.withTransaction {
            db.comparisonDao().addComparison(comparison)

            db.comparisonBacklogDao().removeFromBacklog(
                listOf(
                    ComparisonBacklog(comparison.winner),
                    ComparisonBacklog(comparison.loser)
                )
            )
        }
    }

    fun getComparisons(
        animalFilter: AnimalFilter,
        pageSize: Int
    ): Flow<PagingData<AnimalComparison>> {
        return Pager(
            config = PagingConfig(pageSize)
        ) {
            when (animalFilter) {
                AnimalFilter.ALL -> db.comparisonDao().getAllComparisons()
                else -> db.comparisonDao().getAllComparisonsByType(animalFilter.toType())
            }
        }.flow
    }
}