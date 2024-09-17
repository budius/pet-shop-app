package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.network.models.PetResponseType
import com.ronaldo.pace.services.database.pet.models.PetEntityType

internal object PetTypeMapper {

    val Pet.Type.asEntity: PetEntityType
        get() = when (this) {
            Pet.Type.Dog -> PetEntityType.Dog
            Pet.Type.Cat -> PetEntityType.Cat
            Pet.Type.Parrot -> PetEntityType.Parrot
            Pet.Type.Turtle -> PetEntityType.Turtle
        }

    val PetEntityType.asDomain: Pet.Type
        get() = when (this) {
            PetEntityType.Dog -> Pet.Type.Dog
            PetEntityType.Cat -> Pet.Type.Cat
            PetEntityType.Parrot -> Pet.Type.Parrot
            PetEntityType.Turtle -> Pet.Type.Turtle
        }

    val PetResponseType.asEntity: PetEntityType
        get() = when (this) {
            PetResponseType.Dog -> PetEntityType.Dog
            PetResponseType.Cat -> PetEntityType.Cat
            PetResponseType.Parrot -> PetEntityType.Parrot
            PetResponseType.Turtle -> PetEntityType.Turtle
        }
}