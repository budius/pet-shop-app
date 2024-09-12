package com.ronaldo.pace.feature.store.list.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ronaldo.pace.design.theme.PetShopTheme
import com.ronaldo.pace.feature.store.list.Item
import com.ronaldo.pace.store.R

@Composable
internal fun StoreListItem(
    item: Item,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Image(
                modifier = Modifier.align(Alignment.BottomEnd),
                painter = painterResource(item.icon),
                contentDescription = item.iconDescription,
                alpha = 0.2f
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(item.line1, style = PetShopTheme.typography.bodyMedium)
                    Text(
                        item.line2,
                        style = PetShopTheme.typography.labelSmall,
                        color = PetShopTheme.colors.tertiary
                    )
                }
                Text(
                    text = item.price,
                    // The padding here is intentional to stay above the background image.
                    // I'm not sure if that is good design or not, but it was on purpose.
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = PetShopTheme.typography.titleLarge
                )
            }
        }
    }
}


@Preview
@Composable
internal fun PreviewStoreListItem() {
    PetShopTheme {
        StoreListItem(
            Item(
                id = "1234-656768",
                line1 = "Pickles",
                line2 = "5 months old, a very good doggo",
                price = "€39,99",
                icon = R.drawable.ic_dog,
                iconDescription = "background image of a dog"
            ),
            Modifier.padding(12.dp)
        ) {}
    }
}