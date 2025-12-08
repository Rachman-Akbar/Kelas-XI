package com.komputerkit.aplikasimonitoringkelas.kurikulum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository
import com.komputerkit.aplikasimonitoringkelas.data.repository.AttendanceRepository
import com.komputerkit.aplikasimonitoringkelas.kepsek.KepsekMappers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class KurikulumViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // ==================== CACHING CONFIGURATION ====================
    private val CACHE_DURATION = 5 * 60 * 1000L // 5 minutes

    // Cached data with timestamps
    private val _cachedSchedules = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Schedule>?>(null)
    private val _lastScheduleLoad = MutableStateFlow<Long>(0)

    private val _cachedTeacherAttendances = MutableStateFlow<List<TeacherAttendance>?>(null)
    private val _lastTeacherAttendanceLoad = MutableStateFlow<Long>(0)

    private val _cachedTeacherPermissions = MutableStateFlow<List<TeacherPermission>?>(null)
    private val _lastTeacherPermissionLoad = MutableStateFlow<Long>(0)

    private val _cachedSubstituteTeachers = MutableStateFlow<List<SubstituteTeacher>?>(null)
    private val _lastSubstituteTeacherLoad = MutableStateFlow<Long>(0)

    // Teacher Permissions State
    private val _teacherPermissions = MutableStateFlow<List<TeacherPermission>>(emptyList())
    val teacherPermissions: StateFlow<List<TeacherPermission>> = _teacherPermissions

    // Teacher Attendance State  
    private val _teacherAttendances = MutableStateFlow<List<TeacherAttendance>>(emptyList())
    val teacherAttendances: StateFlow<List<TeacherAttendance>> = _teacherAttendances

    // Substitute Teachers State
    private val _substituteTeachers = MutableStateFlow<List<SubstituteTeacher>>(emptyList())
    val substituteTeachers: StateFlow<List<SubstituteTeacher>> = _substituteTeachers

    // Reference Data
    private val _guruList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>>(emptyList())
    val guruList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>> = _guruList

    private val _classList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>>(emptyList())
    val classList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>> = _classList

    private val _scheduleList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Schedule>>(emptyList())
    val scheduleList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Schedule>> = _scheduleList

    // Filter State
    private val _tanggalFilter = MutableStateFlow<String?>(null)
    val tanggalFilter: StateFlow<String?> = _tanggalFilter

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter

    private val _kelasIdFilter = MutableStateFlow<Int?>(null)
    val kelasIdFilter: StateFlow<Int?> = _kelasIdFilter

    private val _hariFilter = MutableStateFlow<String?>(null)
    val hariFilter: StateFlow<String?> = _hariFilter

    private val _jenisIzinFilter = MutableStateFlow<String?>(null)
    val jenisIzinFilter: StateFlow<String?> = _jenisIzinFilter

    private val _statusApprovalFilter = MutableStateFlow<String?>(null)
    val statusApprovalFilter: StateFlow<String?> = _statusApprovalFilter

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage

    init {
        // Preload reference data
        viewModelScope.launch {
            loadGurus()
            loadClasses()
        }
    }

    // Load Teacher Permissions
    fun loadTeacherPermissions(tanggal: String? = null, jenisIzin: String? = null, statusApproval: String? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastTeacherPermissionLoad.value) < CACHE_DURATION
            
            if (!forceRefresh && isCacheValid && _cachedTeacherPermissions.value != null) {
                _teacherPermissions.value = _cachedTeacherPermissions.value!!
                return@launch
            }
            
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getTeacherPermissions(
                    token = token,
                    guruId = null,
                    tanggal = tanggal,
                    statusApproval = statusApproval,
                    jenisIzin = jenisIzin,
                    perPage = 100
                )
                if (response.isSuccessful) {
                    val permissionsData = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission>>)?.data ?: emptyList()
                    val mappedPermissions = KepsekMappers.mapTeacherPermissionsFull(permissionsData)
                    _teacherPermissions.value = mappedPermissions
                    _cachedTeacherPermissions.value = mappedPermissions
                    _lastTeacherPermissionLoad.value = now
                } else if (response.code() == 404) {
                    _teacherPermissions.value = emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat izin guru: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load Teacher Attendance
    fun loadTeacherAttendance(tanggal: String? = null, guruId: Int? = null, kelasId: Int? = null, statusKehadiran: String? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastTeacherAttendanceLoad.value) < CACHE_DURATION

            if (!forceRefresh && isCacheValid && _cachedTeacherAttendances.value != null) {
                _teacherAttendances.value = _cachedTeacherAttendances.value!!
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getTeacherAttendance(token, tanggal, guruId, kelasId, statusKehadiran, null)
                if (response.isSuccessful) {
                    val attendanceList = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherAttendance>>)?.data ?: emptyList()
                    val mappedAttendances = KepsekMappers.mapTeacherAttendances(attendanceList)
                    _teacherAttendances.value = mappedAttendances
                    _cachedTeacherAttendances.value = mappedAttendances
                    _lastTeacherAttendanceLoad.value = now
                } else if (response.code() == 404) {
                    _teacherAttendances.value = emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat kehadiran guru: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load Substitute Teachers
    fun loadSubstituteTeachers(tanggal: String? = null, status: String? = null, kelasId: Int? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastSubstituteTeacherLoad.value) < CACHE_DURATION

            if (!forceRefresh && isCacheValid && _cachedSubstituteTeachers.value != null) {
                _substituteTeachers.value = _cachedSubstituteTeachers.value!!
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getSubstituteTeachers(token, tanggal, kelasId, status, null)
                if (response.isSuccessful) {
                    val substituteData = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacher>>)?.data ?: emptyList()
                    val mappedSubstitutes = KepsekMappers.mapSubstitutes(substituteData)
                    _substituteTeachers.value = mappedSubstitutes
                    _cachedSubstituteTeachers.value = mappedSubstitutes
                    _lastSubstituteTeacherLoad.value = now
                } else if (response.code() == 404) {
                    _substituteTeachers.value = emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat guru pengganti: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load Reference Data
    fun loadGurus() {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getGurus(token)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        _guruList.value = apiResult.data ?: emptyList()
                    }
                } else if (response.code() == 404) {
                    _guruList.value = emptyList()
                }
            } catch (e: Exception) {
                // Ignore errors for reference data
            }
        }
    }

    fun loadClasses() {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getClasses(token)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        _classList.value = apiResult.data ?: emptyList()
                    }
                } else if (response.code() == 404) {
                    _classList.value = emptyList()
                }
            } catch (e: Exception) {
                // Ignore errors for reference data
            }
        }
    }

    fun loadSchedules(hari: String? = null, kelasId: Int? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastScheduleLoad.value) < CACHE_DURATION
            
            if (!forceRefresh && isCacheValid && _cachedSchedules.value != null) {
                _scheduleList.value = _cachedSchedules.value!!
                return@launch
            }
            
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getSchedules(token, kelasId, null, hari)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        val schedules = apiResult.data ?: emptyList()
                        _scheduleList.value = schedules
                        _cachedSchedules.value = schedules
                        _lastScheduleLoad.value = now
                    } else {
                        _errorMessage.value = apiResult?.message ?: "Gagal mengambil data jadwal"
                    }
                } else if (response.code() == 404) {
                    _scheduleList.value = emptyList()
                } else {
                    _errorMessage.value = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Kesalahan koneksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Filter Setters
    fun setTanggalFilter(tanggal: String?) { _tanggalFilter.value = tanggal }
    fun setStatusFilter(status: String?) { _statusFilter.value = status }
    fun setKelasIdFilter(kelasId: Int?) { _kelasIdFilter.value = kelasId }
    fun setHariFilter(hari: String?) { _hariFilter.value = hari }
    fun setJenisIzinFilter(jenisIzin: String?) { _jenisIzinFilter.value = jenisIzin }
    fun setStatusApprovalFilter(statusApproval: String?) { _statusApprovalFilter.value = statusApproval }

    // Apply Filters
    fun applyPermissionFilters() {
        loadTeacherPermissions(_tanggalFilter.value, _jenisIzinFilter.value, _statusApprovalFilter.value)
    }

    fun applyAttendanceFilters() {
        loadTeacherAttendance(_tanggalFilter.value, null, _kelasIdFilter.value, _statusFilter.value)
    }

    fun applySchedulesFilters() {
        loadSchedules(_hariFilter.value, _kelasIdFilter.value)
    }

    fun applySubstituteFilters() {
        val tanggal = _tanggalFilter.value
        val status = _statusFilter.value
        val kelasId = _kelasIdFilter.value
        loadSubstituteTeachers(tanggal, status, kelasId)
    }

    // Update Info Message
    fun updateInfo(message: String) {
        _infoMessage.value = message
    }

    // Clear Messages
    fun clearError() { _errorMessage.value = null }
    fun clearInfo() { _infoMessage.value = null }

    // Update Permission Status (Approve/Reject) with reason
    fun updateTeacherPermissionStatus(permissionId: Int, status: String, reason: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val userId = authRepository.getUserId() // Get current user ID
                
                // If reason is not provided, show error
                if (reason.isNullOrBlank()) {
                    _errorMessage.value = "Catatan approval wajib diisi"
                    _isLoading.value = false
                    return@launch
                }
                
                val updateRequest = com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermissionUpdateRequest(
                    status_approval = status, // Will be "dijadwalkan" or "ditolak"
                    catatan_approval = reason,
                    tanggal_approval = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
                    disetujui_oleh = userId // Add user ID who approved
                )

                val response = attendanceRepository.updateTeacherPermission(token, permissionId, updateRequest)

                if (response.isSuccessful) {
                    val updatedPermission = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission>)?.data
                    if (updatedPermission != null) {
                        // Reload the list to get updated data with relationships
                        loadTeacherPermissions(forceRefresh = true)
                        _infoMessage.value = "Status izin guru berhasil diperbarui"
                    } else {
                        _errorMessage.value = "Gagal memperbarui status izin guru: data tidak valid"
                    }
                } else {
                    _errorMessage.value = "Gagal memperbarui status izin guru: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update Substitute Teacher Status with reason
    fun updateSubstituteTeacherStatus(substituteId: Int, status: String, reason: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val userId = authRepository.getUserId() // Get current user ID consistently
                
                // If reason is not provided, show error
                if (reason.isNullOrBlank()) {
                    _errorMessage.value = "Catatan approval wajib diisi"
                    _isLoading.value = false
                    return@launch
                }
                
                val updateRequest = com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacherUpdateRequest(
                    status_penggantian = status,
                    catatan_approval = reason,
                    disetujui_oleh = userId
                )

                val response = attendanceRepository.updateSubstituteTeacher(token, substituteId, updateRequest)

                if (response.isSuccessful) {
                    val updatedSubstitute = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacher>)?.data
                    if (updatedSubstitute != null) {
                        // Reload the list to get updated data with relationships
                        loadSubstituteTeachers(forceRefresh = true)
                        _infoMessage.value = "Status guru pengganti berhasil diperbarui"
                    } else {
                        _errorMessage.value = "Gagal memperbarui status guru pengganti: data tidak valid"
                    }
                } else {
                    _errorMessage.value = "Gagal memperbarui status guru pengganti: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Backward compatibility with old code
    @Deprecated("Use teacherPermissions instead")
    val permissions: StateFlow<List<Permission>> = MutableStateFlow(emptyList())
    
    @Deprecated("Use substituteTeachers instead")
    val substitutes: StateFlow<List<SubstituteTeacher>> = _substituteTeachers
    
    @Deprecated("Use loadTeacherPermissions instead")
    fun loadPermissions() { loadTeacherPermissions() }
    
    @Deprecated("Use loadSubstituteTeachers instead")
    fun loadSubstitutes() { loadSubstituteTeachers() }
    
    @Deprecated("Use setTanggalFilter instead")
    fun setDayFilter(day: String?) { setTanggalFilter(day) }
    
    @Deprecated("Not used")
    val dayFilter: StateFlow<String?> = _tanggalFilter
    
    @Deprecated("Not used")
    val classFilter: StateFlow<String?> = MutableStateFlow(null)
    
    @Deprecated("Not used")
    val attendances: StateFlow<List<Attendance>> = MutableStateFlow(emptyList())
    
    @Deprecated("Not used")
    fun loadAttendances() {}
    
    @Deprecated("Not used")
    fun setClassFilter(kelas: String?) {}
    
    // Methods for creating attendance
    fun createTeacherAttendance(
        guruId: Int,
        jadwalId: Int,
        tanggal: String,
        status: String,
        keterangan: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()

                val request = com.komputerkit.aplikasimonitoringkelas.data.models.TeacherAttendanceRequest(
                    guru_id = guruId,
                    jadwal_id = jadwalId,
                    tanggal = tanggal,
                    status_kehadiran = status,
                    keterangan = keterangan
                )

                val response = attendanceRepository.createTeacherAttendance(token, request)

                if (response.isSuccessful) {
                    _infoMessage.value = "Kehadiran guru berhasil ditambahkan"
                    // Optionally reload the attendance list to show the new entry
                    loadTeacherAttendance(tanggal = tanggal, guruId = guruId)
                } else {
                    val errorBody = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { response.message() }
                    _errorMessage.value = "Gagal menambahkan kehadiran guru: $errorBody"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createStudentAttendance(
        siswaId: Int,
        jadwalId: Int,
        tanggal: String,
        status: String,
        keterangan: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()

                val request = com.komputerkit.aplikasimonitoringkelas.data.models.StudentAttendanceRequest(
                    siswa_id = siswaId,
                    jadwal_id = jadwalId,
                    tanggal = tanggal,
                    status = status,
                    keterangan = keterangan
                )

                val response = attendanceRepository.createStudentAttendance(token, request)

                if (response.isSuccessful) {
                    _infoMessage.value = "Kehadiran siswa berhasil ditambahkan"
                    // Optionally reload the student attendance list to show the new entry
                } else {
                    val errorBody = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { response.message() }
                    _errorMessage.value = "Gagal menambahkan kehadiran siswa: $errorBody"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load students by class to populate dropdown
    fun loadStudentsByClass(kelasId: Int) {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getStudentsByClass(token, kelasId)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        // Update the schedule to include students (if needed in the future)
                    }
                }
            } catch (e: Exception) {
                // Handle error silently since it's reference data
            }
        }
    }

    // Load enum options for status dropdowns
    fun loadEnumOptions() {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getAllEnums(token)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        // Enum options would be handled at the API level
                    }
                }
            } catch (e: Exception) {
                // Handle error silently since it's reference data
            }
        }
    }

    @Deprecated("Not used")
    fun addSubstitute(guruAsliId: Int, guruPenggantiId: Int, kelasId: Int, tanggal: String) {}
}