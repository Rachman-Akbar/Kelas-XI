package com.example.kishaapp.data.model

import com.google.firebase.Timestamp

data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val imageUrls: List<String> = emptyList(),
    val category: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val location: String = "",
    val type: String = "product",
    val createdAt: Timestamp? = null
)
