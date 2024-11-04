package com.ronaldo.pace.feature.store

import com.ronaldo.pace.feature.store.list.StoreListViewModel
import com.ronaldo.pace.repository.pets.PetsRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun storeFeatureModule() = module {
    viewModel { provideStoreListViewModel() }
}

private fun Scope.provideStoreListViewModel(): StoreListViewModel {
    val pets: PetsRepository = get()

    return StoreListViewModel(
        getData = { pets.getPets(it) },
        loadMore = pets::loadMore
    )
}
