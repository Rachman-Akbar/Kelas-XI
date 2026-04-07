package com.example.kishaapp.data.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "customer",
    val authProvider: String = ""
)

object UserRoles {
    const val ADMIN = "admin"
    const val SELLER = "seller"
    const val CUSTOMER = "customer"
}
