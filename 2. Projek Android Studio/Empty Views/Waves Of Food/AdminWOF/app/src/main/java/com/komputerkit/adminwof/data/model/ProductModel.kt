package com.komputerkit.adminwof.data.model

/**
 * Data model untuk Product
 * Sama dengan struktur di aplikasi user
 */
data class ProductModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = ""
)
