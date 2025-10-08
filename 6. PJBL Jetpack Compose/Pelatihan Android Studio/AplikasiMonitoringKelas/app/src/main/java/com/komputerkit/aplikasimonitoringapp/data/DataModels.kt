package com.komputerkit.aplikasimonitoringapp.data

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
    SABTU("Sabtu"),
    MINGGU("Minggu")
}

enum class Kelas(val displayName: String) {
    X_RPL("X RPL"),
    XI_RPL("XI RPL"),
    XII_RPL("XII RPL")
}

enum class MataPelajaran(val displayName: String) {
    IPA("IPA"),
    IPS("IPS"),
    BAHASA("Bahasa")
}

enum class NamaGuru(val displayName: String) {
    SITI("Siti"),
    BUDI("Budi"), 
    ADI("Adi"),
    AGUS("Agus")
}

data class UserEntry(
    val nama: String,
    val email: String,
    val role: UserRole
)

data class JadwalEntry(
    val hari: Hari,
    val kelas: Kelas,
    val mataPelajaran: MataPelajaran,
    val guru: NamaGuru,
    val jamKe: String
)

enum class UserRole(val displayName: String) {
    SISWA("Siswa"),
    KURIKULUM("Kurikulum"),
    KEPALA_SEKOLAH("Kepala Sekolah"),
    ADMIN("Admin")
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.SISWA,
    val isLoggedIn: Boolean = false
)