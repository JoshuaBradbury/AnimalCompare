package uk.co.newagedev.animalcompare.domain.room.relations

import androidx.room.Embedded
import uk.co.newagedev.animalcompare.domain.model.Animal
import java.time.LocalDateTime

data class FavouriteAnimal(
    @Embedded
    val animal: Animal,
    val count: Int,
    val lastSwiped: LocalDateTime,
)