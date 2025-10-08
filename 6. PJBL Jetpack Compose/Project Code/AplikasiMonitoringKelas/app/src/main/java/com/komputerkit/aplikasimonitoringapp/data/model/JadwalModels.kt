package com.komputerkit.aplikasimonitoringapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Jadwal Model
 */
data class JadwalModel(
    val id: Int,
    @SerializedName("kelas_id")
    val kelasId: Int,
    @SerializedName("mata_pelajaran_id")
    val mataPelajaranId: Int,
    @SerializedName("guru_id")
    val guruId: Int,
    val hari: String,
    @SerializedName("jam_mulai")
    val jamMulai: String,
    @SerializedName("jam_selesai")
    val jamSelesai: String,
    val ruangan: String?,
    val semester: String,
    @SerializedName("tahun_ajaran")
    val tahunAjaran: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    
    // Relationships
    val kelas: KelasModel?,
    @SerializedName("mata_pelajaran")
    val mataPelajaran: MataPelajaranModel?,
    val guru: GuruModel?
)

/**
 * Jadwal Request Model
 */
data class JadwalRequest(
    @SerializedName("kelas_id")
    val kelasId: Int,
    @SerializedName("mata_pelajaran_id")
    val mataPelajaranId: Int,
    @SerializedName("guru_id")
    val guruId: Int,
    val hari: String,
    @SerializedName("jam_mulai")
    val jamMulai: String,
    @SerializedName("jam_selesai")
    val jamSelesai: String,
    val ruangan: String? = null,
    val semester: String,
    @SerializedName("tahun_ajaran")
    val tahunAjaran: String,
    val status: String = "aktif"
)

/**
 * Pagination Meta
 */
data class PaginationMeta(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val total: Int
)

/**
 * Jadwal Response with Pagination
 */
data class JadwalPaginatedData(
    val data: List<JadwalModel>,
    val meta: PaginationMeta
)

data class JadwalResponse(
    val success: Boolean,
    val message: String,
    val data: JadwalPaginatedData
)

/**
 * Single Jadwal Response
 */
data class SingleJadwalResponse(
    val success: Boolean,
    val message: String,
    val data: JadwalModel
)
