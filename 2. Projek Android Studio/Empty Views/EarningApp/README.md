# Earning App - Android Application

Aplikasi Android "Earning App" yang dibuat dengan fokus pada desain front-end menggunakan Kotlin dan XML.

## 📱 Fitur Aplikasi

### 1. Splash Screen

- Tampilan awal aplikasi dengan logo dan animasi loading
- Durasi 3 detik sebelum berpindah ke halaman pendaftaran
- Menggunakan tema biru dengan logo aplikasi yang menarik

### 2. Halaman Pendaftaran

- Form registrasi dengan field:
  - Nama Lengkap
  - Email
  - Nomor Telepon
- Validasi input sebelum melanjutkan ke halaman utama
- Desain menggunakan Material Design Components

### 3. Halaman Utama (MainActivity)

- Menampilkan saldo koin pengguna
- Kategori menu dengan card-based design:
  - **Spin the Wheel** - Permainan untuk mendapatkan koin
  - **Profil Saya** - Kelola informasi profil
  - **Penarikan Dana** - Tukar koin dengan uang

### 4. Permainan Spin the Wheel

- Roda interaktif dengan 5 segmen hadiah (5, 10, 20, 50, 100 koin)
- Animasi rotasi dengan efek decelerate
- Sistem reward random berdasarkan posisi roda
- Update saldo koin secara real-time

### 5. Halaman Profil

- Edit informasi profil pengguna
- Field yang dapat diubah:
  - Nama Lengkap
  - Email
  - Nomor Telepon
- Foto profil dengan background circular

### 6. Halaman Penarikan Dana

- Tampilan saldo koin saat ini
- Form penarikan dengan validasi:
  - Minimum penarikan 1000 koin
  - Pilihan metode pembayaran (Bank Transfer, E-Wallet)
  - Input nomor rekening/akun
- Kurs konversi: 100 koin = Rp 1

## 🎨 Desain dan Tema

### Warna Kustom (colors.xml)

- **Primary Colors**: Biru (#2196F3, #1976D2)
- **Accent Colors**: Hijau, Orange, Merah, Ungu
- **Background**: Light grey (#F5F5F5)
- **Text Colors**: Dark grey, Medium grey, Light grey
- **Gold**: Untuk elemen koin (#FFD700)

### Ikon dan Drawable

- **ic_app_logo.xml** - Logo aplikasi dengan desain koin
- **ic_coin.xml** - Ikon koin untuk UI
- **ic_profile.xml** - Ikon profil pengguna
- **ic_withdraw.xml** - Ikon penarikan dana
- **ic_spin_wheel.xml** - Ikon roda putar
- **wheel_background.xml** - Desain roda dengan 5 segmen warna
- **wheel_pointer.xml** - Pointer untuk roda
- **circle_background.xml** - Background circular untuk foto profil
- **spinner_background.xml** - Background untuk dropdown spinner

## 🏗️ Struktur Aplikasi

### Activities

1. **SplashActivity** - Halaman splash dengan auto-redirect
2. **RegisterActivity** - Pendaftaran pengguna baru
3. **MainActivity** - Dashboard utama dengan kategori menu
4. **SpinWheelActivity** - Permainan roda putar
5. **ProfileActivity** - Kelola profil pengguna
6. **WithdrawActivity** - Penarikan dana

### Layout Files

- `activity_splash.xml` - Layout splash screen
- `activity_register.xml` - Layout form pendaftaran
- `activity_main.xml` - Layout dashboard utama
- `activity_spin_wheel.xml` - Layout permainan roda
- `activity_profile.xml` - Layout profil pengguna
- `activity_withdraw.xml` - Layout penarikan dana

## 🚀 Cara Menjalankan

1. Buka project di Android Studio
2. Sync Gradle files
3. Jalankan aplikasi di emulator atau device fisik
4. Aplikasi akan memulai dari Splash Screen

## 📋 Flow Aplikasi

```
Splash Screen (3s)
      ↓
Registration Form
      ↓
Main Dashboard
   ↓   ↓   ↓
Spin  Profile  Withdraw
Wheel   Edit     Funds
```

## 🛠️ Teknologi yang Digunakan

- **Bahasa**: Kotlin
- **UI**: XML dengan Material Design Components
- **Animasi**: ObjectAnimator untuk roda putar
- **Navigation**: Intent-based navigation
- **Theme**: Material Theme dengan custom colors

## 📝 Catatan Pengembangan

- Semua string text disimpan dalam `strings.xml` untuk kemudahan lokalisasi
- Menggunakan CardView untuk konsistensi desain
- Implementasi validasi input pada semua form
- Responsive design yang compatible dengan berbagai ukuran layar
- Splash screen diatur sebagai launcher activity di AndroidManifest.xml

## 🎯 Future Enhancements

- Integrasi dengan Firebase untuk penyimpanan data
- Implementasi sistem autentikasi
- Notifikasi push untuk reward harian
- Animasi transisi antar halaman
- Mode dark theme
