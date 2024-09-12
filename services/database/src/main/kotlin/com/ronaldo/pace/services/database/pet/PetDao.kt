package com.ronaldo.pace.services.database.pet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlin.time.Duration

@Dao
abstract class PetDao {

    @Insert
    abstract suspend fun insert(pets: List<PetEntity>)

    @Query("SELECT * FROM pets ORDER BY priority ASC")
    abstract fun getAll(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE type = :type ORDER BY priority ASC")
    abstract fun getByType(type: PetEntityType): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE dateOfBirth >= :minDateOfBirth ORDER BY priority ASC")
    abstract fun internalGetBornAfter(minDateOfBirth: Long): Flow<List<PetEntity>>

    @Transaction
    open fun getYoungerThan(maxAge: Duration): Flow<List<PetEntity>> {
        val now = Clock.System.now()
        val minDateOfBirth = (now - maxAge).toEpochMilliseconds()
        return internalGetBornAfter(minDateOfBirth)
    }
}
