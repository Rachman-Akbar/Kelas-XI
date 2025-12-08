package com.komputerkit.aplikasimonitoringkelas.kepsek

import com.komputerkit.aplikasimonitoringkelas.common.*
import org.junit.Assert.*
import org.junit.Test

class KepsekUtilsTest {

    @Test
    fun filterAndSortSchedules_filtersAndSorts() {
        val list = listOf(
            Schedule(id = 1, guruId = 1, kelasId = 1, hari = "Selasa", jam = "", mapel = "", guruName = "", kelasName = "", nipGuru = null, kodeMapel = null, tahunAjaran = null, jamKe = 2, jamMulai = "09:00", jamSelesai = "09:45", ruangan = null),
            Schedule(id = 2, guruId = 2, kelasId = 1, hari = "Senin", jam = "", mapel = "", guruName = "", kelasName = "", nipGuru = null, kodeMapel = null, tahunAjaran = null, jamKe = 1, jamMulai = "07:00", jamSelesai = "07:45", ruangan = null),
            Schedule(id = 3, guruId = 3, kelasId = 2, hari = "Senin", jam = "", mapel = "", guruName = "", kelasName = "", nipGuru = null, kodeMapel = null, tahunAjaran = null, jamKe = 3, jamMulai = "10:00", jamSelesai = "10:45", ruangan = null)
        )

        val filtered = KepsekUtils.filterAndSortSchedules(list, "Senin", null)
        // Should only include id 2 and 3 (Senin) and be sorted by jamKe (1 then 3)
        assertEquals(2, filtered.size)
        assertEquals(2, filtered[0].id)
        assertEquals(3, filtered[1].id)

        val kelasFiltered = KepsekUtils.filterAndSortSchedules(list, null, 1)
        // Should include id 1 and 2 (kelasId == 1) and ordered by day (Senin then Selasa)
        assertEquals(2, kelasFiltered.size)
        assertEquals(2, kelasFiltered[0].id) // Senin jamKe 1
        assertEquals(1, kelasFiltered[1].id) // Selasa jamKe 2
    }

    @Test
    fun filterTeacherAttendances_filtersByDateStatusAndKelas() {
        val jadwalList = listOf(
            Schedule(id = 10, guruId = 1, kelasId = 5, hari = "Senin", jam = "", mapel = "", guruName = "", kelasName = "", nipGuru = null, kodeMapel = null, tahunAjaran = null, jamKe = null, jamMulai = null, jamSelesai = null, ruangan = null)
        )

        val attendances = listOf(
            TeacherAttendance(id = 100, jadwalId = 10, guruId = 1, tanggal = "2025-11-30", statusKehadiran = "Hadir", waktuDatang = null, durasiKeterlambatan = null, keterangan = null, guruName = null),
            TeacherAttendance(id = 101, jadwalId = 10, guruId = 1, tanggal = "2025-11-29", statusKehadiran = "Alpa", waktuDatang = null, durasiKeterlambatan = null, keterangan = null, guruName = null)
        )

        val filteredDate = KepsekUtils.filterTeacherAttendances(attendances, "2025-11-30", null, null, jadwalList)
        assertEquals(1, filteredDate.size)
        assertEquals(100, filteredDate[0].id)

        val filteredStatus = KepsekUtils.filterTeacherAttendances(attendances, null, "Alpa", null, jadwalList)
        assertEquals(1, filteredStatus.size)
        assertEquals(101, filteredStatus[0].id)

        val filteredKelas = KepsekUtils.filterTeacherAttendances(attendances, null, null, 5, jadwalList)
        assertEquals(2, filteredKelas.size)
    }

    @Test
    fun filterPermissions_dateRangeAndStatus() {
        val perms = listOf(
            TeacherPermission(id = 1, guruId = 1, tanggalMulai = "2025-11-28", tanggalSelesai = "2025-11-30", durasiHari = 3, jenisIzin = "Sakit", keterangan = "", statusApproval = "Approved"),
            TeacherPermission(id = 2, guruId = 1, tanggalMulai = "2025-12-01", tanggalSelesai = "2025-12-02", durasiHari = 2, jenisIzin = "Izin", keterangan = "", statusApproval = "Pending")
        )

        val onDate = KepsekUtils.filterPermissions(perms, "2025-11-29", null, null)
        assertEquals(1, onDate.size)
        assertEquals(1, onDate[0].id)

        val byStatus = KepsekUtils.filterPermissions(perms, null, "Pending", null)
        assertEquals(1, byStatus.size)
        assertEquals(2, byStatus[0].id)
    }

    @Test
    fun filterSubstitutes_filtersCorrectly() {
        val jadwalList = listOf(Schedule(id = 20, guruId = 2, kelasId = 8, hari = "Senin", jam = "", mapel = "", guruName = "", kelasName = "", nipGuru = null, kodeMapel = null, tahunAjaran = null, jamKe = null, jamMulai = null, jamSelesai = null, ruangan = null))
        val subs = listOf(
            SubstituteTeacher(id = 1, guruAsliId = 2, guruPenggantiId = 3, jadwalId = 20, tanggal = "2025-11-30", namaGuruAsli = "A", namaGuruPengganti = "B", kelas = "Kelas 8", statusPenggantian = "Approved", keterangan = null),
            SubstituteTeacher(id = 2, guruAsliId = 2, guruPenggantiId = 3, jadwalId = 20, tanggal = "2025-11-29", namaGuruAsli = "A", namaGuruPengganti = "B", kelas = "Kelas 8", statusPenggantian = "Pending", keterangan = null)
        )

        val byDate = KepsekUtils.filterSubstitutes(subs, "2025-11-30", null, null, jadwalList)
        assertEquals(1, byDate.size)
        assertEquals(1, byDate[0].id)

        val byStatus = KepsekUtils.filterSubstitutes(subs, null, "Pending", null, jadwalList)
        assertEquals(1, byStatus.size)
        assertEquals(2, byStatus[0].id)
    }
}
