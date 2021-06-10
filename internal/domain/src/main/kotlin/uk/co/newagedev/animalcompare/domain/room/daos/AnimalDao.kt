package uk.co.newagedev.animalcompare.domain.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.co.newagedev.animalcompare.domain.model.Animal

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals WHERE id LIKE :animalPattern")
    fun getAllOfType(animalPattern: String): Flow<List<Animal>>

    @Insert
    suspend fun addAnimals(vararg animals: Animal)
}