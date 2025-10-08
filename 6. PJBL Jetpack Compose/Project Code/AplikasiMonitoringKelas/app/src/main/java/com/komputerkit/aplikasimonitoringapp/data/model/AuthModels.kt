package com.komputerkit.aplikasimonitoringapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Login Request Model
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Login Response Model
 */
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData
)

data class LoginData(
    @SerializedName("token")
    val accessToken: String?,
    val user: User
)

/**
 * User Model
 */
data class User(
    val id: Int,
    @SerializedName("nama")
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

/**
 * User Request Model (untuk create user)
 */
data class UserRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String,
    val role: String
)

/**
 * User Response Model (untuk list users)
 */
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class UserListResponse(
    val success: Boolean,
    val message: String,
    val data: List<UserResponse>
)
