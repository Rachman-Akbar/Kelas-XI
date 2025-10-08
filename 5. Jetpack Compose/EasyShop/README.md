# EasyShop - Aplikasi E-Commerce Android

Aplikasi e-commerce modern yang dibangun menggunakan **Jetpack Compose** dan **Firebase** untuk sistem autentikasi dan penyimpanan data.

## Fitur Utama

### ✅ Fitur yang Sudah Diimplementasi

1. **Sistem Autentikasi**

   - Sign Up dengan validasi input lengkap
   - Login dengan Firebase Authentication
   - Logout
   - Penanganan error yang user-friendly

2. **Validasi Input**

   - Validasi email format
   - Validasi password minimal 6 karakter
   - Validasi nama minimal 2 karakter
   - Validasi nomor telepon
   - Validasi konfirmasi password

3. **Navigasi**

   - Navigation Compose untuk perpindahan antar screen
   - Auto-redirect berdasarkan status autentikasi
   - Proper back stack management

4. **UI/UX**

   - Material Design 3
   - Loading states dengan indikator
   - Error handling dengan pesan yang jelas
   - Responsive design

5. **Keamanan**
   - Password disembunyikan dengan toggle visibility
   - Validasi client-side sebelum Firebase call
   - Exception handling yang robust

## Teknologi yang Digunakan

- **Android**: API 24+ (Android 7.0)
- **Jetpack Compose**: UI toolkit modern
- **Firebase Authentication**: Sistem login/register
- **Firebase Firestore**: Database NoSQL
- **Navigation Compose**: Navigasi antar screen
- **Kotlin Coroutines**: Asynchronous programming
- **StateFlow**: State management
- **Material Design 3**: Design system

## Struktur Proyek

```
app/src/main/java/com/komputerkit/easyshop/
├── data/
│   ├── model/
│   │   ├── User.kt
│   │   └── ValidationResult.kt
│   └── repository/
│       └── AuthRepository.kt
├── di/
│   └── AppModule.kt
├── navigation/
│   └── AppNavigation.kt
├── ui/
│   ├── components/
│   │   ├── CustomTextField.kt
│   │   └── LoadingButton.kt
│   ├── screens/
│   │   ├── auth/
│   │   │   ├── LoginScreen.kt
│   │   │   └── SignUpScreen.kt
│   │   └── HomeScreen.kt
│   ├── viewmodel/
│   │   └── AuthViewModel.kt
│   └── theme/
├── utils/
│   ├── ValidationUtil.kt
│   ├── Resource.kt
│   └── FirebaseErrorHandler.kt
└── MainActivity.kt
```

## Setup Firebase

1. Pastikan file `google-services.json` sudah ada dalam folder `app/`
2. Aktifkan Authentication dan Firestore di Firebase Console
3. Tambahkan method Sign-in Email/Password di Authentication

## Best Practices yang Diimplementasi

### 1. **Validasi Input & Keamanan**

- ✅ Validasi client-side sebelum panggil Firebase
- ✅ Pesan error yang user-friendly
- ✅ Password minimal 6 karakter
- ✅ Format email yang valid
- ✅ Konfirmasi password yang cocok

### 2. **Implementasi Login (Sign In)**

- ✅ Fungsi `signIn` dengan Firebase Authentication
- ✅ Penanganan success/failure
- ✅ Auto-redirect setelah login berhasil

### 3. **Navigasi Paska Otentikasi**

- ✅ Callback `onSuccess` untuk navigasi
- ✅ StateFlow untuk trigger navigasi
- ✅ Navigation dari Auth ke Home screen

### 4. **Optimasi Kinerja (Coroutines)**

- ✅ Suspend functions untuk semua operasi I/O
- ✅ viewModelScope untuk background operations
- ✅ Loading states untuk UX yang baik

## Cara Menjalankan

1. Clone repository
2. Buka project di Android Studio
3. Pastikan `google-services.json` ada di folder `app/`
4. Sync project dengan Gradle files
5. Run aplikasi di emulator atau device

## Pengembangan Selanjutnya

- [ ] Implementasi produk dan kategori
- [ ] Shopping cart functionality
- [ ] Payment integration
- [ ] Order management
- [ ] User profile management
- [ ] Push notifications
- [ ] Offline support

## Kontribusi

Proyek ini dibuat untuk tujuan pembelajaran dan dapat dikembangkan lebih lanjut sesuai kebutuhan.
