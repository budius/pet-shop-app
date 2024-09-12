package com.ronaldo.pace.repository.pets

import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.network.models.PetResponseType
import com.ronaldo.pace.repository.pets.PetTypeMapper.asDomain
import com.ronaldo.pace.repository.pets.PetTypeMapper.asEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import org.junit.Test

class PetTypeMapperTest {

    //regin domainAsEntity
    @Test
    fun `domainAsEntity should map dog`() {
        assertThat(Pet.Type.Dog.asEntity).isEqualTo(PetEntityType.Dog)
    }

    @Test
    fun `domainAsEntity should map cat`() {
        assertThat(Pet.Type.Cat.asEntity).isEqualTo(PetEntityType.Cat)
    }

    @Test
    fun `domainAsEntity should map turtle`() {
        assertThat(Pet.Type.Turtle.asEntity).isEqualTo(PetEntityType.Turtle)
    }

    @Test
    fun `domainAsEntity should map parrot`() {
        assertThat(Pet.Type.Parrot.asEntity).isEqualTo(PetEntityType.Parrot)
    }

    //endregion
    //regin entityAsDomain
    @Test
    fun `entityAsDomain should map dog`() {
        assertThat(PetEntityType.Dog.asDomain).isEqualTo(Pet.Type.Dog)
    }

    @Test
    fun `entityAsDomain should map cat`() {
        assertThat(PetEntityType.Cat.asDomain).isEqualTo(Pet.Type.Cat)
    }

    @Test
    fun `entityAsDomain should map turtle`() {
        assertThat(PetEntityType.Turtle.asDomain).isEqualTo(Pet.Type.Turtle)
    }

    @Test
    fun `entityAsDomain should map parrot`() {
        assertThat(PetEntityType.Parrot.asDomain).isEqualTo(Pet.Type.Parrot)
    }

    //endregion
    //regin responseAsEntity
    @Test
    fun `responseAsEntity should map dog`() {
        assertThat(PetResponseType.Dog.asEntity).isEqualTo(PetEntityType.Dog)
    }

    @Test
    fun `responseAsEntity should map cat`() {
        assertThat(PetResponseType.Cat.asEntity).isEqualTo(PetEntityType.Cat)
    }

    @Test
    fun `responseAsEntity should map turtle`() {
        assertThat(PetResponseType.Turtle.asEntity).isEqualTo(PetEntityType.Turtle)
    }

    @Test
    fun `responseAsEntity should map parrot`() {
        assertThat(PetResponseType.Parrot.asEntity).isEqualTo(PetEntityType.Parrot)
    }
    //endregion

}