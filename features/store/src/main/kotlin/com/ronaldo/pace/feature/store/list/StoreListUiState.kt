package com.ronaldo.pace.feature.store.list

import androidx.annotation.DrawableRes
import com.ronaldo.pace.feature.common.UiVisibility

internal data class StoreListUiState(
    val fullScreenVisibility: UiVisibility,
    val loadMoreVisibility: UiVisibility,
    val items: List<Item>,
) {
    companion object {
        val initial = StoreListUiState(UiVisibility.Loading, UiVisibility.Loading, emptyList())
    }
}


internal data class Item(
    val id: String,
    val line1: String,
    val line2: String,
    val price: String,
    @DrawableRes val icon: Int,
    val iconDescription: String,
)