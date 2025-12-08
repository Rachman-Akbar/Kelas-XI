package com.komputerkit.aplikasimonitoringkelas.data.models

import com.komputerkit.aplikasimonitoringkelas.data.models.User

// Enum options model for dynamic filter options from database
data class EnumOptions(
    val status_kehadiran_guru: List<String>,
    val status_kehadiran_siswa: List<String>,
    val status_penggantian: List<String>,
    val status_approval: List<String>,
    val jenis_izin: List<String>,
    val hari: List<String>
)

data class Attendance(
    val id: Int,
    val siswa_id: Int? = null,
    val guru_id: Int? = null,
    val jadwal_id: Int,
    val tanggal: String,
    val status: String,
    val keterangan: String? = null
)

data class TeacherAttendance(
    val id: Int,
    val jadwal_id: Int,
    val guru_id: Int,
    val tanggal: String,
    val status_kehadiran: String?,
    val waktu_datang: String? = null,
    val durasi_keterlambatan: Int? = null,
    val keterangan: String? = null,
    val guru: Guru? = null,
    val jadwal: Schedule? = null
)

data class StudentAttendance(
    val id: Int,
    val siswa_id: Int,
    val jadwal_id: Int,
    val tanggal: String,
    val status: String,
    val keterangan: String? = null,
    val siswa: Siswa? = null,
    val jadwal: Schedule? = null
)

data class Siswa(
    val id: Int,
    val nama: String,
    val nis: String? = null,
    val kelas_id: Int? = null,
    val kelas: Kelas? = null
)

data class Kelas(
    val id: Int,
    val nama: String
)

// For teacher permission
data class TeacherPermission(
    val id: Int,
    val guru_id: Int,
    val tanggal_mulai: String,
    val tanggal_selesai: String,
    val durasi_hari: Int,
    val jenis_izin: String,
    val keterangan: String,
    val file_surat: String? = null,
    val status_approval: String,
    val disetujui_oleh: Int? = null,
    val tanggal_approval: String? = null,
    val catatan_approval: String? = null,
    val guru: Guru? = null,
    val disetujui_oleh_user: User? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

// For substitute teachers
data class SubstituteTeacher(
    val id: Int,
    val jadwal_id: Int,
    val guru_asli_id: Int,
    val guru_pengganti_id: Int,
    val tanggal: String,
    val status_penggantian: String?,
    val keterangan: String? = null,
    val catatan_approval: String? = null,
    val dibuat_oleh: Int? = null,
    val disetujui_oleh: Int? = null,
    val guru_asli: Guru? = null,
    val guru_pengganti: Guru? = null,
    val jadwal: Schedule? = null,
    val dibuat_oleh_user: User? = null,
    val disetujui_oleh_user: User? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

// For schedule
data class Schedule(
    val id: Int,
    val kelas_id: Int,
    val mata_pelajaran_id: Int,
    val guru_id: Int,
    val hari: String,
    val jam_ke: Int,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruangan: String? = null,
    val status: String? = null,
    val tahun_ajaran: String? = null,
    val guru: Guru? = null,
    val mata_pelajaran: MataPelajaran? = null,
    val kelas: Class? = null
)

// For class
data class Class(
    val id: Int,
    val nama: String,
    val tingkat: Int,
    val jurusan: String,
    val wali_kelas_id: Int? = null,
    val kapasitas: Int,
    val jumlah_siswa: Int,
    val ruangan: String? = null,
    val status: String
)

// Minimal models for Guru and Mata Pelajaran returned by API
data class Guru(
    val id: Int,
    val nama: String,
    val nip: String? = null,
    val status: String? = null
)

data class MataPelajaran(
    val id: Int,
    val nama: String,
    val kode: String? = null,
    val status: String? = null
)