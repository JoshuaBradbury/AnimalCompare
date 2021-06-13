package uk.co.newagedev.animalcompare.data.room.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.domain.model.ComparisonBacklog
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalInBacklog

@Dao
interface ComparisonBacklogDao {

    @Transaction
    @Query(
        """
        SELECT to_compare.id, 
        to_compare.animal
        FROM to_compare
        LEFT OUTER JOIN animals ON to_compare.animal = animals.id
         WHERE type = :animalType
        ORDER BY animals.id
    """
    )
    fun getBacklog(animalType: AnimalType): Flow<List<AnimalInBacklog>>

    @Query("SELECT * FROM to_compare")
    fun getCurrentBacklog(): List<ComparisonBacklog>

    @Insert
    suspend fun addToBacklog(comparisonBacklog: List<ComparisonBacklog>)

    @Delete
    suspend fun removeFromBacklog(comparisonBacklog: List<ComparisonBacklog>)
}