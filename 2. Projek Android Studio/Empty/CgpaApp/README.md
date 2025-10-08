# Kalkulator IPK (CGPA Calculator)

Aplikasi Android sederhana untuk menghitung Indeks Prestasi Kumulatif (IPK) menggunakan Jetpack Compose.

## Fitur-Fitur

### ✨ Antarmuka Pengguna yang Responsif

- **Design Modern**: Menggunakan Material Design 3 dengan Jetpack Compose
- **UI Responsif**: Antarmuka yang menyesuaikan dengan berbagai ukuran layar
- **Color Scheme**: Tema warna yang konsisten dan eye-friendly

### 📝 Input Data Mata Kuliah

- **Nama Mata Kuliah**: Field input untuk nama mata kuliah
- **Nilai (Grade)**: Dropdown selector dengan sistem penilaian standar Indonesia:
  - A (4.0), A- (3.7), B+ (3.3), B (3.0), B- (2.7)
  - C+ (2.3), C (2.0), C- (1.7), D+ (1.3), D (1.0)
  - E (0.0), F (0.0)
- **SKS (Credit)**: Input numerik dengan keyboard angka untuk jumlah SKS

### 🧮 Perhitungan IPK

- **IPK Semester**: Menghitung IPK untuk semester saat ini
- **IPK Kumulatif**: Menghitung IPK keseluruhan berdasarkan data semester sebelumnya
- **Total SKS**: Menampilkan total SKS yang telah diambil
- **Total Poin**: Menampilkan total poin yang diperoleh

### 📊 Data Semester Sebelumnya

- Input IPK semester sebelumnya
- Input total SKS semester sebelumnya
- Otomatis menghitung IPK kumulatif

### 🏆 Predikat Kelulusan

Sistem predikat berdasarkan IPK:

- **Cum Laude**: IPK ≥ 3.75
- **Sangat Memuaskan**: IPK ≥ 3.5
- **Memuaskan**: IPK ≥ 3.0
- **Cukup**: IPK ≥ 2.5
- **Kurang**: IPK ≥ 2.0
- **Sangat Kurang**: IPK < 2.0

### 🛠️ Fitur Tambahan

- **Daftar Mata Kuliah**: Menampilkan semua mata kuliah yang telah diinput
- **Hapus Mata Kuliah**: Kemampuan untuk menghapus mata kuliah tertentu
- **Clear All**: Tombol untuk menghapus semua data
- **Real-time Calculation**: Perhitungan otomatis setelah menekan tombol hitung

## Teknologi yang Digunakan

### 🚀 Framework & Libraries

- **Jetpack Compose**: Framework UI modern untuk Android
- **Material Design 3**: Design system terbaru dari Google
- **ViewModel**: Architecture component untuk state management
- **Kotlin**: Bahasa pemrograman utama

### 🏗️ Arsitektur

- **MVVM Pattern**: Model-View-ViewModel architecture
- **Composable Functions**: UI yang modular dan reusable
- **State Management**: Menggunakan mutableStateOf dan remember

### 📱 Komponen UI

- **Cards**: Container untuk setiap section
- **OutlinedTextField**: Input fields dengan border
- **DropdownMenu**: Selector untuk nilai
- **LazyColumn**: Scrollable list yang efisien
- **Buttons**: Action buttons dengan Material Design

## Struktur Project

```
app/src/main/java/com/komputerkit/cgpaapp/
├── MainActivity.kt                 # Entry point aplikasi
├── model/
│   └── Subject.kt                 # Data model untuk mata kuliah dan hasil
├── viewmodel/
│   └── CGPAViewModel.kt           # State management dan business logic
├── ui/
│   ├── components/
│   │   ├── SubjectComponents.kt   # Komponen untuk input dan list mata kuliah
│   │   └── CGPAComponents.kt      # Komponen untuk hasil dan data sebelumnya
│   ├── screens/
│   │   └── CGPACalculatorScreen.kt # Main screen aplikasi
│   └── theme/                     # Theme configuration
└── utils/
    └── GradeUtils.kt              # Utility functions untuk konversi nilai
```

## Cara Menggunakan

1. **Input Data Sebelumnya** (Opsional):

   - Masukkan IPK semester sebelumnya
   - Masukkan total SKS semester sebelumnya
   - Klik "Update Data Sebelumnya"

2. **Tambah Mata Kuliah**:

   - Isi nama mata kuliah
   - Pilih nilai dari dropdown
   - Masukkan jumlah SKS
   - Klik "Tambah Mata Kuliah"

3. **Hitung IPK**:

   - Setelah menambah mata kuliah, klik "Hitung IPK"
   - Lihat hasil IPK semester dan kumulatif
   - Lihat predikat kelulusan

4. **Kelola Data**:
   - Hapus mata kuliah tertentu dengan tombol delete
   - Hapus semua data dengan tombol "Hapus Semua"

## Requirements

- **Android API Level**: Minimum 24 (Android 7.0)
- **Target SDK**: 36
- **Kotlin**: 1.9+
- **Jetpack Compose**: Latest stable version

## Instalasi

1. Clone repository ini
2. Buka project di Android Studio
3. Sync gradle files
4. Run aplikasi di emulator atau device

---

**Dibuat dengan ❤️ menggunakan Jetpack Compose**
