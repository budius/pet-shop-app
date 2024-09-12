package com.ronaldo.pace.repository.pets

import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.network.models.PetResponseItem
import com.ronaldo.pace.network.models.PetResponseType
import com.ronaldo.pace.network.models.PetsResponse
import com.ronaldo.pace.network.rest.GetPets
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.Test

class PetsLoadRepositoryTest {
    @Test
    fun `should push loaded data to the database`() = runBlocking {
        // given
        var count = 0
        var received: List<PetEntity>? = null
        val getPets = GetPets {
            count++
            Result.success(PetsResponse(0, 3, listOf(r1, r2, r3)))
        }
        val addPets: AddPets = { received = it }
        val sut = createSut(getPets = getPets, addPets = addPets)

        // when
        val result = sut.loadMore()

        // then
        val expected = listOf(e1, e2, e3)
        assertThat(count).isEqualTo(1)
        assertThat(result).isEqualTo(Result.success(Unit))
        assertThat(received).isEqualTo(expected)
    }

    @Test
    fun `should return error in case of failed API call`() = runBlocking {
        // given
        val exception = Exception("Network Error")
        var count = 0
        var received: List<PetEntity>? = null
        val getPets = GetPets {
            count++
            Result.failure(exception)
        }
        val addPets: AddPets = { received = it }
        val sut = createSut(getPets = getPets, addPets = addPets)

        // when
        val result = sut.loadMore()

        // then
        assertThat(count).isEqualTo(1)
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        assertThat(received).isNull()
    }
}

private fun createSut(
    getPets: GetPets = GetPets { Result.success(PetsResponse(0, 3, listOf(r1, r2, r3))) },
    addPets: AddPets = {},
    getPetsFlow: GetFlowOfPets = { flowOf(listOf(e1, e2, e3)) },
) = PetsLoadRepository(
    getPets = getPets,
    addPets = addPets,
    getPetsFlow = getPetsFlow,
)

private val r1 =
    PetResponseItem(
        "123",
        "io",
        2999,
        "a good boy",
        PetResponseType.Dog,
        Instant.fromEpochMilliseconds(2345678),
        0.3f
    )
private val r2 =
    PetResponseItem(
        "234",
        "ganymede",
        3179,
        "an old turtle",
        PetResponseType.Turtle,
        Instant.fromEpochMilliseconds(3855740),
        0.6f
    )
private val r3 =
    PetResponseItem(
        "345",
        "titan",
        2333,
        "a sweet kitten",
        PetResponseType.Cat,
        Instant.fromEpochMilliseconds(5038239),
        0.12f
    )

private val e1 =
    PetEntity("123", "io", "a good boy", 2345678, 2999, PetEntityType.Dog, 0.3f)
private val e2 =
    PetEntity("234", "ganymede", "an old turtle", 3855740, 3179, PetEntityType.Turtle, 0.6f)
private val e3 =
    PetEntity("345", "titan", "a sweet kitten", 5038239, 2333, PetEntityType.Cat, 0.12f)
