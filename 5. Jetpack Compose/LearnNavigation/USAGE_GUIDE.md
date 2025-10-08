# Panduan Penggunaan Aplikasi Learn Navigation

## 🚀 Cara Menjalankan Aplikasi

### Prasyarat

- Android Studio Arctic Fox atau versi yang lebih baru
- Android SDK 21 atau lebih tinggi terinstal
- Device Android atau Emulator

### Langkah-langkah

1. Buka Android Studio
2. Pilih "Open an Existing Project"
3. Navigasi ke folder proyek `LearnNavigation`
4. Tunggu proses sync Gradle selesai
5. Pilih device/emulator target
6. Klik tombol Run (▶️) atau tekan Shift+F10

## 📱 Fitur dan Fungsionalitas

### Screen A (Layar Utama)

- **Judul**: Menampilkan "Screen A" di bagian atas
- **Input Field**: TextField untuk memasukkan nama pengguna
- **Tombol Navigasi**: "Go to Screen B" untuk berpindah ke layar kedua
- **Validasi**: Jika field kosong, akan mengirim "Tidak ada nama"

### Screen B (Layar Tujuan)

- **Judul**: Menampilkan "Screen B" di bagian atas
- **Display Card**: Menampilkan nama yang diterima dalam card yang menarik
- **Tombol Kembali**: "Kembali ke Screen A" dengan ikon panah
- **Fallback**: Menampilkan "Nama tidak tersedia" jika tidak ada data

## 🔧 Teknologi yang Digunakan

### Dependencies Utama

```kotlin
implementation("androidx.navigation:navigation-compose:2.7.4")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose:1.11.0")
```

### Arsitektur

- **MVVM Pattern**: Separation of concerns
- **Compose Navigation**: Type-safe navigation
- **Material 3**: Modern UI components

## 📋 Struktur Kode

### Routes.kt

```kotlin
object Routes {
    const val SCREEN_A = "screen_a"
    const val SCREEN_B = "screen_b/{name}"

    fun createScreenBRoute(name: String): String {
        return "screen_b/$name"
    }
}
```

### MyAppNavigation.kt

- Setup NavController dan NavHost
- Handle encoding/decoding parameter
- Manage navigation logic

### ScreenA.kt & ScreenB.kt

- UI Composable functions
- State management dengan remember
- Event handling untuk navigasi

## 🎯 Flow Aplikasi

1. **Start**: Aplikasi membuka Screen A
2. **Input**: User memasukkan nama di TextField
3. **Navigate**: User menekan "Go to Screen B"
4. **Display**: Screen B menampilkan nama yang diinput
5. **Back**: User dapat kembali ke Screen A

## 🧪 Testing

### Manual Testing

1. Jalankan aplikasi
2. Test input nama normal: "John Doe"
3. Test input kosong: ""
4. Test special characters: "José María"
5. Test navigasi bolak-balik

### Expected Results

- ✅ Navigasi lancar antar screen
- ✅ Parameter terkirim dengan benar
- ✅ Handle empty input
- ✅ Special characters ter-encode proper
- ✅ Back navigation berfungsi

## 🐛 Troubleshooting

### Build Issues

- Pastikan Gradle Wrapper versi kompatibel
- Clean project: `./gradlew clean`
- Invalidate cache di Android Studio

### Runtime Issues

- Periksa minimum SDK version
- Pastikan Navigation dependency ter-import
- Check ProGuard rules jika build release

## 📚 Referensi

- [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- [Material 3 Design](https://m3.material.io/)
- [Compose UI](https://developer.android.com/jetpack/compose)
