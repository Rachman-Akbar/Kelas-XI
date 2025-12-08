package com.komputerkit.aplikasimonitoringkelas.data.models

// Request body classes for POST operations

data class TeacherPermissionRequest(
    val guru_id: Int,
    val tanggal_mulai: String,
    val tanggal_selesai: String,
    val jenis_izin: String,
    val keterangan: String,
    val status_approval: String = "pending"
)

data class SubstituteTeacherRequest(
    val jadwal_id: Int,
    val guru_asli_id: Int,
    val guru_pengganti_id: Int,
    val tanggal: String,
    val status_penggantian: String = "pending",
    val keterangan: String? = null,
    val catatan_approval: String? = null
)

// Request body class for updating permission status
data class TeacherPermissionUpdateRequest(
    val status_approval: String,
    val catatan_approval: String? = null,
    val tanggal_approval: String? = null,
    val disetujui_oleh: Int? = null
)

// Request body class for updating substitute teacher status
data class SubstituteTeacherUpdateRequest(
    val status_penggantian: String,
    val catatan_approval: String? = null,
    val disetujui_oleh: Int? = null
)

// Request body class for creating student attendance
data class StudentAttendanceRequest(
    val siswa_id: Int,
    val jadwal_id: Int,
    val tanggal: String,
    val status: String,
    val keterangan: String? = null,
    val diinput_oleh: Int? = null
)

// Request body class for creating teacher attendance
data class TeacherAttendanceRequest(
    val guru_id: Int,
    val jadwal_id: Int,
    val tanggal: String,
    val status_kehadiran: String,
    val waktu_datang: String? = null,
    val durasi_keterlambatan: Int? = null,
    val keterangan: String? = null,
    val diinput_oleh: Int? = null
)
