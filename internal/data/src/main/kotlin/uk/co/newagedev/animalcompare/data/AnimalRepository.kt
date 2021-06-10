package uk.co.newagedev.animalcompare.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import javax.inject.Inject
import kotlin.coroutines.resume

class AnimalRepository @Inject constructor() {

    fun getAnimalFlow(animalType: AnimalType): Flow<Pair<Animal, Animal>?> {
        return flowOf(
            Animal(
                0,
                "https://amayei.nyc3.digitaloceanspaces.com/2019/10/58e336b26ee69cbfb21d906c57b8ac8f9cb53bdf.jpg"
            ) to Animal(
                1,
                "https://amayei.nyc3.digitaloceanspaces.com/2019/10/58e336b26ee69cbfb21d906c57b8ac8f9cb53bdf.jpg"
            )
        )
    }

    suspend fun submitWinner(animal: Animal) {

    }

    suspend fun submitLoser(animal: Animal) {

    }
}