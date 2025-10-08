package com.komputerkit.aplikasimonitoringapp.data.repository

import com.komputerkit.aplikasimonitoringapp.data.local.dao.JadwalDao
import com.komputerkit.aplikasimonitoringapp.data.local.entity.JadwalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk mengelola data Jadwal
 */
class JadwalRepository(private val jadwalDao: JadwalDao) {
    
    fun getAllJadwal(): Flow<List<JadwalEntity>> = jadwalDao.getAllJadwal()
    
    suspend fun getJadwalById(id: Int): JadwalEntity? = jadwalDao.getJadwalById(id)
    
    fun getJadwalByKelas(kelasId: Int): Flow<List<JadwalEntity>> = jadwalDao.getJadwalByKelas(kelasId)
    
    fun getJadwalByGuru(guruId: Int): Flow<List<JadwalEntity>> = jadwalDao.getJadwalByGuru(guruId)
    
    fun getJadwalByHari(hari: String): Flow<List<JadwalEntity>> = jadwalDao.getJadwalByHari(hari)
    
    suspend fun insertJadwal(jadwal: JadwalEntity) = jadwalDao.insertJadwal(jadwal)
    
    suspend fun insertAllJadwal(jadwalList: List<JadwalEntity>) = jadwalDao.insertAllJadwal(jadwalList)
    
    suspend fun updateJadwal(jadwal: JadwalEntity) = jadwalDao.updateJadwal(jadwal)
    
    suspend fun deleteJadwal(jadwal: JadwalEntity) = jadwalDao.deleteJadwal(jadwal)
    
    suspend fun deleteAllJadwal() = jadwalDao.deleteAllJadwal()
}
