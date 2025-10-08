package com.komputerkit.easyshop.data.model

data class Banner(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val actionUrl: String = "",
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val startDate: Long = 0,
    val endDate: Long = 0
)