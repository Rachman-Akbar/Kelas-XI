package com.komputerkit.aplikasimonitoringapp.data.network

import com.komputerkit.aplikasimonitoringapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service Interface
 * Semua endpoint API didefinisikan di sini
 */
interface ApiService {
    
    // ============ Authentication ============
    
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("logout")
    suspend fun logout(): Response<Any>
    
    // ============ Users ============
    
    @GET("users")
    suspend fun getUsers(): Response<UserListResponse>
    
    @POST("users")
    suspend fun createUser(@Body request: UserRequest): Response<Any>
    
    // ============ Jadwal ============
    
    @GET("jadwals")
    suspend fun getJadwals(
        @Query("hari") hari: String? = null,
        @Query("kelas_id") kelasId: Int? = null,
        @Query("per_page") perPage: Int = 10
    ): Response<JadwalResponse>
    
    @GET("jadwals/{id}")
    suspend fun getJadwalById(@Path("id") id: Int): Response<SingleJadwalResponse>
    
    @POST("jadwals")
    suspend fun createJadwal(@Body request: JadwalRequest): Response<SingleJadwalResponse>
    
    @PUT("jadwals/{id}")
    suspend fun updateJadwal(
        @Path("id") id: Int,
        @Body request: JadwalRequest
    ): Response<SingleJadwalResponse>
    
    @DELETE("jadwals/{id}")
    suspend fun deleteJadwal(@Path("id") id: Int): Response<Any>
    
    // ============ Guru ============
    
    @GET("gurus")
    suspend fun getGurus(
        @Query("status") status: String? = null,
        @Query("search") search: String? = null,
        @Query("per_page") perPage: Int = 10
    ): Response<GuruResponse>
    
    @GET("gurus/{id}")
    suspend fun getGuruById(@Path("id") id: Int): Response<Any>
    
    // ============ Kelas ============
    
    @GET("kelas")
    suspend fun getKelas(
        @Query("status") status: String? = null,
        @Query("tingkat") tingkat: String? = null,
        @Query("per_page") perPage: Int = 10
    ): Response<KelasResponse>
    
    @GET("kelas/{id}")
    suspend fun getKelasById(@Path("id") id: Int): Response<Any>
    
    // ============ Mata Pelajaran ============
    
    @GET("mata-pelajarans")
    suspend fun getMataPelajarans(
        @Query("kategori") kategori: String? = null,
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int = 10
    ): Response<MataPelajaranResponse>
    
    @GET("mata-pelajarans/{id}")
    suspend fun getMataPelajaranById(@Path("id") id: Int): Response<Any>
    
    // ============ Siswa ============
    
    @GET("siswas")
    suspend fun getSiswas(
        @Query("kelas_id") kelasId: Int? = null,
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int = 10
    ): Response<Any>
    
    @GET("siswas/{id}")
    suspend fun getSiswaById(@Path("id") id: Int): Response<Any>
}
