package com.komputerkit.aplikasimonitoringapp.data.local.dao

import androidx.room.*
import com.komputerkit.aplikasimonitoringapp.data.local.entity.KelasEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk tabel Kelas
 */
@Dao
interface KelasDao {
    
    @Query("SELECT * FROM kelas ORDER BY tingkat_kelas ASC, nama_kelas ASC")
    fun getAllKelas(): Flow<List<KelasEntity>>
    
    @Query("SELECT * FROM kelas WHERE id = :id")
    suspend fun getKelasById(id: Int): KelasEntity?
    
    @Query("SELECT * FROM kelas WHERE tingkat_kelas = :tingkat")
    fun getKelasByTingkat(tingkat: String): Flow<List<KelasEntity>>
    
    @Query("SELECT * FROM kelas WHERE nama_kelas LIKE '%' || :query || '%'")
    fun searchKelas(query: String): Flow<List<KelasEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKelas(kelas: KelasEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKelas(kelasList: List<KelasEntity>)
    
    @Update
    suspend fun updateKelas(kelas: KelasEntity)
    
    @Delete
    suspend fun deleteKelas(kelas: KelasEntity)
    
    @Query("DELETE FROM kelas")
    suspend fun deleteAllKelas()
}
