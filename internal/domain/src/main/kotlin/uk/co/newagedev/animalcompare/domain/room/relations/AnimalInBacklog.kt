package uk.co.newagedev.animalcompare.domain.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.ComparisonBacklog

data class AnimalInBacklog(
    @Embedded
    val comparisonBacklog: ComparisonBacklog,
    @Relation(
        parentColumn = "animal",
        entityColumn = "id"
    )
    val animal: Animal,
)