package com.example.kishaapp.data.model

data class MarketplaceCartItem(
    val id: String = "",
    val cartId: String = "",
    val productId: String = "",
    val productTitle: String = "",
    val productImageUrl: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0
) {
    val subtotal: Double
        get() = quantity * price
}
