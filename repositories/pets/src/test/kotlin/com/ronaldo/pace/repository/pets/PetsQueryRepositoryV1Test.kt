package com.ronaldo.pace.repository.pets

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.time.Duration.Companion.days

class PetsQueryRepositoryV1Test {

    @Test
    fun `should return all pets`() = runBlocking {
        // given
        var count = 0
        val getPetsFlow: GetFlowOfPets = {
            count++
            flowOf(listOf(e1, e2, e3))
        }
        val sut = createSut(getPetsFlow = getPetsFlow)

        // when
        val flow = sut.getPets(flowOf(PetFilters.empty))
        val result = flow.first()

        // then
        val expected = listOf(d1, d2, d3)
        assertThat(result).isEqualTo(expected)
        assertThat(count).isEqualTo(1)

    }

    @Test
    fun `should return turtles`() = runBlocking {
        // given
        var count = 0
        val getFlowOfPetsByType: GetFlowOfPetsByType = {
            count++
            flowOf(listOf(e2))
        }
        val sut = createSut(getFlowOfPetsByType = getFlowOfPetsByType)

        // when
        sut.getPets(flowOf(PetFilters(Pet.Type.Turtle))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(d2)
            assertThat(result).isEqualTo(expected)
            assertThat(count).isEqualTo(1)

        }
    }

    @Test
    fun `should return young turtles`() = runBlocking {
        // given
        val dateOfBirth = (Clock.System.now() - 30.days).toEpochMilliseconds()
        var count = 0
        val getFlowOfPetsByType: GetFlowOfPetsByType = {
            count++
            flowOf(
                listOf(
                    e2,
                    e2.copy(
                        id = "result",
                        dateOfBirth = dateOfBirth
                    )
                )
            )
        }
        val sut = createSut(getFlowOfPetsByType = getFlowOfPetsByType)

        // when
        sut.getPets(flowOf(PetFilters(Pet.Type.Turtle, 60.days))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(
                d2.copy(
                    id = "result",
                    dateOfBirth = Instant.fromEpochMilliseconds(dateOfBirth)
                )
            )
            assertThat(result).isEqualTo(expected)
            assertThat(count).isEqualTo(1)

        }
    }


    @Test
    fun `should return young pets`() = runBlocking {
        // given
        var count = 0
        val getFlowOfPetsMaxAgeDays: GetFlowOfPetsMaxAgeDays = {
            count++
            flowOf(listOf(e1, e3))
        }
        val sut = createSut(getFlowOfPetsMaxAgeDays = getFlowOfPetsMaxAgeDays)

        // when
        sut.getPets(flowOf(PetFilters(maxAge = 30.days))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(d1, d3)
            assertThat(result).isEqualTo(expected)
            assertThat(count).isEqualTo(1)

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
            assertThat(result).isEqualTo(expected)
        }
    }
}

private fun createSut(
    getPetsFlow: GetFlowOfPets = { flowOf(listOf(e1, e2, e3)) },
    getFlowOfPetsByType: GetFlowOfPetsByType = { flowOf(listOf(e2)) },
    getFlowOfPetsMaxAgeDays: GetFlowOfPetsMaxAgeDays = { flowOf(listOf(e2, e3)) }
) = PetsQueryRepositoryV1(
    getPetsFlow = getPetsFlow,
    getPetsFlowByType = getFlowOfPetsByType,
    getPetsFlowMaxAgeDays = getFlowOfPetsMaxAgeDays
)

//region mock data


private val e1 =
    PetEntity("123", "io", "a good boy", 2345678, 2999, PetEntityType.Dog, 0.3f)
private val e2 =
    PetEntity("234", "ganymede", "an old turtle", 3855740, 3179, PetEntityType.Turtle, 0.6f)
private val e3 =
    PetEntity("345", "titan", "a sweet kitten", 5038239, 2333, PetEntityType.Cat, 0.12f)

private val d1 =
    Pet("123", "io", "a good boy", Instant.fromEpochMilliseconds(2345678), 2999, Pet.Type.Dog)
private val d2 =
    Pet(
        "234",
        "ganymede",
        "an old turtle",
        Instant.fromEpochMilliseconds(3855740),
        3179,
        Pet.Type.Turtle
    )
private val d3 =
    Pet(
        "345",
        "titan",
        "a sweet kitten",
        Instant.fromEpochMilliseconds(5038239),
        2333,
        Pet.Type.Cat
    )
//endregion