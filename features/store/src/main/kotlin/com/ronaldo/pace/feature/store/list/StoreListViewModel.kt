package com.ronaldo.pace.feature.store.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.feature.common.AsyncSupplierCase
import com.ronaldo.pace.feature.common.FlowUseCase
import com.ronaldo.pace.feature.common.UiVisibility
import com.ronaldo.pace.feature.common.UiVisibility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class StoreListViewModel(
    getData: FlowUseCase<Flow<PetFilters>, List<Pet>>,
    initData: AsyncSupplierCase<Result<Unit>>,
    private val loadMore: AsyncSupplierCase<Result<Unit>>,
    inScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = inScope ?: viewModelScope

    private val filter = MutableStateFlow(PetFilters.empty)
    private val dataSource = getData(filter)
    private val visibility = MutableStateFlow(Loading)

    private val mapper = StoreListMapper()

    val uiState: StateFlow<StoreListUiState> = combine(
        filter, dataSource.onStart { emit(emptyList()) }, visibility
    ) { filter, data, visible ->
        mapper.map(filter, data, visible)
    }.stateIn(
        scope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000), StoreListUiState.initial
    )

    init {
        scope.launch {
            initData()
                .onSuccess { visibility.value = Content }
                .onFailure { visibility.value = Error }
        }
    }

    internal fun onUiEvent(event: StoreListUiEvent) = scope.launch {
        when (event) {
            is StoreListUiEvent.OnItemSelected -> {} // TODO(add to cart)
            StoreListUiEvent.Retry -> load()
            StoreListUiEvent.OnReachedBottom -> load()
        }
    }

    private suspend fun load() {
        if (visibility.value == Loading) {
            // debounce duplicate calls from the UI
            return
        }
        visibility.value = Loading
        loadMore()
            .onSuccess { visibility.value = Content }
            .onFailure { visibility.value = Error }
    }

}