package com.komputerkit.wavesoffood.data.model

/**
 * Data model untuk User
 */
data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val address: String = "",
    val cartItems: Map<String, Int> = emptyMap(), // Product ID to Quantity (changed from Long to Int)
    val favorites: List<String> = emptyList() // List of Product IDs marked as favorite
)
