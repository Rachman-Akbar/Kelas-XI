package com.komputerkit.aplikasimonitoringkelas.kepsek

import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.data.models.Schedule as ApiSchedule
import com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacher as ApiSubstituteTeacher
import com.komputerkit.aplikasimonitoringkelas.data.models.TeacherAttendance as ApiTeacherAttendance
import com.komputerkit.aplikasimonitoringkelas.data.models.StudentAttendance as ApiStudentAttendance
import com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission as ApiTeacherPermissionFull


object KepsekMappers {
    fun mapSchedules(apiSchedules: List<ApiSchedule>): List<com.komputerkit.aplikasimonitoringkelas.common.Schedule> {
        return apiSchedules.map { jadwal ->
            // Try to extract nama from relationships if available
            val guruName = jadwal.guru?.nama ?: "Guru ${jadwal.guru_id}"
            val kelasName = jadwal.kelas?.nama ?: "Kelas ${jadwal.kelas_id}"
            val mapelName = jadwal.mata_pelajaran?.nama ?: "Mata Pelajaran ${jadwal.mata_pelajaran_id}"
            val nipGuru = jadwal.guru?.nip ?: "NIP-${jadwal.guru_id}"
            val kodeMapel = jadwal.mata_pelajaran?.kode ?: jadwal.mata_pelajaran_id.toString()
            
            com.komputerkit.aplikasimonitoringkelas.common.Schedule(
                id = jadwal.id,
                guruId = jadwal.guru_id,
                kelasId = jadwal.kelas_id,
                hari = jadwal.hari,
                jam = "${jadwal.jam_mulai} - ${jadwal.jam_selesai}",
                mapel = mapelName,
                guruName = guruName,
                kelasName = kelasName,
                nipGuru = nipGuru,
                kodeMapel = kodeMapel,
                tahunAjaran = jadwal.tahun_ajaran,
                jamKe = jadwal.jam_ke,
                jamMulai = jadwal.jam_mulai,
                jamSelesai = jadwal.jam_selesai,
                ruangan = jadwal.ruangan
            )
        }
    }

    fun mapPermissions(apiPerms: List<ApiTeacherPermissionFull>): List<Permission> {
        return apiPerms.map { perm ->
            Permission(
                id = perm.id,
                guruId = perm.guru_id,
                tanggal = perm.tanggal_mulai,
                alasan = perm.keterangan,
                status = perm.status_approval
            )
        }
    }

    fun mapSubstitutes(apiSubs: List<ApiSubstituteTeacher>): List<com.komputerkit.aplikasimonitoringkelas.common.SubstituteTeacher> {
        return apiSubs.map { sub ->
            // Extract names from relationships if available
            val namaGuruAsli = sub.guru_asli?.nama ?: "Guru Asli ${sub.guru_asli_id}"
            val namaGuruPengganti = sub.guru_pengganti?.nama ?: "Guru Pengganti ${sub.guru_pengganti_id}"
            val kelasName = sub.jadwal?.kelas?.nama ?: "Kelas ${sub.jadwal_id}"
            val approverName = sub.disetujui_oleh_user?.name ?: ""
            val tanggalApproval = sub.updated_at

            com.komputerkit.aplikasimonitoringkelas.common.SubstituteTeacher(
                id = sub.id,
                guruAsliId = sub.guru_asli_id,
                guruPenggantiId = sub.guru_pengganti_id,
                jadwalId = sub.jadwal_id,
                tanggal = sub.tanggal,
                namaGuruAsli = namaGuruAsli,
                namaGuruPengganti = namaGuruPengganti,
                kelas = kelasName,
                statusPenggantian = sub.status_penggantian ?: "pending",
                keterangan = sub.keterangan ?: "",
                catatanApproval = sub.catatan_approval ?: "",
                disetujuiOleh = sub.disetujui_oleh?.toString() ?: "",
                approverName = approverName,
                tanggalApproval = tanggalApproval
            )
        }
    }

    fun mapTeacherAttendances(apiList: List<ApiTeacherAttendance>): List<com.komputerkit.aplikasimonitoringkelas.common.TeacherAttendance> {
        return apiList.map { a ->
            // Extract names from relationships if available
            val guruName = a.guru?.nama ?: "Guru ${a.guru_id}"
            val kelasName = a.jadwal?.kelas?.nama
            val mataPelajaran = a.jadwal?.mata_pelajaran?.nama
            
            com.komputerkit.aplikasimonitoringkelas.common.TeacherAttendance(
                id = a.id,
                jadwalId = a.jadwal_id,
                guruId = a.guru_id,
                tanggal = a.tanggal,
                statusKehadiran = a.status_kehadiran,
                waktuDatang = a.waktu_datang,
                durasiKeterlambatan = a.durasi_keterlambatan,
                keterangan = a.keterangan,
                guruName = guruName,
                kelasName = kelasName,
                mataPelajaran = mataPelajaran
            )
        }
    }

    fun mapStudentAttendances(apiList: List<ApiStudentAttendance>): List<com.komputerkit.aplikasimonitoringkelas.common.StudentAttendance> {
        return apiList.map { a ->
            // Extract names from relationships if available
            val siswaName = a.siswa?.nama
            val kelasName = a.siswa?.kelas?.nama ?: a.jadwal?.kelas?.nama
            val mataPelajaran = a.jadwal?.mata_pelajaran?.nama
            val guruName = a.jadwal?.guru?.nama
            
            com.komputerkit.aplikasimonitoringkelas.common.StudentAttendance(
                id = a.id,
                siswaId = a.siswa_id,
                jadwalId = a.jadwal_id,
                tanggal = a.tanggal,
                status = a.status,
                keterangan = a.keterangan,
                siswa = com.komputerkit.aplikasimonitoringkelas.common.Student(
                    id = a.siswa_id,
                    nama = siswaName,
                    nis = a.siswa?.nis,
                    kelasId = a.siswa?.kelas_id,
                    kelas = a.siswa?.kelas?.let { 
                        com.komputerkit.aplikasimonitoringkelas.common.ClassMinimal(
                            id = it.id,
                            nama = it.nama
                        )
                    }
                ),
                jadwal = null,
                kelasName = kelasName,
                mataPelajaran = mataPelajaran,
                guruName = guruName
            )
        }
    }

    fun mapTeacherPermissionsFull(apiList: List<ApiTeacherPermissionFull>): List<com.komputerkit.aplikasimonitoringkelas.common.TeacherPermission> {
        return apiList.map { p ->
            val approverName = p.disetujui_oleh_user?.name ?: ""
            val tanggalApproval = p.tanggal_approval
            com.komputerkit.aplikasimonitoringkelas.common.TeacherPermission(
                id = p.id,
                guruId = p.guru_id,
                tanggalMulai = p.tanggal_mulai,
                tanggalSelesai = p.tanggal_selesai,
                durasiHari = p.durasi_hari,
                jenisIzin = p.jenis_izin,
                keterangan = p.keterangan,
                statusApproval = p.status_approval,
                disetujuiOleh = p.disetujui_oleh?.toString() ?: "",
                catatanApproval = p.catatan_approval ?: "",
                guruName = p.guru?.nama ?: "Guru ${p.guru_id}",
                approverName = approverName,
                tanggalApproval = tanggalApproval
            )
        }
    }
}
