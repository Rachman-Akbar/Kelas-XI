package com.komputerkit.aplikasimonitoringapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel Kehadiran di Room Database
 */
@Entity(tableName = "kehadiran")
data class KehadiranEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "siswa_id")
    val siswaId: Int,
    @ColumnInfo(name = "jadwal_id")
    val jadwalId: Int,
    val tanggal: String,
    val status: String, // hadir, izin, sakit, alpha
    val keterangan: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)
