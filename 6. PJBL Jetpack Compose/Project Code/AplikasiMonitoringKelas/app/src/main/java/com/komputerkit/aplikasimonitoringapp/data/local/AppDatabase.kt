package com.komputerkit.aplikasimonitoringapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.komputerkit.aplikasimonitoringapp.data.local.dao.*
import com.komputerkit.aplikasimonitoringapp.data.local.entity.*

/**
 * Room Database untuk Aplikasi Monitoring Kelas
 */
@Database(
    entities = [
        SiswaEntity::class,
        GuruEntity::class,
        KelasEntity::class,
        MataPelajaranEntity::class,
        JadwalEntity::class,
        KehadiranEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun siswaDao(): SiswaDao
    abstract fun guruDao(): GuruDao
    abstract fun kelasDao(): KelasDao
    abstract fun mataPelajaranDao(): MataPelajaranDao
    abstract fun jadwalDao(): JadwalDao
    abstract fun kehadiranDao(): KehadiranDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "monitoring_kelas_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
