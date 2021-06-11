package uk.co.newagedev.animalcompare.domain.fake

import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType

object FakeAnimal {

    private val dogs = listOf(
        Animal(
            "dog1",
            "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg?crop=1.00xw:0.669xh;0,0.190xh&resize=1200:*"
        ),
        Animal("dog2", "https://www.rd.com/wp-content/uploads/2021/01/GettyImages-588935825.jpg"),
    )

    fun getFakeAnimal(animalType: AnimalType): Animal {
        return when (animalType) {
            is AnimalType.Dog -> {
                dogs.random()
            }
        }
    }
}