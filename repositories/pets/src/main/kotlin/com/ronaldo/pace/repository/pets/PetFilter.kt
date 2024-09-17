package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.repository.pets.PetTypeMapper.asEntity
import com.ronaldo.pace.services.database.pet.models.PetEntity
import kotlinx.datetime.Clock

/**
 * This filter is a private part of the repository,
 * that was extracted to its own function for better separation of concerns and better testability.
 *
 * So just like the repository itself, it operates between service and domain data types.
 */
internal object PetFilter {

    fun filter(pet: PetEntity, filter: PetFilters): Boolean {
        // type
        val type = filter.type?.let { pet.type == it.asEntity } ?: true

        // price
        val price = filter.maxPrice?.let { pet.price <= it } ?: true

        // max age
        val age = filter.maxAge?.let {
            val now = Clock.System.now().toEpochMilliseconds()
            val age = now - pet.dateOfBirth
            age <= it.inWholeMilliseconds
        } ?: true

        return type && price && age
    }

}