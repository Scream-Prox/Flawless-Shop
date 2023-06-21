package com.example.fshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fshop.databases.CartItem
import com.example.fshop.repositories.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository, private val userEmail: String) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    init {
        viewModelScope.launch {
            repository.getCartByUser(userEmail).collect { carts ->
                _cartItems.value = carts
                _totalPrice.value = carts.sumByDouble { it.price.toDouble() }
            }
        }
    }

    fun deleteCartItem(cartId: Int) = viewModelScope.launch {
        repository.deleteCartItem(cartId)
    }
}
