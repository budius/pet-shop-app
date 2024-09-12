package com.ronaldo.pace.domain.pets

import kotlinx.datetime.Instant

data class Pet(
    val id: String,
    val name: String,
    val description: String,
    val dateOfBirth: Instant,
    val price: Int,
    val type: Type,
) {
    enum class Type {
        Dog,
        Cat,
        Parrot,
        Turtle,
    }
}


