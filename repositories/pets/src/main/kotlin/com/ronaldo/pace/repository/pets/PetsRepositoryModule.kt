package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.network.rest.GetPets
import com.ronaldo.pace.services.database.pet.PetDao
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun petsRepositoryModule() = module {
    single { providesPetsQueryRepository() }
}

private fun Scope.providesPetsQueryRepository(): PetsRepository {
    val dao: PetDao = get()
    val rest: GetPets = get()
    return PetsRepository(
        getPetsEntityFlow = dao::getAll,
        addPetsEntity = dao::insert,
        countPetsEntity = dao::getPetCount,
        getPetsApi = rest
    )
}