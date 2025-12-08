package com.komputerkit.aplikasimonitoringkelas.network.models

// Models that match the API response structures
data class JadwalDetail(
    val id: Int,
    val kelasId: Int,
    val mataPelajaranId: Int,
    val guruId: Int,
    val hari: String,
    val jamKe: Int,
    val jamMulai: String,
    val jamSelesai: String,
    val ruangan: String?,
    val status: String,
    val tahunAjaran: String?,
    val kodeMapel: String?,
    val mataPelajaran: String,
    val namaGuru: String,
    val nipGuru: String,
    val kelas: String
)

data class KehadiranGuru(
    val id: Int,
    val jadwalId: Int,
    val guruId: Int,
    val tanggal: String,
    val statusKehadiran: String,
    val waktuDatang: String?,
    val durasiKeterlambatan: Int?,
    val keterangan: String?
)

data class KehadiranSiswa(
    val id: Int,
    val siswaId: Int,
    val jadwalId: Int,
    val tanggal: String,
    val status: String,
    val keterangan: String?
)

data class IzinGuru(
    val id: Int,
    val guruId: Int,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val durasiHari: Int,
    val jenisIzin: String,
    val keterangan: String,
    val statusApproval: String,
    val disetujuiOleh: Int?,
    val tanggalApproval: String?
)

data class GuruPengganti(
    val id: Int,
    val jadwalId: Int,
    val guruAsliId: Int,
    val guruPenggantiId: Int,
    val tanggal: String,
    val statusPenggantian: String,
    val keterangan: String?
)