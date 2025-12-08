package com.komputerkit.aplikasimonitoringkelas.repository

import com.komputerkit.aplikasimonitoringkelas.network.models.*
import com.komputerkit.aplikasimonitoringkelas.data.repository.AttendanceRepository
import okhttp3.Dispatcher

// Repository interfaces to maintain compatibility with existing architecture
class JadwalRepository(private val attendanceRepository: AttendanceRepository) {
    constructor(apiService: com.komputerkit.aplikasimonitoringkelas.data.api.ApiService) : this(AttendanceRepository())

    suspend fun filterJadwal(hari: String?, kelasId: Int?): Result<List<JadwalDetail>> {
        return try {
            val token = getTokenFromStorage() // Need to implement token retrieval
            val response = attendanceRepository.getSchedules(token, kelasId, null, hari)
            val schedulesList = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.Schedule>) ?: emptyList()
            val mappedData = schedulesList.map { schedule ->
                JadwalDetail(
                    id = schedule.id,
                    kelasId = schedule.kelas_id,
                    mataPelajaranId = schedule.mata_pelajaran_id,
                    guruId = schedule.guru_id,
                    hari = schedule.hari,
                    jamKe = schedule.jam_ke,
                    jamMulai = schedule.jam_mulai,
                    jamSelesai = schedule.jam_selesai,
                    ruangan = schedule.ruangan,
                    status = schedule.status ?: "",
                    tahunAjaran = "",
                    kodeMapel = "",
                    mataPelajaran = "", // Would need to fetch separately
                    namaGuru = "", // Would need to fetch separately
                    nipGuru = "", // Would need to fetch separately
                    kelas = "" // Would need to fetch separately
                )
            } ?: emptyList()
            Result.success(mappedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getTokenFromStorage(): String {
        // In a real implementation, this would retrieve the token from DataStore or SharedPreferences
        return ""
    }
}

class KehadiranRepository(private val attendanceRepository: AttendanceRepository) {
    constructor(apiService: com.komputerkit.aplikasimonitoringkelas.data.api.ApiService) : this(AttendanceRepository())

    suspend fun getAllKehadiranGuru(): Result<List<KehadiranGuru>> {
        return try {
            val token = getTokenFromStorage()
            val response = attendanceRepository.getTeacherAttendance(token, null, null, null, null, null)
            val attendanceList = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherAttendance>) ?: emptyList()
            val mappedData = attendanceList.map { att ->
                KehadiranGuru(
                    id = att.id,
                    jadwalId = att.jadwal_id,
                    guruId = att.guru_id,
                    tanggal = att.tanggal,
                    statusKehadiran = att.status_kehadiran ?: "-",
                    waktuDatang = att.waktu_datang,
                    durasiKeterlambatan = att.durasi_keterlambatan,
                    keterangan = att.keterangan
                )
            } ?: emptyList()
            Result.success(mappedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun filterKehadiranGuruByDay(tanggal: String?, status: String?): Result<List<KehadiranGuru>> {
        // This implementation would need to filter the results appropriately
        return getAllKehadiranGuru()
    }

    suspend fun getAllKehadiranSiswa(): Result<List<KehadiranSiswa>> {
        return try {
            val token = getTokenFromStorage()
            val response = attendanceRepository.getStudentAttendance(token)
            val attendanceList = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.StudentAttendance>) ?: emptyList()
            val mappedData = attendanceList.map { att ->
                KehadiranSiswa(
                    id = att.id,
                    siswaId = att.siswa_id ?: 0,
                    jadwalId = att.jadwal_id,
                    tanggal = att.tanggal,
                    status = att.status,
                    keterangan = att.keterangan
                )
            } ?: emptyList()
            Result.success(mappedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun filterKehadiranSiswaByDay(tanggal: String?, kelasId: Int?, status: String?): Result<List<KehadiranSiswa>> {
        // This implementation would need to filter the results appropriately
        return getAllKehadiranSiswa()
    }

    private fun getTokenFromStorage(): String {
        // In a real implementation, this would retrieve the token from DataStore or SharedPreferences
        return ""
    }
}

class IzinGuruRepository(private val attendanceRepository: AttendanceRepository) {
    constructor(apiService: com.komputerkit.aplikasimonitoringkelas.data.api.ApiService) : this(AttendanceRepository())

    suspend fun getAllIzinGuru(): Result<List<IzinGuru>> {
        return try {
            val token = getTokenFromStorage()
            val response = attendanceRepository.getTeacherPermissions(token)
            val mappedData = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission>>)?.data?.map { perm ->
                IzinGuru(
                    id = perm.id,
                    guruId = perm.guru_id,
                    tanggalMulai = perm.tanggal_mulai,
                    tanggalSelesai = perm.tanggal_selesai,
                    durasiHari = perm.durasi_hari,
                    jenisIzin = perm.jenis_izin,
                    keterangan = perm.keterangan,
                    statusApproval = perm.status_approval,
                    disetujuiOleh = null, // Would need to fetch from actual API response
                    tanggalApproval = null // Would need to fetch from actual API response
                )
            } ?: emptyList()
            Result.success(mappedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun filterIzinGuruByDay(tanggal: String?, status: String?): Result<List<IzinGuru>> {
        // This implementation would need to filter the results appropriately
        return getAllIzinGuru()
    }

    private fun getTokenFromStorage(): String {
        // In a real implementation, this would retrieve the token from DataStore or SharedPreferences
        return ""
    }
}

class GuruPenggantiRepository(private val attendanceRepository: AttendanceRepository) {
    constructor(apiService: com.komputerkit.aplikasimonitoringkelas.data.api.ApiService) : this(AttendanceRepository())

    suspend fun getAllGuruPengganti(): Result<List<GuruPengganti>> {
        return try {
            val token = getTokenFromStorage()
            val response = attendanceRepository.getSubstituteTeachers(token)
            val substituteList = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacher>) ?: emptyList()
            val mappedData = substituteList.map { sub ->
                GuruPengganti(
                    id = sub.id,
                    jadwalId = sub.jadwal_id,
                    guruAsliId = sub.guru_asli_id,
                    guruPenggantiId = sub.guru_pengganti_id,
                    tanggal = sub.tanggal,
                    statusPenggantian = sub.status_penggantian ?: "",
                    keterangan = sub.keterangan
                )
            }
            Result.success(mappedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun filterGuruPenggantiByDay(tanggal: String?, status: String?): Result<List<GuruPengganti>> {
        // This implementation would need to filter the results appropriately
        return getAllGuruPengganti()
    }

    private fun getTokenFromStorage(): String {
        // In a real implementation, this would retrieve the token from DataStore or SharedPreferences
        return ""
    }
}