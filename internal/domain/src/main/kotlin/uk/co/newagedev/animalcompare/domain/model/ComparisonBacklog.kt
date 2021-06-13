package uk.co.newagedev.animalcompare.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "to_compare")
data class ComparisonBacklog(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val animal: Int,
)