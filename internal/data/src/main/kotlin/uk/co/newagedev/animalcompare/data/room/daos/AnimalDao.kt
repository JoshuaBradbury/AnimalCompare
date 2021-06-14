package uk.co.newagedev.animalcompare.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType

@Dao
interface AnimalDao {

    @Insert
    suspend fun addAnimals(animals: List<Animal>): List<Long>

    @Query("SELECT EXISTS(SELECT * FROM animals WHERE url = :url)")
    suspend fun doesExist(url: String): Boolean

    @Query("SELECT ID FROM animals WHERE type = :animalType ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomAnimals(animalType: AnimalType, limit: Int): List<Int>

    @Query("SELECT COUNT(*) FROM animals WHERE type = :animalType")
    suspend fun getAnimalCountByType(animalType: AnimalType): Int
}