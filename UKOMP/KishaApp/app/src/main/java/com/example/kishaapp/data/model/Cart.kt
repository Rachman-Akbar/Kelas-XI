package com.example.kishaapp.data.model

import com.google.firebase.Timestamp

data class Cart(
    val id: String = "",
    val userId: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class CartWithItems(
    val cart: Cart,
    val items: List<MarketplaceCartItem>
) {
    val subtotal: Double = items.sumOf { it.subtotal }
}
