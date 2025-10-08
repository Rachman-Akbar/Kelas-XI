package com.komputerkit.easyshop.data.model

data class CartItem(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = "",
    val quantity: Int = 1,
    val userId: String = "",
    val addedAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = productPrice * quantity
}