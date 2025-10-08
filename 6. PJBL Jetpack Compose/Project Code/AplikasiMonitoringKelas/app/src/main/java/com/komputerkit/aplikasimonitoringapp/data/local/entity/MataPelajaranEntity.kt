package com.komputerkit.aplikasimonitoringapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel Mata Pelajaran di Room Database
 */
@Entity(tableName = "mata_pelajaran")
data class MataPelajaranEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "nama_mata_pelajaran")
    val namaMataPelajaran: String,
    val kode: String,
    val deskripsi: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)
