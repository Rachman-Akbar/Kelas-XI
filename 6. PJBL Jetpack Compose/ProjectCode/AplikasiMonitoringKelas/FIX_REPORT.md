# Laporan Perbaikan Android

## Perbaikan utama

- Base URL API tidak lagi bergantung pada IP development yang ditanam langsung di source.
- Retrofit/OkHttp menggunakan satu konfigurasi API aktif.
- Logging HTTP sensitif dibatasi pada build debug dan header Authorization disamarkan.
- Repository networking lama yang tumpang tindih dan tidak dipakai telah dihapus.
- Penyimpanan sesi menggunakan DataStore sebagai sumber token dan identitas user.
- Login menolak respons sukses yang tidak memiliki token.
- Logout tetap membersihkan sesi lokal apabila server tidak dapat dihubungi.
- Token kosong ditolak sebelum repository fitur membuat request berautentikasi.
- Pagination daftar presensi/izin/guru pengganti dinaikkan sampai 100 item untuk menghindari data layar terpotong pada penggunaan normal.
- Kontrak nested kelas pada jadwal diselaraskan dengan payload backend minimal.
- Gradle wrapper dinormalisasi ke line ending Unix agar dapat dijalankan di Linux/macOS/CI.
- Tidak ada perubahan yang disengaja pada file UI/Compose visual.

## Verifikasi

Percobaan `./gradlew testDebugUnitTest` telah dilakukan. Gradle wrapper dapat dijalankan setelah normalisasi, tetapi environment audit tidak dapat mengunduh distribusi Gradle dari `services.gradle.org` karena akses jaringan keluar tidak tersedia. Karena itu kompilasi Android penuh harus dijalankan kembali pada mesin pengembangan yang memiliki Android SDK dan akses dependency repository.
