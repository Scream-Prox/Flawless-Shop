package com.example.fshop.repositories

import com.example.fshop.databases.CartDao
import com.example.fshop.databases.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {
    fun getCartByUser(userEmail: String): Flow<List<CartItem>> = cartDao.getCartByUser(userEmail)
    suspend fun deleteCartItem(cartId: Int) = cartDao.deleteCartItem(cartId)
}