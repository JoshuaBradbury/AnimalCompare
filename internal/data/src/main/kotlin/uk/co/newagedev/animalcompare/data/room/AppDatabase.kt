package uk.co.newagedev.animalcompare.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.Comparison
import uk.co.newagedev.animalcompare.domain.model.ComparisonBacklog
import uk.co.newagedev.animalcompare.data.room.daos.AnimalDao
import uk.co.newagedev.animalcompare.data.room.daos.ComparisonBacklogDao
import uk.co.newagedev.animalcompare.data.room.daos.ComparisonDao

@Database(
    entities = [Animal::class, Comparison::class, ComparisonBacklog::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun animalDao(): AnimalDao
    abstract fun comparisonDao(): ComparisonDao
    abstract fun comparisonBacklogDao(): ComparisonBacklogDao
}