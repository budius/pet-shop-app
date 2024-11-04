package com.ronaldo.pace.feature.store.list

import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.feature.common.RelativeAgeMapper
import com.ronaldo.pace.feature.common.UiVisibility
import com.ronaldo.pace.store.R
import kotlinx.datetime.Instant

internal class StoreListMapper(
    private val relativeAgeMapper: (Instant) -> String = RelativeAgeMapper::map
) {
    fun map(data: List<Pet>, visible: UiVisibility): StoreListUiState = StoreListUiState(
        fullScreenVisibility = if (data.isEmpty()) visible else UiVisibility.Content,
        loadMoreVisibility = visible,
        items = data.map { pet ->
            Item(
                id = pet.id,
                line1 = pet.name,
                line2 = line2(pet),
                price = price(pet),
                icon = icon(pet),
                iconDescription = iconDescription(pet),
            )
        })

    private fun line2(pet: Pet): String {
        return buildString {
            append(relativeAgeMapper(pet.dateOfBirth))
            val desc = pet.description.replaceFirstChar { it.lowercase() }
            if (desc.isNotBlank()) {
                append(", ")
                append(desc)
            }
        }
    }

    private fun price(pet: Pet): String {
        val base = pet.price.toString().padStart(3, '0')
        return buildString {
            append("€")
            append(base.substring(0, base.length - 2))
            append(",")
            append(base.substring(base.length - 2))
        }
    }

    private fun icon(pet: Pet): Int = when (pet.type) {
        Pet.Type.Dog -> R.drawable.ic_dog
        Pet.Type.Cat -> R.drawable.ic_cat
        Pet.Type.Parrot -> R.drawable.ic_parrot
        Pet.Type.Turtle -> R.drawable.ic_turtle
    }

    private fun iconDescription(pet: Pet): String {
        val type = when (pet.type) {
            Pet.Type.Dog -> "dog"
            Pet.Type.Cat -> "cat"
            Pet.Type.Parrot -> "parrot"
            Pet.Type.Turtle -> "turtle"
        }
        return "background image of a $type"
    }
}