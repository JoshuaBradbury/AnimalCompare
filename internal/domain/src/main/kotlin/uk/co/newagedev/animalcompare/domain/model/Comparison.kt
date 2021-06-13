package uk.co.newagedev.animalcompare.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "comparisons")
data class Comparison(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val winner: Int,
    val loser: Int,
    val dateCompared: LocalDateTime,
)