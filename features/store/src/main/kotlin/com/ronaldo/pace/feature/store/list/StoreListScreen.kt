package com.ronaldo.pace.feature.store.list

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ronaldo.pace.feature.store.list.ui.StoreListUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoreListScreen() {

    val viewModel: StoreListViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    StoreListUi(uiState, viewModel::onUiEvent)

}

