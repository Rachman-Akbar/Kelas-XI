package com.komputerkit.business.models

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val image: String = ""
)
