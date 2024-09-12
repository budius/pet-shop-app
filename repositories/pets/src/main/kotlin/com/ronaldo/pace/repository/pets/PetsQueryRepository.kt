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
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal typealias AddPets = suspend (List<PetEntity>) -> Unit
internal typealias GetFlowOfPets = () -> Flow<List<PetEntity>>
internal typealias GetFlowOfPetsByType = (PetEntityType) -> Flow<List<PetEntity>>
internal typealias GetFlowOfPetsMaxAgeDays = (Duration) -> Flow<List<PetEntity>>

// I don't wanna be buried in a pet repository (Ramones, 1989)
/**
 * Classical repository to coordinate multiple data sources into a concise interface.
 * When new data is requested by presentation layer, the repository request more from the API and
 * store it to the database. The refreshed data can be observed by the presentation from the getter.
 */
class PetsQueryRepository(
    private val getPetsFlow: GetFlowOfPets,
    private val getPetsFlowByType: GetFlowOfPetsByType,
    private val getPetsFlowMaxAgeDays: GetFlowOfPetsMaxAgeDays
) {

    fun getPets(filters: Flow<PetFilters>): Flow<List<Pet>> {
        return filters
            .onStart { emit(PetFilters.empty) }
            .distinctUntilChanged()
            .flatMapLatest { currentFilter ->

                // destruct the types to workaround smart cast not working from different modules
                // as filter can be expanded or have order changed, IMHO is safer to manually
                // destruct it to avoid changes in the future break in unexpected ways
                val type = currentFilter.type
                val maxAge = currentFilter.maxAge
                val maxPrice = currentFilter.maxPrice

                if (type != null) {
                    getByTypeAndMaxAge(type, maxAge)
                } else if (maxAge != null) {
                    getPetsFlowMaxAgeDays(maxAge)
                } else {
                    getPetsFlow()
                }.run {
                    if (maxPrice != null) {
                        this.map { list -> list.filter { pet -> pet.price <= maxPrice } }
                    } else {
                        this
                    }
                }
            }.map { list -> list.map { pet -> pet.asDomain } }
    }

    private fun getByTypeAndMaxAge(type: Pet.Type, maxAge: Duration?): Flow<List<PetEntity>> {
        // In the README there are observations and an "issue" about this piece of code

        // Here is the point where a RawQuery would be helpful.
        // As the number of possible filters and sorting grows, it gets quite confusing
        // to have some filters in SQL and some done via kotlin Collection functions or
        // even worse (as seen here), Collection functions duplicating functionality from
        // the SQL query. Ideally it would be all Flow/Collections or all SQL
        val byType = getPetsFlowByType(type.asEntity)
        return if (maxAge != null) {
            byType.map { list ->
                list.filter { pet ->
                    (now() - pet.dateOfBirth).milliseconds < maxAge
                }
            }
        } else {
            byType
        }

    }

    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

}