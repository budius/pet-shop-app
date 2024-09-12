package com.ronaldo.pace.repository.pets

import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.network.models.PetResponseItem
import com.ronaldo.pace.network.models.PetResponseType
import com.ronaldo.pace.repository.pets.PetMapper.asDomain
import com.ronaldo.pace.repository.pets.PetMapper.asEntity
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.datetime.Instant
import org.junit.Test

class PetMapperTest {

    @Test
    fun `entityAsDomain should map pet`() {
        val input = PetEntity(
            id = "123",
            name = "thor",
            description = "a good boy",
            dateOfBirth = 2345678,
            price = 4523,
            type = PetEntityType.Dog,
            priority = 0.3f
        )
        val output = input.asDomain
        val expected = Pet(
            id = "123",
            name = "thor",
            description = "a good boy",
            dateOfBirth = Instant.fromEpochMilliseconds(2345678),
            price = 4523,
            type = Pet.Type.Dog
        )
        assertThat(output).isEqualTo(expected)
    }

    @Test
    fun `responseAsEntity should map pet`() {
        val input = PetResponseItem(
            id = "123",
            name = "thor",
            price = 4523,
            description = "a good boy",
            type = PetResponseType.Dog,
            dateOfBirth = Instant.fromEpochMilliseconds(2345678),
            priority = 0.3f
        )
        val output = input.asEntity
        val expected = PetEntity(
            id = "123",
            name = "thor",
            description = "a good boy",
            dateOfBirth = 2345678,
            price = 4523,
            type = PetEntityType.Dog,
            priority = 0.3f
        )
        assertThat(output).isEqualTo(expected)
    }
}