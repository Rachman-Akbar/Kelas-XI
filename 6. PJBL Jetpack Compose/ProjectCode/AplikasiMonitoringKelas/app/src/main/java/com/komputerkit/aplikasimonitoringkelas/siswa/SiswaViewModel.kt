package com.komputerkit.aplikasimonitoringkelas.siswa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.data.repository.AttendanceRepository
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.joinAll
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SiswaViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    // ==================== CACHING CONFIGURATION ====================
    private val CACHE_DURATION = 5 * 60 * 1000L // 5 minutes

    // Cached data with timestamps
    private val _cachedSchedules = MutableStateFlow<List<Schedule>?>(null)
    private val _lastScheduleLoad = MutableStateFlow<Long>(0)

    private val _cachedAttendances = MutableStateFlow<List<Attendance>?>(null)
    private val _lastAttendanceLoad = MutableStateFlow<Long>(0)

    private val _cachedStudentAttendances = MutableStateFlow<List<StudentAttendance>?>(null)
    private val _lastStudentAttendanceLoad = MutableStateFlow<Long>(0)

    private val _cachedSubstitutes = MutableStateFlow<List<SubstituteTeacher>?>(null)
    private val _lastSubstituteLoad = MutableStateFlow<Long>(0)

    private val _cachedTeacherAttendances = MutableStateFlow<List<TeacherAttendance>?>(null)
    private val _lastTeacherAttendanceLoad = MutableStateFlow<Long>(0)

    private val _cachedTeacherPermissions = MutableStateFlow<List<TeacherPermission>?>(null)
    private val _lastTeacherPermissionLoad = MutableStateFlow<Long>(0)

    // Public state flows
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules

    private val _attendances = MutableStateFlow<List<Attendance>>(emptyList())
    val attendances: StateFlow<List<Attendance>> = _attendances

    private val _substitutes = MutableStateFlow<List<SubstituteTeacher>>(emptyList())
    val substitutes: StateFlow<List<SubstituteTeacher>> = _substitutes

    private val _studentAttendances = MutableStateFlow<List<StudentAttendance>>(emptyList())
    val studentAttendances: StateFlow<List<StudentAttendance>> = _studentAttendances

    private val _teacherAttendances = MutableStateFlow<List<TeacherAttendance>>(emptyList())
    val teacherAttendances: StateFlow<List<TeacherAttendance>> = _teacherAttendances

    private val _teacherPermissions = MutableStateFlow<List<TeacherPermission>>(emptyList())
    val teacherPermissions: StateFlow<List<TeacherPermission>> = _teacherPermissions

    private val _kelasFilter = MutableStateFlow<String?>(null)
    val kelasFilter: StateFlow<String?> = _kelasFilter

    private val _classList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>>(emptyList())
    val classList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>> = _classList

    // User's kelas_id from account (used as default/fallback)
    private val _userKelasId = MutableStateFlow<Int?>(null)
    val userKelasId: StateFlow<Int?> = _userKelasId

    // User's kelas name
    private val _userKelasName = MutableStateFlow<String?>(null)
    val userKelasName: StateFlow<String?> = _userKelasName

    // Guru and Mata Pelajaran State (reference data for enrichment)
    private val _guruList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>>(emptyList())
    val guruList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>> = _guruList

    private val _mataPelajaranList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.MataPelajaran>>(emptyList())
    val mataPelajaranList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.MataPelajaran>> = _mataPelajaranList

    private val _siswaList = MutableStateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Siswa>>(emptyList())
    val siswaList: StateFlow<List<com.komputerkit.aplikasimonitoringkelas.data.models.Siswa>> = _siswaList

    // UI State
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _tanggalFilter = MutableStateFlow<String?>(null)
    val tanggalFilter: StateFlow<String?> = _tanggalFilter

    private val _dayFilter = MutableStateFlow<String?>(null)
    val dayFilter: StateFlow<String?> = _dayFilter

    init {
        // Load user's kelas_id and tanggal when ViewModel is created
        _tanggalFilter.value = getCurrentDate()

        viewModelScope.launch {
            try {
                android.util.Log.d("SiswaViewModel", "=== INIT: Loading kelas_id from DataStore ===")

                // Try to get kelas_id from DataStore first
                var kelasId = authRepository.getKelasId()
                android.util.Log.d("SiswaViewModel", "Kelas ID from DataStore: $kelasId")

                if (kelasId == null) {
                    android.util.Log.w("SiswaViewModel", "Kelas ID is NULL - Attempting to fetch from API...")

                    // Try to get user info from API using /me endpoint
                    try {
                        val result = authRepository.getUserInfo()
                        if (result.isSuccess) {
                            val userInfo = result.getOrNull()
                            val apiKelasId = userInfo?.data?.user?.kelas_id

                            if (apiKelasId != null) {
                                android.util.Log.d("SiswaViewModel", "Kelas ID fetched from API: $apiKelasId")
                                kelasId = apiKelasId
                            } else {
                                android.util.Log.e("SiswaViewModel", "Kelas ID not available in API response!")
                                _errorMessage.value = "Kelas ID tidak ditemukan. Silakan login ulang."
                            }
                        } else {
                            android.util.Log.e("SiswaViewModel", "Failed to fetch user info from API")
                            _errorMessage.value = "Tidak dapat mengambil informasi pengguna. Silakan login ulang."
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SiswaViewModel", "Error fetching user info from API", e)
                        _errorMessage.value = "Kelas ID tidak ditemukan. Silakan login ulang."
                    }
                }

                // Set the user's kelas ID regardless of source
                if (kelasId != null) {
                    _userKelasId.value = kelasId
                    android.util.Log.d("SiswaViewModel", "Loading kelas name for kelasId: $kelasId")
                    loadKelasName(kelasId)

                    // Load data for the user's class now that we have the kelasId
                    android.util.Log.d("SiswaViewModel", "Loading initial data for class ID: $kelasId")
                    loadSchedules(kelasId, forceRefresh = true)
                    loadStudentAttendances(kelasId, forceRefresh = true)
                    loadTeacherAttendances(kelasId, forceRefresh = true)
                } else {
                    android.util.Log.e("SiswaViewModel", "Kelas ID is still NULL after all attempts!")
                    _errorMessage.value = "Kelas ID tidak ditemukan. Silakan login ulang."
                }
            } catch (e: Exception) {
                android.util.Log.e("SiswaViewModel", "Error loading kelas_id", e)
                _errorMessage.value = "Gagal memuat data user: ${e.message}"
            }
        }
        // Load enum options (filters) immediately so UI dialogs have data
        viewModelScope.launch {
            try {
                loadEnumOptions()
            } catch (e: Exception) {
                android.util.Log.e("SiswaViewModel", "Error loading enum options on init", e)
            }
        }
    }

    // Public method to set error messages from UI
    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    fun setInfoMessage(message: String?) {
        _infoMessage.value = message
    }

    fun loadSiswaList(kelasId: Int) {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                android.util.Log.d("SiswaViewModel", "Loading siswa list for kelasId: $kelasId")

                val response = attendanceRepository.getStudentsByClass(token, kelasId)
                if (response.isSuccessful) {
                    val students = response.body()?.data ?: emptyList()
                    _siswaList.value = students
                    android.util.Log.d("SiswaViewModel", "Loaded ${students.size} students for kelas $kelasId")
                } else {
                    android.util.Log.e("SiswaViewModel", "Failed to load students: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SiswaViewModel", "Error loading students", e)
            }
        }
    }

    private fun loadKelasName(kelasId: Int) {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                android.util.Log.d("SiswaViewModel", "Fetching kelas name for kelasId: $kelasId")

                val response = attendanceRepository.getClasses(token)
                if (response.isSuccessful) {
                    val classes = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>) ?: emptyList()
                    android.util.Log.d("SiswaViewModel", "Total classes fetched: ${classes.size}")

                    val kelas = classes.find { it.id == kelasId }
                    _userKelasName.value = kelas?.nama

                    android.util.Log.d("SiswaViewModel", "Kelas name found: ${kelas?.nama ?: "NOT FOUND"}")
                } else {
                    android.util.Log.e("SiswaViewModel", "Failed to fetch classes: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SiswaViewModel", "Error loading kelas name", e)
            }
        }
    }

    fun loadSchedules(kelasId: Int, hari: String? = null, forceRefresh: Boolean = false) {
        android.util.Log.d("SiswaViewModel", "=== loadSchedules CALLED ===")
        android.util.Log.d("SiswaViewModel", "kelasId: $kelasId, hari: $hari, forceRefresh: $forceRefresh")

        viewModelScope.launch {
            _errorMessage.value = null
            _isLoading.value = true

            // Check cache validity
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastScheduleLoad.value) < CACHE_DURATION

            // Use cached data if valid and not forcing refresh
            if (!forceRefresh && isCacheValid && _cachedSchedules.value != null) {
                android.util.Log.d("SiswaViewModel", "Using cached schedules")
                _schedules.value = _cachedSchedules.value!!
                _isLoading.value = false
                return@launch
            }

            try {
                val token = authRepository.getToken()
                android.util.Log.d("SiswaViewModel", "Token: ${if (token.isNotEmpty()) "Present (${token.take(20)}...)" else "EMPTY!"}")
                android.util.Log.d("SiswaViewModel", "Calling API: getSchedules(kelasId=$kelasId, hari=$hari)")

                // Load reference data and WAIT for completion before proceeding
                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                if (_classList.value.isEmpty()) {
                    jobs.add(launch { fetchClasses() })
                }
                if (_guruList.value.isEmpty()) {
                    jobs.add(launch { fetchGurus() })
                }
                if (_mataPelajaranList.value.isEmpty()) {
                    jobs.add(launch { fetchMataPelajaran() })
                }
                // Wait for all reference data to load
                jobs.joinAll()
                android.util.Log.d("SiswaViewModel", "Reference sizes: classes=${_classList.value.size}, gurus=${_guruList.value.size}, mapel=${_mataPelajaranList.value.size}")

                val response = attendanceRepository.getSchedules(token, kelasId = kelasId, hari = hari)
                if (response.isSuccessful) {
                    val body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.Schedule>) ?: emptyList()
                    android.util.Log.d("SiswaViewModel", "Schedules loaded: ${body.size} items")

                    val mappedSchedules = body.map { api ->
                        Schedule(
                            id = api.id,
                            guruId = api.guru_id,
                            kelasId = api.kelas_id,
                            hari = api.hari,
                            jam = "${api.jam_mulai ?: ""}-${api.jam_selesai ?: ""}",
                            mapel = "Mata Pelajaran ${api.mata_pelajaran_id}", // Will be enriched later
                            guruName = "Guru ${api.guru_id}", // Will be enriched later
                            kelasName = "Kelas ${api.kelas_id}", // Will be enriched later
                            nipGuru = null,
                            kodeMapel = api.mata_pelajaran_id.toString(), // This is the key for enrichment
                            tahunAjaran = null,
                            jamKe = api.jam_ke,
                            jamMulai = api.jam_mulai,
                            jamSelesai = api.jam_selesai,
                            ruangan = api.ruangan
                        )
                    }

                    // Enrich schedule data with actual names from reference data
                    val enrichedSchedules = enrichScheduleData(mappedSchedules)

                    // Update cache
                    _cachedSchedules.value = enrichedSchedules
                    _lastScheduleLoad.value = now
                    _schedules.value = enrichedSchedules

                    android.util.Log.d("SiswaViewModel", "Schedules enriched and cached: ${enrichedSchedules.size} items")
                } else {
                    _errorMessage.value = "Gagal memuat jadwal: ${response.message()}"
                    android.util.Log.e("SiswaViewModel", "Failed to load schedules: ${response.code()} - ${response.message()}")
                }
            } catch (e: java.net.SocketTimeoutException) {
                _errorMessage.value = "Waktu koneksi habis saat memuat jadwal. Silakan periksa koneksi internet Anda dan coba lagi."
                android.util.Log.e("SiswaViewModel", "Timeout loading schedules", e)
            } catch (e: java.net.UnknownHostException) {
                _errorMessage.value = "Tidak dapat terhubung ke server untuk jadwal. Silakan periksa koneksi internet Anda."
                android.util.Log.e("SiswaViewModel", "Unknown host loading schedules", e)
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan memuat jadwal: ${e.message}"
                android.util.Log.e("SiswaViewModel", "Error loading schedules", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAttendances(kelasId: Int, tanggal: String? = _tanggalFilter.value, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // Check cache validity
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastAttendanceLoad.value) < CACHE_DURATION

            // Use cached data if valid and not forcing refresh
            if (!forceRefresh && isCacheValid && _cachedAttendances.value != null) {
                _attendances.value = _cachedAttendances.value!!
                return@launch
            }

            try {
                val token = authRepository.getToken()
                // Ensure reference data is loaded before fetching attendance
                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                if (_classList.value.isEmpty()) {
                    jobs.add(launch { fetchClasses() })
                }
                if (_guruList.value.isEmpty()) {
                    jobs.add(launch { fetchGurus() })
                }
                if (_mataPelajaranList.value.isEmpty()) {
                    jobs.add(launch { fetchMataPelajaran() })
                }
                jobs.joinAll()

                val response = attendanceRepository.getTeacherAttendance(token, tanggal, null, kelasId, null, null)
                if (response.isSuccessful) {
                    val body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherAttendance>) ?: emptyList()
                    val mappedAttendances = body.map { api ->
                        Attendance(
                            id = api.id,
                            siswaId = null,
                            guruId = api.guru_id,
                            jadwalId = api.jadwal_id,
                            tanggal = api.tanggal,
                            status = api.status_kehadiran ?: "",
                            keterangan = api.keterangan
                        )
                    }

                    // Update cache
                    _cachedAttendances.value = mappedAttendances
                    _lastAttendanceLoad.value = now
                    _attendances.value = mappedAttendances
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun loadClasses() {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getClasses(token)
                if (response.isSuccessful) {
                    val data = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.Class>) ?: emptyList()
                    _classList.value = data
                } else if (response.code() == 404) {
                    _classList.value = emptyList()
                }
            } catch (e: java.net.SocketTimeoutException) {
                _errorMessage.value = "Waktu koneksi habis saat memuat kelas. Silakan periksa koneksi internet Anda dan coba lagi."
            } catch (e: java.net.UnknownHostException) {
                _errorMessage.value = "Tidak dapat terhubung ke server untuk kelas. Silakan periksa koneksi internet Anda."
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan memuat kelas: ${e.message}"
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun setTanggalFilter(tanggal: String?) {
        _tanggalFilter.value = tanggal
    }

    // Enum options (loaded from backend)
    private val _jenisIzinOptions = MutableStateFlow<List<String>>(listOf("Semua"))
    val jenisIzinOptions: StateFlow<List<String>> = _jenisIzinOptions

    private val _statusPenggantiOptions = MutableStateFlow<List<String>>(listOf("Semua"))
    val statusPenggantiOptions: StateFlow<List<String>> = _statusPenggantiOptions

    fun loadEnumOptions() {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getAllEnums(token)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true && apiResult.data != null) {
                        val enums = apiResult.data
                        _jenisIzinOptions.value = listOf("Semua") + (enums.jenis_izin ?: emptyList())
                        _statusPenggantiOptions.value = listOf("Semua") + (enums.status_penggantian ?: emptyList())
                    }
                }
            } catch (e: Exception) {
                // fallback to sensible defaults
                _jenisIzinOptions.value = listOf("Semua", "sakit", "izin", "cuti", "dinas_luar", "lainnya")
                _statusPenggantiOptions.value = listOf("Semua", "pending", "dijadwalkan", "selesai", "tidak_hadir")
            }
        }
    }

    fun loadSubstitutes(kelasId: Int) {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                // Ensure reference data is loaded before fetching substitutes
                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                if (_classList.value.isEmpty()) {
                    jobs.add(launch { fetchClasses() })
                }
                if (_guruList.value.isEmpty()) {
                    jobs.add(launch { fetchGurus() })
                }
                if (_schedules.value.isEmpty()) {
                    jobs.add(launch { loadSchedules(kelasId = kelasId) })
                }
                jobs.joinAll()

                val response = attendanceRepository.getSubstituteTeachers(token, tanggal = null, kelasId = kelasId)
                if (response.isSuccessful) {
                    val body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacher>) ?: emptyList()
                    _substitutes.value = body.map { api ->
                        // Get mata pelajaran from jadwal
                        val jadwal = _schedules.value.find { it.id == api.jadwal_id }
                        val mataPelajaran = jadwal?.mapel

                        SubstituteTeacher(
                            id = api.id,
                            guruAsliId = api.guru_asli_id,
                            guruPenggantiId = api.guru_pengganti_id,
                            jadwalId = api.jadwal_id,
                            tanggal = api.tanggal,
                            namaGuruAsli = api.guru_asli?.nama ?: "Guru ${api.guru_asli_id}",
                            namaGuruPengganti = api.guru_pengganti?.nama ?: "Guru ${api.guru_pengganti_id}",
                            kelas = api.jadwal?.kelas?.nama ?: "Kelas ${api.jadwal_id}",
                            mataPelajaran = mataPelajaran,
                            statusPenggantian = api.status_penggantian ?: "",
                            keterangan = api.keterangan ?: ""
                        )
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun loadStudentAttendances(kelasId: Int, forceRefresh: Boolean = false) {
        android.util.Log.d("SiswaViewModel", "=== loadStudentAttendances CALLED ===")
        android.util.Log.d("SiswaViewModel", "kelasId: $kelasId, forceRefresh: $forceRefresh")

        viewModelScope.launch {
            _isLoading.value = true

            // Check cache validity
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastStudentAttendanceLoad.value) < CACHE_DURATION

            // Use cached data if valid and not forcing refresh
            if (!forceRefresh && isCacheValid && _cachedStudentAttendances.value != null) {
                android.util.Log.d("SiswaViewModel", "Using cached student attendances")
                _studentAttendances.value = _cachedStudentAttendances.value!!
                _isLoading.value = false
                return@launch
            }

            try {
                val token = authRepository.getToken()
                android.util.Log.d("SiswaViewModel", "Token: ${if (token.isNotEmpty()) "Present (${token.take(20)}...)" else "EMPTY!"}")
                android.util.Log.d("SiswaViewModel", "Calling API: getStudentAttendance(kelasId=$kelasId)")

                // Load reference data first and wait for completion
                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                if (_siswaList.value.isEmpty()) {
                    jobs.add(launch { fetchSiswa(kelasId) })
                }
                if (_classList.value.isEmpty()) {
                    jobs.add(launch { fetchClasses() })
                }
                if (_schedules.value.isEmpty()) {
                    // If schedules are empty, fetch schedules (this will itself ensure references)
                    jobs.add(launch { loadSchedules(kelasId = kelasId) })
                }
                jobs.joinAll()
                android.util.Log.d("SiswaViewModel", "Reference data loaded: ${_siswaList.value.size} siswa, ${_schedules.value.size} jadwal")

                val response = attendanceRepository.getStudentAttendance(token, tanggal = null, kelasId = kelasId)
                if (response.isSuccessful) {
                    val body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.StudentAttendance>) ?: emptyList()
                    android.util.Log.d("SiswaViewModel", "Student attendances loaded: ${body.size} items")

                    val mappedAttendances = body.map { api ->
                        StudentAttendance(
                            id = api.id,
                            siswaId = api.siswa_id,
                            jadwalId = api.jadwal_id,
                            tanggal = api.tanggal,
                            status = api.status,
                            keterangan = api.keterangan,
                            siswaName = null,  // Will be enriched
                            mataPelajaran = null  // Will be enriched
                        )
                    }

                    // Enrich with nama siswa dan mata pelajaran
                    val enrichedAttendances = enrichStudentAttendanceData(mappedAttendances)
                    android.util.Log.d("SiswaViewModel", "Student attendances enriched: ${enrichedAttendances.size} items")

                    // Update cache
                    _cachedStudentAttendances.value = enrichedAttendances
                    _lastStudentAttendanceLoad.value = now
                    _studentAttendances.value = enrichedAttendances
                } else {
                    _errorMessage.value = "Gagal memuat kehadiran siswa: ${response.message()}"
                    android.util.Log.e("SiswaViewModel", "Failed to load student attendances: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error memuat kehadiran siswa: ${e.message}"
                android.util.Log.e("SiswaViewModel", "Error loading student attendances", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTeacherAttendances(kelasId: Int, forceRefresh: Boolean = false) {
        android.util.Log.d("SiswaViewModel", "=== loadTeacherAttendances CALLED ===")
        android.util.Log.d("SiswaViewModel", "kelasId: $kelasId, forceRefresh: $forceRefresh")

        viewModelScope.launch {
            _errorMessage.value = null
            _isLoading.value = true

            // Check cache validity
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastTeacherAttendanceLoad.value) < CACHE_DURATION

            // Use cached data if valid and not forcing refresh
            if (!forceRefresh && isCacheValid && _cachedTeacherAttendances.value != null) {
                android.util.Log.d("SiswaViewModel", "Using cached teacher attendances")
                _teacherAttendances.value = _cachedTeacherAttendances.value!!
                _isLoading.value = false
                return@launch
            }

            try {
                val token = authRepository.getToken()
                android.util.Log.d("SiswaViewModel", "Token: ${if (token.isNotEmpty()) "Present (${token.take(20)}...)" else "EMPTY!"}")
                android.util.Log.d("SiswaViewModel", "Calling API: getTeacherAttendance(kelasId=$kelasId)")
                android.util.Log.d("SiswaViewModel", "Loading teacher attendances for kelasId: $kelasId")

                // Ensure reference data is loaded
                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                if (_classList.value.isEmpty()) {
                    jobs.add(launch { fetchClasses() })
                }
                if (_guruList.value.isEmpty()) {
                    jobs.add(launch { fetchGurus() })
                }
                if (_mataPelajaranList.value.isEmpty()) {
                    jobs.add(launch { fetchMataPelajaran() })
                }
                jobs.joinAll()

                val response = attendanceRepository.getTeacherAttendance(token, null, null, kelasId, null, null)
                if (response.isSuccessful) {
                    val body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherAttendance>) ?: emptyList()
                    android.util.Log.d("SiswaViewModel", "Teacher attendances loaded: ${body.size} items")

                    val mappedAttendances = body.map { api ->
                        val guru = _guruList.value.find { it.id == api.guru_id }
                        val kelas = _classList.value.find { it.id == kelasId }
                        // Try to get mapel from jadwal if available
                        val mapel = api.jadwal?.mata_pelajaran?.nama

                        TeacherAttendance(
                            id = api.id,
                            jadwalId = api.jadwal_id,
                            guruId = api.guru_id,
                            tanggal = api.tanggal,
                            statusKehadiran = api.status_kehadiran,
                            waktuDatang = api.waktu_datang,
                            durasiKeterlambatan = api.durasi_keterlambatan,
                            keterangan = api.keterangan,
                            guruName = guru?.nama,
                            kelasName = kelas?.nama,
                            mataPelajaran = mapel
                        )
                    }

                    _teacherAttendances.value = mappedAttendances
                    _cachedTeacherAttendances.value = mappedAttendances
                    _lastTeacherAttendanceLoad.value = now
                } else {
                    _errorMessage.value = "Gagal memuat kehadiran guru: ${response.message()}"
                    android.util.Log.e("SiswaViewModel", "Failed to load teacher attendances: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error memuat kehadiran guru: ${e.message}"
                android.util.Log.e("SiswaViewModel", "Error loading teacher attendances", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setKelasFilter(kelas: String?) {
        _kelasFilter.value = kelas
    }

    // ==================== REFERENCE DATA METHODS ====================

    fun loadMataPelajaran() {
        viewModelScope.launch {
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

    fun loadGurus() {
        viewModelScope.launch {
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

    fun loadSiswa(kelasId: Int? = null) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val token = authRepository.getToken()
                val response = attendanceRepository.getStudentsByClass(token, kelasId ?: 0)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult?.success == true) {
                        _siswaList.value = apiResult.data ?: emptyList()
                        android.util.Log.d("SiswaViewModel", "Siswa loaded: ${_siswaList.value.size} items")
                    } else {
                        _siswaList.value = emptyList()
                    }
                } else {
                    _siswaList.value = emptyList()
                }
            } catch (e: java.net.SocketTimeoutException) {
                _errorMessage.value = "Waktu koneksi habis saat memuat siswa. Silakan periksa koneksi internet Anda dan coba lagi."
            } catch (e: java.net.UnknownHostException) {
                _errorMessage.value = "Tidak dapat terhubung ke server untuk siswa. Silakan periksa koneksi internet Anda."
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan memuat siswa: ${e.message}"
            }
        }
    }

    // ==================== DATA ENRICHMENT ====================

    // Load reference data concurrently without blocking main operation
    private fun loadReferenceDataIfNeeded(token: String) {
        viewModelScope.launch {
            // Load all reference data concurrently
            val classesJob = launch {
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

            val gurusJob = launch {
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

            val mapelJob = launch {
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

            // Wait for all reference data to be loaded
            joinAll(classesJob, gurusJob, mapelJob)
        }
    }

    // Synchronous fetch helpers - suspend functions that actually perform network calls
    private suspend fun fetchClasses() {
        try {
            val token = authRepository.getToken()
            val response = attendanceRepository.getClasses(token)
            if (response.isSuccessful) {
                val apiResult = response.body()
                if (apiResult?.success == true) {
                    _classList.value = apiResult.data ?: emptyList()
                }
            }
        } catch (e: Exception) {
            // ignore here; caller will handle log/state
        }
    }

    private suspend fun fetchGurus() {
        try {
            val token = authRepository.getToken()
            val response = attendanceRepository.getGurus(token)
            if (response.isSuccessful) {
                val apiResult = response.body()
                if (apiResult?.success == true) {
                    _guruList.value = apiResult.data ?: emptyList()
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private suspend fun fetchMataPelajaran() {
        try {
            val token = authRepository.getToken()
            val response = attendanceRepository.getMataPelajaran(token)
            if (response.isSuccessful) {
                val apiResult = response.body()
                if (apiResult?.success == true) {
                    _mataPelajaranList.value = apiResult.data ?: emptyList()
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private suspend fun fetchSiswa(kelasId: Int?) {
        try {
            val token = authRepository.getToken()
            val response = attendanceRepository.getStudentsByClass(token, kelasId ?: 0)
            if (response.isSuccessful) {
                val apiResult = response.body()
                if (apiResult?.success == true) {
                    _siswaList.value = apiResult.data ?: emptyList()
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun enrichScheduleData(mappedSchedules: List<com.komputerkit.aplikasimonitoringkelas.common.Schedule>): List<com.komputerkit.aplikasimonitoringkelas.common.Schedule> {
        // Create maps for quick lookup of reference data
        val classMap = _classList.value.associateBy { it.id }
        val guruMap = _guruList.value.associateBy { it.id }
        val mapelMap = _mataPelajaranList.value.associateBy { it.id }

        // Enhance with meaningful names instead of just IDs
        return mappedSchedules.map { schedule ->
            val className = classMap[schedule.kelasId]?.nama ?: "Kelas ${schedule.kelasId}"
            val guruName = when {
                !schedule.guruName.isNullOrEmpty() && !schedule.guruName.startsWith("Guru") -> schedule.guruName
                guruMap[schedule.guruId] != null -> guruMap[schedule.guruId]?.nama ?: "Guru ${schedule.guruId}"
                else -> "Guru ${schedule.guruId}"
            }
            val nipGuru = schedule.nipGuru ?: guruMap[schedule.guruId]?.nip ?: "NIP-${schedule.guruId}"
            val mapelName = when {
                !schedule.mapel.isNullOrEmpty() && !schedule.mapel.startsWith("Mata Pelajaran") -> schedule.mapel // Keep existing name if it's already properly set
                schedule.kodeMapel?.toIntOrNull() != null && mapelMap.containsKey(schedule.kodeMapel.toInt()) -> {
                    mapelMap[schedule.kodeMapel.toInt()]?.nama ?: "Mata Pelajaran ${schedule.kodeMapel}"
                }
                else -> {
                    // Fallback to the original mapping logic if needed
                    "Mata Pelajaran ${schedule.kodeMapel ?: schedule.jamKe?.toString() ?: ""}"
                }
            }

            schedule.copy(
                kelasName = className,
                guruName = guruName,
                nipGuru = nipGuru,
                mapel = mapelName
            )
        }
    }

    private fun enrichStudentAttendanceData(mappedAttendances: List<StudentAttendance>): List<StudentAttendance> {
        // Create maps for quick lookup
        val siswaMap = _siswaList.value.associateBy { it.id }
        val scheduleMap = _schedules.value.associateBy { it.id }
        val guruMap = _guruList.value.associateBy { it.id }
        val classMap = _classList.value.associateBy { it.id }

        android.util.Log.d("SiswaViewModel", "Enriching with ${siswaMap.size} siswa and ${scheduleMap.size} schedules, gurus=${guruMap.size}, classes=${classMap.size}")

        val enriched = mappedAttendances.map { attendance ->
            val siswaName = siswaMap[attendance.siswaId]?.nama ?: "Siswa ${attendance.siswaId}"
            val schedule = scheduleMap[attendance.jadwalId]
            val mataPelajaran = schedule?.mapel ?: "Mata Pelajaran ${attendance.jadwalId}"
            val kelasName = schedule?.kelasName ?: schedule?.kelasId?.let { classMap[it]?.nama } ?: "Kelas ${schedule?.kelasId ?: attendance.jadwalId}"
            val guruName = schedule?.guruName ?: schedule?.guruId?.let { guruMap[it]?.nama } ?: "Guru ${schedule?.guruId ?: "?"}"

            attendance.copy(
                siswaName = siswaName,
                mataPelajaran = mataPelajaran,
                kelasName = kelasName,
                guruName = guruName
            )
        }

        android.util.Log.d("SiswaViewModel", "First enriched student attendance: ${enriched.firstOrNull()}")
        return enriched
    }

    // ==================== FILTER METHODS ====================

    fun setDayFilter(day: String?) {
        _dayFilter.value = day
    }

    // New filter methods for student role
    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
    }

    fun applyStudentAttendanceFilter(kelasId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = authRepository.getToken()
                val status = _statusFilter.value
                android.util.Log.d("SiswaViewModel", "Loading student attendance with kelasId: $kelasId, status: $status")

                val response = attendanceRepository.getStudentAttendance(token, null, kelasId)
                if (response.isSuccessful) {
                    var body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.StudentAttendance>) ?: emptyList()

                    // Filter by status if specified
                    if (!status.isNullOrEmpty() && status != "Semua") {
                        body = body.filter { att -> att.status.equals(status, ignoreCase = true) }
                    }

                    val mappedAttendances = body.map { api ->
                        StudentAttendance(
                            id = api.id,
                            siswaId = api.siswa_id,
                            jadwalId = api.jadwal_id,
                            tanggal = api.tanggal,
                            status = api.status,
                            keterangan = api.keterangan
                        )
                    }

                    _studentAttendances.value = mappedAttendances
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error filtering student attendance: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyTeacherAttendanceFilter(kelasId: Int) {
        loadTeacherAttendances(kelasId, forceRefresh = true)
    }

    fun applyApprovedTeacherPermissionFilter(kelasId: Int) {
        android.util.Log.d("SiswaViewModel", "=== applyApprovedTeacherPermissionFilter CALLED ===")
        android.util.Log.d("SiswaViewModel", "kelasId: $kelasId")

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = authRepository.getToken()
                // Only load approved permissions
                val statusApproval = "disetujui"

                android.util.Log.d("SiswaViewModel", "Loading schedules for class to find teachers...")

                // Load reference data first and wait for completion
                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                if (_classList.value.isEmpty()) {
                    jobs.add(launch { fetchClasses() })
                }
                if (_guruList.value.isEmpty()) {
                    jobs.add(launch { fetchGurus() })
                }

                // Load schedules for this class and wait for completion
                jobs.add(launch { loadSchedules(kelasId) })

                // Wait for all jobs to complete
                jobs.joinAll()

                // Now get all schedules for the class to find associated teachers
                val schedules = _schedules.value.filter { it.kelasId == kelasId }
                android.util.Log.d("SiswaViewModel", "Found ${schedules.size} schedules for class $kelasId")

                // Get teacher IDs from schedules for this class
                val teacherIdsInClass = schedules.map { it.guruId }.distinct()
                android.util.Log.d("SiswaViewModel", "Teachers in class $kelasId: ${teacherIdsInClass.size} teachers")
                android.util.Log.d("SiswaViewModel", "Teacher IDs: $teacherIdsInClass")

                // Load permissions for these teachers with approved status
                android.util.Log.d("SiswaViewModel", "Fetching teacher permissions with status: $statusApproval")
                val response = attendanceRepository.getTeacherPermissions(token, null, null, statusApproval, null, 100)
                if (response.isSuccessful) {
                    val body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission>) ?: emptyList()
                    android.util.Log.d("SiswaViewModel", "Total permissions fetched: ${body.size}")

                    // Filter to only permissions from teachers in this class
                    val filteredPermissions = body.filter { perm ->
                        teacherIdsInClass.contains(perm.guru_id)
                    }
                    android.util.Log.d("SiswaViewModel", "Filtered permissions for class $kelasId: ${filteredPermissions.size} items")

                    // Convert to teacher attendance format for display
                    val mappedPermissions = filteredPermissions.map { api ->
                        TeacherAttendance(
                            id = api.id,
                            jadwalId = -1, // Not associated with specific schedule
                            guruId = api.guru_id,
                            tanggal = api.tanggal_mulai,
                            statusKehadiran = "izin",
                            waktuDatang = null,
                            durasiKeterlambatan = null,
                            keterangan = api.keterangan,
                            guruName = api.guru?.nama,
                            kelasName = _classList.value.find { it.id == kelasId }?.nama,
                            mataPelajaran = null
                        )
                    }

                    // We'll store these in the attendance list for display purposes
                    _attendances.value = filteredPermissions.map { api ->
                        Attendance(
                            id = api.id,
                            siswaId = null,
                            guruId = api.guru_id,
                            jadwalId = -1,
                            tanggal = api.tanggal_mulai,
                            status = "izin",
                            keterangan = api.keterangan,
                            guruName = api.guru?.nama ?: "Guru ${api.guru_id}"
                        )
                    }

                    android.util.Log.d("SiswaViewModel", "Teacher permissions successfully filtered and stored")
                } else {
                    android.util.Log.e("SiswaViewModel", "Failed to fetch permissions: ${response.code()}")
                    _errorMessage.value = "Gagal memuat izin guru: ${response.message()}"
                }
            } catch (e: Exception) {
                android.util.Log.e("SiswaViewModel", "Error filtering teacher permissions", e)
                _errorMessage.value = "Error filtering teacher permissions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyApprovedSubstituteTeacherFilter(kelasId: Int) {
        android.util.Log.d("SiswaViewModel", "=== applyApprovedSubstituteTeacherFilter CALLED ===")
        android.util.Log.d("SiswaViewModel", "kelasId: $kelasId")

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = authRepository.getToken()
                // Load substitute teachers with specific statuses for student view: "Dijadwalkan", "Selesai", "tidak_hadir"
                val validStatuses = listOf("dijadwalkan", "selesai", "tidak_hadir")

                android.util.Log.d("SiswaViewModel", "Loading reference data (classes, teachers)...")

                // Load reference data if needed
                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                if (_classList.value.isEmpty()) {
                    jobs.add(launch { fetchClasses() })
                }
                if (_guruList.value.isEmpty()) {
                    jobs.add(launch { fetchGurus() })
                }
                // Load schedules to potentially enrich data
                jobs.add(launch { loadSchedules(kelasId) })

                jobs.joinAll()

                // Fetch substitute teachers for this class without filtering by status initially
                android.util.Log.d("SiswaViewModel", "Fetching substitute teachers for kelas_id=$kelasId")
                val response = attendanceRepository.getSubstituteTeachers(token, null, kelasId, null, null)

                if (response.isSuccessful) {
                    var body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacher>) ?: emptyList()
                    android.util.Log.d("SiswaViewModel", "Total substitute teachers loaded: ${body.size} items for class $kelasId")

                    // Filter by the valid statuses for student view
                    body = body.filter { apiSub ->
                        val status = apiSub.status_penggantian?.lowercase() ?: ""
                        validStatuses.contains(status)
                    }
                    android.util.Log.d("SiswaViewModel", "Filtered substitute teachers by valid statuses: ${body.size} items")

                    _substitutes.value = body.map { api ->
                        SubstituteTeacher(
                            id = api.id,
                            guruAsliId = api.guru_asli_id,
                            guruPenggantiId = api.guru_pengganti_id,
                            jadwalId = api.jadwal_id,
                            tanggal = api.tanggal,
                            namaGuruAsli = api.guru_asli?.nama ?: "Guru ${api.guru_asli_id}",
                            namaGuruPengganti = api.guru_pengganti?.nama ?: "Guru ${api.guru_pengganti_id}",
                            kelas = api.jadwal?.kelas?.nama ?: "Kelas ${api.jadwal_id}",
                            statusPenggantian = api.status_penggantian ?: "",
                            keterangan = api.keterangan ?: ""
                        )
                    }

                    android.util.Log.d("SiswaViewModel", "Substitute teachers successfully stored: ${_substitutes.value.size} items")
                } else {
                    android.util.Log.e("SiswaViewModel", "Failed to fetch substitute teachers: ${response.code()}")
                    _errorMessage.value = "Gagal memuat guru pengganti: ${response.message()}"
                }
            } catch (e: Exception) {
                android.util.Log.e("SiswaViewModel", "Error filtering substitute teachers", e)
                _errorMessage.value = "Error filtering substitute teachers: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTeacherPermissions(kelasId: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _errorMessage.value = null
            _isLoading.value = true

            // Check cache validity
            val now = System.currentTimeMillis()
            val isCacheValid = (now - _lastTeacherPermissionLoad.value) < CACHE_DURATION

            if (!forceRefresh && isCacheValid && _cachedTeacherPermissions.value != null) {
                _teacherPermissions.value = _cachedTeacherPermissions.value!!
                _isLoading.value = false
                return@launch
            }

            try {
                val token = authRepository.getToken()
                android.util.Log.d("SiswaViewModel", "Loading teacher permissions for kelasId: $kelasId")

                // Load reference data (wait for completion)
                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                if (_classList.value.isEmpty()) {
                    jobs.add(launch { fetchClasses() })
                }
                if (_guruList.value.isEmpty()) {
                    jobs.add(launch { fetchGurus() })
                }
                // Load schedules to determine which teachers are in this class
                jobs.add(launch { loadSchedules(kelasId) })
                jobs.joinAll()

                // Get teacher IDs from schedules for this class to find related permissions
                val schedules = _schedules.value.filter { it.kelasId == kelasId }
                val teacherIdsInClass = schedules.map { it.guruId }.distinct()

                // Load all permissions and filter by teacher IDs in the class with "disetujui" status
                val response = attendanceRepository.getTeacherPermissions(token, null, null, "disetujui", null, 100)
                if (response.isSuccessful) {
                    val body = (response.body()?.data as? List<com.komputerkit.aplikasimonitoringkelas.data.models.TeacherPermission>) ?: emptyList()
                    android.util.Log.d("SiswaViewModel", "Teacher permissions loaded: ${body.size} items")

                    // Filter to only permissions from teachers in this class
                    val filteredPermissions = body.filter { perm ->
                        teacherIdsInClass.contains(perm.guru_id)
                    }

                    val mappedPermissions = filteredPermissions.map { api ->
                        val guru = _guruList.value.find { it.id == api.guru_id }
                        val kelas = _classList.value.find { it.id == kelasId }
                        TeacherPermission(
                            id = api.id,
                            guruId = api.guru_id,
                            tanggalMulai = api.tanggal_mulai,
                            tanggalSelesai = api.tanggal_selesai,
                            durasiHari = api.durasi_hari,
                            jenisIzin = api.jenis_izin,
                            keterangan = api.keterangan,
                            statusApproval = api.status_approval ?: "",
                            approverName = api.disetujui_oleh_user?.name,
                            tanggalApproval = api.tanggal_approval,
                            catatanApproval = api.catatan_approval,
                            guruName = guru?.nama
                        )
                    }

                    _teacherPermissions.value = mappedPermissions
                    _cachedTeacherPermissions.value = mappedPermissions
                    _lastTeacherPermissionLoad.value = now
                } else {
                    _errorMessage.value = "Gagal memuat izin guru: ${response.message()}"
                    android.util.Log.e("SiswaViewModel", "Failed to load teacher permissions: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error memuat izin guru: ${e.message}"
                android.util.Log.e("SiswaViewModel", "Error loading teacher permissions", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSubstituteStatus(
        substituteId: Int,
        status: String,
        catatanApproval: String? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val request = com.komputerkit.aplikasimonitoringkelas.data.models.SubstituteTeacherUpdateRequest(
                    status_penggantian = status,
                    catatan_approval = catatanApproval,
                    disetujui_oleh = null
                )
                val response = attendanceRepository.updateSubstituteTeacher(token, substituteId, request)
                if (response.isSuccessful) {
                    // Set success message for notification
                    _infoMessage.value = "Status guru pengganti berhasil diperbarui menjadi '$status'"
                    // Reload substitutes after successful update
                    _userKelasId.value?.let { loadSubstitutes(it) }
                    onSuccess()
                } else {
                    val errorBody = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { response.message() }
                    val errorMsg = "Gagal mengubah status: $errorBody"
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Terjadi kesalahan: ${e.message}"
                _errorMessage.value = errorMsg
                onError(errorMsg)
            }
        }
    }

    fun addAttendance(guruId: Int, kelasId: Int, tanggal: String, status: String, keterangan: String? = null) {
        viewModelScope.launch {
            val newAttendance = Attendance(
                id = _attendances.value.size + 1,
                siswaId = null,
                guruId = guruId,
                jadwalId = kelasId,
                tanggal = tanggal,
                status = status,
                keterangan = keterangan
            )
            _attendances.value = _attendances.value + newAttendance
        }
    }

    fun editAttendance(attendanceId: Int, status: String, keterangan: String? = null) {
        viewModelScope.launch {
            val updatedAttendances = _attendances.value.map { attendance ->
                if (attendance.id == attendanceId) {
                    attendance.copy(status = status, keterangan = keterangan)
                } else {
                    attendance
                }
            }
            _attendances.value = updatedAttendances
        }
    }

    fun markStudentAbsent(studentId: Int, kelasId: Int, tanggal: String, keterangan: String? = null) {
        viewModelScope.launch {
            val newStudentAttendance = StudentAttendance(
                id = _studentAttendances.value.size + 1,
                siswaId = studentId,
                jadwalId = kelasId,
                tanggal = tanggal,
                status = "tidak_hadir",
                keterangan = keterangan
            )
            _studentAttendances.value = _studentAttendances.value + newStudentAttendance
        }
    }

    // ==================== SUBMIT DATA METHODS ====================

    fun submitStudentAttendance(
        siswaId: Int,
        jadwalId: Int,
        tanggal: String,
        statusKehadiran: String,
        keterangan: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        android.util.Log.d("SiswaViewModel", "=== submitStudentAttendance CALLED ===")
        android.util.Log.d("SiswaViewModel", "siswaId: $siswaId, jadwalId: $jadwalId, tanggal: $tanggal, status: '$statusKehadiran'")
        android.util.Log.d("SiswaViewModel", "Status details - length: ${statusKehadiran.length}, isEmpty: ${statusKehadiran.isEmpty()}, value: [$statusKehadiran]")
        
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val userId = authRepository.getUserId()
                android.util.Log.d("SiswaViewModel", "Token obtained: ${token.take(20)}..., User ID: $userId")
                
                val request = com.komputerkit.aplikasimonitoringkelas.data.models.StudentAttendanceRequest(
                    siswa_id = siswaId,
                    jadwal_id = jadwalId,
                    tanggal = tanggal,
                    status = statusKehadiran,
                    keterangan = keterangan,
                    diinput_oleh = userId
                )
                
                android.util.Log.d("SiswaViewModel", "Request object: $request")
                android.util.Log.d("SiswaViewModel", "Request.status: '${request.status}' (length: ${request.status.length})")
                
                val response = attendanceRepository.createStudentAttendance(token, request)
                android.util.Log.d("SiswaViewModel", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    android.util.Log.d("SiswaViewModel", "Student attendance submitted successfully")
                    // Reload student attendances after successful submission with force refresh
                    _userKelasId.value?.let { loadStudentAttendances(it, forceRefresh = true) }
                    onSuccess()
                } else {
                    val errorBody = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { response.message() }
                    android.util.Log.e("SiswaViewModel", "Failed to submit: $errorBody")
                    onError("Gagal menyimpan kehadiran siswa: $errorBody")
                }
            } catch (e: Exception) {
                android.util.Log.e("SiswaViewModel", "Error submitting student attendance", e)
                onError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun submitTeacherAttendance(
        guruId: Int,
        jadwalId: Int,
        tanggal: String,
        statusKehadiran: String,
        keterangan: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        android.util.Log.d("SiswaViewModel", "=== submitTeacherAttendance CALLED ===")
        android.util.Log.d("SiswaViewModel", "guruId: $guruId, jadwalId: $jadwalId, tanggal: $tanggal, status: '$statusKehadiran'")
        android.util.Log.d("SiswaViewModel", "Status details - length: ${statusKehadiran.length}, isEmpty: ${statusKehadiran.isEmpty()}, value: [$statusKehadiran]")
        
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val userId = authRepository.getUserId()
                android.util.Log.d("SiswaViewModel", "Token obtained: ${token.take(20)}..., User ID: $userId")
                
                // Get current time for waktu_datang if status is 'hadir' or 'telat'
                val waktuDatang = if (statusKehadiran == "hadir" || statusKehadiran == "telat") {
                    val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    timeFormat.format(java.util.Date())
                } else null
                
                val request = com.komputerkit.aplikasimonitoringkelas.data.models.TeacherAttendanceRequest(
                    guru_id = guruId,
                    jadwal_id = jadwalId,
                    tanggal = tanggal,
                    status_kehadiran = statusKehadiran,
                    waktu_datang = waktuDatang,
                    durasi_keterlambatan = null,
                    keterangan = keterangan,
                    diinput_oleh = userId
                )
                
                android.util.Log.d("SiswaViewModel", "Request object: $request")
                android.util.Log.d("SiswaViewModel", "Request.status_kehadiran: '${request.status_kehadiran}' (length: ${request.status_kehadiran.length})")
                
                val response = attendanceRepository.createTeacherAttendance(token, request)
                android.util.Log.d("SiswaViewModel", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    android.util.Log.d("SiswaViewModel", "Teacher attendance submitted successfully")
                    // Reload teacher attendances after successful submission with force refresh
                    _userKelasId.value?.let { loadTeacherAttendances(it, forceRefresh = true) }
                    onSuccess()
                } else {
                    val errorBody = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { response.message() }
                    android.util.Log.e("SiswaViewModel", "Failed to submit: $errorBody")
                    onError("Gagal menyimpan kehadiran guru: $errorBody")
                }
            } catch (e: Exception) {
                android.util.Log.e("SiswaViewModel", "Error submitting teacher attendance", e)
                onError("Terjadi kesalahan: ${e.message}")
            }
        }
    }


}