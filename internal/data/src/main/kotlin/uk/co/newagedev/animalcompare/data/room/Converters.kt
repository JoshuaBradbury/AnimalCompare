package uk.co.newagedev.animalcompare.data.room

import androidx.room.TypeConverter
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    @TypeConverter
    fun toAnimalType(animalType: String?): AnimalType? {
        return when (animalType) {
            "cat" -> AnimalType.Cat
            "dog" -> AnimalType.Dog
            else -> null
        }
    }

    @TypeConverter
    fun fromAnimalType(animalType: AnimalType?): String? {
        return animalType?.toString()
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): Long? {
        return dateTime?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun toLocalDateTime(epochTime: Long?): LocalDateTime? {
        return if (epochTime != null) {
            LocalDateTime.ofEpochSecond(epochTime, 0, ZoneOffset.UTC)
        } else {
            null
        }
    }
}