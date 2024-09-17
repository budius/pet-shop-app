package com.ronaldo.pace.services.database.pet

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.services.database.PetStoreDatabase
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PetDaoTest {
    private lateinit var db: PetStoreDatabase
    private lateinit var dao: PetDao

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PetStoreDatabase::class.java).build()
        dao = db.petDao()
    }

    @After
    fun after() {
        db.clearAllTables()
        db.close()
    }


    @Test
    fun should_return_all_the_data() = runBlocking {
        // given
        dao.insert(testData)

        // when
        val result = dao.getAll().first()

        // then
        assertThat(result).isEqualTo(testData.sortedBy { it.priority })
    }
}

private val testData = listOf(
    PetEntity("1", "king", "d1", 10, 100, PetEntityType.Dog, .3f),
    PetEntity("2", "soup", "d2", 20, 200, PetEntityType.Dog, .2f),
    PetEntity("3", "wood", "d3", 30, 300, PetEntityType.Cat, .5f),
    PetEntity("4", "beer", "d4", 40, 400, PetEntityType.Cat, .7f),
    PetEntity("5", "queen", "d5", 50, 500, PetEntityType.Turtle, .1f),
)