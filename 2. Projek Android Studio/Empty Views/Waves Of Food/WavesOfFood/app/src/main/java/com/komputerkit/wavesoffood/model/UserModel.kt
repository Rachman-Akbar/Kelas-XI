package com.komputerkit.wavesoffood.model

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val address: String = "",
    val cartItems: Map<String, Long> = emptyMap() // Product ID to Quantity
)
