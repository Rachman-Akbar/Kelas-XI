package com.komputerkit.aplikasimonitoringkelas.data.repository

import com.komputerkit.aplikasimonitoringkelas.data.api.ApiConfig
import com.komputerkit.aplikasimonitoringkelas.data.models.*

class AttendanceRepository {
    
    suspend fun getTeacherAttendance(token: String, tanggal: String? = null, guruId: Int? = null, kelasId: Int? = null, statusKehadiran: String? = null, perPage: Int? = null) =
        ApiConfig.apiService.getTeacherAttendance("Bearer $token", tanggal, guruId, kelasId, statusKehadiran, perPage)

    suspend fun createTeacherAttendance(token: String, attendance: TeacherAttendanceRequest) =
        ApiConfig.apiService.createTeacherAttendance("Bearer $token", attendance)

    suspend fun getStudentAttendance(token: String, tanggal: String? = null, kelasId: Int? = null) =
        ApiConfig.apiService.getStudentAttendance("Bearer $token", tanggal, kelasId)

    suspend fun createStudentAttendance(token: String, attendance: StudentAttendanceRequest) =
        ApiConfig.apiService.createStudentAttendance("Bearer $token", attendance)

    suspend fun getTeacherPermissions(token: String, guruId: Int? = null, tanggal: String? = null, statusApproval: String? = null, jenisIzin: String? = null, perPage: Int? = null) =
        ApiConfig.apiService.getTeacherPermissions("Bearer $token", guruId, tanggal, statusApproval, jenisIzin, perPage)

    suspend fun updateTeacherPermission(token: String, permissionId: Int, permission: TeacherPermissionUpdateRequest) =
        ApiConfig.apiService.updateTeacherPermission("Bearer $token", permissionId, permission)

    suspend fun createTeacherPermission(token: String, permission: TeacherPermissionRequest) =
        ApiConfig.apiService.createTeacherPermission("Bearer $token", permission)

    suspend fun getSubstituteTeachers(token: String, tanggal: String? = null, kelasId: Int? = null, statusPenggantian: String? = null, perPage: Int? = null) =
        ApiConfig.apiService.getSubstituteTeachers("Bearer $token", tanggal, kelasId, statusPenggantian, perPage)

    suspend fun getSubstituteTeachersByGuru(token: String, guruId: Int, tanggal: String? = null, perPage: Int? = null, role: String? = null) =
        ApiConfig.apiService.getSubstituteTeachersByGuru("Bearer $token", guruId, tanggal, perPage, role)

    suspend fun updateSubstituteTeacher(token: String, substituteId: Int, substitute: SubstituteTeacherUpdateRequest) =
        ApiConfig.apiService.updateSubstituteTeacher("Bearer $token", substituteId, substitute)

    suspend fun createSubstituteTeacher(token: String, substitute: SubstituteTeacherRequest) =
        ApiConfig.apiService.createSubstituteTeacher("Bearer $token", substitute)

    suspend fun getSchedules(token: String, kelasId: Int? = null, guruId: Int? = null, hari: String? = null) =
        ApiConfig.apiService.getSchedules("Bearer $token", kelasId, guruId, hari)

    suspend fun getClasses(token: String) =
        ApiConfig.apiService.getClasses("Bearer $token")

    suspend fun getStudentsByClass(token: String, kelasId: Int) =
        ApiConfig.apiService.getStudentsByClass("Bearer $token", kelasId)

    suspend fun getGurus(token: String) =
        ApiConfig.apiService.getGurus("Bearer $token")

    suspend fun getMataPelajaran(token: String) =
        ApiConfig.apiService.getMataPelajaran("Bearer $token")

    suspend fun getSchedulesByClass(token: String, filters: Map<String, Any>) =
        ApiConfig.apiService.getSchedulesByClass("Bearer $token", filters)

    // Enum endpoints
    suspend fun getAllEnums(token: String) =
        ApiConfig.apiService.getAllEnums("Bearer $token")

    suspend fun getEnumByType(token: String, type: String) =
        ApiConfig.apiService.getEnumByType("Bearer $token", type)

    suspend fun getDistinctValues(token: String, table: String, column: String) =
        ApiConfig.apiService.getDistinctValues("Bearer $token", table, column)
}