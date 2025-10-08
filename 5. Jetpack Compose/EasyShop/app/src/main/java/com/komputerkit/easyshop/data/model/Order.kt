package com.komputerkit.easyshop.data.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userName: String = "",
    val items: List<OrderItem> = emptyList(),
    val shippingAddress: ShippingAddress = ShippingAddress(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shippingCost: Double = 0.0,
    val total: Double = 0.0,
    val status: String = "Ordered", // Ordered, Processing, Shipped, Delivered, Cancelled
    val paymentMethod: String = "",
    val paymentStatus: String = "Pending", // Pending, Paid, Failed
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = "",
    val quantity: Int = 1,
    val totalPrice: Double = 0.0
)

data class ShippingAddress(
    val recipientName: String = "",
    val fullAddress: String = "",
    val phoneNumber: String = "",
    val city: String = "",
    val postalCode: String = "",
    val notes: String = ""
)