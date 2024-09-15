package com.ronaldo.pace.services.database.pet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
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

    @RawQuery(observedEntities = [PetEntity::class])
    abstract fun internalGetWithFilters(query: RoomRawQuery): Flow<List<PetEntity>>

    fun getWithFilters(
        type: PetEntityType? = null,
        maxAge: Duration? = null,
        maxPrice: Int? = null,
    ): Flow<List<PetEntity>> {
        val sql = buildString {
            append("SELECT * FROM pets")
            if (type != null || maxAge != null || maxPrice != null) append(" WHERE")
            if (type != null) {
                append(" type = '${type.name}'")
                if (maxAge != null || maxPrice != null) append(" AND")
            }
            if (maxAge != null) {
                val maxDateOfBirth = (Clock.System.now() - maxAge).toEpochMilliseconds()
                append(" dateOfBirth >= $maxDateOfBirth")
                if (maxPrice != null) append(" AND")
            }
            if (maxPrice != null) {
                append(" price <= $maxPrice")
            }
            append(" ORDER BY priority ASC")
        }
        val query = RoomRawQuery(
            sql = sql,
            //onBindStatement = { it.bindLong(1, 3) }
        )

        return internalGetWithFilters(query)

    }

    //@Transaction
    fun getYoungerThan(maxAge: Duration): Flow<List<PetEntity>> {
        val now = Clock.System.now()
        val minDateOfBirth = (now - maxAge).toEpochMilliseconds()
        return internalGetBornAfter(minDateOfBirth)
    }
}
//
//fun PetDao.getWithFilters(
//    type: PetEntityType?,
//    maxAge: Long?,
//    maxPrice: Int?,
//): Flow<List<PetEntity>> {
//    val sql = buildString {
//        append("SELECT * FROM pets ORDER BY priority ASC")
//        if (type != null || maxAge != null || maxPrice != null) append(" WHERE")
//        if (type != null) {
//            append("type = ${type.name}")
//            if (maxAge != null || maxPrice != null) append(" AND")
//        }
//        if (maxAge != null) {
//            val maxDateOfBirth = Clock.System.now().toEpochMilliseconds() - maxAge
//            append(" dateOfBirth <= $maxDateOfBirth")
//            if (maxPrice != null) append(" AND")
//        }
//        if (maxPrice != null) {
//            append(" price <= $maxPrice")
//        }
//    }
//    val query = RoomRawQuery(
//        sql = sql,
//        //onBindStatement = { it.bindLong(1, 3) }
//    )
//
//    return internalGetWithFilters(query)
//
//}
