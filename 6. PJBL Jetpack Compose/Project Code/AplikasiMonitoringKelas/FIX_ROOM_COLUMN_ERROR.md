# 🔧 FIX: Room Database Column Name Error

## ❌ Error yang Terjadi

```
[ksp] SQL error or missing database (no such column: kelas_id)
[ksp] SQL error or missing database (no such column: mata_pelajaran_id)
[ksp] SQL error or missing database (no such column: tingkat_kelas)
... dan error serupa untuk kolom lainnya
```

## 🔍 Penyebab Masalah

Room Database menggunakan **nama property Kotlin** sebagai nama kolom, bukan nama dari `@SerializedName`.

**Contoh Masalah:**

```kotlin
@Entity(tableName = "siswa")
data class SiswaEntity(
    @SerializedName("kelas_id")  // ❌ Ini untuk JSON, bukan Room!
    val kelasId: Int
)
```

Room akan mencari kolom bernama `kelasId` (camelCase), bukan `kelas_id` (snake_case).

## ✅ Solusi

Gunakan `@ColumnInfo` dari Room untuk mapping nama kolom database:

```kotlin
@Entity(tableName = "siswa")
data class SiswaEntity(
    @ColumnInfo(name = "kelas_id")  // ✅ Benar untuk Room!
    val kelasId: Int
)
```

## 📝 File yang Diperbaiki

### 1. SiswaEntity.kt

```kotlin
import androidx.room.ColumnInfo  // ✅ Import Room annotation

@Entity(tableName = "siswa")
data class SiswaEntity(
    @PrimaryKey
    val id: Int,
    val nama: String,
    val nis: String,
    @ColumnInfo(name = "kelas_id")         // ✅ Fixed
    val kelasId: Int,
    @ColumnInfo(name = "tanggal_lahir")    // ✅ Fixed
    val tanggalLahir: String? = null,
    // ... dst
)
```

### 2. GuruEntity.kt

```kotlin
@ColumnInfo(name = "mata_pelajaran_id")  // ✅ Fixed
val mataPelajaranId: Int? = null,
@ColumnInfo(name = "no_telepon")         // ✅ Fixed
val noTelepon: String? = null,
```

### 3. KelasEntity.kt

```kotlin
@ColumnInfo(name = "nama_kelas")      // ✅ Fixed
val namaKelas: String,
@ColumnInfo(name = "tingkat_kelas")   // ✅ Fixed
val tingkatKelas: String,
@ColumnInfo(name = "wali_kelas_id")   // ✅ Fixed
val waliKelasId: Int? = null,
@ColumnInfo(name = "tahun_ajaran")    // ✅ Fixed
val tahunAjaran: String? = null,
```

### 4. MataPelajaranEntity.kt

```kotlin
@ColumnInfo(name = "nama_mata_pelajaran")  // ✅ Fixed
val namaMataPelajaran: String,
```

### 5. JadwalEntity.kt

```kotlin
@ColumnInfo(name = "kelas_id")           // ✅ Fixed
val kelasId: Int,
@ColumnInfo(name = "mata_pelajaran_id")  // ✅ Fixed
val mataPelajaranId: Int,
@ColumnInfo(name = "guru_id")            // ✅ Fixed
val guruId: Int,
@ColumnInfo(name = "jam_mulai")          // ✅ Fixed
val jamMulai: String,
@ColumnInfo(name = "jam_selesai")        // ✅ Fixed
val jamSelesai: String,
```

### 6. KehadiranEntity.kt

```kotlin
@ColumnInfo(name = "siswa_id")   // ✅ Fixed
val siswaId: Int,
@ColumnInfo(name = "jadwal_id")  // ✅ Fixed
val jadwalId: Int,
```

## 🎯 Perbedaan @SerializedName vs @ColumnInfo

| Annotation        | Digunakan Untuk             | Library |
| ----------------- | --------------------------- | ------- |
| `@SerializedName` | JSON parsing (API response) | Gson    |
| `@ColumnInfo`     | Database column mapping     | Room    |

### Contoh Lengkap (Jika Perlu Keduanya):

```kotlin
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

// Untuk model yang digunakan API & Database
data class SiswaModel(
    @SerializedName("kelas_id")    // Untuk JSON dari API
    @ColumnInfo(name = "kelas_id")  // Untuk Room Database
    val kelasId: Int
)
```

**Tapi** karena kita pisah Entity (Room) dan Model (API), kita cukup gunakan:

- `@ColumnInfo` di Entity (untuk Room)
- `@SerializedName` di Model (untuk API)

## ✅ Build Status

Setelah perbaikan:

```bash
./gradlew clean assembleDebug
```

**Result:** ✅ BUILD SUCCESSFUL

Warnings yang muncul (deprecation) tidak masalah dan tidak mempengaruhi build.

## 📚 Best Practice

### ✅ DO:

```kotlin
// Entity untuk Room Database
@Entity(tableName = "siswa")
data class SiswaEntity(
    @ColumnInfo(name = "kelas_id")
    val kelasId: Int
)

// Model untuk API
data class SiswaModel(
    @SerializedName("kelas_id")
    val kelasId: Int
)
```

### ❌ DON'T:

```kotlin
// Jangan gunakan @SerializedName di Entity Room
@Entity(tableName = "siswa")
data class SiswaEntity(
    @SerializedName("kelas_id")  // ❌ Wrong!
    val kelasId: Int
)
```

## 🔄 Testing

### 1. Verify Build

```bash
cd AplikasiMonitoringKelas
./gradlew clean assembleDebug
```

### 2. Verify Database Schema

Setelah app running, gunakan Database Inspector di Android Studio:

- Tools → Database Inspector
- Select "monitoring_kelas_database"
- Verify column names match (kelas_id, guru_id, etc.)

### 3. Test CRUD Operations

```kotlin
// Insert
val siswa = SiswaEntity(
    id = 1,
    nama = "John",
    nis = "12345",
    kelasId = 1  // Akan disimpan sebagai "kelas_id" di database
)
dao.insertSiswa(siswa)

// Query
dao.getSiswaByKelas(1)  // Query: WHERE kelas_id = 1
```

## 📊 Summary

| File                   | Changes                                  | Status   |
| ---------------------- | ---------------------------------------- | -------- |
| SiswaEntity.kt         | Added @ColumnInfo for snake_case columns | ✅ Fixed |
| GuruEntity.kt          | Added @ColumnInfo for snake_case columns | ✅ Fixed |
| KelasEntity.kt         | Added @ColumnInfo for snake_case columns | ✅ Fixed |
| MataPelajaranEntity.kt | Added @ColumnInfo for snake_case columns | ✅ Fixed |
| JadwalEntity.kt        | Added @ColumnInfo for snake_case columns | ✅ Fixed |
| KehadiranEntity.kt     | Added @ColumnInfo for snake_case columns | ✅ Fixed |

## 🎉 Result

✅ All KSP errors resolved  
✅ Build successful  
✅ Database schema correct  
✅ Ready to use!

---

**Date:** October 8, 2025  
**Fix Type:** Room Database Column Mapping  
**Impact:** All entities fixed  
**Build Status:** ✅ SUCCESS
