package com.komputerkit.aplikasimonitoringapp.data.local.dao

import androidx.room.*
import com.komputerkit.aplikasimonitoringapp.data.local.entity.KehadiranEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk tabel Kehadiran
 */
@Dao
interface KehadiranDao {
    
    @Query("SELECT * FROM kehadiran ORDER BY tanggal DESC")
    fun getAllKehadiran(): Flow<List<KehadiranEntity>>
    
    @Query("SELECT * FROM kehadiran WHERE id = :id")
    suspend fun getKehadiranById(id: Int): KehadiranEntity?
    
    @Query("SELECT * FROM kehadiran WHERE siswa_id = :siswaId ORDER BY tanggal DESC")
    fun getKehadiranBySiswa(siswaId: Int): Flow<List<KehadiranEntity>>
    
    @Query("SELECT * FROM kehadiran WHERE jadwal_id = :jadwalId ORDER BY tanggal DESC")
    fun getKehadiranByJadwal(jadwalId: Int): Flow<List<KehadiranEntity>>
    
    @Query("SELECT * FROM kehadiran WHERE tanggal = :tanggal ORDER BY siswa_id ASC")
    fun getKehadiranByTanggal(tanggal: String): Flow<List<KehadiranEntity>>
    
    @Query("SELECT * FROM kehadiran WHERE siswa_id = :siswaId AND tanggal BETWEEN :startDate AND :endDate ORDER BY tanggal DESC")
    fun getKehadiranByPeriode(siswaId: Int, startDate: String, endDate: String): Flow<List<KehadiranEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKehadiran(kehadiran: KehadiranEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKehadiran(kehadiranList: List<KehadiranEntity>)
    
    @Update
    suspend fun updateKehadiran(kehadiran: KehadiranEntity)
    
    @Delete
    suspend fun deleteKehadiran(kehadiran: KehadiranEntity)
    
    @Query("DELETE FROM kehadiran")
    suspend fun deleteAllKehadiran()
}
