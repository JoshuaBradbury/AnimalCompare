package uk.co.newagedev.animalcompare.data.room.daos

import androidx.paging.PagingSource
import androidx.room.*
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.domain.model.Comparison
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalComparison

@Dao
interface ComparisonDao {

    @Transaction
    @Query(
        """
            SELECT c.id AS id,
            c.dateCompared AS dateCompared,
            a1.id AS w_id,
            a1.url AS w_url,
            a1.type AS w_type,
            a2.id AS l_id,
            a2.url AS l_url,
            a2.type AS l_type
            FROM comparisons c
            LEFT OUTER JOIN animals a1 ON c.winner = a1.id
            LEFT OUTER JOIN animals a2 ON c.loser = a2.id
            ORDER BY c.id DESC
            """
    )
    fun getAllComparisons(): PagingSource<Int, AnimalComparison>

    @Transaction
    @Query(
        """
            SELECT c.id AS id,
            c.dateCompared AS dateCompared, 
            a1.id AS w_id,
            a1.url AS w_url,
            a1.type AS w_type, 
            a2.id AS l_id,
            a2.url AS l_url,
            a2.type AS l_type
            FROM comparisons c
            LEFT OUTER JOIN animals a1 ON c.winner = a1.id 
            LEFT OUTER JOIN animals a2 ON c.loser = a2.id
            WHERE a1.type = :animalType AND a2.type = :animalType
            ORDER BY c.id DESC
            """
    )
    fun getAllComparisonsByType(animalType: AnimalType): PagingSource<Int, AnimalComparison>

    @Delete
    suspend fun deleteComparison(comparison: Comparison)

    @Insert
    suspend fun addComparison(comparison: Comparison)
}