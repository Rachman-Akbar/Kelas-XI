package com.komputerkit.business.models

data class Production(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val productionDate: Long = System.currentTimeMillis(),
    val status: ProductionStatus = ProductionStatus.PENDING,
    val notes: String = "",
    val materialsUsed: List<MaterialUsage> = emptyList(),
    val totalCost: Double = 0.0,
    val pic: String? = null
)

data class MaterialUsage(
    val materialId: String = "",
    val materialName: String = "",
    val quantity: Double = 0.0,
    val unit: String = "kg",
    val cost: Double = 0.0
)

enum class ProductionStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
