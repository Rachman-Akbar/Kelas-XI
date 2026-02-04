package com.komputerkit.business.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STAFF,
    val photoUrl: String = ""
)

enum class UserRole {
    OWNER,
    MANAGER,
    STAFF
}
