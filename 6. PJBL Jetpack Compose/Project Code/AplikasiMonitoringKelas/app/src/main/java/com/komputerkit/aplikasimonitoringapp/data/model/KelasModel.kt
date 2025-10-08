package com.komputerkit.aplikasimonitoringapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Kelas Model
 */
data class KelasModel(
    val id: Int,
    val nama: String,
    val tingkat: String,
    val jurusan: String?,
    @SerializedName("wali_kelas")
    val waliKelas: String?,
    @SerializedName("tahun_ajaran")
    val tahunAjaran: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

/**
 * Kelas Response with Pagination
 */
data class KelasPaginatedData(
    val data: List<KelasModel>,
    val meta: PaginationMeta
)

data class KelasResponse(
    val success: Boolean,
    val message: String,
    val data: KelasPaginatedData
)
