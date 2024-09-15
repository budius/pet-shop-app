@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.repository.pets.PetMapper.asDomain
import com.ronaldo.pace.repository.pets.PetTypeMapper.asEntity
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration

class PetsQueryRepositoryQuery(
    private val query: (PetEntityType?, Duration?, Int?) -> Flow<List<PetEntity>>
) : PetsQueryRepository {
    override fun getPets(filters: Flow<PetFilters>): Flow<List<Pet>> = filters
        .onStart { emit(PetFilters.empty) }
        .distinctUntilChanged()
        .flatMapLatest { f ->
            query(f.type?.asEntity, f.maxAge, f.maxPrice)
        }.map { list -> list.map { pet -> pet.asDomain } }
}