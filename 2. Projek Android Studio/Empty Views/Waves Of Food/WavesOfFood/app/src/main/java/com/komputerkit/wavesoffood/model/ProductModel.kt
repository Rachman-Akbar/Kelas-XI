package com.komputerkit.wavesoffood.model

data class ProductModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = ""
)
