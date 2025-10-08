package com.komputerkit.easyshop.data.model

data class Category(
    val id: String = "",
    val name: String = "",
    val iconUrl: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val productCount: Int = 0,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)