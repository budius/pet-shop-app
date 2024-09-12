package com.ronaldo.pace.services.database

import android.content.Context
import androidx.room.Room
import org.koin.dsl.module

fun databaseModule(context: Context) = module {
    val db: PetStoreDatabase by lazy {
        // the database shouldn't be exposed outside the module
        // hence it is lazily created inside the module lambda
        Room.databaseBuilder(context, PetStoreDatabase::class.java, "pet-store").build()
    }
    single { db.cartDao() }
    single { db.petDao() }
}