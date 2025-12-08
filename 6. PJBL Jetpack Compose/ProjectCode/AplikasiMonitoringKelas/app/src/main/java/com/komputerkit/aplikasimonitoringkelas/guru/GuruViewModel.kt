package com.komputerkit.aplikasimonitoringkelas.guru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository
import com.komputerkit.aplikasimonitoringkelas.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.snapshotFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.komputerkit.aplikasimonitoringkelas.kepsek.KepsekMappers
import com.komputerkit.aplikasimonitoringkelas.kepsek.KepsekUtils

// Abstract base used by UI screens so they can depend on a stable API
abstract class GuruViewModel : ViewModel() {
    abstract val schedules: StateFlow<List<Schedule>>
    abstract val permissions: StateFlow<List<Permission>>
    abstract val teacherPermissions: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.common.TeacherPermission>>
    abstract val substitutes: StateFlow<List<SubstituteTeacher>>
    abstract val kehadiranGuruList: StateFlow<List<TeacherAttendance>>
    abstract val guruList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>>
    abstract val mataPelajaranList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.MataPelajaran>>
    abstract val classList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>>
    abstract val kelasFilter: StateFlow<String?>
    abstract val kelasIdFilter: StateFlow<Int?>
    abstract val dayFilter: StateFlow<String?>
    abstract val tanggalFilter: StateFlow<String?>
    abstract val substituteRoleFilter: StateFlow<String?>
    abstract val substituteStatusFilter: StateFlow<String?>
    abstract val jenisIzinFilter: StateFlow<String?>
    abstract val statusApprovalFilter: StateFlow<String?>
    abstract val currentGuruId: StateFlow<Int?>
    abstract val statusKehadiranFilter: StateFlow<String?>
    abstract val kelasIdKehadiranFilter: StateFlow<Int?>
    abstract val infoMessage: StateFlow<String?>
    abstract val isLoading: StateFlow<Boolean>
    abstract val errorMessage: StateFlow<String?>

    abstract fun getCurrentDate(): String
    abstract fun applyJadwalFilter()
    abstract fun applyKehadiranGuruFilter()
    abstract fun applyKehadiranSiswaFilter()
    abstract fun applyPermissionsFilter()
    abstract fun applySubstitutesFilter()
    abstract fun setSubstituteRoleFilter(role: String?)
    abstract fun setKelasIdFilter(kelasId: Int?)
    abstract fun setSubstituteStatusFilter(status: String?)
    abstract fun setJenisIzinFilter(jenisIzin: String?)
    abstract fun setStatusApprovalFilter(status: String?)
    abstract fun setStatusKehadiranFilter(status: String?)
    abstract fun setKelasIdKehadiranFilter(kelasId: Int?)
    abstract fun clearAllFilters()

    abstract fun loadSchedules(guruId: Int? = null, kelasId: Int? = null, hari: String? = null)
    abstract fun loadPermissions(guruId: Int? = null)
    abstract fun loadSubstitutes(guruId: Int? = null, tanggal: String? = null, role: String? = null)
    abstract fun loadKehadiranGuru(tanggal: String? = null)
    abstract fun loadKehadiranSiswa(tanggal: String? = null, kelasId: Int? = null)
    abstract fun loadClasses()
    abstract fun loadGurus()
    abstract fun loadMataPelajaran()
    abstract fun setKelasFilter(kelas: String?)
    abstract fun setDayFilter(day: String?)
    abstract fun setTanggalFilter(tanggal: String?)
    abstract fun clearError()
    abstract fun clearInfo()
    abstract fun requestPermission(alasan: String, tanggalMulai: String, tanggalSelesai: String, jenisIzin: String = "izin")
    abstract fun requestSubstituteTeacher(guruPenggantiId: Int, tanggal: String, alasan: String)
    abstract fun requestSubstituteTeacherWithSchedule(schedule: com.komputerkit.aplikasimonitoringkelas.common.Schedule, guruPenggantiId: Int, tanggal: String, reason: String)
    abstract fun setErrorMessage(message: String?)
}

class RealGuruViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : GuruViewModel() {

    // ==================== CACHING CONFIGURATION ====================
    private val CACHE_DURATION = 5 * 60 * 1000L // 5 minutes

    // Cached data with timestamps
    private val _cachedSchedules = MutableStateFlow<List<Schedule>?>(null)
    private val _lastScheduleLoad = MutableStateFlow<Long>(0)

    private val _cachedPermissions = MutableStateFlow<List<Permission>?>(null)
    private val _lastPermissionLoad = MutableStateFlow<Long>(0)

    private val _cachedSubstitutes = MutableStateFlow<List<SubstituteTeacher>?>(null)
    private val _lastSubstituteLoad = MutableStateFlow<Long>(0)

    private val _cachedKehadiranGuru = MutableStateFlow<List<TeacherAttendance>?>(null)
    private val _lastKehadiranGuruLoad = MutableStateFlow<Long>(0)

    // Jadwal State
    private val _jadwalList = MutableStateFlow<List<Schedule>>(emptyList())
    // Public alias expected by UI
    override val schedules: StateFlow<List<Schedule>> = _jadwalList

    // Permissions State
    private val _permissions = MutableStateFlow<List<Permission>>(emptyList())
    override val permissions: StateFlow<List<Permission>> = _permissions

    // Full teacher permission objects
    private val _teacherPermissions = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.common.TeacherPermission>>(emptyList())
    override val teacherPermissions: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.common.TeacherPermission>> = _teacherPermissions

    // Substitute Teacher State
    private val _substitutes = MutableStateFlow<List<SubstituteTeacher>>(emptyList())
    override val substitutes: StateFlow<List<SubstituteTeacher>> = _substitutes

    // Kehadiran Guru State
    private val _kehadiranGuruList = MutableStateFlow<List<TeacherAttendance>>(emptyList())
    override val kehadiranGuruList: StateFlow<List<TeacherAttendance>> = _kehadiranGuruList

    // Kehadiran Siswa State
    private val _kehadiranSiswaList = MutableStateFlow<List<StudentAttendance>>(emptyList())
    val kehadiranSiswaList: StateFlow<List<StudentAttendance>> = _kehadiranSiswaList

    // Kelas State
    private val _classList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>>(emptyList())
    override val classList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>> = _classList

    // Guru reference list
    private val _guruList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>>(emptyList())
    override val guruList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>> = _guruList

    // Mata pelajaran reference
    private val _mataPelajaranList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.MataPelajaran>>(emptyList())
    override val mataPelajaranList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.MataPelajaran>> get() = _mataPelajaranList

    // Filter State
    private val _kelasFilter = MutableStateFlow<String?>(null)
    override val kelasFilter: StateFlow<String?> = _kelasFilter

    private val _kelasIdFilter = MutableStateFlow<Int?>(null)
    override val kelasIdFilter: StateFlow<Int?> = _kelasIdFilter

    private val _dayFilter = MutableStateFlow<String?>(null)
    override val dayFilter: StateFlow<String?> = _dayFilter

    private val _tanggalFilter = MutableStateFlow<String?>(null)
    override val tanggalFilter: StateFlow<String?> = _tanggalFilter

    private val _substituteRoleFilter = MutableStateFlow<String?>(null)
    override val substituteRoleFilter: StateFlow<String?> = _substituteRoleFilter

    private val _substituteStatusFilter = MutableStateFlow<String?>(null)
    override val substituteStatusFilter: StateFlow<String?> = _substituteStatusFilter

    private val _jenisIzinFilter = MutableStateFlow<String?>(null)
    override val jenisIzinFilter: StateFlow<String?> = _jenisIzinFilter

    private val _statusApprovalFilter = MutableStateFlow<String?>(null)
    override val statusApprovalFilter: StateFlow<String?> = _statusApprovalFilter

    private val _statusKehadiranFilter = MutableStateFlow<String?>(null)
    override val statusKehadiranFilter: StateFlow<String?> = _statusKehadiranFilter

    private val _kelasIdKehadiranFilter = MutableStateFlow<Int?>(null)
    override val kelasIdKehadiranFilter: StateFlow<Int?> = _kelasIdKehadiranFilter

    // UI State
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage
    private val _infoMessage = MutableStateFlow<String?>(null)
    override val infoMessage: StateFlow<String?> = _infoMessage

    override fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun loadSchedules(guruId: Int?, kelasId: Int?, hari: String?) {
        loadSchedules(guruId, kelasId, hari, forceRefresh = false)
    }

    fun loadSchedules(guruId: Int?, kelasId: Int?, hari: String?, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastScheduleLoad.value) < CACHE_DURATION

            if (!forceRefresh && isCacheValid && _cachedSchedules.value != null) {
                _jadwalList.value = _cachedSchedules.value!!
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            android.util.Log.d("GuruViewModel", "loadSchedules called - guruId=$guruId, kelasId=$kelasId, hari=$hari, forceRefresh=$forceRefresh")
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getSchedules(token, kelasId, guruId, hari)
                if (response.isSuccessful) {
                    android.util.Log.d("GuruViewModel", "getSchedules success: ${response.code()}")
                    val jadwalData = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.Schedule>>)?.data ?: emptyList()
                    val mappedSchedules = KepsekMappers.mapSchedules(jadwalData)
                    _jadwalList.value = mappedSchedules
                    _cachedSchedules.value = mappedSchedules
                    _lastScheduleLoad.value = now
                } else if (response.code() == 404) {
                    _jadwalList.value = emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat jadwal: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun loadPermissions(guruId: Int?) {
        loadPermissions(guruId, forceRefresh = false)
    }

    fun loadPermissions(guruId: Int?, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastPermissionLoad.value) < CACHE_DURATION

            if (!forceRefresh && isCacheValid && _cachedPermissions.value != null) {
                _permissions.value = _cachedPermissions.value!!
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val jenisIzin = _jenisIzinFilter.value
                val statusApproval = _statusApprovalFilter.value
                val response = attendanceRepository.getTeacherPermissions(token, guruId, null, statusApproval, jenisIzin, 100)
                if (response.isSuccessful) {
                    val permissionsData = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission>>)?.data ?: emptyList()
                    val mappedPermissions = KepsekMappers.mapPermissions(permissionsData)
                    _permissions.value = mappedPermissions
                    _cachedPermissions.value = mappedPermissions
                    _lastPermissionLoad.value = now
                    // also keep the full TeacherPermission objects for richer UI
                    _teacherPermissions.value = KepsekMappers.mapTeacherPermissionsFull(permissionsData)
                } else if (response.code() == 404) {
                    _permissions.value = emptyList()
                    _teacherPermissions.value = emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat izin: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun loadSubstitutes(guruId: Int?, tanggal: String?, role: String?) {
        loadSubstitutes(guruId, tanggal, role, forceRefresh = false)
    }

    fun loadSubstitutes(guruId: Int?, tanggal: String?, role: String?, status: String? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastSubstituteLoad.value) < CACHE_DURATION

            if (!forceRefresh && isCacheValid && _cachedSubstitutes.value != null) {
                _substitutes.value = _cachedSubstitutes.value!!
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()

                // Map role from UI format to API format
                val roleVal = when (role ?: _substituteRoleFilter.value) {
                    "Guru Asli" -> "asli"
                    "Guru Pengganti" -> "pengganti"
                    "Semua" -> "both"
                    null -> "both"
                    else -> role
                }

                println("DEBUG loadSubstitutes: guruId=$guruId, tanggal=$tanggal, role=$roleVal, status=$status")

                val response = if (guruId != null) {
                    attendanceRepository.getSubstituteTeachersByGuru(token, guruId, tanggal, null, roleVal)
                } else {
                    // If no guruId provided, fall back to general endpoint
                    // For status filtering, we'll use the general endpoint
                    attendanceRepository.getSubstituteTeachers(token, tanggal, null, status, null)
                }

                if (response.isSuccessful) {
                    val substituteData = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacher>>)?.data ?: emptyList()
                    println("DEBUG: Received ${substituteData.size} substitutes from API")

                    val mapped = KepsekMappers.mapSubstitutes(substituteData)
                    println("DEBUG: Mapped to ${mapped.size} substitute objects")

                    // Show all substitutes - filtering will be done in UI layer
                    _substitutes.value = mapped
                    _cachedSubstitutes.value = mapped
                    _lastSubstituteLoad.value = now
                    println("DEBUG: Loaded ${mapped.size} substitutes")

                    // try to enrich substitute teacher info by ensuring guru list is loaded
                    if (_guruList.value.isEmpty()) {
                        loadGurus()
                    }
                } else if (response.code() == 404) {
                    println("DEBUG: 404 - No substitutes found")
                    _substitutes.value = emptyList()
                } else {
                    println("DEBUG: Error ${response.code()} - ${response.message()}")
                    _errorMessage.value = "Gagal memuat guru pengganti: ${response.message()}"
                }
            } catch (e: Exception) {
                println("DEBUG: Exception - ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun loadKehadiranGuru(tanggal: String?) {
        // Use the current user's guru ID and applied filters
        // Ensure we have a valid guru ID before making the API call
        viewModelScope.launch {
            var effectiveGuruId = _currentGuruId.value
            if (effectiveGuruId == null) {
                // Try to load from auth repository if not set in ViewModel
                effectiveGuruId = authRepository.getGuruId()
                if (effectiveGuruId != null) {
                    _currentGuruId.value = effectiveGuruId
                }
            }

            // If still null, try to get from network
            if (effectiveGuruId == null) {
                try {
                    val userInfoResult = authRepository.getUserInfo()
                    if (userInfoResult.isSuccess) {
                        val loginResp = userInfoResult.getOrNull()
                        val user = loginResp?.data?.user
                        if (user?.guru_id != null) {
                            effectiveGuruId = user.guru_id
                            _currentGuruId.value = user.guru_id
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GuruViewModel", "Failed to load user info: ${e.message}")
                }
            }

            // Only proceed if we have a valid guru ID
            if (effectiveGuruId != null) {
                loadKehadiranGuruInternal(tanggal, effectiveGuruId, _kelasIdKehadiranFilter.value, _statusKehadiranFilter.value, forceRefresh = false)
            } else {
                android.util.Log.e("GuruViewModel", "Cannot load teacher attendance - guru ID not available")
                _errorMessage.value = "Tidak dapat memuat data kehadiran: ID guru tidak ditemukan"
            }
        }
    }

    private fun loadKehadiranGuruInternal(tanggal: String?, guruId: Int? = null, kelasId: Int? = null, statusKehadiran: String? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastKehadiranGuruLoad.value) < CACHE_DURATION

            if (!forceRefresh && isCacheValid && _cachedKehadiranGuru.value != null) {
                _kehadiranGuruList.value = _cachedKehadiranGuru.value!!
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            android.util.Log.d("GuruViewModel", "loadKehadiranGuruInternal called - tanggal=$tanggal, guruId=$guruId, kelasId=$kelasId, status=$statusKehadiran, forceRefresh=$forceRefresh")
            try {
                val token = authRepository.getToken()
                // Filter by current user's guru_id for teacher role, or use provided guruId
                var effectiveGuruId: Int? = guruId ?: _currentGuruId.value
                android.util.Log.d("GuruViewModel", "Initial effective guruId: $effectiveGuruId")

                if (effectiveGuruId == null) {
                    try {
                        // Try DataStore fallback
                        val storedGuruId = authRepository.getGuruId()
                        if (storedGuruId != null) {
                            effectiveGuruId = storedGuruId
                            _currentGuruId.value = storedGuruId
                            android.util.Log.d("GuruViewModel", "Fallback: found stored guruId=$storedGuruId")
                        } else {
                            // As last resort try network user info
                            val userInfoResult = authRepository.getUserInfo()
                            if (userInfoResult.isSuccess) {
                                val loginResp = userInfoResult.getOrNull()
                                val user = loginResp?.data?.user
                                if (user?.guru_id != null) {
                                    effectiveGuruId = user.guru_id
                                    _currentGuruId.value = user.guru_id
                                    android.util.Log.d("GuruViewModel", "Fallback: userInfo returned guruId=${user.guru_id}")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("GuruViewModel", "Unable to determine guruId from fallback: ${e.message}")
                    }
                }

                // For teacher attendance screen, we must have a valid guruId to filter correctly
                if (effectiveGuruId == null) {
                    android.util.Log.e("GuruViewModel", "Cannot load teacher attendance: guruId is null. The user may not be properly authenticated as a teacher.")
                    _errorMessage.value = "Tidak dapat memuat data kehadiran guru: ID guru tidak valid"
                    _kehadiranGuruList.value = emptyList() // Show empty state
                    return@launch
                }

                android.util.Log.d("GuruViewModel", "Calling getTeacherAttendance with: tanggal=$tanggal, guruId=$effectiveGuruId, kelasId=$kelasId, status=$statusKehadiran")
                val response = attendanceRepository.getTeacherAttendance(token, tanggal, effectiveGuruId, kelasId, statusKehadiran, null)

                android.util.Log.d("GuruViewModel", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val rawBody = response.body()
                    android.util.Log.d("GuruViewModel", "Raw response body type: ${rawBody?.javaClass?.simpleName}")

                    val apiResult = rawBody as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherAttendance>>
                    android.util.Log.d("GuruViewModel", "ApiResult success: ${apiResult?.success}, message: ${apiResult?.message}")

                    val attendanceList = apiResult?.data ?: emptyList()
                    android.util.Log.d("GuruViewModel", "Attendance list from API size: ${attendanceList.size}")

                    if (attendanceList.isNotEmpty()) {
                        android.util.Log.d("GuruViewModel", "First attendance: guruId=${attendanceList[0].guru_id}, jadwalId=${attendanceList[0].jadwal_id}, tanggal=${attendanceList[0].tanggal}, status=${attendanceList[0].status_kehadiran}")
                        android.util.Log.d("GuruViewModel", "First attendance guru name: ${attendanceList[0].guru?.nama}")
                        android.util.Log.d("GuruViewModel", "First attendance jadwal: ${attendanceList[0].jadwal?.mata_pelajaran?.nama}")
                    }

                    val mappedAttendance = KepsekMappers.mapTeacherAttendances(attendanceList)
                    android.util.Log.d("GuruViewModel", "Mapped attendance size: ${mappedAttendance.size}")

                    if (mappedAttendance.isNotEmpty()) {
                        android.util.Log.d("GuruViewModel", "First mapped: guruName=${mappedAttendance[0].guruName}, kelas=${mappedAttendance[0].kelasName}, status=${mappedAttendance[0].statusKehadiran}")
                    }

                    _kehadiranGuruList.value = mappedAttendance
                    _cachedKehadiranGuru.value = mappedAttendance
                    _lastKehadiranGuruLoad.value = now
                } else if (response.code() == 404) {
                    android.util.Log.d("GuruViewModel", "getTeacherAttendance returned 404 - setting empty list")
                    _kehadiranGuruList.value = emptyList()
                } else {
                    val errorBody = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    android.util.Log.e("GuruViewModel", "getTeacherAttendance error: ${response.code()} - ${response.message()}")
                    android.util.Log.e("GuruViewModel", "Error body: $errorBody")
                    _errorMessage.value = "Gagal memuat kehadiran guru: ${response.message()}"
                }
            } catch (e: Exception) {
                android.util.Log.e("GuruViewModel", "Exception loading kehadiran guru: ${e.message}", e)
                e.printStackTrace()
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun loadKehadiranSiswa(tanggal: String?, kelasId: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getStudentAttendance(token, tanggal, kelasId)
                if (response.isSuccessful) {
                    val attendanceList = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.StudentAttendance>>)?.data ?: emptyList()
                    _kehadiranSiswaList.value = KepsekMappers.mapStudentAttendances(attendanceList)
                } else if (response.code() == 404) {
                    _kehadiranSiswaList.value = emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat kehadiran siswa: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun loadClasses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getClasses(token)
                if (response.isSuccessful) {
                    val classList = (response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>>)?.data ?: emptyList()
                    _classList.value = classList
                    if (_guruList.value.isEmpty()) {
                        loadGurus()
                    }
                } else if (response.code() == 404) {
                    _classList.value = emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat kelas: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun setKelasFilter(kelas: String?) {
        _kelasFilter.value = kelas
        // Also update kelasIdFilter if needed
        if (!kelas.isNullOrEmpty() && kelas != "Semua") {
            val kelasId = _classList.value.find { it.nama == kelas }?.id
            _kelasIdFilter.value = kelasId
        } else {
            _kelasIdFilter.value = null
        }
    }
    override fun setKelasIdFilter(kelasId: Int?) { _kelasIdFilter.value = kelasId }
    override fun setDayFilter(day: String?) { _dayFilter.value = day }
    override fun setTanggalFilter(tanggal: String?) { _tanggalFilter.value = tanggal }
    override fun setSubstituteRoleFilter(role: String?) { _substituteRoleFilter.value = role }
    override fun setSubstituteStatusFilter(status: String?) { _substituteStatusFilter.value = status }
    override fun setJenisIzinFilter(jenisIzin: String?) { _jenisIzinFilter.value = jenisIzin }
    override fun setStatusApprovalFilter(status: String?) { _statusApprovalFilter.value = status }
    override fun setStatusKehadiranFilter(status: String?) { _statusKehadiranFilter.value = status }
    override fun setKelasIdKehadiranFilter(kelasId: Int?) { _kelasIdKehadiranFilter.value = kelasId }
    override fun clearError() { _errorMessage.value = null }
    override fun clearInfo() { _infoMessage.value = null }

    init { _tanggalFilter.value = getCurrentDate() }

    // Current user context (guru_id / kelas_id) loaded from AuthRepository
    private val _currentGuruId = MutableStateFlow<Int?>(null)
    override val currentGuruId: StateFlow<Int?> = _currentGuruId
    private var currentKelasId: Int? = null

    init {
        viewModelScope.launch {
            try {
                val userInfoResult = authRepository.getUserInfo()
                if (userInfoResult.isSuccess) {
                    val loginResp = userInfoResult.getOrNull()
                    val user = loginResp?.data?.user
                    _currentGuruId.value = user?.guru_id
                    currentKelasId = user?.kelas_id
                }
            } catch (e: Exception) {
                // ignore errors retrieving user info; fallbacks will be used
            }
            android.util.Log.d("GuruViewModel", "init - currentGuruId after network call: ${_currentGuruId.value}")
            // If network userInfo wasn't available, fall back to DataStore-stored values
            if (_currentGuruId.value == null) {
                try {
                    val storedGuruId = authRepository.getGuruId()
                    val storedKelasId = authRepository.getKelasId()
                    if (storedGuruId != null) {
                        _currentGuruId.value = storedGuruId
                    }
                    if (storedKelasId != null) {
                        currentKelasId = storedKelasId
                    }
                } catch (e: Exception) {
                    // ignore fallback errors
                }
            }

            android.util.Log.d("GuruViewModel", "init - currentGuruId after fallback: ${_currentGuruId.value}")

            // Preload reference data for smoother UX
            loadClasses()
            loadGurus()
            loadMataPelajaran()

            // Immediately apply filters once initial reference data and current user id are available
            // Use forceRefresh via the apply* methods so the UI gets fresh API data
            applyJadwalFilter()
            applyKehadiranGuruFilter()
            applyPermissionsFilter()
            applySubstitutesFilter()

            // Start reactive collectors so when UI updates filters, ViewModel reapplies them
            // Collect directly from StateFlows for reliability in ViewModel
            launch {
                _kelasFilter.collect {
                    applyJadwalFilter()
                }
            }

            launch {
                _dayFilter.collect {
                    applyJadwalFilter()
                }
            }

            launch {
                _tanggalFilter.collect {
                    applyKehadiranGuruFilter()
                }
            }

            launch {
                _statusKehadiranFilter.collect {
                    applyKehadiranGuruFilter()
                }
            }

            launch {
                _kelasIdKehadiranFilter.collect {
                    applyKehadiranGuruFilter()
                }
            }

            launch {
                _jenisIzinFilter.collect {
                    applyPermissionsFilter()
                }
            }

            launch {
                _statusApprovalFilter.collect {
                    applyPermissionsFilter()
                }
            }

            launch {
                _substituteRoleFilter.collect {
                    applySubstitutesFilter()
                }
            }

            launch {
                _substituteStatusFilter.collect {
                    applySubstitutesFilter()
                }
            }

            // Also collect currentGuruId changes to reload attendance when it's available
            launch {
                _currentGuruId.collect { guruId ->
                    if (guruId != null) {
                        applyKehadiranGuruFilter()
                    }
                }
            }
        }
    }

    override fun applyJadwalFilter() {
        viewModelScope.launch {
            var guruId = _currentGuruId.value
            if (guruId == null) {
                try {
                    val stored = authRepository.getGuruId()
                    if (stored != null) {
                        _currentGuruId.value = stored
                        guruId = stored
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }
        // Use kelasIdFilter and convert kelas name to ID if needed
        val kelasId = _kelasIdFilter.value
        val kelasName = _kelasFilter.value
        val day = _dayFilter.value

        // If kelas filter is set by name, convert to kelasId
        val finalKelasId = if (kelasId != null) {
            kelasId
        } else if (!kelasName.isNullOrEmpty() && kelasName != "Semua") {
            _classList.value.find { it.nama == kelasName }?.id
        } else {
            null
        }

            // Force refresh to avoid stale cache issues when filters change
            loadSchedules(guruId, finalKelasId, day.takeIf { it != "Semua" }, forceRefresh = true)
        }
    }

    override fun applyKehadiranGuruFilter() {
        viewModelScope.launch {
            var guruId = _currentGuruId.value
            if (guruId == null) {
                try {
                    // Try to get from auth repository
                    guruId = authRepository.getGuruId()
                    if (guruId != null) {
                        _currentGuruId.value = guruId
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GuruViewModel", "Error getting guru ID from auth repo: ${e.message}")
                }
            }

            // If still null, try to get from network user info
            if (guruId == null) {
                try {
                    val userInfoResult = authRepository.getUserInfo()
                    if (userInfoResult.isSuccess) {
                        val loginResp = userInfoResult.getOrNull()
                        val user = loginResp?.data?.user
                        if (user?.guru_id != null) {
                            guruId = user.guru_id
                            _currentGuruId.value = user.guru_id
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GuruViewModel", "Error getting user info from network: ${e.message}")
                }
            }

            // Force refresh to ensure latest data is fetched when filters change
            loadKehadiranGuruInternal(_tanggalFilter.value, guruId, _kelasIdKehadiranFilter.value, _statusKehadiranFilter.value, forceRefresh = true)
        }
    }

    override fun applyKehadiranSiswaFilter() {
        val kelasId = if (_kelasFilter.value != null) currentKelasId ?: 1 else null
        loadKehadiranSiswa(_tanggalFilter.value, kelasId)
    }

    override fun applyPermissionsFilter() {
        viewModelScope.launch {
            var guruId = _currentGuruId.value
            if (guruId == null) {
                try {
                    val stored = authRepository.getGuruId()
                    if (stored != null) {
                        _currentGuruId.value = stored
                        guruId = stored
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }
            // Use existing loader with forceRefresh to avoid stale results
            loadPermissions(guruId ?: 1, forceRefresh = true)
        }
    }

    override fun applySubstitutesFilter() {
        viewModelScope.launch {
            var guruAsliId = _currentGuruId.value
            if (guruAsliId == null) {
                try {
                    val stored = authRepository.getGuruId()
                    if (stored != null) {
                        _currentGuruId.value = stored
                        guruAsliId = stored
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }
            val status = _substituteStatusFilter.value
        // Don't pass tanggal filter - load all dates
        // Pass the current role filter value
        // Force refresh to ensure up-to-date substitutes when filters change
            // Force refresh to ensure up-to-date substitutes when filters change
            loadSubstitutes(guruAsliId, null, _substituteRoleFilter.value, status = status, forceRefresh = true)
        }
    }

    override fun clearAllFilters() { _kelasFilter.value = null; _dayFilter.value = null; _tanggalFilter.value = getCurrentDate() }

    // Load guru reference data
    override fun loadGurus() {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getGurus(token)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        _guruList.value = apiResult.data ?: emptyList()
                    } else {
                        _guruList.value = emptyList()
                    }
                } else if (response.code() == 404) {
                    _guruList.value = emptyList()
                }
            } catch (e: Exception) {
                // ignore errors for reference data
            }
        }
    }

    override fun loadMataPelajaran() {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getMataPelajaran(token)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        _mataPelajaranList.value = apiResult.data ?: emptyList()
                    } else {
                        _mataPelajaranList.value = emptyList()
                    }
                } else if (response.code() == 404) {
                    _mataPelajaranList.value = emptyList()
                }
            } catch (e: Exception) {
                // ignore errors for reference data
            }
        }
    }

    override fun requestPermission(alasan: String, tanggalMulai: String, tanggalSelesai: String, jenisIzin: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()

                // Convert jenis_izin to lowercase with underscore format as required by backend
                val jenisIzinFormatted = when (jenisIzin.lowercase()) {
                    "sakit" -> "sakit"
                    "izin" -> "izin"
                    "cuti" -> "cuti"
                    "dinas luar" -> "dinas_luar"
                    "lainnya" -> "lainnya"
                    else -> jenisIzin.lowercase().replace(" ", "_")
                }

                val permissionRequest = com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermissionRequest(
                    guru_id = _currentGuruId.value ?: 1,
                    tanggal_mulai = tanggalMulai,
                    tanggal_selesai = tanggalSelesai,
                    jenis_izin = jenisIzinFormatted,
                    keterangan = alasan,
                    status_approval = "pending"
                )

                println("DEBUG: Sending permission data: $permissionRequest")

                val response = attendanceRepository.createTeacherPermission(token, permissionRequest)
                if (response.isSuccessful) {
                    println("DEBUG: Permission created successfully")
                    loadPermissions(_currentGuruId.value, forceRefresh = true)
                    _infoMessage.value = "Izin berhasil dibuat"
                } else {
                    val code = response.code()
                    val err = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { response.message() }
                    println("DEBUG: Failed to create permission - Code: $code, Error: $err")
                    _errorMessage.value = "Gagal membuat izin: HTTP $code - $err"
                }
            } catch (e: Exception) {
                println("DEBUG: Exception: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun requestSubstituteTeacher(guruPenggantiId: Int, tanggal: String, alasan: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val currentGuruIdVal = _currentGuruId.value ?: 1

                // Load schedules if not loaded yet to find jadwal_id
                if (_jadwalList.value.isEmpty()) {
                    println("DEBUG: Loading schedules to find jadwal_id")
                    loadSchedules(currentGuruIdVal, null, null)
                    // Wait a bit for schedules to load
                    kotlinx.coroutines.delay(500)
                }

                // Attempt to resolve a related jadwal_id from loaded schedules for the current guru
                val jadwalId = _jadwalList.value.firstOrNull { s ->
                    s.guruId == currentGuruIdVal && try {
                        // map tanggal to day name in Indonesian matching schedule.hari
                        val parts = tanggal.split("-")
                        if (parts.size == 3) {
                            val y = parts[0].toInt(); val m = parts[1].toInt(); val d = parts[2].toInt()
                            val cal = java.util.Calendar.getInstance()
                            cal.set(y, m - 1, d)
                            val day = when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
                                java.util.Calendar.MONDAY -> "Senin"
                                java.util.Calendar.TUESDAY -> "Selasa"
                                java.util.Calendar.WEDNESDAY -> "Rabu"
                                java.util.Calendar.THURSDAY -> "Kamis"
                                java.util.Calendar.FRIDAY -> "Jumat"
                                java.util.Calendar.SATURDAY -> "Sabtu"
                                java.util.Calendar.SUNDAY -> "Minggu"
                                else -> ""
                            }
                            s.hari.equals(day, ignoreCase = true)
                        } else false
                    } catch (_: Exception) { false }
                }?.id ?: 1 // Default to 1 if no schedule found

                val substituteRequest = com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacherRequest(
                    jadwal_id = jadwalId,
                    guru_asli_id = currentGuruIdVal,
                    guru_pengganti_id = guruPenggantiId,
                    tanggal = tanggal,
                    status_penggantian = "pending",
                    keterangan = alasan.ifEmpty { null } ?: alasan
                )

                println("DEBUG: Sending substitute data: $substituteRequest")

                val response = attendanceRepository.createSubstituteTeacher(token, substituteRequest)
                if (response.isSuccessful) {
                    println("DEBUG: Substitute created successfully")
                    // Reload substitutes for the current guru
                    loadSubstitutes(currentGuruIdVal, null, null, forceRefresh = true)
                    _infoMessage.value = "Permintaan guru pengganti berhasil dikirim"
                } else {
                    val code = response.code()
                    val err = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { response.message() }
                    println("DEBUG: Failed to create substitute - Code: $code, Error: $err")
                    _errorMessage.value = "Gagal membuat guru pengganti: HTTP $code - $err"
                }
            } catch (e: Exception) {
                println("DEBUG: Exception: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add method for requesting substitute with a specific schedule (to match student role consistency)
    override fun requestSubstituteTeacherWithSchedule(schedule: Schedule, guruPenggantiId: Int, tanggal: String, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val currentGuruIdVal = _currentGuruId.value ?: 1

                val substituteRequest = com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacherRequest(
                    jadwal_id = schedule.id,
                    guru_asli_id = currentGuruIdVal,
                    guru_pengganti_id = guruPenggantiId,
                    tanggal = tanggal,
                    status_penggantian = "pending",
                    keterangan = reason.ifEmpty { null } ?: reason
                )

                println("DEBUG: Sending substitute data with specific schedule: $substituteRequest")

                val response = attendanceRepository.createSubstituteTeacher(token, substituteRequest)
                if (response.isSuccessful) {
                    println("DEBUG: Substitute created successfully for specific schedule")
                    // Reload substitutes for the current guru
                    loadSubstitutes(currentGuruIdVal, null, null, forceRefresh = true)
                    _infoMessage.value = "Permintaan guru pengganti berhasil dikirim"
                } else {
                    val code = response.code()
                    val err = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { response.message() }
                    println("DEBUG: Failed to create substitute - Code: $code, Error: $err")
                    _errorMessage.value = "Gagal membuat guru pengganti: HTTP $code - $err"
                }
            } catch (e: Exception) {
                println("DEBUG: Exception: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }
}