package com.ronaldo.pace.repository.pets

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.network.models.PetResponseItem
import com.ronaldo.pace.network.models.PetResponseType
import com.ronaldo.pace.network.models.PetsResponse
import com.ronaldo.pace.network.rest.GetPets
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.time.Duration.Companion.days

class PetsRepositoryTest {

    //region filtering
    @Test
    fun `should return all pets`() = runBlocking {
        // given
        var count = 0
        val getPetsEntityFlow: GetFlowOfPets = {
            count++
            flowOf(listOf(e1, e2, e3))
        }
        val sut = createSut(getPetsEntityFlow = getPetsEntityFlow)

        // when
        val flow = sut.getPets(flowOf(PetFilters.empty))
        val result = flow.first()

        // then
        val expected = listOf(d1, d2, d3)
        assertThat(result).isEqualTo(Result.success(expected))
        assertThat(count).isEqualTo(1)

    }

    @Test
    fun `should return turtles`() = runBlocking {
        // given
        val sut = createSut()

        // when
        sut.getPets(flowOf(PetFilters(Pet.Type.Turtle))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(d2)
            assertThat(result).isEqualTo(Result.success(expected))
        }
    }

    @Test
    fun `should return young turtles`() = runBlocking {

        // given
        val pet2Birthday = ageInMillis(59.days)
        val getPetsEntityFlow: GetFlowOfPets = {
            flowOf(
                listOf(
                    e1.copy(dateOfBirth = ageInMillis(62.days)),
                    e2.copy(dateOfBirth = pet2Birthday),
                    e3.copy(dateOfBirth = ageInMillis(312.days))
                )
            )
        }
        val sut = createSut(getPetsEntityFlow = getPetsEntityFlow)

        // when
        sut.getPets(flowOf(PetFilters(Pet.Type.Turtle, 60.days))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(
                d2.copy(dateOfBirth = Instant.fromEpochMilliseconds(pet2Birthday))
            )
            assertThat(result).isEqualTo(Result.success(expected))

        }
    }


    @Test
    fun `should return young pets`() = runBlocking {
        // given
        val pet1Birthday = ageInMillis(2.days)
        val pet3Birthday = ageInMillis(29.days)
        val getPetsEntityFlow: GetFlowOfPets = {
            flowOf(
                listOf(
                    e1.copy(dateOfBirth = pet1Birthday),
                    e2.copy(dateOfBirth = ageInMillis(31.days)),
                    e3.copy(dateOfBirth = pet3Birthday),
                )
            )
        }
        val sut = createSut(getPetsEntityFlow = getPetsEntityFlow)

        // when
        sut.getPets(flowOf(PetFilters(maxAge = 30.days))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(
                d1.copy(dateOfBirth = Instant.fromEpochMilliseconds(pet1Birthday)),
                d3.copy(dateOfBirth = Instant.fromEpochMilliseconds(pet3Birthday)),
            )
            assertThat(result).isEqualTo(Result.success(expected))
        }
    }

    @Test
    fun `should return cheap pets`() = runBlocking {
        // given
        val sut = createSut()

        // when
        sut.getPets(flowOf(PetFilters(maxPrice = 3000))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(d1, d3)
            assertThat(result).isEqualTo(Result.success(expected))
        }
    }
    //endregion

    //region loading
    @Test
    fun `should load more when requested`() = runBlocking {
        // given
        val requestIndex = mutableListOf<Long>()
        val getPetsApi = GetPets { index ->
            requestIndex += index
            Result.success(PetsResponse(0, 100, listOf(r3)))
        }

        val database = MutableStateFlow(listOf(e1, e2))
        val getPetsEntityFlow: GetFlowOfPets = { database }
        val addPetsEntity: AddPets = { database.update { it.toMutableList().apply { addAll(it) } } }
        val countPetsEntity: GetPetCount = { database.value.size.toLong() }

        val sut = createSut(
            getPetsEntityFlow = getPetsEntityFlow,
            addPetsEntity = addPetsEntity,
            countPetsEntity = countPetsEntity,
            getPetsApi = getPetsApi
        )
        sut.getPets(flowOf(PetFilters.empty)).test {
            awaitItem()
            assertThat(requestIndex).isEqualTo(emptyList<Long>())

            // when
            sut.loadMore()

            //then
            awaitItem()
            assertThat(requestIndex).isEqualTo(listOf(2L))
        }
    }

    @Test
    fun `should load pets when initial request with empty filter is empty`() = runBlocking {
        // given
        val responses = mutableListOf(
            PetsResponse(0, 100, listOf(r1)),
            PetsResponse(1, 100, listOf(r2)),
        )
        val getPetsApi = GetPets {
            Result.success(responses.removeFirst())
        }

        val database = MutableStateFlow(listOf<PetEntity>())
        val getPetsEntityFlow: GetFlowOfPets = { database }
        var addPetsCounter = 0
        val addPetsEntity: AddPets = {
            addPetsCounter++
            database.value = database.value.toMutableList().apply {
                addAll(it)
            }
        }
        val countPetsEntity: GetPetCount = { database.value.size.toLong() }

        val sut = createSut(
            getPetsEntityFlow = getPetsEntityFlow,
            addPetsEntity = addPetsEntity,
            countPetsEntity = countPetsEntity,
            getPetsApi = getPetsApi
        )

        // when
        sut.getPets(flowOf(PetFilters.empty)).test {

            assertThat(awaitItem().getOrThrow().size).isEqualTo(0)
            awaitItem()

            // then
            assertThat(responses.size).isEqualTo(1)
            assertThat(addPetsCounter).isEqualTo(1)

            // when
            sut.loadMore()
            awaitItem()
            // then
            assertThat(responses.size).isEqualTo(0)
            assertThat(addPetsCounter).isEqualTo(2)
        }
    }

    @Test
    fun `should load pets when initial request with filters is empty`() = runBlocking {
        // given
        val responses = mutableListOf(
            PetsResponse(0, 100, listOf(r2)),
            PetsResponse(1, 100, listOf(r3)),
        )
        val getPetsApi = GetPets {
            Result.success(responses.removeFirst())
        }

        val database = MutableStateFlow(listOf(e1))
        val getPetsEntityFlow: GetFlowOfPets = { database }
        var addPetsCounter = 0
        val addPetsEntity: AddPets = {
            addPetsCounter++
            database.value = database.value.toMutableList().apply {
                addAll(it)
            }
        }
        val countPetsEntity: GetPetCount = { database.value.size.toLong() }

        val sut = createSut(
            getPetsEntityFlow = getPetsEntityFlow,
            addPetsEntity = addPetsEntity,
            countPetsEntity = countPetsEntity,
            getPetsApi = getPetsApi
        )

        // when
        sut.getPets(flowOf(PetFilters(Pet.Type.Turtle))).test {

            assertThat(awaitItem().getOrThrow().size).isEqualTo(0)
            awaitItem()

            // then
            assertThat(responses.size).isEqualTo(1)
            assertThat(addPetsCounter).isEqualTo(1)

            // when
            sut.loadMore()
            awaitItem()
            // then
            assertThat(responses.size).isEqualTo(0)
            assertThat(addPetsCounter).isEqualTo(2)
        }
    }


    @Test
    fun `should return error initial load more fails`() = runBlocking {
        // given
        val exception = RuntimeException("404 or I/O exception")
        val getPetsApi = GetPets { Result.failure(exception) }
        val getPetsEntityFlow: GetFlowOfPets = { MutableStateFlow(emptyList()) }

        val sut = createSut(
            getPetsEntityFlow = getPetsEntityFlow,
            getPetsApi = getPetsApi
        )

        // when
        sut.getPets(flowOf(PetFilters.empty)).test {
            assertThat(awaitItem().exceptionOrNull()).isEqualTo(exception)
        }
    }
    //endregion
}

private fun createSut(
    getPetsEntityFlow: GetFlowOfPets = { flowOf(listOf(e1, e2, e3)) },
    addPetsEntity: AddPets = { },
    countPetsEntity: GetPetCount = { 3 },
    getPetsApi: GetPets = GetPets {
        Result.success(PetsResponse(0, 100, listOf(r1, r2, r3)))
    },
) = PetsRepository(
    getPetsEntityFlow = getPetsEntityFlow,
    addPetsEntity = addPetsEntity,
    countPetsEntity = countPetsEntity,
    getPetsApi = getPetsApi,
)

//region mock data
private val e1 = PetEntity(
    id = "123",
    name = "io",
    description = "a good boy",
    dateOfBirth = 2345678,
    price = 2999,
    type = PetEntityType.Dog,
    priority = 0.3f
)
private val e2 = PetEntity(
    id = "234",
    name = "ganymede",
    description = "an old turtle",
    dateOfBirth = 3855740,
    price = 3179,
    type = PetEntityType.Turtle,
    priority = 0.6f
)
private val e3 = PetEntity(
    id = "345",
    name = "titan",
    description = "a sweet kitten",
    dateOfBirth = 5038239,
    price = 2333,
    type = PetEntityType.Cat,
    priority = 0.12f
)
private val d1 = Pet(
    id = "123",
    name = "io",
    description = "a good boy",
    dateOfBirth = Instant.fromEpochMilliseconds(2345678),
    price = 2999,
    type = Pet.Type.Dog
)
private val d2 = Pet(
    id = "234",
    name = "ganymede",
    description = "an old turtle",
    dateOfBirth = Instant.fromEpochMilliseconds(3855740),
    price = 3179,
    type = Pet.Type.Turtle
)
private val d3 = Pet(
    id = "345",
    name = "titan",
    description = "a sweet kitten",
    dateOfBirth = Instant.fromEpochMilliseconds(5038239),
    price = 2333,
    type = Pet.Type.Cat
)
private val r1 = PetResponseItem(
    id = "123",
    name = "io",
    price = 2999,
    description = "a good boy",
    type = PetResponseType.Dog,
    dateOfBirth = Instant.fromEpochMilliseconds(2345678),
    priority = 0.3f
)
private val r2 = PetResponseItem(
    id = "234",
    name = "ganymede",
    price = 3179,
    description = "an old turtle",
    type = PetResponseType.Turtle,
    dateOfBirth = Instant.fromEpochMilliseconds(3855740),
    priority = 0.6f
)
private val r3 = PetResponseItem(
    id = "345",
    name = "titan",
    price = 2333,
    description = "a sweet kitten",
    type = PetResponseType.Cat,
    dateOfBirth = Instant.fromEpochMilliseconds(5038239),
    priority = 0.12f
)
//endregion