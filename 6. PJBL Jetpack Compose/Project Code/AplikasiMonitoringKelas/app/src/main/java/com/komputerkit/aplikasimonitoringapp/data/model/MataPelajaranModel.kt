package com.komputerkit.aplikasimonitoringapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Mata Pelajaran Model
 */
data class MataPelajaranModel(
    val id: Int,
    val kode: String,
    val nama: String,
    val sks: Int,
    val kategori: String,
    val deskripsi: String?,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

/**
 * Mata Pelajaran Response with Pagination
 */
data class MataPelajaranPaginatedData(
    val data: List<MataPelajaranModel>,
    val meta: PaginationMeta
)

data class MataPelajaranResponse(
    val success: Boolean,
    val message: String,
    val data: MataPelajaranPaginatedData
)
