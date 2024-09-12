package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.network.models.PetsResponse
import com.ronaldo.pace.network.rest.GetPets
import com.ronaldo.pace.repository.pets.PetMapper.asEntity
import kotlinx.coroutines.flow.first

// See README about the load/query separation
class PetsLoadRepository(
    private val getPets: GetPets,
    private val addPets: AddPets,
    private val getPetsFlow: GetFlowOfPets,
) {

    suspend fun initData(): Result<Unit> {
        return if (getPetsFlow().first().isEmpty()) {
            loadAfter(0)
        } else {
            Result.success(Unit)
        }
    }

    suspend fun loadMore(): Result<Unit> {
        val index = getPetsFlow().first().size
        return loadAfter(index)
    }

    private suspend fun loadAfter(index: Int): Result<Unit> {
        val result: Result<PetsResponse> = getPets(index)
        result.getOrNull()?.let { response ->
            addPets(response.items.map { it.asEntity })
        }
        return result.map { }
    }

}