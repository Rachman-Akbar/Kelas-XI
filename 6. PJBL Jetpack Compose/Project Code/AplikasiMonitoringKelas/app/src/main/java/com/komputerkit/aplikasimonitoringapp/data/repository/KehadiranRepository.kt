package com.komputerkit.aplikasimonitoringapp.data.repository

import com.komputerkit.aplikasimonitoringapp.data.local.dao.KehadiranDao
import com.komputerkit.aplikasimonitoringapp.data.local.entity.KehadiranEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk mengelola data Kehadiran
 */
class KehadiranRepository(private val kehadiranDao: KehadiranDao) {
    
    fun getAllKehadiran(): Flow<List<KehadiranEntity>> = kehadiranDao.getAllKehadiran()
    
    suspend fun getKehadiranById(id: Int): KehadiranEntity? = kehadiranDao.getKehadiranById(id)
    
    fun getKehadiranBySiswa(siswaId: Int): Flow<List<KehadiranEntity>> = kehadiranDao.getKehadiranBySiswa(siswaId)
    
    fun getKehadiranByJadwal(jadwalId: Int): Flow<List<KehadiranEntity>> = kehadiranDao.getKehadiranByJadwal(jadwalId)
    
    fun getKehadiranByTanggal(tanggal: String): Flow<List<KehadiranEntity>> = kehadiranDao.getKehadiranByTanggal(tanggal)
    
    fun getKehadiranByPeriode(siswaId: Int, startDate: String, endDate: String): Flow<List<KehadiranEntity>> = 
        kehadiranDao.getKehadiranByPeriode(siswaId, startDate, endDate)
    
    suspend fun insertKehadiran(kehadiran: KehadiranEntity) = kehadiranDao.insertKehadiran(kehadiran)
    
    suspend fun insertAllKehadiran(kehadiranList: List<KehadiranEntity>) = kehadiranDao.insertAllKehadiran(kehadiranList)
    
    suspend fun updateKehadiran(kehadiran: KehadiranEntity) = kehadiranDao.updateKehadiran(kehadiran)
    
    suspend fun deleteKehadiran(kehadiran: KehadiranEntity) = kehadiranDao.deleteKehadiran(kehadiran)
    
    suspend fun deleteAllKehadiran() = kehadiranDao.deleteAllKehadiran()
}
