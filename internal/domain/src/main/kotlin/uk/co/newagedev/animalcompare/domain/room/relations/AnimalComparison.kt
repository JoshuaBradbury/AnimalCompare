package uk.co.newagedev.animalcompare.domain.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import uk.co.newagedev.animalcompare.domain.model.Animal
import java.time.LocalDateTime

data class AnimalComparison(
    val id: Int,
    val dateCompared: LocalDateTime,
    @Embedded(prefix = "w_")
    val winner: Animal,
    @Embedded(prefix = "l_")
    val loser: Animal,
)