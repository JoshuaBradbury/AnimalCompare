package uk.co.newagedev.animalcompare.domain.room.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uk.co.newagedev.animalcompare.domain.model.Comparison
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison

@Dao
interface ComparisonDao {

    @Transaction
    @Query("SELECT * FROM comparisons")
    fun getAllComparisons(): Flow<List<AnimalComparison>>

    @Delete
    suspend fun deleteComparison(comparison: Comparison)

    @Insert
    suspend fun addComparison(comparison: Comparison)
}