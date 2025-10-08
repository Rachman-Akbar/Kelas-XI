package com.komputerkit.aplikasimonitoringapp.data.repository

import com.komputerkit.aplikasimonitoringapp.data.local.dao.SiswaDao
import com.komputerkit.aplikasimonitoringapp.data.local.entity.SiswaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk mengelola data Siswa
 * Menghubungkan antara API dan Local Database
 */
class SiswaRepository(private val siswaDao: SiswaDao) {
    
    // Get all siswa from local database
    fun getAllSiswa(): Flow<List<SiswaEntity>> = siswaDao.getAllSiswa()
    
    // Get siswa by ID
    suspend fun getSiswaById(id: Int): SiswaEntity? = siswaDao.getSiswaById(id)
    
    // Get siswa by kelas
    fun getSiswaByKelas(kelasId: Int): Flow<List<SiswaEntity>> = siswaDao.getSiswaByKelas(kelasId)
    
    // Search siswa
    fun searchSiswa(query: String): Flow<List<SiswaEntity>> = siswaDao.searchSiswa(query)
    
    // Insert single siswa
    suspend fun insertSiswa(siswa: SiswaEntity) = siswaDao.insertSiswa(siswa)
    
    // Insert multiple siswa (for syncing from API)
    suspend fun insertAllSiswa(siswaList: List<SiswaEntity>) = siswaDao.insertAllSiswa(siswaList)
    
    // Update siswa
    suspend fun updateSiswa(siswa: SiswaEntity) = siswaDao.updateSiswa(siswa)
    
    // Delete siswa
    suspend fun deleteSiswa(siswa: SiswaEntity) = siswaDao.deleteSiswa(siswa)
    
    // Delete all siswa (for fresh sync)
    suspend fun deleteAllSiswa() = siswaDao.deleteAllSiswa()
}
