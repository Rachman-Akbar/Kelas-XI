# Learn Navigation - Android Jetpack Compose App

Aplikasi Android sederhana yang mendemonstrasikan navigasi antar layar menggunakan Jetpack Compose dan Navigation Component.

## 📱 Fitur Aplikasi

- **Screen A**: Layar utama dengan input field dan tombol navigasi
- **Screen B**: Layar tujuan yang menampilkan data yang diterima dari Screen A
- **Navigation**: Navigasi dengan passing arguments antar layar

## 🏗️ Struktur Proyek

```
app/src/main/java/com/komputerkit/learnnavigaton/
├── MainActivity.kt          # Activity utama
├── MyAppNavigation.kt       # Setup navigasi dan NavHost
├── Routes.kt               # Definisi rute navigasi
├── ScreenA.kt              # Composable Screen A
└── ScreenB.kt              # Composable Screen B
```

## 🔧 Dependencies

- **Jetpack Compose**: UI toolkit modern untuk Android
- **Navigation Compose**: Untuk navigasi antar layar
- **Material 3**: Design system terbaru dari Google

## 🚀 Cara Menjalankan

1. Clone repository ini
2. Buka proyek di Android Studio
3. Sync Gradle files
4. Jalankan aplikasi di emulator atau device fisik

## 📋 Spesifikasi Teknis

- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 36
- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose

## 🎯 Cara Kerja Aplikasi

1. **Screen A**:

   - Menampilkan judul "Screen A"
   - TextField untuk input nama pengguna
   - Tombol "Go to Screen B" untuk navigasi

2. **Screen B**:

   - Menampilkan judul "Screen B"
   - Menampilkan nama yang diterima dari Screen A
   - Jika tidak ada input, menampilkan "Nama tidak tersedia"

3. **Navigasi**:
   - Menggunakan NavController dan NavHost
   - Rute didefinisikan dalam object Routes
   - Parameter nama di-encode/decode untuk menghindari masalah karakter khusus

## 📝 Contoh Penggunaan

```kotlin
// Navigasi dari Screen A ke Screen B
onNavigateToScreenB = { name ->
    val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
    navController.navigate(Routes.createScreenBRoute(encodedName))
}
```

## 🔍 Testing

Aplikasi ini sudah ditest untuk:

- ✅ Build successful
- ✅ Navigasi antar layar
- ✅ Passing arguments
- ✅ Handle empty input

## 👨‍💻 Developer

Proyek ini dibuat sebagai pembelajaran navigasi di Jetpack Compose.
