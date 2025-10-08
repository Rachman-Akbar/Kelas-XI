package com.komputerkit.aplikasimonitoringapp.data.local.dao

import androidx.room.*
import com.komputerkit.aplikasimonitoringapp.data.local.entity.JadwalEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk tabel Jadwal
 */
@Dao
interface JadwalDao {
    
    @Query("SELECT * FROM jadwal ORDER BY hari ASC, jam_mulai ASC")
    fun getAllJadwal(): Flow<List<JadwalEntity>>
    
    @Query("SELECT * FROM jadwal WHERE id = :id")
    suspend fun getJadwalById(id: Int): JadwalEntity?
    
    @Query("SELECT * FROM jadwal WHERE kelas_id = :kelasId ORDER BY hari ASC, jam_mulai ASC")
    fun getJadwalByKelas(kelasId: Int): Flow<List<JadwalEntity>>
    
    @Query("SELECT * FROM jadwal WHERE guru_id = :guruId ORDER BY hari ASC, jam_mulai ASC")
    fun getJadwalByGuru(guruId: Int): Flow<List<JadwalEntity>>
    
    @Query("SELECT * FROM jadwal WHERE hari = :hari ORDER BY jam_mulai ASC")
    fun getJadwalByHari(hari: String): Flow<List<JadwalEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJadwal(jadwal: JadwalEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllJadwal(jadwalList: List<JadwalEntity>)
    
    @Update
    suspend fun updateJadwal(jadwal: JadwalEntity)
    
    @Delete
    suspend fun deleteJadwal(jadwal: JadwalEntity)
    
    @Query("DELETE FROM jadwal")
    suspend fun deleteAllJadwal()
}
