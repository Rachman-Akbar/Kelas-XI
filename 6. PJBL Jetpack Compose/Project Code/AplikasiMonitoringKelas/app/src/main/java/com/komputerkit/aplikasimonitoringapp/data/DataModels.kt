package com.komputerkit.aplikasimonitoringapp.data

// Temporary data models untuk kompatibilitas dengan file Activity lama
// TODO: Nanti akan di-replace dengan data dari API

data class JadwalItem(
    val namaGuru: String,
    val pelajaran: String,
    val kelas: String,
    val jam: String = "08:00 - 09:30",
    val jamKe: String = "1-3",
    val hari: String = "Senin"
)

enum class Hari(val displayName: String) {
    SENIN("Senin"),
    SELASA("Selasa"),
    RABU("Rabu"),
    KAMIS("Kamis"),
    JUMAT("Jumat"),
    SABTU("Sabtu")
}

enum class Kelas(val displayName: String) {
    X_RPL("X RPL"),
    XI_RPL("XI RPL"),
    XII_RPL("XII RPL")
}

enum class MataPelajaran(val displayName: String) {
    IPA("IPA"),
    IPS("IPS"),
    BAHASA("Bahasa"),
    MATEMATIKA("Matematika"),
    BAHASA_INDONESIA("Bahasa Indonesia"),
    BAHASA_INGGRIS("Bahasa Inggris"),
    FISIKA("Fisika"),
    KIMIA("Kimia"),
    BIOLOGI("Biologi")
}

enum class NamaGuru(val displayName: String) {
    AHMAD("Drs. Ahmad Supriyanto"),
    SITI("Siti Nurhaliza, S.Pd"),
    BUDI("Dr. Budi Santoso"),
    RINA("Rina Kartika, M.Pd")
}

data class UserEntry(
    val nama: String,
    val email: String,
    val role: UserRole
)

enum class UserRole(val displayName: String) {
    ADMIN("Admin"),
    KEPALA_SEKOLAH("Kepala Sekolah"),
    KURIKULUM("Kurikulum"),
    SISWA("Siswa")
}

data class JadwalEntry(
    val hari: Hari,
    val kelas: Kelas,
    val mataPelajaran: MataPelajaran,
    val guru: NamaGuru,
    val jamKe: String
)

// Login state untuk UI lama
data class LoginState(
    val email: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.SISWA,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
