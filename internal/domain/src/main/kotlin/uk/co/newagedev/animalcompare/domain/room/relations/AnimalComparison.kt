package uk.co.newagedev.animalcompare.domain.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.Comparison

data class AnimalComparison(
    @Embedded
    val comparison: Comparison,
    @Relation(
        parentColumn = "winner",
        entityColumn = "id"
    )
    val winner: Animal,
    @Relation(
        parentColumn = "loser",
        entityColumn = "id"
    )
    val loser: Animal,
)