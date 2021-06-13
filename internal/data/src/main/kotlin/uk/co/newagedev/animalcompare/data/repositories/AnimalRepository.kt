package uk.co.newagedev.animalcompare.data.repositories

import android.content.Context
import androidx.room.withTransaction
import coil.ImageLoader
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
import uk.co.newagedev.animalcompare.data.api.DogApi
import uk.co.newagedev.animalcompare.data.room.AppDatabase
import uk.co.newagedev.animalcompare.domain.model.Animal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.domain.model.ComparisonBacklog
import javax.inject.Inject
import javax.inject.Singleton

private const val LOAD_AMOUNT = 20
private const val MAX_FILE_SIZE_IN_BYTES = 1024 * 1024 // 1MB

@Singleton
class AnimalRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val db: AppDatabase,
    private val dogApi: DogApi,
    private val coroutineScope: CoroutineScope,
    private val imageLoader: ImageLoader,
) {

    private var loadJob: Job? = null

    init {
        coroutineScope.launch {
            val dogsToCompare = db.comparisonBacklogDao().getBacklog(AnimalType.Dog)
            dogsToCompare
                .distinctUntilChanged()
                .collectLatest {
                    if (it.size < LOAD_AMOUNT / 2) {
                        if (loadJob == null || loadJob?.isCompleted == true) {
                            loadJob = coroutineScope.launch {
                                loadMoreAnimals(AnimalType.Dog)
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
        }

        db.withTransaction {
            // Insert the animals and get their id's so we can store them in the ComparisonBacklog
            val animalIds = db.animalDao().addAnimals(animals)

            // Add the newly inserted animals to the backlog
            db.comparisonBacklogDao().addToBacklog(animalIds.map { id ->
                ComparisonBacklog(id.toInt())
            })
        }

        // Preload their images, without blocking the request
        coroutineScope.launch {
            preloadImages(animals.sortedBy { it.id }.map { it.url })
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

            imageLoader
                .execute(request)
        }
    }

    private suspend fun loadMoreDogs(count: Int): List<Animal> {
        val dogs = mutableListOf<String>()

        // As the API returns a random result, we could end up getting too many duplicates, in which
        // case we should give up for now and try again later
        var counter = 0

        while (dogs.size < count && counter < 50) {
            val dogResponse = dogApi.getRandomDog()

            // Limit the file size of the dog images, so that they don't take too long to load
            if (dogResponse.fileSizeBytes < MAX_FILE_SIZE_IN_BYTES &&
                // Filter to just images, as the dog api can return videos too
                listOf(".jpg", ".png", ".jpeg").any { dogResponse.url.lowercase().endsWith(it) } &&
                // Make sure the animal doesn't exist locally already
                !db.animalDao().doesExist(dogResponse.url) &&
                // Check we haven't already prepared the animal
                !dogs.contains(dogResponse.url)
            ) {
                dogs.add(dogResponse.url)
            }

            counter += 1
        }

        return dogs.map { Animal(0, it, AnimalType.Dog) }
    }

    @OptIn(FlowPreview::class)
    fun getComparisonBacklog(animalType: AnimalType): Flow<Pair<Animal, Animal>> {
        return db.comparisonBacklogDao().getBacklog(animalType)
            .distinctUntilChanged()
            .map { backlog ->
                backlog
                    .map { it.animal }
                    // Pair the animals up to make it easier on the view model
                    .zipWithNext()
                    .first()
            }
    }
}