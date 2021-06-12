package uk.co.newagedev.animalcompare.data.room

import androidx.room.TypeConverter
import uk.co.newagedev.animalcompare.domain.model.AnimalType

class Converters {

    @TypeConverter
    fun toAnimalType(animalType: String?): AnimalType? {
        return when (animalType) {
            "dog" -> AnimalType.Dog
            else -> null
        }
    }

    @TypeConverter
    fun fromAnimalType(animalType: AnimalType?): String? {
        return animalType?.toString()
    }
}