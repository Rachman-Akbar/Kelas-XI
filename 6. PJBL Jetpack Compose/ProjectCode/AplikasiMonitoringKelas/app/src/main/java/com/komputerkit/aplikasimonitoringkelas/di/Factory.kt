package com.komputerkit.aplikasimonitoringkelas.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository
import com.komputerkit.aplikasimonitoringkelas.data.repository.AttendanceRepository
import com.komputerkit.aplikasimonitoringkelas.kepsek.KepsekViewModel
import com.komputerkit.aplikasimonitoringkelas.guru.GuruViewModel
import com.komputerkit.aplikasimonitoringkelas.siswa.SiswaViewModel
import com.komputerkit.aplikasimonitoringkelas.kurikulum.KurikulumViewModel

object ServiceLocator {
    fun provideAuthRepository(context: Context): AuthRepository {
        return AuthRepository(context)
    }
    
    fun provideAttendanceRepository(): AttendanceRepository {
        return AttendanceRepository()
    }
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(KepsekViewModel::class.java) -> {
                KepsekViewModel(attendanceRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(GuruViewModel::class.java) -> {
                com.komputerkit.aplikasimonitoringkelas.guru.RealGuruViewModel(attendanceRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(SiswaViewModel::class.java) -> {
                SiswaViewModel(attendanceRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(KurikulumViewModel::class.java) -> {
                KurikulumViewModel(attendanceRepository, authRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}