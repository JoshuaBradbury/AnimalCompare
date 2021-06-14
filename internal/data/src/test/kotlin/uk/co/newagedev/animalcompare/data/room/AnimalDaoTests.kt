package uk.co.newagedev.animalcompare.data.room

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
import org.robolectric.shadows.ShadowBuild.Q
import uk.co.newagedev.animalcompare.data.CoroutineTestRule
import uk.co.newagedev.animalcompare.data.room.daos.AnimalDao
import uk.co.newagedev.animalcompare.domain.fake.FakeAnimal
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Q])
class AnimalDaoTests {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var animalDao: AnimalDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).build()
        animalDao = db.animalDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun addAnimalAndReadInList(): Unit = runBlocking {
        val animal = FakeAnimal.getFakeAnimal(AnimalType.Dog)

        val id = animalDao.addAnimals(listOf(animal))
        animalDao.doesExist(animal.url) shouldBeEqualTo true
        animalDao.getAnimalCountByType(AnimalType.Dog) shouldBeEqualTo 1
    }
}