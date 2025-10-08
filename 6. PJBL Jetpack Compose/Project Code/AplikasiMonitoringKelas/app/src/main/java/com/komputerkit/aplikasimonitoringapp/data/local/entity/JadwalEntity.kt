package com.komputerkit.aplikasimonitoringapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel Jadwal di Room Database
 */
@Entity(tableName = "jadwal")
data class JadwalEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "kelas_id")
    val kelasId: Int,
    @ColumnInfo(name = "mata_pelajaran_id")
    val mataPelajaranId: Int,
    @ColumnInfo(name = "guru_id")
    val guruId: Int,
    val hari: String,
    @ColumnInfo(name = "jam_mulai")
    val jamMulai: String,
    @ColumnInfo(name = "jam_selesai")
    val jamSelesai: String,
    val ruangan: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)
