package uk.co.newagedev.animalcompare.data.room

import android.os.Build.VERSION_CODES.Q
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import uk.co.newagedev.animalcompare.data.CoroutineTestRule
import uk.co.newagedev.animalcompare.data.room.daos.AnimalDao
import uk.co.newagedev.animalcompare.data.room.daos.ComparisonBacklogDao
import uk.co.newagedev.animalcompare.data.room.daos.ComparisonDao
import uk.co.newagedev.animalcompare.domain.fake.FakeAnimal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.domain.model.Comparison
import uk.co.newagedev.animalcompare.domain.model.ComparisonBacklog
import java.io.IOException
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Q])
class DaoTests {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var animalDao: AnimalDao
    private lateinit var comparisonDao: ComparisonDao
    private lateinit var comparisonBacklogDao: ComparisonBacklogDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).build()

        animalDao = db.animalDao()
        comparisonDao = db.comparisonDao()
        comparisonBacklogDao = db.comparisonBacklogDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun animals(): Unit = runBlocking {
        val animal = FakeAnimal.dogs.first()

        val id = animalDao.addAnimals(listOf(animal)).map { it.toInt() }
        animalDao.doesExist(animal.url) shouldBeEqualTo true
        animalDao.getAnimalCountByType(AnimalType.Dog) shouldBeEqualTo 1
        animalDao.getRandomAnimals(AnimalType.Dog, 1) shouldBeEqualTo id
    }

    @Test
    fun comparisons(): Unit = runBlocking {
        val animal1 = FakeAnimal.dogs.first()
        val animal2 = FakeAnimal.dogs.last()

        val animals = animalDao.addAnimals(listOf(animal1, animal2))
        val swipeTime = LocalDateTime.now().withNano(0)

        val comparison = Comparison(0, animals[0].toInt(), animals[1].toInt(), swipeTime)
        comparisonDao.addComparison(comparison)

        comparisonDao.getById(1) shouldBeEqualTo comparison.copy(id = 1)

        comparisonDao.deleteComparison(comparison.copy(id = 1))
    }

    @Test
    fun backlog(): Unit = runBlocking {
        val animal1 = FakeAnimal.dogs.first()
        val animal2 = FakeAnimal.dogs.last()

        animalDao.addAnimals(listOf(animal1, animal2))

        val backlog = listOf(
            ComparisonBacklog(0, 1),
            ComparisonBacklog(0, 2),
        )

        comparisonBacklogDao.addToBacklog(backlog)

        val backlogWithIds = backlog.mapIndexed { index, comparisonBacklog ->
            comparisonBacklog.copy(
                id = index + 1
            )
        }

        comparisonBacklogDao.getCurrentBacklog() shouldBeEqualTo backlogWithIds

        comparisonBacklogDao.removeFromBacklog(backlogWithIds)
    }
}