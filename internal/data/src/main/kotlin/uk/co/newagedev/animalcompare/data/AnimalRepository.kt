package uk.co.newagedev.animalcompare.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import uk.co.newagedev.animalcompare.data.room.AppDatabase
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.domain.model.Comparison
import javax.inject.Inject

class AnimalRepository @Inject constructor(
    private val db: AppDatabase,
    private val coroutineScope: CoroutineScope,
) {

//    init {
//        coroutineScope.launch {
//            db.animalDao().addAnimals(
//                Animal("dog1", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg?crop=1.00xw:0.669xh;0,0.190xh&resize=1200:*"),
//                Animal("dog2", "https://www.rd.com/wp-content/uploads/2021/01/GettyImages-588935825.jpg"),
//            )
//        }
//    }

    @OptIn(FlowPreview::class)
    fun getAnimalFlow(animalType: AnimalType): Flow<Pair<Animal, Animal>> {
        return db.animalDao().getAllOfType(animalType.pattern())
            .distinctUntilChanged()
            .flatMapConcat {
                it.zipWithNext().asFlow()
            }
    }

    suspend fun submitComparison(comparison: Comparison) {
        return db.comparisonDao()
            .addComparison(comparison)
    }
}