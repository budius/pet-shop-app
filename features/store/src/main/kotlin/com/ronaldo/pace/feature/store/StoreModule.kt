package com.ronaldo.pace.feature.store

import com.ronaldo.pace.feature.store.list.StoreListViewModel
import com.ronaldo.pace.repository.pets.PetsLoadRepository
import com.ronaldo.pace.repository.pets.PetsQueryRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun storeFeatureModule() = module {
    viewModel { provideStoreListViewModel() }
}

private fun Scope.provideStoreListViewModel(): StoreListViewModel {
    val load: PetsLoadRepository = get()
    val query: PetsQueryRepository = get()

    return StoreListViewModel(
        getData = query::getPets,
        initData = load::initData,
        loadMore = load::loadMore
    )
}
