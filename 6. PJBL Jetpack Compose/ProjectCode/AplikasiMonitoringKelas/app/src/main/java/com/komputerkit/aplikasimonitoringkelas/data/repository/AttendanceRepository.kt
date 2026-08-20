package com.komputerkit.aplikasimonitoringkelas.data.repository

import com.komputerkit.aplikasimonitoringkelas.data.api.ApiConfig
import com.komputerkit.aplikasimonitoringkelas.data.models.*

class AttendanceRepository {

    private fun authorization(token: String): String {
        require(token.isNotBlank()) { "Token autentikasi tidak tersedia" }
        return "Bearer ${token.trim()}"
    }
    
    suspend fun getTeacherAttendance(token: String, tanggal: String? = null, guruId: Int? = null, kelasId: Int? = null, statusKehadiran: String? = null, perPage: Int? = 100) =
        ApiConfig.apiService.getTeacherAttendance(authorization(token), tanggal, guruId, kelasId, statusKehadiran, perPage)

    suspend fun createTeacherAttendance(token: String, attendance: TeacherAttendanceRequest) =
        ApiConfig.apiService.createTeacherAttendance(authorization(token), attendance)

    suspend fun getStudentAttendance(token: String, tanggal: String? = null, kelasId: Int? = null, perPage: Int? = 100) =
        ApiConfig.apiService.getStudentAttendance(authorization(token), tanggal, kelasId, perPage)

    suspend fun createStudentAttendance(token: String, attendance: StudentAttendanceRequest) =
        ApiConfig.apiService.createStudentAttendance(authorization(token), attendance)

    suspend fun getTeacherPermissions(token: String, guruId: Int? = null, tanggal: String? = null, statusApproval: String? = null, jenisIzin: String? = null, perPage: Int? = 100) =
        ApiConfig.apiService.getTeacherPermissions(authorization(token), guruId, tanggal, statusApproval, jenisIzin, perPage)

    suspend fun updateTeacherPermission(token: String, permissionId: Int, permission: TeacherPermissionUpdateRequest) =
        ApiConfig.apiService.updateTeacherPermission(authorization(token), permissionId, permission)

    suspend fun createTeacherPermission(token: String, permission: TeacherPermissionRequest) =
        ApiConfig.apiService.createTeacherPermission(authorization(token), permission)

    suspend fun getSubstituteTeachers(token: String, tanggal: String? = null, kelasId: Int? = null, statusPenggantian: String? = null, perPage: Int? = 100) =
        ApiConfig.apiService.getSubstituteTeachers(authorization(token), tanggal, kelasId, statusPenggantian, perPage)

    suspend fun getSubstituteTeachersByGuru(token: String, guruId: Int, tanggal: String? = null, perPage: Int? = 100, role: String? = null) =
        ApiConfig.apiService.getSubstituteTeachersByGuru(authorization(token), guruId, tanggal, perPage, role)

    suspend fun updateSubstituteTeacher(token: String, substituteId: Int, substitute: SubstituteTeacherUpdateRequest) =
        ApiConfig.apiService.updateSubstituteTeacher(authorization(token), substituteId, substitute)

    suspend fun createSubstituteTeacher(token: String, substitute: SubstituteTeacherRequest) =
        ApiConfig.apiService.createSubstituteTeacher(authorization(token), substitute)

    suspend fun getSchedules(token: String, kelasId: Int? = null, guruId: Int? = null, hari: String? = null) =
        ApiConfig.apiService.getSchedules(authorization(token), kelasId, guruId, hari)

    suspend fun getClasses(token: String) =
        ApiConfig.apiService.getClasses(authorization(token))

    suspend fun getStudentsByClass(token: String, kelasId: Int) =
        ApiConfig.apiService.getStudentsByClass(authorization(token), kelasId)

    suspend fun getGurus(token: String) =
        ApiConfig.apiService.getGurus(authorization(token))

    suspend fun getMataPelajaran(token: String) =
        ApiConfig.apiService.getMataPelajaran(authorization(token))

    suspend fun getSchedulesByClass(token: String, filters: Map<String, Any>) =
        ApiConfig.apiService.getSchedulesByClass(authorization(token), filters)

    // Enum endpoints
    suspend fun getAllEnums(token: String) =
        ApiConfig.apiService.getAllEnums(authorization(token))

    suspend fun getEnumByType(token: String, type: String) =
        ApiConfig.apiService.getEnumByType(authorization(token), type)

    suspend fun getDistinctValues(token: String, table: String, column: String) =
        ApiConfig.apiService.getDistinctValues(authorization(token), table, column)
}