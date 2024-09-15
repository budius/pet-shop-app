package com.ronaldo.pace.services.database.pet

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.repository.pets.PetsQueryRepository
import com.ronaldo.pace.repository.pets.PetsQueryRepositoryFlow
import com.ronaldo.pace.repository.pets.PetsQueryRepositoryQuery
import com.ronaldo.pace.services.database.PetStoreDatabase
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.measureTime

@RunWith(AndroidJUnit4::class)
class PetQueryPerformanceTest {

    private lateinit var db: PetStoreDatabase
    private lateinit var dao: PetDao

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PetStoreDatabase::class.java).build()
        dao = db.petDao()
        val now = Clock.System.now()
        runBlocking {
            dao.insert(
                buildList {
                    repeat(100_000) {
                        PetEntity(
                            id = it.toString(),
                            name = "pet $it",
                            description = "lorem ipsum foo bar",
                            dateOfBirth = (now - 1.days - Random.nextInt(2000).days)
                                .toEpochMilliseconds(),
                            price = 500 + Random.nextInt(10000),
                            type = PetEntityType.entries.random(),
                            priority = Random.nextFloat(),
                        )
                    }
                }
            )
        }
    }

    @After
    fun after() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun speed_test_flow_impl(): Unit = runBlocking {
        val repo = PetsQueryRepositoryFlow(dao::getAll)
        val speed = measureTime { testRepo(repo) }
        android.util.Log.d("PetShopApp", "With flow ran in: $speed")
    }

    @Test
    fun speed_test_sql_impl(): Unit = runBlocking {
        val repo = PetsQueryRepositoryQuery(dao::getWithFilters)
        val speed = measureTime { testRepo(repo) }
        android.util.Log.d("PetShopApp", "With sql ran in: $speed")
    }

    private suspend fun testRepo(repo: PetsQueryRepository) {
        repeat(REPEAT) {
            repo.getPets(randomFilters())
                .take(FILTERS).toList(ArrayList(FILTERS))
        }
    }


    private fun randomPetFilter() = PetFilters(
        type = Pet.Type.entries.random(),
        maxAge = Random.nextInt(2000).days,
        maxPrice = 500 + Random.nextInt(10000)
    )

    private fun randomFilters() = flow {
        repeat(Int.MAX_VALUE) {
            emit(randomPetFilter())
        }
    }

}

private const val FILTERS = 1000
private const val REPEAT = 3