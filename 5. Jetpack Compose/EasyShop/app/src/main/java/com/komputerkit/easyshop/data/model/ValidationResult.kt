package com.komputerkit.easyshop.data.model

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)