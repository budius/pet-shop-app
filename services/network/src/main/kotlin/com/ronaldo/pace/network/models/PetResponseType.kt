package com.ronaldo.pace.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PetResponseType {
    @SerialName("DOG")
    Dog,
    @SerialName("CAT")
    Cat,
    @SerialName("PARROT")
    Parrot,
    @SerialName("TURTLE")
    Turtle,
}