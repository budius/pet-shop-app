package com.ronaldo.pace.services.database.cart.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.ronaldo.pace.services.database.pet.models.PetEntity

data class CartEntity(
    @ColumnInfo("quantity") val quantity: Int,
    @Embedded val petEntity: PetEntity,
)
