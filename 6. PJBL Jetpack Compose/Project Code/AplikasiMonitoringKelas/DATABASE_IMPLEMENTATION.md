# Database Implementation & Logout Feature

## 📋 Overview

Dokumentasi ini menjelaskan implementasi Room Database dan fitur Logout untuk aplikasi Monitoring Kelas.

## 🗄️ Room Database Implementation

### 1. Database Architecture

Aplikasi ini menggunakan **Room Database** sebagai local database untuk menyimpan data secara offline dan meningkatkan performa aplikasi.

#### Entities (Tables)

1. **SiswaEntity** - Data siswa
2. **GuruEntity** - Data guru
3. **KelasEntity** - Data kelas
4. **MataPelajaranEntity** - Data mata pelajaran
5. **JadwalEntity** - Data jadwal pelajaran
6. **KehadiranEntity** - Data kehadiran siswa

### 2. Database Structure

```
app/src/main/java/com/komputerkit/aplikasimonitoringapp/data/
├── local/
│   ├── entity/
│   │   ├── SiswaEntity.kt
│   │   ├── GuruEntity.kt
│   │   ├── KelasEntity.kt
│   │   ├── MataPelajaranEntity.kt
│   │   ├── JadwalEntity.kt
│   │   └── KehadiranEntity.kt
│   ├── dao/
│   │   ├── SiswaDao.kt
│   │   ├── GuruDao.kt
│   │   ├── KelasDao.kt
│   │   ├── MataPelajaranDao.kt
│   │   ├── JadwalDao.kt
│   │   └── KehadiranDao.kt
│   └── AppDatabase.kt
└── repository/
    ├── SiswaRepository.kt
    ├── JadwalRepository.kt
    └── KehadiranRepository.kt
```

### 3. Dependencies Added

```kotlin
// build.gradle.kts (Module: app)
plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
}

dependencies {
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}
```

### 4. Key Features

#### A. Entities

Semua entity menggunakan anotasi `@Entity` dan memiliki primary key `@PrimaryKey`.

**Contoh: SiswaEntity**

```kotlin
@Entity(tableName = "siswa")
data class SiswaEntity(
    @PrimaryKey
    val id: Int,
    val nama: String,
    val nis: String,
    @SerializedName("kelas_id")
    val kelasId: Int,
    // ... field lainnya
)
```

#### B. DAO (Data Access Object)

DAO menyediakan method untuk operasi CRUD dengan dukungan Flow untuk reactive data.

**Fitur DAO:**

- ✅ Get all data
- ✅ Get by ID
- ✅ Search
- ✅ Insert (single & multiple)
- ✅ Update
- ✅ Delete
- ✅ Delete all

**Contoh: SiswaDao**

```kotlin
@Dao
interface SiswaDao {
    @Query("SELECT * FROM siswa ORDER BY nama ASC")
    fun getAllSiswa(): Flow<List<SiswaEntity>>

    @Query("SELECT * FROM siswa WHERE kelas_id = :kelasId")
    fun getSiswaByKelas(kelasId: Int): Flow<List<SiswaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiswa(siswa: SiswaEntity)

    // ... method lainnya
}
```

#### C. Repository Pattern

Repository bertindak sebagai single source of truth dan mengelola data dari API dan local database.

```kotlin
class SiswaRepository(private val siswaDao: SiswaDao) {
    fun getAllSiswa(): Flow<List<SiswaEntity>> = siswaDao.getAllSiswa()
    suspend fun insertAllSiswa(siswaList: List<SiswaEntity>) = siswaDao.insertAllSiswa(siswaList)
    // ... method lainnya
}
```

### 5. Database Usage

#### Mendapatkan Database Instance

```kotlin
val database = AppDatabase.getDatabase(context)
val siswaDao = database.siswaDao()
```

#### Insert Data

```kotlin
viewModelScope.launch {
    val siswa = SiswaEntity(
        id = 1,
        nama = "John Doe",
        nis = "12345",
        kelasId = 1
    )
    siswaDao.insertSiswa(siswa)
}
```

#### Retrieve Data with Flow

```kotlin
val siswaList: Flow<List<SiswaEntity>> = siswaDao.getAllSiswa()

// Di Composable
val siswaState by siswaList.collectAsState(initial = emptyList())
```

## 🚪 Logout Feature Implementation

### 1. Logout ViewModel

File: `ui/viewmodel/LogoutViewModel.kt`

```kotlin
class LogoutViewModel : ViewModel() {
    fun logout(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Clear session
            SessionManager(context).clearSession()

            // Clear database
            AppDatabase.getDatabase(context).clearAllTables()

            // Callback
            onSuccess()
        }
    }
}
```

### 2. Logout UI Component

File: `ui/components/LogoutComponents.kt`

**Features:**

- ✅ Logout Button dengan konfirmasi dialog
- ✅ Loading indicator saat proses logout
- ✅ Auto-redirect ke login screen
- ✅ Clear all data (session + database)

**Contoh Penggunaan:**

```kotlin
@Composable
fun MyScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    LogoutButton(
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        // Screen content
    }
}
```

### 3. Updated Activities

Semua activity telah diupdate dengan fitur logout:

#### ✅ AdminActivity

- TopBar dengan title "Admin Panel"
- Logout button di TopBar actions
- 3 Bottom Navigation: Entry User, Entry Jadwal, List

#### ✅ KepalaSekolahActivity

- TopBar dengan title "Kepala Sekolah"
- Logout button di TopBar actions
- 3 Bottom Navigation: Jadwal Pelajaran, Kelas Kosong, List

#### ✅ KurikulumActivity

- TopBar dengan title "Kurikulum"
- Logout button di TopBar actions
- 3 Bottom Navigation: Jadwal Pelajaran, Ganti Guru, List

#### ✅ SiswaActivity

- TopBar dengan title "Siswa"
- Logout button di TopBar actions
- 3 Bottom Navigation: Jadwal Pelajaran, Entri, List

## 📱 User Flow

### Login Flow

1. User login dengan email & password
2. API mengembalikan token & user data
3. Token disimpan di SharedPreferences
4. User data disimpan di SharedPreferences
5. Data dari API di-sync ke local database
6. User diarahkan ke activity sesuai role

### Logout Flow

1. User klik tombol Logout
2. Muncul dialog konfirmasi
3. Setelah konfirmasi:
   - Clear SharedPreferences (token & user data)
   - Clear Room Database (semua tabel)
   - Redirect ke MainActivity (Login screen)
4. User harus login ulang

## 🔄 Data Sync Strategy

### Offline-First Approach

1. **Load from Local Database** - Data ditampilkan langsung dari database lokal
2. **Fetch from API** - Background sync dari API
3. **Update Local Database** - Data dari API disimpan ke database
4. **Update UI** - UI ter-update otomatis karena menggunakan Flow

### Sync Timing

- ✅ On Login - Sync all data
- ✅ On Refresh - Manual refresh
- ✅ On Data Change - Sync spesifik data yang berubah
- ✅ Periodic Sync - Background sync (optional)

## 🛠️ Build & Run

### 1. Sync Gradle

```bash
cd AplikasiMonitoringKelas
./gradlew sync
```

### 2. Build Project

```bash
./gradlew clean assembleDebug
```

### 3. Install to Device

```bash
./gradlew installDebug
```

atau gunakan:

```bash
./install-app.bat
```

## 📊 Database Schema

### Siswa Table

```sql
CREATE TABLE siswa (
    id INTEGER PRIMARY KEY,
    nama TEXT NOT NULL,
    nis TEXT NOT NULL,
    kelas_id INTEGER NOT NULL,
    alamat TEXT,
    tanggal_lahir TEXT,
    jenis_kelamin TEXT,
    foto TEXT,
    created_at TEXT,
    updated_at TEXT
);
```

### Guru Table

```sql
CREATE TABLE guru (
    id INTEGER PRIMARY KEY,
    nama TEXT NOT NULL,
    nip TEXT NOT NULL,
    mata_pelajaran_id INTEGER,
    alamat TEXT,
    no_telepon TEXT,
    email TEXT,
    foto TEXT,
    created_at TEXT,
    updated_at TEXT
);
```

### Kelas Table

```sql
CREATE TABLE kelas (
    id INTEGER PRIMARY KEY,
    nama_kelas TEXT NOT NULL,
    tingkat_kelas TEXT NOT NULL,
    wali_kelas_id INTEGER,
    tahun_ajaran TEXT,
    created_at TEXT,
    updated_at TEXT
);
```

### Mata Pelajaran Table

```sql
CREATE TABLE mata_pelajaran (
    id INTEGER PRIMARY KEY,
    nama_mata_pelajaran TEXT NOT NULL,
    kode TEXT NOT NULL,
    deskripsi TEXT,
    created_at TEXT,
    updated_at TEXT
);
```

### Jadwal Table

```sql
CREATE TABLE jadwal (
    id INTEGER PRIMARY KEY,
    kelas_id INTEGER NOT NULL,
    mata_pelajaran_id INTEGER NOT NULL,
    guru_id INTEGER NOT NULL,
    hari TEXT NOT NULL,
    jam_mulai TEXT NOT NULL,
    jam_selesai TEXT NOT NULL,
    ruangan TEXT,
    created_at TEXT,
    updated_at TEXT
);
```

### Kehadiran Table

```sql
CREATE TABLE kehadiran (
    id INTEGER PRIMARY KEY,
    siswa_id INTEGER NOT NULL,
    jadwal_id INTEGER NOT NULL,
    tanggal TEXT NOT NULL,
    status TEXT NOT NULL,
    keterangan TEXT,
    created_at TEXT,
    updated_at TEXT
);
```

## 🔐 Security Notes

1. **Token Storage** - Token disimpan di SharedPreferences dengan MODE_PRIVATE
2. **Database Encryption** - Untuk produksi, pertimbangkan menggunakan SQLCipher
3. **Data Cleanup** - Semua data ter-clear saat logout
4. **Session Validation** - Validasi token di setiap API call

## 🚀 Next Steps

### Recommended Improvements

1. **Implement ViewModels** untuk setiap screen dengan Room Database
2. **Add Sync Manager** untuk automated background sync
3. **Add Pagination** untuk data yang banyak
4. **Add Search Feature** menggunakan Room query
5. **Add Export Feature** untuk export data ke PDF/Excel
6. **Add Offline Indicator** untuk menunjukkan status koneksi
7. **Add Data Validation** sebelum insert ke database

## 📚 References

- [Room Database Documentation](https://developer.android.com/training/data-storage/room)
- [Kotlin Flow Guide](https://developer.android.com/kotlin/flow)
- [Repository Pattern](https://developer.android.com/topic/architecture/data-layer)
- [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

## ✅ Testing Checklist

- [ ] Login dengan semua role (Admin, Kepsek, Kurikulum, Siswa)
- [ ] Verify data tersimpan di database lokal
- [ ] Test logout dari setiap role
- [ ] Verify database ter-clear setelah logout
- [ ] Test offline functionality
- [ ] Test data sync dari API ke database
- [ ] Test CRUD operations untuk setiap entity

## 🐛 Troubleshooting

### Error: Cannot access database on main thread

**Solution:** Pastikan semua operasi database menggunakan `suspend` function atau `Flow`

### Error: No database found

**Solution:** Pastikan database instance sudah dibuat dengan `AppDatabase.getDatabase(context)`

### Logout tidak berfungsi

**Solution:**

1. Check LogViewModel implementation
2. Verify SessionManager.clearSession() dipanggil
3. Check Intent redirect ke MainActivity

## 📞 Support

Jika ada pertanyaan atau masalah, silakan buat issue di repository ini.

---

**Created:** October 8, 2025
**Version:** 1.0.0
**Author:** Development Team
