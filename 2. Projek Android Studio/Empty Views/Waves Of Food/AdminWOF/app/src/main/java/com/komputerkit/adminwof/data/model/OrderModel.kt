package com.komputerkit.adminwof.data.model

import com.google.firebase.Timestamp

/**
 * Data model untuk Order
 * Sama dengan struktur di aplikasi user
 */
data class OrderModel(
    val id: String = "",
    val userID: String = "",
    val date: Timestamp? = null,
    val items: Map<String, Int> = emptyMap(), // productId to quantity
    val status: String = "",
    val address: String = ""
)
