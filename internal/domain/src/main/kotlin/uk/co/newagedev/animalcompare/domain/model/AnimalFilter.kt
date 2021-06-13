package uk.co.newagedev.animalcompare.domain.model

enum class AnimalFilter {
    ALL,
    DOG,
    CAT;

    fun toType(): AnimalType {
        return when (this) {
            ALL -> throw RuntimeException("ALL animal filter does not have an animal type")
            DOG -> AnimalType.Dog
            CAT -> AnimalType.Cat
        }
    }
}