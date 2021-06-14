package uk.co.newagedev.animalcompare.domain.fake

import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType

object FakeAnimal {

    val dogs = listOf(
        Animal(
            1,
            "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg",
            AnimalType.Dog,
        ),
        Animal(
            2,
            "https://www.rd.com/wp-content/uploads/2021/01/GettyImages-588935825.jpg",
            AnimalType.Dog,
        ),
    )
}