package com.example.kishaapp.data.model

import com.google.firebase.Timestamp

data class Order(
    val id: String = "",
    val userId: String = "",
    val totalPrice: Double = 0.0,
    val status: String = "pending",
    val transactionCode: String = "",
    val createdAt: Timestamp? = null,
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val id: String = "",
    val orderId: String = "",
    val productId: String = "",
    val productTitle: String = "",
    val productImageUrl: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0
) {
    val subtotal: Double
        get() = quantity * price
}
