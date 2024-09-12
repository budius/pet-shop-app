package com.ronaldo.pace.services.database.cart.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartInputEntity(
    @PrimaryKey @ColumnInfo("id") val id: String,
    @ColumnInfo("quantity") val quantity: Int,
)
