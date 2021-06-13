package uk.co.newagedev.animalcompare.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import uk.co.newagedev.animalcompare.data.room.AppDatabase
import uk.co.newagedev.animalcompare.domain.model.AnimalFilter
import uk.co.newagedev.animalcompare.domain.model.Comparison
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalInBacklog
import uk.co.newagedev.animalcompare.domain.room.relations.FavouriteAnimal
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val FAVOURITE_LIMIT = 10

@Singleton
class ComparisonRepository @Inject constructor(
    private val db: AppDatabase,
) {
    suspend fun submitComparison(winner: AnimalInBacklog, loser: AnimalInBacklog) {
        db.withTransaction {
            db.comparisonDao().addComparison(
                Comparison(
                    0,
                    winner.animal.id,
                    loser.animal.id,
                    LocalDateTime.now()
                )
            )

            db.comparisonBacklogDao().removeFromBacklog(
                listOf(winner.comparisonBacklog, loser.comparisonBacklog)
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

    suspend fun deleteComparison(id: Int) {
        db.withTransaction {
            val comparison = db.comparisonDao().getById(id)
            db.comparisonDao().deleteComparison(comparison)
        }
    }

    fun getFavourites(animalFilter: AnimalFilter): Flow<List<FavouriteAnimal>> {
        return when (animalFilter) {
            AnimalFilter.ALL -> db.comparisonDao().getAllFavourites(FAVOURITE_LIMIT)
            else -> db.comparisonDao().getFavouritesBy(animalFilter.toType(), FAVOURITE_LIMIT)
        }
    }
}