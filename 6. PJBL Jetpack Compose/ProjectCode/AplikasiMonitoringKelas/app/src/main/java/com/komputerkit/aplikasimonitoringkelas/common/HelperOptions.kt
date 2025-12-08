package com.komputerkit.aplikasimonitoringkelas.common

// Helper functions for status options across different modules
fun getKehadiranGuruStatusOptions(): List<String> {
    return listOf("hadir", "tidak_hadir", "izin", "sakit", "telat")
}

fun getKehadiranSiswaStatusOptions(): List<String> {
    return listOf("hadir", "tidak_hadir", "izin", "sakit", "telat")
}

fun getDayOptions(): List<String> {
    return listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
}

fun getJenisIzinOptions(): List<String> {
    return listOf("sakit", "izin", "cuti", "dinas_luar", "lainnya")
}

fun getStatusApprovalOptions(): List<String> {
    return listOf("pending", "disetujui", "ditolak")
}

fun getStatusPenggantiOptions(): List<String> {
    return listOf("pending", "dijadwalkan", "selesai", "tidak_hadir", "ditolak")
}