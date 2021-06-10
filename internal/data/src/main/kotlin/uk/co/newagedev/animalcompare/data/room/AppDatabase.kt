package uk.co.newagedev.animalcompare.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.Comparison
import uk.co.newagedev.animalcompare.domain.room.daos.AnimalDao
import uk.co.newagedev.animalcompare.domain.room.daos.ComparisonDao

@Database(entities = [Animal::class, Comparison::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun animalDao(): AnimalDao
    abstract fun comparisonDao(): ComparisonDao
}