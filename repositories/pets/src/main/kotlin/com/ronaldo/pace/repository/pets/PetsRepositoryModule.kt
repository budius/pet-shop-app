package com.ronaldo.pace.repository.pets

import com.ronaldo.pace.network.rest.GetPets
import com.ronaldo.pace.services.database.pet.PetDao
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun petsRepositoryModule() = module {
    single { providesPetsQueryRepository() }
    single { providesPetsLoadRepository() }
}

private fun Scope.providesPetsQueryRepository(): PetsQueryRepository {
    val dao: PetDao = get()
    return PetsQueryRepositoryV1(
        getPetsFlow = dao::getAll,
        getPetsFlowByType = dao::getByType,
        getPetsFlowMaxAgeDays = dao::getYoungerThan
    )
}

private fun Scope.providesPetsLoadRepository(): PetsLoadRepository {
    val dao: PetDao = get()
    val rest: GetPets = get()
    return PetsLoadRepository(rest, dao::insert, dao::getAll)
}