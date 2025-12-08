package com.komputerkit.aplikasimonitoringkelas.kepsek

import com.komputerkit.aplikasimonitoringkelas.common.*
import java.text.SimpleDateFormat
import java.util.Locale

object KepsekUtils {
    fun <T> filterListByStatus(
        list: List<T>,
        statusFilter: String?,
        getStatus: (T) -> String
    ): List<T> {
        return if (!statusFilter.isNullOrEmpty()) {
            list.filter { getStatus(it) == statusFilter }
        } else {
            list
        }
    }

    fun filterAndSortSchedules(list: List<Schedule>, hari: String?, kelasId: Int?): List<Schedule> {
        val filtered = list.filter { jadwal ->
            val matchesHari = hari.isNullOrEmpty() || hari == "Semua" || jadwal.hari.equals(hari, ignoreCase = true)
            val matchesKelas = kelasId == null || jadwal.kelasId == kelasId
            matchesHari && matchesKelas
        }

        val dayOrder = mapOf(
            "senin" to 0,
            "selasa" to 1,
            "rabu" to 2,
            "kamis" to 3,
            "jumat" to 4,
            "sabtu" to 5,
            "minggu" to 6
        )

        return filtered.sortedWith(compareBy({ s -> dayOrder[s.hari?.lowercase() ?: ""] ?: 99 }, { it.jamKe ?: Int.MAX_VALUE }, { it.jamMulai ?: "" }))
    }

    fun filterTeacherAttendances(list: List<TeacherAttendance>, tanggal: String?, status: String?, kelasId: Int?, jadwalList: List<Schedule>): List<TeacherAttendance> {
        val jadwalMap = jadwalList.associateBy { it.id }
        return list.filter { a ->
            val matchesDate = tanggal.isNullOrEmpty() || a.tanggal == tanggal
            val matchesStatus = status.isNullOrEmpty() || a.statusKehadiran.equals(status, ignoreCase = true)
            val matchesKelas = kelasId == null || jadwalMap[a.jadwalId]?.kelasId == kelasId
            matchesDate && matchesStatus && matchesKelas
        }.sortedByDescending {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.tanggal)?.time ?: 0L
            } catch (e: Exception) { 0L }
        }
    }

    fun filterStudentAttendances(list: List<StudentAttendance>, tanggal: String?, status: String?, kelasId: Int?): List<StudentAttendance> {
        return list.filter { a ->
            val matchesDate = tanggal.isNullOrEmpty() || a.tanggal == tanggal
            val matchesStatus = status.isNullOrEmpty() || a.status.equals(status, ignoreCase = true)
            // Check kelasId from jadwal or siswa.kelasId
            val matchesKelas = if (kelasId == null) {
                true
            } else {
                // Try jadwal.kelasId first, then siswa.kelasId
                (a.jadwal?.kelasId == kelasId) || (a.siswa?.kelasId == kelasId)
            }
            matchesDate && matchesStatus && matchesKelas
        }.sortedByDescending {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.tanggal)?.time ?: 0L
            } catch (e: Exception) { 0L }
        }
    }

    fun filterPermissions(list: List<TeacherPermission>, tanggal: String?, status: String?, jenisIzin: String?): List<TeacherPermission> {
        return list.filter { p ->
            val matchesStatus = status.isNullOrEmpty() || p.statusApproval.equals(status, ignoreCase = true)
            val matchesJenisIzin = jenisIzin.isNullOrEmpty() || p.jenisIzin.equals(jenisIzin, ignoreCase = true)
            val matchesDate = if (tanggal.isNullOrEmpty()) true else try {
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val target = df.parse(tanggal)
                val start = df.parse(p.tanggalMulai)
                val end = df.parse(p.tanggalSelesai)
                if (target != null && start != null && end != null) {
                    !target.before(start) && !target.after(end)
                } else false
            } catch (e: Exception) { p.tanggalMulai == tanggal }
            matchesStatus && matchesJenisIzin && matchesDate
        }.sortedByDescending {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.tanggalMulai)?.time ?: 0L
            } catch (e: Exception) { 0L }
        }
    }

    fun filterSubstitutes(list: List<SubstituteTeacher>, tanggal: String?, status: String?, kelasId: Int?, jadwalList: List<Schedule>): List<SubstituteTeacher> {
        val jadwalMap = jadwalList.associateBy { it.id }
        return list.filter { s ->
            val matchesStatus = status.isNullOrEmpty() || s.statusPenggantian.equals(status, ignoreCase = true)
            val matchesDate = tanggal.isNullOrEmpty() || s.tanggal == tanggal
            val matchesKelas = kelasId == null || (jadwalMap[s.jadwalId]?.kelasId == kelasId)
            matchesStatus && matchesDate && matchesKelas
        }.sortedByDescending {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.tanggal)?.time ?: 0L
            } catch (e: Exception) { 0L }
        }
    }
}
