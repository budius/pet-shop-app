@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.network.rest.GetPets
import com.ronaldo.pace.repository.pets.PetMapper.asDomain
import com.ronaldo.pace.services.database.pet.models.PetEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart

internal typealias AddPets = suspend (List<PetEntity>) -> Unit
internal typealias GetFlowOfPets = () -> Flow<List<PetEntity>>

// I don't wanna be buried in a pet repository (Ramones, 1989)
/**
 * Classical repository to coordinate multiple data sources into a concise interface.
 * When new data is requested by presentation layer, the repository request more from the API and
 * store it to the database. The refreshed data can be observed by the presentation from the getter.
 */
class PetsQueryRepository(
    private val getPetsFlow: GetFlowOfPets
) {

    fun getPets(filters: Flow<PetFilters>): Flow<List<Pet>> {

        val actualFilters = filters
            .onStart { emit(PetFilters.empty) }
            .distinctUntilChanged()

        return combine(getPetsFlow(), actualFilters) { pets, filter ->
            pets
                .asSequence()
                .filter { pet -> PetFilter.filter(pet, filter) }
                .map { it.asDomain }
                .toList()
        }
    }
}