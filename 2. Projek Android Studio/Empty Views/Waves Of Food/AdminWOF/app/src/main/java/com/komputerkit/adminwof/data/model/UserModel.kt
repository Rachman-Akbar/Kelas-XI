package com.komputerkit.adminwof.data.model

/**
 * Data model untuk User
 * Untuk menampilkan info user di order management
 */
data class UserModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val address: String = ""
)
