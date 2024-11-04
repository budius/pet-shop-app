package com.ronaldo.pace.feature.store.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.feature.common.AsyncSupplierCase
import com.ronaldo.pace.feature.common.FlowUseCase
import com.ronaldo.pace.feature.common.UiVisibility.Content
import com.ronaldo.pace.feature.common.UiVisibility.Error
import com.ronaldo.pace.feature.common.UiVisibility.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

internal class StoreListViewModel(
    getData: FlowUseCase<Flow<PetFilters>, Result<List<Pet>>>,
    private val loadMore: AsyncSupplierCase<Result<Unit>>,
    inScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = inScope ?: viewModelScope
    private val mapper = StoreListMapper()

    private val filter = MutableStateFlow(PetFilters.empty)
    private val dataSource = getData(filter)
    private val visibility = MutableStateFlow(Content)

    internal val uiState: StateFlow<StoreListUiState> = combine(
        dataSource.onStart { emit(Result.failure(FIRST_LOAD_TOKEN)) }, visibility
    ) { data, visible ->
        data.fold(
            onSuccess = {
                Timber.d("Mapping ${it.size} items to UI. $visible")
                mapper.map(it, visible)
            },
            onFailure = { exception ->
                if (exception === FIRST_LOAD_TOKEN) {
                    Timber.d("Initial data load")
                    StoreListUiState.initial
                } else {
                    Timber.e(exception, "Data load failure ${exception.message}")
                    StoreListUiState(Error, Error, emptyList())
                }
            }
        )
    }.stateIn(
        scope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000), StoreListUiState.initial
    )

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

private val FIRST_LOAD_TOKEN = RuntimeException()