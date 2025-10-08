package com.komputerkit.aplikasimonitoringapp.data.local.dao

import androidx.room.*
import com.komputerkit.aplikasimonitoringapp.data.local.entity.GuruEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk tabel Guru
 */
@Dao
interface GuruDao {
    
    @Query("SELECT * FROM guru ORDER BY nama ASC")
    fun getAllGuru(): Flow<List<GuruEntity>>
    
    @Query("SELECT * FROM guru WHERE id = :id")
    suspend fun getGuruById(id: Int): GuruEntity?
    
    @Query("SELECT * FROM guru WHERE mata_pelajaran_id = :mataPelajaranId")
    fun getGuruByMataPelajaran(mataPelajaranId: Int): Flow<List<GuruEntity>>
    
    @Query("SELECT * FROM guru WHERE nama LIKE '%' || :query || '%' OR nip LIKE '%' || :query || '%'")
    fun searchGuru(query: String): Flow<List<GuruEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuru(guru: GuruEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGuru(guruList: List<GuruEntity>)
    
    @Update
    suspend fun updateGuru(guru: GuruEntity)
    
    @Delete
    suspend fun deleteGuru(guru: GuruEntity)
    
    @Query("DELETE FROM guru")
    suspend fun deleteAllGuru()
}
