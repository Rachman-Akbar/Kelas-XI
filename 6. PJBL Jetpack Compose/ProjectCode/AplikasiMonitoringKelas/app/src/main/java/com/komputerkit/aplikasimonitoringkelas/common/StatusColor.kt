package com.komputerkit.aplikasimonitoringkelas.common

import androidx.compose.ui.graphics.Color

object StatusColor {
    // Color palette (hex)
    private val hadir = Color(0xFF2E7D32)      // green
    private val telat = Color(0xFFFFB300)      // amber
    private val tidakHad = Color(0xFFD32F2F)   // red
    private val izin = Color(0xFF1976D2)       // blue
    private val sakit = Color(0xFF6A1B9A)      // purple
    private val pending = Color(0xFF757575)    // gray
    private val disetujui = Color(0xFF2E7D32)  // green
    private val ditolak = Color(0xFFD32F2F)    // red

    fun getStatusColor(status: String?): Color {
        return when (status?.lowercase()) {
            "hadir", "present" -> hadir
            "telat" -> telat
            "tidak_hadir", "not_present", "alpha" -> tidakHad
            "izin" -> izin
            "sakit" -> sakit
            "pending", "menunggu" -> pending
            "disetujui", "approved" -> disetujui
            "ditolak", "rejected" -> ditolak
            // Status untuk Guru Pengganti
            "dijadwalkan" -> pending
            "selesai" -> hadir
            else -> Color.Unspecified
        }
    }

    fun getStatusLabel(status: String?): String {
        return when (status?.lowercase()) {
            "hadir", "present" -> "Hadir"
            "telat" -> "Terlambat"
            "tidak_hadir", "not_present", "alpha" -> "Tidak Hadir"
            "izin" -> "Izin"
            "sakit" -> "Sakit"
            "pending", "menunggu" -> "Menunggu"
            "disetujui", "approved" -> "Disetujui"
            "ditolak", "rejected" -> "Ditolak"
            // Status untuk Guru Pengganti
            "dijadwalkan" -> "Dijadwalkan"
            "selesai" -> "Selesai"
            else -> status?.replace("_", " ")?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "-"
        }
    }
}
