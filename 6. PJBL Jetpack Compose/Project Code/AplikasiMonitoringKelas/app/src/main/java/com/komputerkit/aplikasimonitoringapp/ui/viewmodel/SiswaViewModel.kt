package com.komputerkit.aplikasimonitoringapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringapp.data.local.AppDatabase
import com.komputerkit.aplikasimonitoringapp.data.local.entity.SiswaEntity
import com.komputerkit.aplikasimonitoringapp.data.repository.SiswaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola data Siswa
 * Contoh implementasi dengan Room Database
 */
class SiswaViewModel(context: Context) : ViewModel() {
    
    private val repository: SiswaRepository
    
    private val _siswaList = MutableStateFlow<List<SiswaEntity>>(emptyList())
    val siswaList: StateFlow<List<SiswaEntity>> = _siswaList.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        val database = AppDatabase.getDatabase(context)
        repository = SiswaRepository(database.siswaDao())
        
        // Load data dari database
        loadSiswa()
    }
    
    /**
     * Load semua siswa dari database
     */
    private fun loadSiswa() {
        viewModelScope.launch {
            repository.getAllSiswa().collect { siswaList ->
                _siswaList.value = siswaList
            }
        }
    }
    
    /**
     * Tambah siswa baru
     */
    fun addSiswa(siswa: SiswaEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertSiswa(siswa)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambah siswa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update data siswa
     */
    fun updateSiswa(siswa: SiswaEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateSiswa(siswa)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal update siswa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Hapus siswa
     */
    fun deleteSiswa(siswa: SiswaEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteSiswa(siswa)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus siswa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Search siswa
     */
    fun searchSiswa(query: String) {
        viewModelScope.launch {
            repository.searchSiswa(query).collect { siswaList ->
                _siswaList.value = siswaList
            }
        }
    }
    
    /**
     * Get siswa by kelas
     */
    fun getSiswaByKelas(kelasId: Int) {
        viewModelScope.launch {
            repository.getSiswaByKelas(kelasId).collect { siswaList ->
                _siswaList.value = siswaList
            }
        }
    }
    
    /**
     * Sync data dari API ke database
     * Panggil ini setelah fetch dari API
     */
    fun syncFromApi(siswaListFromApi: List<SiswaEntity>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertAllSiswa(siswaListFromApi)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal sync data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
