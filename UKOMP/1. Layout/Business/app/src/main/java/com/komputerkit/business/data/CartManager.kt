package com.komputerkit.business.data

import com.komputerkit.business.models.Product

object CartManager {
    private val _cartItems = mutableMapOf<Product, Int>()
    
    val cartItems: Map<Product, Int>
        get() = _cartItems.toMap()
    
    fun addToCart(product: Product, quantity: Int = 1) {
        _cartItems[product] = (_cartItems[product] ?: 0) + quantity
    }
    
    fun removeFromCart(product: Product) {
        val currentQty = _cartItems[product] ?: 0
        if (currentQty > 1) {
            _cartItems[product] = currentQty - 1
        } else {
            _cartItems.remove(product)
        }
    }
    
    fun updateQuantity(product: Product, quantity: Int) {
        if (quantity > 0) {
            _cartItems[product] = quantity
        } else {
            _cartItems.remove(product)
        }
    }
    
    fun getQuantity(product: Product): Int {
        return _cartItems[product] ?: 0
    }
    
    fun clearCart() {
        _cartItems.clear()
    }
    
    fun getTotalItems(): Int {
        return _cartItems.values.sum()
    }
    
    fun getTotalPrice(): Double {
        return _cartItems.entries.sumOf { (product, quantity) ->
            product.sellingPrice * quantity
        }
    }
}
