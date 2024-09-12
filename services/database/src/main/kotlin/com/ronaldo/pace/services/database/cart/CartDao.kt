package com.ronaldo.pace.services.database.cart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ronaldo.pace.services.database.cart.models.CartEntity
import com.ronaldo.pace.services.database.cart.models.CartInputEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart, pets WHERE cart.id = pets.id ORDER BY quantity DESC, priority ASC")
    fun getCart(): Flow<List<CartEntity>>

    @Query("DELETE FROM cart")
    suspend fun clearCart()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun internalInsert(item: CartInputEntity)

    @Query("SELECT * FROM cart WHERE cart.id = :id ")
    suspend fun internalGetById(id: String): CartInputEntity?

    @Transaction
    suspend fun addToCart(petId: String) {
        val newItem = internalGetById(petId)
            ?.let { it.copy(quantity = it.quantity + 1) }
            ?: CartInputEntity(petId, 1)
        internalInsert(newItem)
    }

}