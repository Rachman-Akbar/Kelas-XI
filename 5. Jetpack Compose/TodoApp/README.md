# Todo App - Android Native dengan Kotlin & Jetpack Compose

Aplikasi Todo sederhana yang dibangun dengan Kotlin dan Jetpack Compose menggunakan arsitektur MVVM dan Room Database untuk penyimpanan data persisten.

## ✨ Fitur

- ➕ **Tambah Tugas**: Menambahkan tugas baru dengan mudah
- 📋 **Lihat Daftar**: Menampilkan semua tugas dalam daftar yang terorganisir
- 🗑️ **Hapus Tugas**: Menghapus tugas yang sudah selesai atau tidak diperlukan
- 💾 **Penyimpanan Persisten**: Data tersimpan secara lokal menggunakan Room Database
- 🎨 **UI Modern**: Interface yang bersih menggunakan Material Design 3

## 🏗️ Arsitektur

Aplikasi ini menggunakan **MVVM (Model-View-ViewModel)** architecture pattern:

```
📁 app/src/main/java/com/komputerkit/todoapp/
├── 📁 data/
│   ├── 📁 converter/
│   │   └── DateConverter.kt         # TypeConverter untuk Date ↔ Long
│   ├── 📁 dao/
│   │   └── TodoDao.kt              # Database Access Object
│   ├── 📁 database/
│   │   └── TodoDatabase.kt         # Room Database configuration
│   └── 📁 entity/
│       └── TodoItem.kt             # Data model/Entity
├── 📁 repository/
│   └── TodoRepository.kt           # Repository layer
├── 📁 ui/
│   ├── 📁 screen/
│   │   └── TodoScreen.kt           # Main UI Composable
│   └── 📁 theme/                   # App theme
├── 📁 viewmodel/
│   └── TodoViewModel.kt            # ViewModel + Factory
├── MainActivity.kt                 # Entry point
└── TodoApplication.kt             # Application class
```

## 🛠️ Teknologi yang Digunakan

- **Kotlin** - Bahasa pemrograman utama
- **Jetpack Compose** - Modern UI toolkit untuk Android
- **Room Database** - SQLite abstraction layer untuk data persisten
- **ViewModel & LiveData** - Mengelola UI state dan lifecycle
- **Coroutines** - Asynchronous programming untuk operasi database
- **Material Design 3** - Design system untuk UI/UX
- **KSP** - Kotlin Symbol Processing untuk Room compiler

## 📦 Dependencies

```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// ViewModel & Compose
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
```

## 🚀 Cara Menjalankan

1. **Clone repository** atau buka project di Android Studio
2. **Sync Gradle** - Tunggu hingga semua dependencies terunduh
3. **Build Project** - Pilih Build > Make Project
4. **Run** - Pilih device/emulator dan klik Run

## 📱 Screenshot & Cara Penggunaan

### Menambahkan Tugas

1. Ketik nama tugas di field "New Task"
2. Tekan tombol ➕ atau Enter di keyboard
3. Tugas akan ditambahkan ke daftar dengan timestamp

### Menghapus Tugas

1. Cari tugas yang ingin dihapus di daftar
2. Klik ikon 🗑️ di sebelah kanan tugas
3. Tugas akan langsung terhapus dari daftar dan database

## 🗄️ Database Schema

```sql
CREATE TABLE todo_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title TEXT NOT NULL,
    createdAt INTEGER NOT NULL
);
```

## 🔧 Pengembangan Selanjutnya

Beberapa fitur yang bisa ditambahkan:

- [ ] Edit tugas yang sudah ada
- [ ] Tandai tugas sebagai selesai
- [ ] Kategori atau tag untuk tugas
- [ ] Filter dan pencarian tugas
- [ ] Backup & restore data
- [ ] Dark theme support
- [ ] Widget untuk home screen

## 📝 Catatan

- Data disimpan secara lokal menggunakan Room Database
- Aplikasi menggunakan coroutines untuk operasi database non-blocking
- UI menggunakan State management yang reactive
- TypeConverter digunakan untuk menyimpan Date sebagai Long di database

## 🏷️ Versi

- **Android SDK**: Minimum 24, Target 36
- **Kotlin**: 2.0.21
- **Compose BOM**: 2024.09.00
- **Room**: 2.6.1

---

Dibuat dengan ❤️ menggunakan Kotlin & Jetpack Compose
