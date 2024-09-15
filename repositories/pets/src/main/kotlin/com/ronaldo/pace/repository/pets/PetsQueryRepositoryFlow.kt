package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.repository.pets.PetMapper.asDomain
import com.ronaldo.pace.repository.pets.PetTypeMapper.asEntity
import com.ronaldo.pace.services.database.pet.models.PetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock

/**
 * A Flow based implementation of the pet repo filtering
 */
class PetsQueryRepositoryFlow(
    private val getPetsFlow: GetFlowOfPets
) : PetsQueryRepository {
    
    override fun getPets(filters: Flow<PetFilters>): Flow<List<Pet>> {

        val actualFilters = filters
            .onStart { emit(PetFilters.empty) }
            .distinctUntilChanged()

        return combine(getPetsFlow(), actualFilters) { pets, filter ->
            pets
                .asSequence()
                .filter { pet -> filter(pet, filter) }
                .map { it.asDomain }
                .toList()
        }
    }

    private fun filter(pet: PetEntity, filter: PetFilters): Boolean {
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