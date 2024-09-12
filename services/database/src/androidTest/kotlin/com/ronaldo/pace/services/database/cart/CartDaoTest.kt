package com.ronaldo.pace.services.database.cart

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.services.database.PetStoreDatabase
import com.ronaldo.pace.services.database.cart.models.CartEntity
import com.ronaldo.pace.services.database.pet.PetDao
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartDaoTest {
    private lateinit var db: PetStoreDatabase
    private lateinit var dao: CartDao
    private lateinit var pet: PetDao

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PetStoreDatabase::class.java).build()
        dao = db.cartDao()
        pet = db.petDao()
        runBlocking { pet.insert(testData) }
    }

    @After
    fun after() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun should_return_empty_card() = runBlocking {
        // given
        // default

        // when
        val result = dao.getCart().first()


        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun should_return_one_dog() = runBlocking {
        // given
        val dog = testData[0]
        dao.addToCart(dog.id)

        // when
        val result = dao.getCart().first()

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(CartEntity(1, dog))
    }

    @Test
    fun should_return_two_dogs() = runBlocking {
        // given
        val dog = testData[0]
        dao.addToCart(dog.id)
        dao.addToCart(dog.id)

        // when
        val result = dao.getCart().first()

        // then
        assertThat(result).isEqualTo(listOf(CartEntity(2, dog)))
    }

    @Test
    fun should_return_two_dog_and_one_other() = runBlocking {
        // given
        val dog1 = testData[0]
        val dog2 = testData[1]
        dao.addToCart(dog1.id)
        dao.addToCart(dog1.id)
        dao.addToCart(dog2.id)

        // when
        val result = dao.getCart().first()

        // then
        assertThat(result).isEqualTo(
            listOf(
                CartEntity(2, dog1),
                CartEntity(1, dog2),
            ).sortedBy { it.petEntity.priority }.sortedByDescending { it.quantity })
    }

    @Test
    fun should_return_two_dogs_and_ten_turtles() = runBlocking {
        // given
        val dog = testData[0]
        val turtle = testData[4]
        repeat(2) { dao.addToCart(dog.id) }
        repeat(10) { dao.addToCart(turtle.id) }

        // when
        val result = dao.getCart().first()

        // then
        assertThat(result).isEqualTo(
            listOf(
                CartEntity(2, dog),
                CartEntity(10, turtle),
            ).sortedBy { it.petEntity.priority }.sortedByDescending { it.quantity })
    }

    @Test
    fun should_return_one_dog_one_cat_another_car_and_five_turtles() = runBlocking {
        // given
        val dog = testData[1]
        val cat1 = testData[2]
        val cat2 = testData[3]
        val turtle = testData[4]
        dao.addToCart(dog.id)
        dao.addToCart(cat1.id)
        dao.addToCart(cat2.id)
        repeat(5) { dao.addToCart(turtle.id) }

        // when
        val result = dao.getCart().first()

        // then
        assertThat(result).isEqualTo(
            listOf(
                CartEntity(1, dog),
                CartEntity(1, cat1),
                CartEntity(1, cat2),
                CartEntity(5, turtle),
            ).sortedBy { it.petEntity.priority }.sortedByDescending { it.quantity })
    }

    @Test
    fun should_clear_cart() = runBlocking {
        // given
        repeat(5) { dao.addToCart(testData[2].id) }

        // when
        dao.clearCart()
        val result = dao.getCart().first()

        // then
        assertThat(result).isEmpty()
    }
}


private val testData = listOf(
    PetEntity("1", "king", "d1", 10, 100, PetEntityType.Dog, .1f),
    PetEntity("2", "soup", "d2", 20, 200, PetEntityType.Dog, .2f),
    PetEntity("3", "wood", "d3", 30, 300, PetEntityType.Cat, .3f),
    PetEntity("4", "beer", "d4", 40, 400, PetEntityType.Cat, .4f),
    PetEntity("5", "queen", "d5", 50, 500, PetEntityType.Turtle, .5f),
)