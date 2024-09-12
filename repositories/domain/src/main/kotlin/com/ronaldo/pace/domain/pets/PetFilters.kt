package com.ronaldo.pace.domain.pets

import kotlin.time.Duration

data class PetFilters(
    val type: Pet.Type? = null,
    val maxAge: Duration? = null,
    val maxPrice: Int? = null,
) {
    companion object {
        val empty = PetFilters(null, null, null)
    }
}