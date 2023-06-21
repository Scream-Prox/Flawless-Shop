package com.example.fshop.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert
    fun insertCart(cart: CartItem)

    @Query("SELECT * FROM cart")
    fun getAllCart(): Flow<List<CartItem>>

    @Update
    fun updateCart(cart: CartItem)

    @Query("SELECT * FROM Cart WHERE user_info = :userEmail")
    fun getCartByUser(userEmail: String): Flow<List<CartItem>>

    @Query("DELETE FROM cart WHERE id = :cartId")
    suspend fun deleteCartItem(cartId: Int)

    @Query("DELETE FROM cart WHERE user_info = :userEmail")
    suspend fun deleteCartItemsByUser(userEmail: String)

    @Query("SELECT * FROM cart WHERE user_info = :userEmail")
    fun getCartItemsByUser(userEmail: String): Flow<List<CartItem>>


}