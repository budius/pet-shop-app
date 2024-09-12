package com.ronaldo.pace.feature.store.list

internal sealed class StoreListUiEvent {
    data object Retry : StoreListUiEvent()
    data object OnReachedBottom : StoreListUiEvent()
    data class OnItemSelected(val item: Item) : StoreListUiEvent()
}