package com.komputerkit.easyshop.data.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val categoryId: String = "",
    val stock: Int = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isActive: Boolean = true,
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)