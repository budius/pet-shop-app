package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.network.models.PetResponseItem
import com.ronaldo.pace.repository.pets.PetTypeMapper.asDomain
import com.ronaldo.pace.repository.pets.PetTypeMapper.asEntity
import com.ronaldo.pace.services.database.pet.models.PetEntity
import kotlinx.datetime.Instant

internal object PetMapper {
    val PetEntity.asDomain: Pet
        get() = Pet(
            id = id,
            name = name,
            description = description,
            dateOfBirth = Instant.fromEpochMilliseconds(dateOfBirth),
            price = price,
            type = type.asDomain,
        )

    val PetResponseItem.asEntity: PetEntity
        get() {
            return PetEntity(
                id,
                name,
                description,
                dateOfBirth.toEpochMilliseconds(),
                price,
                type.asEntity,
                priority
            )
        }
}