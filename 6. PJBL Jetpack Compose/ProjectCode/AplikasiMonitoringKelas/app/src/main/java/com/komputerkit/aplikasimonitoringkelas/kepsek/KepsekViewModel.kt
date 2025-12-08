package com.komputerkit.aplikasimonitoringkelas.kepsek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository
import com.komputerkit.aplikasimonitoringkelas.data.repository.AttendanceRepository
// Avoid wildcard import of API models to prevent ambiguous type names between
// `common` and `data.models` packages.
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import com.komputerkit.aplikasimonitoringkelas.kepsek.KepsekMappers
import com.komputerkit.aplikasimonitoringkelas.kepsek.KepsekUtils

class KepsekViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // `_tanggalFilter` initialization moved below after properties are declared

    // Jadwal State
    private val _jadwalList = MutableStateFlow<List<Schedule>>(emptyList())
    val jadwalList: StateFlow<List<Schedule>> = _jadwalList

    // Filtered jadwal exposed to UI so filtering/sorting logic stays in ViewModel
    private val _filteredJadwalList = MutableStateFlow<List<Schedule>>(emptyList())
    val filteredJadwalList: StateFlow<List<Schedule>> = _filteredJadwalList

    // Kehadiran Guru State
    private val _kehadiranGuruList = MutableStateFlow<List<TeacherAttendance>>(emptyList())
    val kehadiranGuruList: StateFlow<List<TeacherAttendance>> = _kehadiranGuruList

    // Filtered kehadiran guru for UI
    private val _filteredKehadiranGuruList = MutableStateFlow<List<TeacherAttendance>>(emptyList())
    val filteredKehadiranGuruList: StateFlow<List<TeacherAttendance>> = _filteredKehadiranGuruList

    // Kehadiran Siswa State
    private val _kehadiranSiswaList = MutableStateFlow<List<StudentAttendance>>(emptyList())
    val kehadiranSiswaList: StateFlow<List<StudentAttendance>> = _kehadiranSiswaList

    // Filtered kehadiran siswa for UI
    private val _filteredKehadiranSiswaList = MutableStateFlow<List<StudentAttendance>>(emptyList())
    val filteredKehadiranSiswaList: StateFlow<List<StudentAttendance>> = _filteredKehadiranSiswaList

    // Izin Guru State
    private val _izinGuruList = MutableStateFlow<List<TeacherPermission>>(emptyList())
    val izinGuruList: StateFlow<List<TeacherPermission>> = _izinGuruList

    // Filtered izin guru for UI
    private val _filteredIzinGuruList = MutableStateFlow<List<TeacherPermission>>(emptyList())
    val filteredIzinGuruList: StateFlow<List<TeacherPermission>> = _filteredIzinGuruList

    // Guru Pengganti State
    private val _guruPenggantiList = MutableStateFlow<List<SubstituteTeacher>>(emptyList())
    val guruPenggantiList: StateFlow<List<SubstituteTeacher>> = _guruPenggantiList

    // Filtered guru pengganti for UI
    private val _filteredGuruPenggantiList = MutableStateFlow<List<SubstituteTeacher>>(emptyList())
    val filteredGuruPenggantiList: StateFlow<List<SubstituteTeacher>> = _filteredGuruPenggantiList

    // Kelas State (for populating dropdowns)
    private val _classList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>>(emptyList())
    val classList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>> = _classList

    // Guru and Mata Pelajaran State (reference data for enrichment)
    private val _guruList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>>(emptyList())
    val guruList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>> = _guruList

    private val _mataPelajaranList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.MataPelajaran>>(emptyList())
    val mataPelajaranList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.MataPelajaran>> = _mataPelajaranList

    // Enum Options State (loaded from database)
    private val _statusKehadiranGuruOptions = MutableStateFlow<List<String>>(emptyList())
    val statusKehadiranGuruOptions: StateFlow<List<String>> = _statusKehadiranGuruOptions

    private val _statusKehadiranSiswaOptions = MutableStateFlow<List<String>>(emptyList())
    val statusKehadiranSiswaOptions: StateFlow<List<String>> = _statusKehadiranSiswaOptions

    private val _statusPenggantiOptions = MutableStateFlow<List<String>>(emptyList())
    val statusPenggantiOptions: StateFlow<List<String>> = _statusPenggantiOptions

    private val _statusApprovalOptions = MutableStateFlow<List<String>>(emptyList())
    val statusApprovalOptions: StateFlow<List<String>> = _statusApprovalOptions

    private val _jenisIzinOptions = MutableStateFlow<List<String>>(emptyList())
    val jenisIzinOptions: StateFlow<List<String>> = _jenisIzinOptions

    private val _hariOptions = MutableStateFlow<List<String>>(emptyList())
    val hariOptions: StateFlow<List<String>> = _hariOptions

    // Filter State
    private val _hariFilter = MutableStateFlow<String?>(null)
    val hariFilter: StateFlow<String?> = _hariFilter

    private val _kelasIdFilter = MutableStateFlow<Int?>(null)
    val kelasIdFilter: StateFlow<Int?> = _kelasIdFilter

    private val _tanggalFilter = MutableStateFlow<String?>(null)
    val tanggalFilter: StateFlow<String?> = _tanggalFilter

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter

    private val _jenisIzinFilter = MutableStateFlow<String?>(null)
    val jenisIzinFilter: StateFlow<String?> = _jenisIzinFilter

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // ==================== HELPER METHODS ====================

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getCurrentDayOfWeek(): String {
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Minggu"
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            else -> "Senin"
        }
    }

    // ==================== JADWAL METHODS ====================

    fun loadJadwal(hari: String? = null, kelasId: Int? = null, setLoadingState: Boolean = true) {
        viewModelScope.launch {
            if (setLoadingState) {
                _isLoading.value = true
                _errorMessage.value = null
            }

            // Load reference data asynchronously in background (don't wait)
            if (_classList.value.isEmpty()) {
                launch { loadClasses() }
            }
            if (_guruList.value.isEmpty()) {
                launch { loadGurus() }
            }
            if (_mataPelajaranList.value.isEmpty()) {
                launch { loadMataPelajaran() }
            }

            try {
                val token = authRepository.getToken()
                // Use flexible filter endpoint that accepts null hari and/or kelasId
                val response = attendanceRepository.getSchedules(token, kelasId, null, hari)

                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        val jadwalData = apiResult.data ?: emptyList()
                        val mappedSchedules = KepsekMappers.mapSchedules(jadwalData)

                        // Enrich schedule data with actual names from reference data
                        _jadwalList.value = enrichScheduleData(mappedSchedules)
                        
                        // Update filters - save what we actually loaded
                        _hariFilter.value = hari
                        _kelasIdFilter.value = kelasId
                    } else {
                        // No data for this request — treat as empty list silently
                        _jadwalList.value = emptyList()
                        if (apiResult?.message != null && !apiResult.message.contains("tidak ditemukan", ignoreCase = true)) {
                            _errorMessage.value = "Gagal memuat jadwal: ${apiResult.message}"
                        }
                    }
                } else if (response.code() == 404) {
                    // No data for this request — treat as empty list silently
                    _jadwalList.value = emptyList()
                } else {
                    if (setLoadingState) {
                        _errorMessage.value = "Gagal memuat jadwal: ${response.message()}"
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                if (setLoadingState) {
                    _errorMessage.value = "Waktu koneksi habis. Silakan periksa koneksi internet Anda dan coba lagi."
                }
            } catch (e: java.net.UnknownHostException) {
                if (setLoadingState) {
                    _errorMessage.value = "Tidak dapat terhubung ke server. Silakan periksa koneksi internet Anda."
                }
            } catch (e: Exception) {
                if (setLoadingState) {
                    _errorMessage.value = "Terjadi kesalahan: ${e.message}"
                }
            } finally {
                if (setLoadingState) {
                    _isLoading.value = false
                }
            }
        }
    }

    // ==================== KEHADIRAN GURU METHODS ====================

    fun loadKehadiranGuru(tanggal: String? = null, status: String? = null, kelasId: Int? = null, setLoadingState: Boolean = true) {
        viewModelScope.launch {
            if (setLoadingState) {
                _isLoading.value = true
                _errorMessage.value = null
            }

            try {
                val token = authRepository.getToken()
                // Don't apply default date filter - get all data
                val effectiveTanggal = tanggal
                val effectiveKelasId = kelasId ?: kelasIdFilter.value
                
                // Load reference data asynchronously in background (don't wait)
                loadReferenceDataIfNeededAsync(token, effectiveKelasId)
                
                // Don't load jadwal here - it causes delay. Enrichment will use available data.
                
                val response = attendanceRepository.getTeacherAttendance(token, effectiveTanggal, null, effectiveKelasId, status, 100)

                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        val attendanceList = apiResult.data ?: emptyList()
                        val mapped = KepsekMappers.mapTeacherAttendances(attendanceList)
                        val enriched = enrichTeacherAttendanceData(mapped)

                        // Backend already filtered by status, no need to filter again
                        _kehadiranGuruList.value = enriched
                        
                        // Update filters
                        _tanggalFilter.value = effectiveTanggal
                        _statusFilter.value = status
                        _kelasIdFilter.value = effectiveKelasId
                    } else {
                        _kehadiranGuruList.value = emptyList()
                        if (setLoadingState && apiResult?.message != null && !apiResult.message.contains("tidak ditemukan", ignoreCase = true)) {
                            _errorMessage.value = "Gagal memuat kehadiran guru: ${apiResult.message}"
                        }
                    }
                } else if (response.code() == 404) {
                    _kehadiranGuruList.value = emptyList()
                } else {
                    if (setLoadingState) {
                        _errorMessage.value = "Gagal memuat kehadiran guru: ${response.message()}"
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                if (setLoadingState) {
                    _errorMessage.value = "Waktu koneksi habis saat memuat kehadiran guru. Silakan periksa koneksi internet Anda dan coba lagi."
                }
            } catch (e: java.net.UnknownHostException) {
                if (setLoadingState) {
                    _errorMessage.value = "Tidak dapat terhubung ke server untuk kehadiran guru. Silakan periksa koneksi internet Anda."
                }
            } catch (e: Exception) {
                if (setLoadingState) {
                    _errorMessage.value = "Terjadi kesalahan saat memuat kehadiran guru: ${e.message}"
                }
            } finally {
                if (setLoadingState) {
                    _isLoading.value = false
                }
            }
        }
    }

    // ==================== KEHADIRAN SISWA METHODS ====================

    fun loadKehadiranSiswa(tanggal: String? = null, kelasId: Int? = null, status: String? = null, setLoadingState: Boolean = true) {
        viewModelScope.launch {
            if (setLoadingState) {
                _isLoading.value = true
                _errorMessage.value = null
            }

            try {
                val token = authRepository.getToken()
                // Don't apply default date filter - get all data
                val effectiveTanggal = tanggal
                
                // Load classes asynchronously in background (don't wait)
                if (_classList.value.isEmpty()) {
                    launch { loadClasses() }
                }
                
                val response = attendanceRepository.getStudentAttendance(token, effectiveTanggal, kelasId)

                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        val attendanceList = apiResult.data ?: emptyList()
                        val mapped = KepsekMappers.mapStudentAttendances(attendanceList)
                        val enriched = enrichStudentAttendanceData(mapped)
                        _kehadiranSiswaList.value = KepsekUtils.filterListByStatus(enriched, status) { it.status }
                        
                        // Update filters
                        _tanggalFilter.value = effectiveTanggal
                        _kelasIdFilter.value = kelasId
                        _statusFilter.value = status
                    } else {
                        _kehadiranSiswaList.value = emptyList()
                        if (setLoadingState && apiResult?.message != null && !apiResult.message.contains("tidak ditemukan", ignoreCase = true)) {
                            _errorMessage.value = "Gagal memuat kehadiran siswa: ${apiResult.message}"
                        }
                    }
                } else if (response.code() == 404) {
                    _kehadiranSiswaList.value = emptyList()
                } else {
                    if (setLoadingState) {
                        _errorMessage.value = "Gagal memuat kehadiran siswa: ${response.message()}"
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                if (setLoadingState) {
                    _errorMessage.value = "Waktu koneksi habis saat memuat kehadiran siswa. Silakan periksa koneksi internet Anda dan coba lagi."
                }
            } catch (e: java.net.UnknownHostException) {
                if (setLoadingState) {
                    _errorMessage.value = "Tidak dapat terhubung ke server untuk kehadiran siswa. Silakan periksa koneksi internet Anda."
                }
            } catch (e: Exception) {
                if (setLoadingState) {
                    _errorMessage.value = "Terjadi kesalahan saat memuat kehadiran siswa: ${e.message}"
                }
            } finally {
                if (setLoadingState) {
                    _isLoading.value = false
                }
            }
        }
    }

    // ==================== IZIN GURU METHODS ====================

    fun loadIzinGuru(tanggal: String? = null, status: String? = null, jenisIzin: String? = null, setLoadingState: Boolean = true) {
        viewModelScope.launch {
            if (setLoadingState) {
                _isLoading.value = true
                _errorMessage.value = null
            }

            try {
                val token = authRepository.getToken()
                // Don't apply default date filter - get all data
                val effectiveTanggal = tanggal
                
                val response = attendanceRepository.getTeacherPermissions(token, null, effectiveTanggal, status, jenisIzin, 100)

                if (response.isSuccessful) {
                            val apiResult = response.body() as? com.komputerkit.aplikasimonitoringkelas.data.models.ApiResult<List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission>>
                            val izinList: List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission> = apiResult?.data ?: emptyList()

                    // Map API permissions to common model - backend already filtered by status
                    val mappedIzin = KepsekMappers.mapTeacherPermissionsFull(izinList)

                    _izinGuruList.value = mappedIzin
                    
                    // Update filters
                    _tanggalFilter.value = effectiveTanggal
                    _statusFilter.value = status
                    _jenisIzinFilter.value = jenisIzin
                } else if (response.code() == 404) {
                    _izinGuruList.value = emptyList()
                } else {
                    if (setLoadingState) {
                        _errorMessage.value = "Gagal memuat izin guru: ${response.message()}"
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                if (setLoadingState) {
                    _errorMessage.value = "Waktu koneksi habis saat memuat izin guru. Silakan periksa koneksi internet Anda dan coba lagi."
                }
            } catch (e: java.net.UnknownHostException) {
                if (setLoadingState) {
                    _errorMessage.value = "Tidak dapat terhubung ke server untuk izin guru. Silakan periksa koneksi internet Anda."
                }
            } catch (e: Exception) {
                if (setLoadingState) {
                    _errorMessage.value = "Terjadi kesalahan saat memuat izin guru: ${e.message}"
                }
            } finally {
                if (setLoadingState) {
                    _isLoading.value = false
                }
            }
        }
    }

    // ==================== GURU PENGGANTI METHODS ====================

    fun loadGuruPengganti(tanggal: String? = null, status: String? = null, setLoadingState: Boolean = true) {
        viewModelScope.launch {
            if (setLoadingState) {
                _isLoading.value = true
                _errorMessage.value = null
            }

            try {
                val token = authRepository.getToken()
                // Don't apply default date filter - get all data
                val effectiveTanggal = tanggal
                
                // Load reference data asynchronously in background (don't wait)
                loadReferenceDataIfNeededAsync(token, kelasIdFilter.value)

                val response = attendanceRepository.getSubstituteTeachers(token, effectiveTanggal, kelasIdFilter.value, status, 100)

                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        val substituteList = apiResult.data ?: emptyList()
                        val mapped = KepsekMappers.mapSubstitutes(substituteList)
                        val enriched = enrichSubstituteTeacherData(mapped)
                        // Backend already filtered by status
                        _guruPenggantiList.value = enriched
                        
                        // Update filters
                        _tanggalFilter.value = effectiveTanggal
                        _statusFilter.value = status
                    } else {
                        _guruPenggantiList.value = emptyList()
                        if (setLoadingState && apiResult?.message != null && !apiResult.message.contains("tidak ditemukan", ignoreCase = true)) {
                            _errorMessage.value = "Gagal memuat guru pengganti: ${apiResult.message}"
                        }
                    }
                } else if (response.code() == 404) {
                    _guruPenggantiList.value = emptyList()
                } else {
                    if (setLoadingState) {
                        _errorMessage.value = "Gagal memuat guru pengganti: ${response.message()}"
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                if (setLoadingState) {
                    _errorMessage.value = "Waktu koneksi habis saat memuat guru pengganti. Silakan periksa koneksi internet Anda dan coba lagi."
                }
            } catch (e: java.net.UnknownHostException) {
                if (setLoadingState) {
                    _errorMessage.value = "Tidak dapat terhubung ke server untuk guru pengganti. Silakan periksa koneksi internet Anda."
                }
            } catch (e: Exception) {
                if (setLoadingState) {
                    _errorMessage.value = "Terjadi kesalahan saat memuat guru pengganti: ${e.message}"
                }
            } finally {
                if (setLoadingState) {
                    _isLoading.value = false
                }
            }
        }
    }

    // ==================== KELAS METHODS ====================

    fun loadClasses() {
        viewModelScope.launch {
            // Don't set loading state for reference data as it runs in background
            // _isLoading.value = true
            _errorMessage.value = null

            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getClasses(token)

                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        val classes = apiResult.data ?: emptyList()
                        _classList.value = classes
                    } else {
                        _classList.value = emptyList()
                        if (apiResult?.message != null) {
                            _errorMessage.value = "Gagal memuat kelas: ${apiResult.message}"
                        }
                    }
                } else if (response.code() == 404) {
                    _classList.value = emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat kelas: ${response.message()}"
                }
            } catch (e: java.net.SocketTimeoutException) {
                _errorMessage.value = "Waktu koneksi habis saat memuat kelas. Silakan periksa koneksi internet Anda dan coba lagi."
            } catch (e: java.net.UnknownHostException) {
                _errorMessage.value = "Tidak dapat terhubung ke server untuk kelas. Silakan periksa koneksi internet Anda."
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan saat memuat kelas: ${e.message}"
            }
        }
    }

    // ==================== ENUM OPTIONS METHODS (Load from Database) ====================

    /**
     * Load all enum options from backend database
     */
    fun loadEnumOptions() {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getAllEnums(token)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true && apiResult.data != null) {
                        val enums = apiResult.data
                        _statusKehadiranGuruOptions.value = listOf("Semua") + enums.status_kehadiran_guru
                        _statusKehadiranSiswaOptions.value = listOf("Semua") + enums.status_kehadiran_siswa
                        _statusPenggantiOptions.value = listOf("Semua") + enums.status_penggantian
                        _statusApprovalOptions.value = listOf("Semua") + enums.status_approval
                        _jenisIzinOptions.value = listOf("Semua") + enums.jenis_izin
                        _hariOptions.value = listOf("Semua") + enums.hari
                    }
                }
            } catch (e: Exception) {
                // Fallback to hardcoded options if API fails
                _statusKehadiranGuruOptions.value = listOf("Semua", "hadir", "telat", "tidak_hadir", "izin", "sakit")
                _statusKehadiranSiswaOptions.value = listOf("Semua", "hadir", "tidak_hadir", "izin", "sakit")
                _statusPenggantiOptions.value = listOf("Semua", "pending", "dijadwalkan", "selesai", "tidak_hadir")
                _statusApprovalOptions.value = listOf("Semua", "pending", "disetujui", "ditolak")
                _jenisIzinOptions.value = listOf("Semua", "sakit", "izin", "cuti", "dinas_luar", "lainnya")
                _hariOptions.value = listOf("Semua", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
            }
        }
    }

    /**
     * Load distinct values from specific table/column for dynamic filters
     */
    fun loadDistinctEnumValues(table: String, column: String, onResult: (List<String>) -> Unit) {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getDistinctValues(token, table, column)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true && apiResult.data != null) {
                        onResult(listOf("Semua") + apiResult.data)
                    }
                }
            } catch (e: Exception) {
                // On error, callback with empty list
                onResult(emptyList())
            }
        }
    }

    // ==================== GURU METHODS ====================

    fun loadGurus() {
        // Optional: Implement if there's an endpoint to get all teachers
        viewModelScope.launch {
            // Don't set loading state for reference data as it runs in background
            // _isLoading.value = true
            _errorMessage.value = null
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
                } else {
                    _guruList.value = emptyList()
                }
            } catch (e: java.net.SocketTimeoutException) {
                _errorMessage.value = "Waktu koneksi habis saat memuat guru. Silakan periksa koneksi internet Anda dan coba lagi."
            } catch (e: java.net.UnknownHostException) {
                _errorMessage.value = "Tidak dapat terhubung ke server untuk guru. Silakan periksa koneksi internet Anda."
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan memuat guru: ${e.message}"
            }
        }
    }

    // ==================== MATA PELAJARAN METHODS ====================

    fun loadMataPelajaran() {
        viewModelScope.launch {
            // Don't set loading state for reference data as it runs in background
            // _isLoading.value = true
            _errorMessage.value = null
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
                } else {
                    _mataPelajaranList.value = emptyList()
                }
            } catch (e: java.net.SocketTimeoutException) {
                _errorMessage.value = "Waktu koneksi habis saat memuat mata pelajaran. Silakan periksa koneksi internet Anda dan coba lagi."
            } catch (e: java.net.UnknownHostException) {
                _errorMessage.value = "Tidak dapat terhubung ke server untuk mata pelajaran. Silakan periksa koneksi internet Anda."
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan memuat mata pelajaran: ${e.message}"
            }
        }
    }

    // ==================== FILTER METHODS ====================

    fun setHariFilter(hari: String?) {
        _hariFilter.value = hari
    }

    fun setKelasIdFilter(kelasId: Int?) {
        _kelasIdFilter.value = kelasId
    }

    fun setTanggalFilter(tanggal: String?) {
        _tanggalFilter.value = tanggal
    }

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
    }

    fun setJenisIzinFilter(jenisIzin: String?) {
        _jenisIzinFilter.value = jenisIzin
    }

    init {
        // Initialize tanggal filter after property declarations
        _tanggalFilter.value = getCurrentDate()

        // Keep a single source of truth for jadwal filtering and sorting.
        // Whenever jadwal data or filters change, recompute the filtered list.
        viewModelScope.launch {
            combine(_jadwalList, _hariFilter, _kelasIdFilter) { list, hari, kelas ->
                KepsekUtils.filterAndSortSchedules(list, hari, kelas)
            }.collect { filtered ->
                _filteredJadwalList.value = filtered
            }
        }
        // Keep filtered variants for other lists in sync with filters
        viewModelScope.launch {
            combine(_kehadiranGuruList, _tanggalFilter, _statusFilter, _kelasIdFilter) { list, tanggal, status, kelas ->
                KepsekUtils.filterTeacherAttendances(list, tanggal, status, kelas, _jadwalList.value)
            }.collect { filtered ->
                _filteredKehadiranGuruList.value = filtered
            }
        }

        viewModelScope.launch {
            combine(_kehadiranSiswaList, _tanggalFilter, _statusFilter, _kelasIdFilter) { list, tanggal, status, kelas ->
                KepsekUtils.filterStudentAttendances(list, tanggal, status, kelas)
            }.collect { filtered ->
                _filteredKehadiranSiswaList.value = filtered
            }
        }

        viewModelScope.launch {
            combine(_izinGuruList, _tanggalFilter, _statusFilter, _jenisIzinFilter) { list, tanggal, status, jenisIzin ->
                KepsekUtils.filterPermissions(list, tanggal, status, jenisIzin)
            }.collect { filtered ->
                _filteredIzinGuruList.value = filtered
            }
        }

        viewModelScope.launch {
            combine(_guruPenggantiList, _tanggalFilter, _statusFilter, _kelasIdFilter) { list, tanggal, status, kelas ->
                KepsekUtils.filterSubstitutes(list, tanggal, status, kelas, _jadwalList.value)
            }.collect { filtered ->
                _filteredGuruPenggantiList.value = filtered
            }
        }
    }

    // ==================== LOAD TODAY'S DATA ====================

    fun loadTodaysDataForKepsek() {
        // Load today's data with possible class and status filters
        val today = getCurrentDate()
        val todayDayName = getCurrentDayOfWeek() // Get current day name (e.g., "Senin", "Selasa")

        // Set defaults for today's data
        _tanggalFilter.value = today
        _hariFilter.value = todayDayName // Set day filter for jadwal

        // Set loading state
        _isLoading.value = true
        _errorMessage.value = null

        // Load all data for today with current filters - run concurrently
        viewModelScope.launch {
            try {
                // Launch all operations concurrently without individual loading states
                val jobs = listOf(
                    async { loadKehadiranGuru(today, _statusFilter.value, _kelasIdFilter.value, setLoadingState = false) },
                    async { loadKehadiranSiswa(today, _kelasIdFilter.value, _statusFilter.value, setLoadingState = false) },
                    async { loadIzinGuru(today, _statusFilter.value, setLoadingState = false) },
                    async { loadGuruPengganti(today, _statusFilter.value, setLoadingState = false) },
                    async { loadJadwal(todayDayName, _kelasIdFilter.value, setLoadingState = false) }
                )
                
                // Wait for all to complete
                jobs.awaitAll()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== DATA ENRICHMENT ====================

    // Load reference data concurrently without blocking main operation
    private fun loadReferenceDataIfNeeded(token: String, kelasId: Int?) {
        loadReferenceDataIfNeededAsync(token, kelasId)
    }
    
    private fun loadReferenceDataIfNeededAsync(token: String, kelasId: Int?) {
        // Load all reference data concurrently in background (don't wait)
        viewModelScope.launch {
            if (_classList.value.isEmpty()) {
                val classesResp = attendanceRepository.getClasses(token)
                if (classesResp.isSuccessful) {
                    val apiResult = classesResp.body()
                    if (apiResult?.success == true) {
                        _classList.value = apiResult.data ?: emptyList()
                    }
                }
            }
        }

        viewModelScope.launch {
            if (_guruList.value.isEmpty()) {
                val gurusResp = attendanceRepository.getGurus(token)
                if (gurusResp.isSuccessful) {
                    val apiResult = gurusResp.body()
                    if (apiResult?.success == true) {
                        _guruList.value = apiResult.data ?: emptyList()
                    }
                }
            }
        }

        viewModelScope.launch {
            if (_mataPelajaranList.value.isEmpty()) {
                val mapelResp = attendanceRepository.getMataPelajaran(token)
                    if (mapelResp.isSuccessful) {
                        val apiResult = mapelResp.body()
                        if (apiResult?.success == true) {
                            _mataPelajaranList.value = apiResult.data ?: emptyList()
                        }
                    }
                }
            }
    }

    private fun enrichScheduleData(mappedSchedules: List<com.komputerkit.aplikasimonitoringkelas.common.Schedule>): List<com.komputerkit.aplikasimonitoringkelas.common.Schedule> {
        // Create maps for quick lookup of reference data (as fallback)
        val classMap = _classList.value.associateBy { it.id }
        val guruMap = _guruList.value.associateBy { it.id }
        val mapelMap = _mataPelajaranList.value.associateBy { it.id }

        // Enhance with meaningful names instead of just IDs
        return mappedSchedules.map { schedule ->
            // Prioritize data from API response, use reference data as fallback
            val className = when {
                !schedule.kelasName.startsWith("Kelas ") -> schedule.kelasName // Already has proper name from API
                classMap.containsKey(schedule.kelasId) -> classMap[schedule.kelasId]?.nama ?: schedule.kelasName
                else -> schedule.kelasName
            }
            
            val guruName = when {
                !schedule.guruName.startsWith("Guru ") -> schedule.guruName // Already has proper name from API
                guruMap.containsKey(schedule.guruId) -> guruMap[schedule.guruId]?.nama ?: schedule.guruName
                else -> schedule.guruName
            }
            
            val nipGuru = when {
                schedule.nipGuru != null && !schedule.nipGuru.startsWith("NIP-") -> schedule.nipGuru
                guruMap.containsKey(schedule.guruId) -> guruMap[schedule.guruId]?.nip ?: schedule.nipGuru
                else -> schedule.nipGuru
            }
            
            val mapelName = when {
                !schedule.mapel.startsWith("Mata Pelajaran") -> schedule.mapel // Already has proper name from API
                schedule.kodeMapel?.toIntOrNull() != null && mapelMap.containsKey(schedule.kodeMapel.toInt()) -> {
                    mapelMap[schedule.kodeMapel.toInt()]?.nama ?: schedule.mapel
                }
                else -> schedule.mapel
            }

            schedule.copy(
                kelasName = className,
                guruName = guruName,
                nipGuru = nipGuru,
                mapel = mapelName
            )
        }
    }

    private fun enrichStudentAttendanceData(mappedAttendances: List<com.komputerkit.aplikasimonitoringkelas.common.StudentAttendance>): List<com.komputerkit.aplikasimonitoringkelas.common.StudentAttendance> {
        // Use reference data to enrich student attendance with class/mapel/guru names
        // Note: Jadwal might be empty, so only use it if available
        val classMap = _classList.value.associateBy { it.id }
        val jadwalMap = _jadwalList.value.associateBy { it.id }
        val guruMap = _guruList.value.associateBy { it.id }
        
        return mappedAttendances.map { attendance ->
            // Only lookup schedule if jadwalMap is not empty
            val schedule = if (jadwalMap.isNotEmpty()) jadwalMap[attendance.jadwalId] else null
            
            // Get class name from schedule
            val className = when {
                schedule != null -> classMap[schedule.kelasId]?.nama ?: "Kelas ${schedule.kelasId}"
                else -> null
            }
            
            // Get mata pelajaran from schedule
            val mataPelajaran = schedule?.mapel
            
            // Get guru name from schedule
            val guruName = schedule?.guruName
            
            attendance.copy(
                kelasName = className,
                mataPelajaran = mataPelajaran,
                guruName = guruName
            )
        }
    }

    private fun enrichTeacherAttendanceData(mappedAttendances: List<com.komputerkit.aplikasimonitoringkelas.common.TeacherAttendance>): List<com.komputerkit.aplikasimonitoringkelas.common.TeacherAttendance> {
        // Use reference data to enrich teacher attendance with teacher/class/mapel names
        // Note: Jadwal might be empty, so only use it if available
        val jadwalMap = _jadwalList.value.associateBy { it.id }
        val classMap = _classList.value.associateBy { it.id }
        val guruMap = _guruList.value.associateBy { it.id }
        val mapelMap = _mataPelajaranList.value.associateBy { it.id }

        return mappedAttendances.map { attendance ->
            // Only lookup schedule if jadwalMap is not empty
            val schedule = if (jadwalMap.isNotEmpty()) jadwalMap[attendance.jadwalId] else null
            
            // Prioritize data from API response, use reference data or schedule as fallback
            val teacherName = when {
                attendance.guruName != null && !attendance.guruName.startsWith("Guru ") -> attendance.guruName
                guruMap.containsKey(attendance.guruId) -> guruMap[attendance.guruId]?.nama
                schedule != null -> schedule.guruName
                else -> null
            } ?: "Guru ${attendance.guruId}"
            
            // Get class name - priority: API > schedule > classMap > fallback
            val className = when {
                attendance.kelasName != null -> attendance.kelasName
                schedule != null && !schedule.kelasName.startsWith("Kelas ") -> schedule.kelasName
                schedule != null && classMap.containsKey(schedule.kelasId) -> classMap[schedule.kelasId]?.nama
                else -> null
            }
            
            // Get mata pelajaran - priority: API > schedule
            val mataPelajaran = attendance.mataPelajaran ?: schedule?.mapel
            
            attendance.copy(
                guruName = teacherName,
                kelasName = className,
                mataPelajaran = mataPelajaran
            )
        }
    }

    private fun enrichSubstituteTeacherData(mappedSubstitutes: List<com.komputerkit.aplikasimonitoringkelas.common.SubstituteTeacher>): List<com.komputerkit.aplikasimonitoringkelas.common.SubstituteTeacher> {
        // Create maps for quick lookup of reference data (as fallback)
        val classMap = _classList.value.associateBy { it.id }
        val jadwalMap = _jadwalList.value.associateBy { it.id }
        val guruMap = _guruList.value.associateBy { it.id }

        return mappedSubstitutes.map { substitute ->
            val schedule = jadwalMap[substitute.jadwalId]
            
            // Prioritize class name from API response
            val kelasName = when {
                !substitute.kelas.startsWith("Kelas ") -> substitute.kelas // Already has proper name from API
                schedule != null && !schedule.kelasName.startsWith("Kelas ") -> schedule.kelasName
                schedule != null && classMap.containsKey(schedule.kelasId) -> classMap[schedule.kelasId]?.nama ?: substitute.kelas
                else -> substitute.kelas
            }

            // Prioritize guru names from API response, use guruMap as fallback
            val namaAsli = when {
                !substitute.namaGuruAsli.startsWith("Guru ") -> substitute.namaGuruAsli
                guruMap.containsKey(substitute.guruAsliId) -> guruMap[substitute.guruAsliId]?.nama ?: substitute.namaGuruAsli
                else -> substitute.namaGuruAsli
            }
            
            val namaPengganti = when {
                !substitute.namaGuruPengganti.startsWith("Guru ") -> substitute.namaGuruPengganti
                guruMap.containsKey(substitute.guruPenggantiId) -> guruMap[substitute.guruPenggantiId]?.nama ?: substitute.namaGuruPengganti
                else -> substitute.namaGuruPengganti
            }

            substitute.copy(
                kelas = kelasName,
                namaGuruAsli = namaAsli,
                namaGuruPengganti = namaPengganti
            )
        }
    }
}