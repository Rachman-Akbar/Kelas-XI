package com.komputerkit.wavesoffood.data.model

import com.google.firebase.Timestamp

/**
 * Data model untuk Order
 */
data class OrderModel(
    val id: String = "",
    val userID: String = "",
    val date: Timestamp = Timestamp.now(),
    val items: Map<String, Int> = emptyMap(), // Product ID to Quantity (changed from Long to Int)
    val status: String = "Pending",
    val address: String = ""
)
