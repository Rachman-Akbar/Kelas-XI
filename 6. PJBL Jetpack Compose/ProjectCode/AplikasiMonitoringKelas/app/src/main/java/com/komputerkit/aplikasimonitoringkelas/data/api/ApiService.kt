package com.komputerkit.aplikasimonitoringkelas.data.api

import com.komputerkit.aplikasimonitoringkelas.data.models.*
import com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Any>

    // Get current user info
    @GET("auth/me")
    suspend fun getUserInfo(@Header("Authorization") token: String): Response<LoginResponse>

    // Attendance endpoints
    @GET("kehadiran-guru")
    suspend fun getTeacherAttendance(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("guru_id") guruId: Int? = null,
        @Query("kelas_id") kelasId: Int? = null,
        @Query("status_kehadiran") statusKehadiran: String? = null,
        @Query("per_page") perPage: Int? = null
    ): Response<ApiResult<List<TeacherAttendance>>>

    @POST("kehadiran-guru")
    suspend fun createTeacherAttendance(
        @Header("Authorization") token: String,
        @Body attendance: TeacherAttendanceRequest
    ): Response<ApiResult<TeacherAttendance>>

    @GET("kehadiran")
    suspend fun getStudentAttendance(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas_id") kelasId: Int? = null
    ): Response<ApiResult<List<StudentAttendance>>>

    @POST("kehadiran")
    suspend fun createStudentAttendance(
        @Header("Authorization") token: String,
        @Body attendance: StudentAttendanceRequest
    ): Response<ApiResult<StudentAttendance>>

    // Permission endpoints
    @GET("izin-guru")
    suspend fun getTeacherPermissions(
        @Header("Authorization") token: String,
        @Query("guru_id") guruId: Int? = null,
        @Query("tanggal") tanggal: String? = null,
        @Query("status_approval") statusApproval: String? = null,
        @Query("jenis_izin") jenisIzin: String? = null,
        @Query("per_page") perPage: Int? = null
    ): Response<ApiResult<List<TeacherPermission>>>

    @PUT("izin-guru/{id}")
    suspend fun updateTeacherPermission(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body permission: TeacherPermissionUpdateRequest
    ): Response<ApiResult<TeacherPermission>>

    @POST("izin-guru")
    suspend fun createTeacherPermission(
        @Header("Authorization") token: String,
        @Body permission: TeacherPermissionRequest
    ): Response<ApiResult<TeacherPermission>>

    // Substitute teacher endpoints
    @GET("guru-pengganti")
    suspend fun getSubstituteTeachers(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas_id") kelasId: Int? = null,
        @Query("status_penggantian") statusPenggantian: String? = null,
        @Query("per_page") perPage: Int? = null
    ): Response<ApiResult<List<SubstituteTeacher>>>

    @GET("guru-pengganti/filter-by-guru/{guruId}")
    suspend fun getSubstituteTeachersByGuru(
        @Header("Authorization") token: String,
        @Path("guruId") guruId: Int,
        @Query("tanggal") tanggal: String? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("role") role: String? = null
    ): Response<ApiResult<List<SubstituteTeacher>>>

    @POST("guru-pengganti")
    suspend fun createSubstituteTeacher(
        @Header("Authorization") token: String,
        @Body substitute: SubstituteTeacherRequest
    ): Response<ApiResult<SubstituteTeacher>>

    @PUT("guru-pengganti/{id}")
    suspend fun updateSubstituteTeacher(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body substitute: SubstituteTeacherUpdateRequest
    ): Response<ApiResult<SubstituteTeacher>>

    // Schedule endpoints
    @GET("jadwal")
    suspend fun getSchedules(
        @Header("Authorization") token: String,
        @Query("kelas_id") kelasId: Int? = null,
        @Query("guru_id") guruId: Int? = null,
        @Query("hari") hari: String? = null
    ): Response<ApiResult<List<Schedule>>>

    // Class endpoints
    @GET("kelas")
    suspend fun getClasses(@Header("Authorization") token: String): Response<ApiResult<List<Class>>>

    // Siswa endpoints
    @GET("siswa")
    suspend fun getStudentsByClass(
        @Header("Authorization") token: String,
        @Query("kelas_id") kelasId: Int
    ): Response<ApiResult<List<Siswa>>>

    // Guru endpoints
    @GET("guru")
    suspend fun getGurus(@Header("Authorization") token: String): Response<ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>>>

    // Mata Pelajaran endpoints
    @GET("mata-pelajaran")
    suspend fun getMataPelajaran(@Header("Authorization") token: String): Response<ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.MataPelajaran>>> 

    // Get schedule by class
    @POST("jadwal/by-hari-kelas")
    suspend fun getSchedulesByClass(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Response<ApiResult<List<Schedule>>>

    // Enum endpoints - Get filter options from database
    @GET("enums")
    suspend fun getAllEnums(@Header("Authorization") token: String): Response<ApiResult<EnumOptions>>

    @GET("enums/{type}")
    suspend fun getEnumByType(
        @Header("Authorization") token: String,
        @Path("type") type: String
    ): Response<ApiResult<List<String>>>

    @GET("enums/distinct/{table}/{column}")
    suspend fun getDistinctValues(
        @Header("Authorization") token: String,
        @Path("table") table: String,
        @Path("column") column: String
    ): Response<ApiResult<List<String>>>
}