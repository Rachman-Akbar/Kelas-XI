package com.komputerkit.aplikasimonitoringkelas.data.models

data class ApiResult<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
