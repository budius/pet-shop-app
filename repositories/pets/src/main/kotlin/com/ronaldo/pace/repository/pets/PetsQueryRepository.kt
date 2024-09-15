package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import kotlinx.coroutines.flow.Flow


// I don't wanna be buried in a pet repository (Ramones, 1989)
/**
 * Classical repository to coordinate multiple data sources into a concise interface.
 * When new data is requested by presentation layer, the repository request more from the API and
 * store it to the database. The refreshed data can be observed by the presentation from the getter.
 */
interface PetsQueryRepository {
    fun getPets(filters: Flow<PetFilters>): Flow<List<Pet>>
}