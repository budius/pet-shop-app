package com.ronaldo.pace.services.database.pet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ronaldo.pace.services.database.pet.models.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PetDao {

    @Insert
    abstract suspend fun insert(pets: List<PetEntity>)

    @Query("SELECT * FROM pets ORDER BY priority ASC")
    abstract fun getAll(): Flow<List<PetEntity>>

    @Query("SELECT COUNT(*) FROM pets")
    abstract suspend fun getPetCount(): Long

}
