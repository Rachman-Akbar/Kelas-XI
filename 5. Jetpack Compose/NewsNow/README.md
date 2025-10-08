# NewsNow - Android News App

Aplikasi berita Android yang dibangun dengan **Jetpack Compose** dan arsitektur **MVVM**. Aplikasi ini mengintegrasikan News API untuk menampilkan berita terkini dengan fitur lengkap.

## ✨ Fitur

- 📰 **Daftar Berita**: Menampilkan berita terkini dari berbagai kategori
- 🏷️ **Kategori**: Filter berita berdasarkan kategori (Business, Technology, Sports, dll)
- 🔍 **Pencarian**: Mencari berita berdasarkan keyword
- 📖 **Detail Artikel**: WebView untuk membaca artikel lengkap
- 🎨 **Modern UI**: Dibangun dengan Jetpack Compose dan Material 3

## 🛠️ Tech Stack

- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - Clean architecture pattern
- **Navigation Compose** - Type-safe navigation
- **Retrofit** - HTTP client untuk API calls
- **Coil** - Image loading library
- **Kotlin Serialization** - JSON serialization
- **StateFlow & LiveData** - Reactive state management

## 🚀 Setup

### 1. Mendapatkan News API Key

1. Kunjungi [NewsAPI.org](https://newsapi.org/)
2. Daftar untuk mendapatkan API key gratis
3. Copy API key yang diberikan

### 2. Konfigurasi API Key

Buka file `app/src/main/java/com/komputerkit/newsnow/util/Constants.kt` dan ganti:

```kotlin
const val NEWS_API_KEY = "YOUR_NEWS_API_KEY_HERE"
```

dengan API key yang Anda dapatkan:

```kotlin
const val NEWS_API_KEY = "your_actual_api_key_here"
```

### 3. Build & Run

1. Sync project dengan Gradle
2. Build dan jalankan aplikasi di emulator atau device

## 📱 Screenshots

- **Home Screen**: Daftar berita dengan kategori filter
- **Search**: Pencarian berita real-time
- **Article Detail**: WebView untuk membaca artikel lengkap

## 🏗️ Arsitektur

```
app/
├── data/
│   ├── api/           # Retrofit API services
│   ├── model/         # Data models
│   └── repository/    # Data repository
├── navigation/        # Navigation routes
├── ui/
│   ├── screen/        # Composable screens
│   ├── theme/         # App theme
│   └── viewmodel/     # ViewModels
└── util/              # Utility classes
```

## 🔧 Dependencies

- **Compose Navigation**: androidx.navigation:navigation-compose:2.7.5
- **ViewModel Compose**: androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0
- **Retrofit**: com.squareup.retrofit2:retrofit:2.9.0
- **Coil**: io.coil-kt:coil-compose:2.5.0
- **Kotlin Serialization**: org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2

## 📝 Todo

- [ ] Implementasi cache offline
- [ ] Dark mode support
- [ ] Pull-to-refresh
- [ ] Bookmark artikel
- [ ] Share artikel
- [ ] Notifikasi berita breaking

## 🤝 Contributing

Kontribusi selalu diterima! Silakan buat pull request atau buka issue untuk saran dan perbaikan.

## 📄 License

Project ini menggunakan MIT License.
