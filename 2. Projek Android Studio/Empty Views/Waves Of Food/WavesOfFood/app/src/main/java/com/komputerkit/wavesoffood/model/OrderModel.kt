package com.komputerkit.wavesoffood.model

import com.google.firebase.Timestamp

data class OrderModel(
    val id: String = "",
    val userID: String = "",
    val date: Timestamp? = null,
    val items: Map<String, Long> = emptyMap(), // Product ID to Quantity
    val status: String = "",
    val address: String = ""
)
