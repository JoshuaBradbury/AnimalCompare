package uk.co.newagedev.animalcompare.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comparisons")
data class Comparison(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val winner: Int,
    val loser: Int,
)