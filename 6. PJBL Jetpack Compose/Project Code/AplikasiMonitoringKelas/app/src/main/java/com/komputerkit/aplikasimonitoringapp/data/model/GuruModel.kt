package com.komputerkit.aplikasimonitoringapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Guru Model
 */
data class GuruModel(
    val id: Int,
    val nip: String,
    val nama: String,
    val email: String?,
    @SerializedName("no_telepon")
    val noTelepon: String?,
    val alamat: String?,
    @SerializedName("tanggal_lahir")
    val tanggalLahir: String?,
    @SerializedName("jenis_kelamin")
    val jenisKelamin: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

/**
 * Guru Response with Pagination
 */
data class GuruPaginatedData(
    val data: List<GuruModel>,
    val meta: PaginationMeta
)

data class GuruResponse(
    val success: Boolean,
    val message: String,
    val data: GuruPaginatedData
)
