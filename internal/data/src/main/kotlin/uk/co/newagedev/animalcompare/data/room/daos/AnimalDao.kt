package uk.co.newagedev.animalcompare.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uk.co.newagedev.animalcompare.domain.model.Animal

@Dao
interface AnimalDao {

    @Insert
    suspend fun addAnimals(animals: List<Animal>): List<Long>

    @Query("SELECT EXISTS(SELECT * FROM animals WHERE url = :url)")
    suspend fun doesExist(url: String): Boolean
}