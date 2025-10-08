# 🚀 QUICK START GUIDE - Database & Logout

## ⚡ Quick Summary

### ✅ Yang Sudah Ditambahkan:

1. **Room Database** dengan 6 tabel (Siswa, Guru, Kelas, Mata Pelajaran, Jadwal, Kehadiran)
2. **Fitur Logout** di semua role (Admin, Kepsek, Kurikulum, Siswa)
3. **ViewModel & Repository Pattern** untuk clean architecture
4. **Contoh Screen** dengan database integration

---

## 📦 Build & Install

```bash
cd AplikasiMonitoringKelas
./gradlew clean assembleDebug
./gradlew installDebug
```

atau gunakan batch file:

```bash
./install-app.bat
```

---

## 🎯 Cara Pakai Database

### 1. Di ViewModel (Recommended)

```kotlin
class MyViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val siswaDao = database.siswaDao()

    fun loadData() {
        viewModelScope.launch {
            siswaDao.getAllSiswa().collect { siswaList ->
                // Update UI
            }
        }
    }
}
```

### 2. Di Composable (Simple)

```kotlin
@Composable
fun MyScreen() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val siswaList by database.siswaDao().getAllSiswa()
        .collectAsState(initial = emptyList())

    // Display siswaList
}
```

---

## 🚪 Cara Pakai Logout

### Simple Button

```kotlin
@Composable
fun MyScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Screen") },
                actions = {
                    LogoutButton()
                }
            )
        }
    ) { /* content */ }
}
```

---

## 🔄 Contoh CRUD Operations

### Create (Insert)

```kotlin
viewModelScope.launch {
    val siswa = SiswaEntity(
        id = 1,
        nama = "John Doe",
        nis = "12345",
        kelasId = 1
    )
    database.siswaDao().insertSiswa(siswa)
}
```

### Read (Query)

```kotlin
// Get all
val allSiswa: Flow<List<SiswaEntity>> = database.siswaDao().getAllSiswa()

// Get by ID
val siswa = database.siswaDao().getSiswaById(1)

// Search
val results = database.siswaDao().searchSiswa("John")
```

### Update

```kotlin
viewModelScope.launch {
    val updatedSiswa = siswa.copy(nama = "John Updated")
    database.siswaDao().updateSiswa(updatedSiswa)
}
```

### Delete

```kotlin
viewModelScope.launch {
    database.siswaDao().deleteSiswa(siswa)
}
```

---

## 📱 Testing Scenario

### Test 1: Login & Logout (Admin)

1. ✅ Login dengan `admin@sekolah.com / admin123`
2. ✅ Lihat TopBar ada tombol "Logout"
3. ✅ Klik Logout → Konfirmasi
4. ✅ Redirect ke login screen
5. ✅ Coba login lagi → Berhasil

### Test 2: Database CRUD

1. ✅ Login sebagai admin
2. ✅ Tambah user baru (data masuk ke database)
3. ✅ Logout (database ter-clear)
4. ✅ Login lagi (database kosong)

### Test 3: Semua Role

- ✅ Admin → Logout works
- ✅ Kepsek → Logout works
- ✅ Kurikulum → Logout works
- ✅ Siswa → Logout works (jika ada)

---

## 🗂️ File Penting

### Database Files

```
app/src/main/java/.../data/local/
├── AppDatabase.kt         # Main database
├── entity/               # 6 entity files
└── dao/                  # 6 DAO files
```

### Logout Files

```
app/src/main/java/.../ui/
├── viewmodel/LogoutViewModel.kt
└── components/LogoutComponents.kt
```

### Updated Files

```
AdminActivity.kt          # Added TopBar + Logout
KepalaSekolahActivity.kt  # Added TopBar + Logout
KurikulumActivity.kt      # Added TopBar + Logout
SiswaActivity.kt          # Added TopBar + Logout
```

---

## 🎨 UI Changes

**Before:**

```
┌────────────────────┐
│                    │
│   Bottom Nav       │
└────────────────────┘
```

**After:**

```
┌────────────────────┐
│ Title    [Logout]  │ ← TopBar
├────────────────────┤
│                    │
│   Content          │
│                    │
├────────────────────┤
│   Bottom Nav       │
└────────────────────┘
```

---

## 💡 Best Practices

### 1. Always Use ViewModel

```kotlin
// ❌ Bad
@Composable
fun MyScreen() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AppDatabase.getDatabase(context).siswaDao().insertSiswa(...)
    }
}

// ✅ Good
@Composable
fun MyScreen(viewModel: MyViewModel = viewModel()) {
    val siswaList by viewModel.siswaList.collectAsState()
}
```

### 2. Use Flow for Reactive UI

```kotlin
// ✅ Data otomatis update di UI
dao.getAllSiswa().collect { siswaList ->
    _uiState.value = siswaList
}
```

### 3. Handle Loading & Error

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<SiswaEntity>) : UiState()
    data class Error(val message: String) : UiState()
}
```

---

## 🐛 Common Issues

### Issue: Database not created

**Fix:** Pastikan context yang diberikan adalah applicationContext

### Issue: Main thread exception

**Fix:** Gunakan `viewModelScope.launch {}` atau `suspend function`

### Issue: Logout tidak clear data

**Fix:** Periksa `AppDatabase.getDatabase(context).clearAllTables()`

---

## 📚 Documentation Files

- `DATABASE_IMPLEMENTATION.md` - Full technical documentation
- `IMPLEMENTATION_SUMMARY.md` - Complete summary
- `QUICK_START.md` - This file

---

## 🆘 Need Help?

### Check Build Errors

```bash
./gradlew clean
./gradlew assembleDebug --stacktrace
```

### Check Runtime Errors

- Lihat Logcat di Android Studio
- Filter: `com.komputerkit.aplikasimonitoringapp`

### Sync Issues

```bash
./gradlew --refresh-dependencies
```

---

## ✨ Next Steps

1. ✅ Build & install app
2. ✅ Test login/logout semua role
3. ✅ Test database CRUD operations
4. 🔄 Integrate dengan API untuk sync data
5. 🔄 Add more features (search, filter, export)

---

**Created:** October 8, 2025  
**Status:** Ready for Testing  
**Build Status:** In Progress
