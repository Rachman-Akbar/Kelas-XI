package com.komputerkit.aplikasimonitoringapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel Guru di Room Database
 */
@Entity(tableName = "guru")
data class GuruEntity(
    @PrimaryKey
    val id: Int,
    val nama: String,
    val nip: String,
    @ColumnInfo(name = "mata_pelajaran_id")
    val mataPelajaranId: Int? = null,
    val alamat: String? = null,
    @ColumnInfo(name = "no_telepon")
    val noTelepon: String? = null,
    val email: String? = null,
    val foto: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)
