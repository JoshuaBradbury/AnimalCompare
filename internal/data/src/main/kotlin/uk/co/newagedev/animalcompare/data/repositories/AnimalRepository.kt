package uk.co.newagedev.animalcompare.data.repositories

import android.content.Context
import androidx.room.withTransaction
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Precision
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.co.newagedev.animalcompare.common.ImageSize
import uk.co.newagedev.animalcompare.data.api.CatApi
import uk.co.newagedev.animalcompare.data.api.DogApi
import uk.co.newagedev.animalcompare.data.api.FoxApi
import uk.co.newagedev.animalcompare.data.room.AppDatabase
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.domain.model.ComparisonBacklog
import uk.co.newagedev.animalcompare.domain.room.relations.AnimalInBacklog
import javax.inject.Inject
import javax.inject.Singleton

private const val LOAD_AMOUNT = 20
private const val MAX_FILE_SIZE_IN_BYTES = 1024 * 1024 // 1MB

private const val RECYCLE_COUNT = 20
private const val RECYCLE_THRESHOLD_MIN = 40
private const val RECYCLE_THRESHOLD_MAX = 200

@Singleton
class AnimalRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val db: AppDatabase,
    private val dogApi: DogApi,
    private val catApi: CatApi,
    private val foxApi: FoxApi,
    private val coroutineScope: CoroutineScope,
) {

    init {
        for (animalType in listOf(AnimalType.Dog, AnimalType.Cat, AnimalType.Fox)) {
            var loadJob: Job? = null

            coroutineScope.launch {
                db.comparisonBacklogDao().getBacklog(animalType)
                    .distinctUntilChanged()
                    .collectLatest {
                        if (it.size < LOAD_AMOUNT / 2) {
                            if (loadJob == null || loadJob?.isCompleted == true) {
                                loadJob = coroutineScope.launch {
                                    loadMoreAnimals(animalType)
                                }
                            }
                        }
                    }

            }
        }
    }

    private suspend fun loadMoreAnimals(animalType: AnimalType) {
        // Fetches more animals based on the type
        val animals = when (animalType) {
            AnimalType.Dog -> loadMoreDogs(LOAD_AMOUNT)
            AnimalType.Cat -> loadMoreCats(LOAD_AMOUNT)
            AnimalType.Fox -> loadMoreFoxes(LOAD_AMOUNT)
        }

        db.withTransaction {
            // Insert the animals and get their id's so we can store them in the ComparisonBacklog
            val animalIds = db.animalDao().addAnimals(animals)

            // Add the newly inserted animals to the backlog
            db.comparisonBacklogDao().addToBacklog(animalIds.map { id ->
                ComparisonBacklog(0, id.toInt())
            })
        }

        // Preload their images, without blocking the request
        coroutineScope.launch {
            preloadImages(animals.sortedBy { it.id }.map { it.url })
        }

        recycleAnimals(animalType)
    }

    private suspend fun recycleAnimals(animalType: AnimalType) {
        val totalAnimals = db.animalDao().getAnimalCountByType(animalType)

        val recycleAmount = when {
            totalAnimals > RECYCLE_THRESHOLD_MAX -> RECYCLE_COUNT
            totalAnimals > RECYCLE_THRESHOLD_MIN -> {
                RECYCLE_COUNT * (totalAnimals - RECYCLE_THRESHOLD_MIN) / (RECYCLE_THRESHOLD_MAX - RECYCLE_THRESHOLD_MIN)
            }
            else -> return
        }

        db.withTransaction {
            val animalsToRecycle =
                db.animalDao().getRandomAnimals(animalType, recycleAmount).distinct()
            val currentBacklog = db.comparisonBacklogDao().getCurrentBacklog()

            db.comparisonBacklogDao()
                .addToBacklog(animalsToRecycle
                    .filter { animal -> !currentBacklog.any { it.animal == animal } }
                    .map { id ->
                        ComparisonBacklog(0, id)
                    })
        }
    }

    private suspend fun preloadImages(urls: List<String>) {
        urls.forEach { url ->
            val request = ImageRequest
                .Builder(context)
                .data(url)
                .size(ImageSize.MEDIUM)
                .precision(Precision.EXACT)
                .build()

            context.imageLoader
                .execute(request)
        }
    }

    private suspend fun loadMoreDogs(count: Int): List<Animal> {
        return loadMoreAnimal(
            count,
            AnimalType.Dog,
        ) {
            val dogResponse = dogApi.getRandomDog()

            // Limit the file size of the dog images, so that they don't take too long to load
            if (dogResponse.fileSizeBytes < MAX_FILE_SIZE_IN_BYTES &&
                // Filter to just images, as the dog api can return videos too
                listOf(".jpg", ".png", ".jpeg").any { dogResponse.url.lowercase().endsWith(it) }
            ) {
                dogResponse.url
            } else {
                null
            }

        }
    }

    private suspend fun loadMoreCats(count: Int): List<Animal> {
        return loadMoreAnimal(
            count,
            AnimalType.Cat,
        ) {
            val catResponse = catApi.getRandomCat()

            // Filter to just images of a certain type, as the cat api can return gifs too
            if (listOf(".jpg", ".png", ".jpeg").any { catResponse.file.lowercase().endsWith(it) }) {
                catResponse.file
            } else {
                null
            }
        }
    }

    private suspend fun loadMoreFoxes(count: Int): List<Animal> {
        return loadMoreAnimal(
            count,
            AnimalType.Fox,
        ) {
            // The Fox api is nice and only returns jpgs, hopefully in a small enough file size
            foxApi.getRandomFox().image
        }
    }

    private suspend fun loadMoreAnimal(
        count: Int,
        animalType: AnimalType,
        getAndCheckFile: suspend () -> String?
    ): List<Animal> {
        val animals = mutableListOf<String>()

        // As the API returns a random result, we could end up getting too many duplicates, in which
        // case we should give up for now and try again later
        var counter = 0

        while (animals.size < count && counter < 50) {
            val animal = getAndCheckFile()

            if (animal != null &&
                // Make sure the animal doesn't exist locally already
                !db.animalDao().doesExist(animal) &&
                // Check we haven't already prepared the animal
                !animals.contains(animal)
            ) {
                animals.add(animal)
            }

            counter += 1
        }

        return animals.map { Animal(0, it, animalType) }
    }

    @OptIn(FlowPreview::class)
    fun getComparisonBacklog(animalType: AnimalType): Flow<Pair<AnimalInBacklog, AnimalInBacklog>> {
        return db.comparisonBacklogDao().getBacklog(animalType)
            .distinctUntilChanged()
            .map { backlog ->
                backlog
                    // Pair the animals up to make it easier on the view model
                    .zipWithNext()
                    .first()
            }
    }
}