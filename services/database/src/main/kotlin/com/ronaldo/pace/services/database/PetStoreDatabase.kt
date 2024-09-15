package com.ronaldo.pace.services.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ronaldo.pace.services.database.cart.CartDao
import com.ronaldo.pace.services.database.cart.models.CartInputEntity
import com.ronaldo.pace.services.database.pet.PetDao
import com.ronaldo.pace.services.database.pet.models.PetEntity

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        PetEntity::class,
        CartInputEntity::class,
    ],
)
abstract class PetStoreDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun petDao(): PetDao
}