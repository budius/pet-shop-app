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

class PetsQueryRepositoryTest {

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
        val sut = createSut()

        // when
        sut.getPets(flowOf(PetFilters(Pet.Type.Turtle))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(d2)
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun `should return young turtles`() = runBlocking {

        // given
        val pet2Birthday = ageInMillis(59.days)
        val getPetsFlow: GetFlowOfPets = {
            flowOf(
                listOf(
                    e1.copy(dateOfBirth = ageInMillis(62.days)),
                    e2.copy(dateOfBirth = pet2Birthday),
                    e3.copy(dateOfBirth = ageInMillis(312.days))
                )
            )
        }
        val sut = createSut(getPetsFlow = getPetsFlow)

        // when
        sut.getPets(flowOf(PetFilters(Pet.Type.Turtle, 60.days))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(
                d2.copy(dateOfBirth = Instant.fromEpochMilliseconds(pet2Birthday))
            )
            assertThat(result).isEqualTo(expected)

        }
    }


    @Test
    fun `should return young pets`() = runBlocking {
        // given
        val pet1Birthday = ageInMillis(2.days)
        val pet3Birthday = ageInMillis(29.days)
        val getPetsFlow: GetFlowOfPets = {
            flowOf(
                listOf(
                    e1.copy(dateOfBirth = pet1Birthday),
                    e2.copy(dateOfBirth = ageInMillis(31.days)),
                    e3.copy(dateOfBirth = pet3Birthday),
                )
            )
        }
        val sut = createSut(getPetsFlow = getPetsFlow)

        // when
        sut.getPets(flowOf(PetFilters(maxAge = 30.days))).test {
            val result = expectMostRecentItem()

            // then
            val expected = listOf(
                d1.copy(dateOfBirth = Instant.fromEpochMilliseconds(pet1Birthday)),
                d3.copy(dateOfBirth = Instant.fromEpochMilliseconds(pet3Birthday)),
            )
            assertThat(result).isEqualTo(expected)

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
) = PetsQueryRepository(
    getPetsFlow = getPetsFlow,
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