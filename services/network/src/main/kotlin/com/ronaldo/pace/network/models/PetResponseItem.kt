package com.ronaldo.pace.network.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PetResponseItem(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("price") val price: Int,
    @SerialName("description") val description: String,
    @SerialName("type") val type: PetResponseType,
    @SerialName("dateOfBirth") val dateOfBirth: Instant,
    @SerialName("priority") val priority: Float,
)