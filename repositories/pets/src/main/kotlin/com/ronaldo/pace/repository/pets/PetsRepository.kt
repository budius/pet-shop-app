package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.network.rest.GetPets
import com.ronaldo.pace.repository.pets.PetMapper.asDomain
import com.ronaldo.pace.repository.pets.PetMapper.asEntity
import com.ronaldo.pace.services.database.pet.models.PetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

internal typealias AddPets = suspend (List<PetEntity>) -> Unit
internal typealias GetFlowOfPets = () -> Flow<List<PetEntity>>
internal typealias GetPetCount = suspend () -> Long

// I don't wanna be buried in a pet repository (Ramones, 1989)
/**
 * Classical repository to coordinate multiple data sources into a concise interface.
 * When new data is requested by presentation layer, the repository request more from the API and
 * store it to the database. The refreshed data can be observed by the presentation from the getter.
 */
class PetsRepository(
    private val getPetsEntityFlow: GetFlowOfPets,
    private val addPetsEntity: AddPets,
    private val countPetsEntity: GetPetCount,
    private val getPetsApi: GetPets,
) {

    private val loadMutex = Mutex()

    fun getPets(filters: Flow<PetFilters>): Flow<Result<List<Pet>>> {

        val localFilters = filters
            .onStart { emit(PetFilters.empty) }
            .distinctUntilChanged()

        return combine(getPetsEntityFlow(), localFilters) { pets, filter ->
            val result = pets.asSequence().filter { pet -> PetFilter.filter(pet, filter) }
                .map { it.asDomain }.toList()

            if (result.isEmpty()) {
                Timber.d("Auto-loading more pets from API. In DB ${pets.size}")
                internalLoadMore().map { emptyList() }
            } else {
                Timber.d("Delivering ${result.size}/${pets.size} pets to the UI")
                Result.success(result)
            }
        }
    }

    suspend fun loadMore(): Result<Unit> {
        Timber.d("Loading more data requested from the UI layer")
        return internalLoadMore()
    }

    private suspend fun internalLoadMore(): Result<Unit> {
        return loadMutex.withLock {
            val index = countPetsEntity()
            getPetsApi(index).onSuccess { response ->
                Timber.d("Loaded ${response.items.size} from API after $index")
                addPetsEntity(response.items.map { it.asEntity })
            }.onFailure {
                Timber.e(it, "Failed to load data. ${it.message}")
            }.map { } // map to `Unit`
        }
    }
}