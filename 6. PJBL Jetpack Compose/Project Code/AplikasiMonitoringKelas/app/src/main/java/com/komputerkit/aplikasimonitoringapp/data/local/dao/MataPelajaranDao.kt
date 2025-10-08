package com.komputerkit.aplikasimonitoringapp.data.local.dao

import androidx.room.*
import com.komputerkit.aplikasimonitoringapp.data.local.entity.MataPelajaranEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk tabel Mata Pelajaran
 */
@Dao
interface MataPelajaranDao {
    
    @Query("SELECT * FROM mata_pelajaran ORDER BY nama_mata_pelajaran ASC")
    fun getAllMataPelajaran(): Flow<List<MataPelajaranEntity>>
    
    @Query("SELECT * FROM mata_pelajaran WHERE id = :id")
    suspend fun getMataPelajaranById(id: Int): MataPelajaranEntity?
    
    @Query("SELECT * FROM mata_pelajaran WHERE nama_mata_pelajaran LIKE '%' || :query || '%' OR kode LIKE '%' || :query || '%'")
    fun searchMataPelajaran(query: String): Flow<List<MataPelajaranEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMataPelajaran(mataPelajaran: MataPelajaranEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMataPelajaran(mataPelajaranList: List<MataPelajaranEntity>)
    
    @Update
    suspend fun updateMataPelajaran(mataPelajaran: MataPelajaranEntity)
    
    @Delete
    suspend fun deleteMataPelajaran(mataPelajaran: MataPelajaranEntity)
    
    @Query("DELETE FROM mata_pelajaran")
    suspend fun deleteAllMataPelajaran()
}
