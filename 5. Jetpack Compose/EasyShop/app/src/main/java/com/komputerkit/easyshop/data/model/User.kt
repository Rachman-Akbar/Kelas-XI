package com.komputerkit.easyshop.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val favorites: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)