package com.komputerkit.aplikasimonitoringkelas.common

// Common models that are used across the app
data class Schedule(
    val id: Int,
    val guruId: Int,
    val kelasId: Int,
    val hari: String,
    val jam: String,
    val mapel: String,
    val guruName: String,
    val kelasName: String,
    val kelas: ClassMinimal? = null,
    val nipGuru: String?,
    val kodeMapel: String?,
    val tahunAjaran: String?,
    val jamKe: Int?,
    val jamMulai: String?,
    val jamSelesai: String?,
    val ruangan: String?
)
// Compatibility alias for Schedule
val Schedule.mataPelajaranName: String get() = this.mapel

data class Permission(
    val id: Int,
    val guruId: Int,
    val tanggal: String,
    val alasan: String,
    val status: String
)

data class SubstituteTeacher(
    val id: Int,
    val guruAsliId: Int,
    val guruPenggantiId: Int,
    val jadwalId: Int? = null,
    val tanggal: String,
    val namaGuruAsli: String,
    val namaGuruPengganti: String,
    val kelas: String,
    val mataPelajaran: String? = null,
    val statusPenggantian: String,
    val keterangan: String,
    val catatanApproval: String? = null,
    val disetujuiOleh: String? = null,
    val approverName: String? = null,
    val tanggalApproval: String? = null
)
// Compatibility aliases used by various screens
val SubstituteTeacher.guruAsliName: String get() = this.namaGuruAsli
val SubstituteTeacher.guruPenggantiName: String get() = this.namaGuruPengganti
val SubstituteTeacher.kelasName: String get() = this.kelas
val SubstituteTeacher.status: String get() = this.statusPenggantian

data class Attendance(
    val id: Int,
    val siswaId: Int? = null,
    val guruId: Int? = null,
    val jadwalId: Int,
    val tanggal: String,
    val status: String,
    val keterangan: String? = null,
    val guruName: String? = null
)

data class TeacherAttendance(
    val id: Int,
    val jadwalId: Int,
    val guruId: Int,
    val tanggal: String,
    val statusKehadiran: String?,
    val waktuDatang: String? = null,
    val durasiKeterlambatan: Int? = null,
    val keterangan: String? = null,
    val guruName: String? = null,
    val kelasName: String? = null,
    val mataPelajaran: String? = null
)

data class StudentAttendance(
    val id: Int,
    val siswaId: Int,
    val jadwalId: Int,
    val tanggal: String,
    val status: String,
    val keterangan: String? = null,
    val siswa: Student? = null,
    val jadwal: Schedule? = null,
    val siswaName: String? = null,
    val kelasName: String? = null,
    val mataPelajaran: String? = null,
    val guruName: String? = null
)

data class TeacherPermission(
    val id: Int,
    val guruId: Int,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val durasiHari: Int,
    val jenisIzin: String,
    val keterangan: String,
    val statusApproval: String,
    val catatanApproval: String? = null,
    val guruName: String? = null,
    val disetujuiOleh: String? = null,
    val approverName: String? = null,
    val tanggalApproval: String? = null
)

data class Student(
    val id: Int,
    val nama: String?,
    val nis: String? = null,
    val kelasId: Int? = null,
    val kelas: ClassMinimal? = null
)

data class ClassMinimal(
    val id: Int,
    val nama: String?
)