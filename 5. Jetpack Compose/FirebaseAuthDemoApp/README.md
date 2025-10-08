# Firebase Authentication Demo App - Setup Guide

## 1. Dependencies Setup ✅

Dependensi berikut telah ditambahkan ke proyek:

### Firebase Dependencies (app/build.gradle.kts):

```kotlin
// Firebase
implementation(platform(libs.firebase.bom))
implementation(libs.firebase.auth)

// Navigation
implementation(libs.navigation.compose)
implementation(libs.lifecycle.viewmodel)
```

### Version Catalog (gradle/libs.versions.toml):

```toml
firebaseBom = "33.5.1"
googleServices = "4.4.2"
navigationCompose = "2.8.4"
lifecycleViewmodel = "2.9.4"
```

### Plugin Google Services:

- Ditambahkan di `build.gradle.kts` (project level)
- Ditambahkan di `app/build.gradle.kts` (module level)

## 2. Firebase Project Configuration ✅

File `google-services.json` sudah berada di lokasi yang tepat:

```
app/google-services.json
```

**Project Information:**

- Project ID: `fir-authdemoapp-ed316`
- Application ID: `com.komputerkit.firebaseauthdemoapp`

## 3. Authentication Enablement

### Langkah-langkah di Firebase Console:

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Pilih project: `fir-authdemoapp-ed316`
3. Klik "Authentication" di sidebar kiri
4. Klik tab "Sign-in method"
5. Klik "Email/Password"
6. Enable "Email/Password" (switch pertama)
7. Opsional: Enable "Email link (passwordless sign-in)" jika diinginkan
8. Klik "Save"

## 4. Environment Setup ✅

### Konfigurasi Android:

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Java Version**: 11
- **Kotlin Version**: 2.0.21

### Permissions (sudah ditambahkan):

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 5. Running the Application

### Kompilasi:

```bash
cd "c:\Akbar\5. Jetpack Compose\FirebaseAuthDemoApp"
.\gradlew assembleDebug --no-daemon
```

### Install ke Device/Emulator:

```bash
.\gradlew installDebug
```

### Atau gunakan Android Studio:

- Klik tombol "Run" (Ctrl+R)
- Pilih device/emulator target

## 6. Troubleshooting

### Masalah Umum:

#### 1. Build Fails - Google Services Plugin

**Error**: `Plugin with id 'com.google.gms.google-services' not found`
**Solution**: ✅ Sudah ditangani - plugin telah ditambahkan

#### 2. Firebase Auth Not Working

**Periksa**:

- File `google-services.json` di folder `app/`
- Internet permission di AndroidManifest.xml ✅
- Email/Password authentication enabled di Firebase Console

#### 3. Navigation Issues

**Periksa**:

- Navigation Compose dependency ✅
- ViewModel dependency ✅

#### 4. Gradle Sync Issues

**Solution**:

```bash
.\gradlew clean
.\gradlew build --refresh-dependencies
```

#### 5. Emulator Connection Issues

**Periksa**:

- Internet connection di emulator
- Google Play Services di emulator

### Testing Authentication:

#### Register Test:

1. Jalankan aplikasi
2. Klik "Don't have an account? Register"
3. Masukkan email dan password (minimum 6 karakter)
4. Klik "Register"

#### Login Test:

1. Di halaman login, masukkan email dan password yang terdaftar
2. Klik "Login"

## 7. Struktur Aplikasi

### File yang Dibuat:

- `AuthRepository.kt` - Repository untuk Firebase Auth
- `AuthViewModel.kt` - ViewModel untuk state management
- `screens/LoginScreen.kt` - UI login
- `screens/RegisterScreen.kt` - UI register
- `screens/HomeScreen.kt` - UI setelah login
- `navigation/Screen.kt` - Route definitions
- `navigation/AppNavigation.kt` - Navigation setup

### Fitur yang Diimplementasi:

- ✅ Email/Password Registration
- ✅ Email/Password Login
- ✅ User State Management
- ✅ Navigation between screens
- ✅ Error handling
- ✅ Loading states
- ✅ Logout functionality

## 8. Next Steps

### Fitur Tambahan yang Bisa Diimplementasi:

- Password reset
- Email verification
- Social login (Google, Facebook)
- User profile management
- Offline support

### Security Enhancements:

- Input validation
- Password strength checking
- Rate limiting
- Session management

## Status: ✅ READY TO RUN

Aplikasi sudah siap dijalankan! Pastikan:

1. Email/Password authentication enabled di Firebase Console
2. Device/emulator memiliki koneksi internet
3. Google Play Services tersedia (untuk emulator)
