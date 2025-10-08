package com.komputerkit.aplikasimonitoringapp.data.local.dao

import androidx.room.*
import com.komputerkit.aplikasimonitoringapp.data.local.entity.SiswaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk tabel Siswa
 */
@Dao
interface SiswaDao {
    
    @Query("SELECT * FROM siswa ORDER BY nama ASC")
    fun getAllSiswa(): Flow<List<SiswaEntity>>
    
    @Query("SELECT * FROM siswa WHERE id = :id")
    suspend fun getSiswaById(id: Int): SiswaEntity?
    
    @Query("SELECT * FROM siswa WHERE kelas_id = :kelasId ORDER BY nama ASC")
    fun getSiswaByKelas(kelasId: Int): Flow<List<SiswaEntity>>
    
    @Query("SELECT * FROM siswa WHERE nama LIKE '%' || :query || '%' OR nis LIKE '%' || :query || '%'")
    fun searchSiswa(query: String): Flow<List<SiswaEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiswa(siswa: SiswaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSiswa(siswaList: List<SiswaEntity>)
    
    @Update
    suspend fun updateSiswa(siswa: SiswaEntity)
    
    @Delete
    suspend fun deleteSiswa(siswa: SiswaEntity)
    
    @Query("DELETE FROM siswa")
    suspend fun deleteAllSiswa()
}
