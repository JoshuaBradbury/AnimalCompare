package uk.co.newagedev.animalcompare.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val url: String,
    val type: AnimalType,
)