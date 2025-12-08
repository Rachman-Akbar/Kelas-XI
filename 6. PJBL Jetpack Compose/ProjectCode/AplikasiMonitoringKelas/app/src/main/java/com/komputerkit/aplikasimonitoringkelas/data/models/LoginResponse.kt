package com.komputerkit.aplikasimonitoringkelas.data.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData? = null
)

data class LoginData(
    val user: User,
    val token: String,
    @SerializedName("token_type")
    val token_type: String
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("guru_id")
    val guru_id: Int? = null,
    @SerializedName("kelas_id")
    val kelas_id: Int? = null
)