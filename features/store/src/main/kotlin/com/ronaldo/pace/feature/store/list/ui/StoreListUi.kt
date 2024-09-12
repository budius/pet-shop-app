@file:OptIn(ExperimentalFoundationApi::class)

package com.ronaldo.pace.feature.store.list.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ronaldo.pace.design.theme.PetShopTheme
import com.ronaldo.pace.feature.common.UiVisibility
import com.ronaldo.pace.feature.store.list.Item
import com.ronaldo.pace.feature.store.list.StoreListUiEvent
import com.ronaldo.pace.feature.store.list.StoreListUiEvent.*
import com.ronaldo.pace.feature.store.list.StoreListUiState
import com.ronaldo.pace.store.R

private val itemPadding = Modifier.padding(12.dp)

@Composable
internal fun StoreListUi(uiState: StoreListUiState, onUiEvent: (StoreListUiEvent) -> Unit) {
    // TODO add scaffold / toolbar
    when (uiState.fullScreenVisibility) {
        UiVisibility.Loading -> DrawFullscreenLoading()
        UiVisibility.Content -> DrawContent(uiState, onUiEvent)
        UiVisibility.Error -> DrawFullscreenError { onUiEvent(Retry) }
    }
}

@Composable
private fun DrawContent(uiState: StoreListUiState, onUiEvent: (StoreListUiEvent) -> Unit) {
    val listState = rememberLazyListState()
    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            onUiEvent(OnReachedBottom)
        }
    }
    LazyColumn(Modifier.fillMaxSize(), listState, PaddingValues(vertical = 32.dp)) {
        items(uiState.items, key = { it.id }) { item ->
            StoreListItem(item, itemPadding.animateItemPlacement()) {
                onUiEvent(OnItemSelected(item))
            }
        }
        when (uiState.loadMoreVisibility) {
            UiVisibility.Loading -> drawLoadMoreLoading()
            UiVisibility.Error -> drawLoadMoreError { onUiEvent(Retry) }
            UiVisibility.Content -> {} // no-op
        }
    }
}

@Composable
private fun DrawFullscreenLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}


private fun LazyListScope.drawLoadMoreLoading() {
    item("loading") {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp)
                .animateItemPlacement(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun DrawFullscreenError(onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Something didn't work as planned,\n" +
                    "maybe check your internet and try again.",
            textAlign = TextAlign.Center,
            style = PetShopTheme.typography.titleMedium
        )
        Spacer(Modifier.size(48.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}


private fun LazyListScope.drawLoadMoreError(onRetry: () -> Unit) {
    item("error") {
        // I have a feeling that an UI designer would kill me
        // by using the same item for error as for the normal ones
        StoreListItem(
            Item(
                "error",
                "Something didn't work as planned",
                "Check your internet and tap here to try again",
                "",
                R.drawable.ic_error,
                "exclamation point, indication of error"
            ),
            itemPadding.animateItemPlacement(),
            onRetry
        )
    }
}

@Preview
@Composable
internal fun PreviewStoreListUi(
    @PreviewParameter(PreviewProviderStoreList::class) uiState: StoreListUiState
) {
    PetShopTheme {
        StoreListUi(uiState) {}
    }
}

internal class PreviewProviderStoreList : PreviewParameterProvider<StoreListUiState> {

    private val data = listOf(
        Item("1", "Pegasus", "3 years old, fiery dog", "€75,99", R.drawable.ic_dog, ""),
        Item("2", "Ocelot", "2 months young, cuddly turtle", "€123,44", R.drawable.ic_turtle, "")
    )
    override val values: Sequence<StoreListUiState>
        get() = sequenceOf(
            StoreListUiState(UiVisibility.Content, UiVisibility.Loading, data),
            StoreListUiState(UiVisibility.Content, UiVisibility.Error, data),
            StoreListUiState(UiVisibility.Loading, UiVisibility.Loading, emptyList()),
            StoreListUiState(UiVisibility.Error, UiVisibility.Loading, emptyList()),
        )

}