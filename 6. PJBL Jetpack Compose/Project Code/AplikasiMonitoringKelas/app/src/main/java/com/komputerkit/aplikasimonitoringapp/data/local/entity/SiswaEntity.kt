package com.komputerkit.aplikasimonitoringapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel Siswa di Room Database
 */
@Entity(tableName = "siswa")
data class SiswaEntity(
    @PrimaryKey
    val id: Int,
    val nama: String,
    val nis: String,
    @ColumnInfo(name = "kelas_id")
    val kelasId: Int,
    val alamat: String? = null,
    @ColumnInfo(name = "tanggal_lahir")
    val tanggalLahir: String? = null,
    @ColumnInfo(name = "jenis_kelamin")
    val jenisKelamin: String? = null,
    val foto: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)
