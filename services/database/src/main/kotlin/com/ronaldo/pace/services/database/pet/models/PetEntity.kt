package com.ronaldo.pace.services.database.pet.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @ColumnInfo("id") @PrimaryKey val id: String,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("dateOfBirth") val dateOfBirth: Long,
    @ColumnInfo("price") val price: Int,
    @ColumnInfo("type") val type: PetEntityType,
    @ColumnInfo("priority") val priority: Float,
)