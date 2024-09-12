package com.ronaldo.pace.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PetsResponse(
    @SerialName("index") val index: Int,
    @SerialName("count") val count: Int,
    @SerialName("items") val items: List<PetResponseItem>
)