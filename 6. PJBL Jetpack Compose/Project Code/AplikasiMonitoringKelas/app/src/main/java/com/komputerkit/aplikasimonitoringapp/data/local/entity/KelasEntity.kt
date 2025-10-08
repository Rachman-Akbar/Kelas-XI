package com.komputerkit.aplikasimonitoringapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel Kelas di Room Database
 */
@Entity(tableName = "kelas")
data class KelasEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "nama_kelas")
    val namaKelas: String,
    @ColumnInfo(name = "tingkat_kelas")
    val tingkatKelas: String,
    @ColumnInfo(name = "wali_kelas_id")
    val waliKelasId: Int? = null,
    @ColumnInfo(name = "tahun_ajaran")
    val tahunAjaran: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)
