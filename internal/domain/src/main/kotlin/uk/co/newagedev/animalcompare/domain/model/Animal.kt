package uk.co.newagedev.animalcompare.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A generic animal entity poses a few issues, the first being that the different APIs return
 * their own set of id's for each type of animal, leading to clashes. To alleviate this there are
 * two solutions, the first being to include the animal type as well, which is fairly extensible,
 * but causes a mess down the line in terms of referencing multiple properties. The second is to
 * combine the key from the offset from the API, putting them in their own unique namespace as to
 * avoid clashes. I decided to opt for the later, they will be of the form "(animalName)(id)"
 *
 * The drawbacks of this though are that you cannot use an animal name that is wholly contained
 * by another animal name, e.g. ox and fox. In this instance we would need to make them distinct
 * enough, so ox would become oox or oxx or something.
 */

@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey
    val id: String,
    val url: String,
)