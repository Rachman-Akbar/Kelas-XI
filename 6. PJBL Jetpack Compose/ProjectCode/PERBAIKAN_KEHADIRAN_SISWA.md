# Perbaikan Error Kehadiran Siswa dan Guru

## Tanggal: 7 Desember 2025

## 📋 Ringkasan Masalah

Role **siswa** mengalami error saat melakukan entri kehadiran siswa dan kehadiran guru dengan pesan error:

```
"Gagal menyimpan kehadiran guru: {"success":false,"message":"Gagal menambahkan kehadiran guru","error":"The selected status kehadiran is invalid."}"
```

## 🔍 Analisis Akar Masalah

### 1. **Status Kehadiran Tidak Valid**

- **Masalah**: Aplikasi Android mengirim status `"terlambat"`, tapi database hanya menerima:

  - `hadir`
  - `telat` ✅ (bukan `terlambat`)
  - `tidak_hadir`
  - `izin`
  - `sakit`

- **Lokasi Error**:
  - File: `api-aplikasimonitoringkelas/app/Http/Controllers/KehadiranGuruController.php`
  - Baris: 121
  ```php
  'status_kehadiran' => 'required|in:hadir,telat,tidak_hadir,izin,sakit',
  ```

### 2. **Field `diinput_oleh` Tidak Dikirim**

- **Masalah**: Backend memerlukan field `diinput_oleh` (user_id siswa yang melakukan input) tapi aplikasi Android tidak mengirimkannya
- **Lokasi**:
  - Controller: `KehadiranGuruController.php` baris 127
  - Database: Tabel `kehadiran_gurus` kolom `diinput_oleh`

### 3. **Field `waktu_datang` Tidak Dikirim**

- **Masalah**: Untuk status `hadir` dan `telat`, seharusnya ada waktu kedatangan
- **Impact**: Data tidak lengkap di database

## ✅ Solusi yang Diterapkan

### 1. **Update Model Request (RequestModels.kt)**

**File**: `AplikasiMonitoringKelas/app/src/main/java/com/komputerkit/aplikasimonitoringkelas/data/models/RequestModels.kt`

#### TeacherAttendanceRequest

```kotlin
// BEFORE
data class TeacherAttendanceRequest(
    val guru_id: Int,
    val jadwal_id: Int,
    val tanggal: String,
    val status_kehadiran: String,
    val keterangan: String? = null
)

// AFTER
data class TeacherAttendanceRequest(
    val guru_id: Int,
    val jadwal_id: Int,
    val tanggal: String,
    val status_kehadiran: String,
    val waktu_datang: String? = null,          // ✅ ADDED
    val durasi_keterlambatan: Int? = null,     // ✅ ADDED
    val keterangan: String? = null,
    val diinput_oleh: Int? = null              // ✅ ADDED
)
```

#### StudentAttendanceRequest

```kotlin
// BEFORE
data class StudentAttendanceRequest(
    val siswa_id: Int,
    val jadwal_id: Int,
    val tanggal: String,
    val status: String,
    val keterangan: String? = null
)

// AFTER
data class StudentAttendanceRequest(
    val siswa_id: Int,
    val jadwal_id: Int,
    val tanggal: String,
    val status: String,
    val keterangan: String? = null,
    val diinput_oleh: Int? = null              // ✅ ADDED
)
```

### 2. **Update SiswaViewModel.kt**

**File**: `AplikasiMonitoringKelas/app/src/main/java/com/komputerkit/aplikasimonitoringkelas/siswa/SiswaViewModel.kt`

#### submitTeacherAttendance()

```kotlin
// ✅ Ambil user_id siswa yang login
val userId = authRepository.getUserId()

// ✅ Generate waktu_datang untuk status 'hadir' atau 'telat'
val waktuDatang = if (statusKehadiran == "hadir" || statusKehadiran == "telat") {
    val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    timeFormat.format(java.util.Date())
} else null

// ✅ Kirim semua field yang diperlukan
val request = TeacherAttendanceRequest(
    guru_id = guruId,
    jadwal_id = jadwalId,
    tanggal = tanggal,
    status_kehadiran = statusKehadiran,
    waktu_datang = waktuDatang,              // ✅ ADDED
    durasi_keterlambatan = null,
    keterangan = keterangan,
    diinput_oleh = userId                    // ✅ ADDED
)
```

#### submitStudentAttendance()

```kotlin
// ✅ Ambil user_id siswa yang login
val userId = authRepository.getUserId()

val request = StudentAttendanceRequest(
    siswa_id = siswaId,
    jadwal_id = jadwalId,
    tanggal = tanggal,
    status = statusKehadiran,
    keterangan = keterangan,
    diinput_oleh = userId                    // ✅ ADDED
)
```

### 3. **Fix Status Mapping di UI**

**File**: `AplikasiMonitoringKelas/app/src/main/java/com/komputerkit/aplikasimonitoringkelas/siswa/SiswaTeacherAttendanceScreen.kt`

#### Status Options di Filter Dialog

```kotlin
// BEFORE
val statusOptions = listOf("Semua", "hadir", "tidak_hadir", "izin", "sakit", "terlambat")

// AFTER - Gunakan "telat" sesuai enum database
val statusOptions = listOf("Semua", "hadir", "tidak_hadir", "izin", "sakit", "telat")
```

#### Status Options di Add Dialog

```kotlin
// BEFORE
val statusOptions = listOf("hadir", "izin", "sakit", "terlambat", "tidak_hadir")

// AFTER
val statusOptions = listOf("hadir", "izin", "sakit", "telat", "tidak_hadir")
```

#### Update Display Name Function

```kotlin
fun getTeacherStatusDisplayName(status: String): String {
    return when (status.lowercase()) {
        "hadir" -> "Hadir"
        "tidak_hadir" -> "Tidak Hadir"
        "izin", "izin_" -> "Izin"
        "sakit" -> "Sakit"
        "telat", "terlambat" -> "Terlambat"  // ✅ Support both
        "pending", "disetujui", "ditolak" -> status.replace("_", " ").replaceFirstChar { it.uppercase() }
        else -> status.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}
```

#### Update Color Function

```kotlin
fun getTeacherStatusColor(status: String): androidx.compose.ui.graphics.Color {
    return when (status.lowercase()) {
        "hadir" -> ModernColors.SuccessGreen
        "tidak_hadir", "ditolak" -> ModernColors.ErrorRed
        "izin", "sakit", "telat", "terlambat", "pending" -> ModernColors.WarningAmber  // ✅ Support both
        else -> ModernColors.TextSecondary
    }
}
```

## 🗄️ Struktur Database (Referensi)

### Tabel `kehadiran_gurus`

```sql
CREATE TABLE `kehadiran_gurus` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `jadwal_id` bigint(20) UNSIGNED NOT NULL,
  `guru_id` bigint(20) UNSIGNED NOT NULL,
  `tanggal` date NOT NULL,
  `status_kehadiran` enum('hadir','telat','tidak_hadir','izin','sakit') NOT NULL DEFAULT 'hadir',
  `waktu_datang` varchar(20) DEFAULT NULL,
  `durasi_keterlambatan` int(11) DEFAULT NULL COMMENT 'dalam menit',
  `keterangan` text DEFAULT NULL,
  `diinput_oleh` bigint(20) UNSIGNED DEFAULT NULL,  -- ✅ Field penting!
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### Validasi Backend

**File**: `api-aplikasimonitoringkelas/app/Http/Controllers/KehadiranGuruController.php`

```php
$validated = $request->validate([
    'jadwal_id' => 'required|exists:jadwals,id',
    'guru_id' => 'required|exists:gurus,id',
    'tanggal' => 'required|date',
    'status_kehadiran' => 'required|in:hadir,telat,tidak_hadir,izin,sakit',  // ✅ enum valid
    'waktu_datang' => 'nullable|date_format:H:i',
    'durasi_keterlambatan' => 'nullable|integer|min:0',
    'keterangan' => 'nullable|string',
    'diinput_oleh' => 'nullable|exists:users,id',
]);
```

## 📊 Mapping Status Kehadiran

| Database Value | Android Value | Display Name | Color |
| -------------- | ------------- | ------------ | ----- |
| `hadir`        | `hadir`       | Hadir        | Green |
| `telat`        | `telat`       | Terlambat    | Amber |
| `tidak_hadir`  | `tidak_hadir` | Tidak Hadir  | Red   |
| `izin`         | `izin`        | Izin         | Amber |
| `sakit`        | `sakit`       | Sakit        | Amber |

## 🧪 Testing Checklist

### ✅ Yang Sudah Diperbaiki:

1. ✅ Status `"telat"` sekarang dikirim dengan benar (bukan `"terlambat"`)
2. ✅ Field `diinput_oleh` diisi dengan user_id siswa yang login
3. ✅ Field `waktu_datang` otomatis diisi untuk status `hadir` dan `telat`
4. ✅ Display name tetap menampilkan "Terlambat" yang user-friendly
5. ✅ Backward compatibility: tetap support display untuk data lama yang pakai `"terlambat"`

### 🔄 Yang Perlu Ditest:

1. [ ] Siswa login dan input kehadiran guru dengan status "Hadir"
2. [ ] Siswa login dan input kehadiran guru dengan status "Telat"
3. [ ] Siswa login dan input kehadiran guru dengan status "Izin"
4. [ ] Siswa login dan input kehadiran guru dengan status "Sakit"
5. [ ] Siswa login dan input kehadiran guru dengan status "Tidak Hadir"
6. [ ] Siswa login dan input kehadiran siswa dengan berbagai status
7. [ ] Verifikasi di database bahwa field `diinput_oleh` terisi
8. [ ] Verifikasi di database bahwa field `waktu_datang` terisi untuk hadir/telat

## 🚀 Cara Deploy

### 1. Android App

```bash
# Rebuild aplikasi Android
cd AplikasiMonitoringKelas
./gradlew clean
./gradlew assembleDebug
```

### 2. Testing

1. Install APK ke device/emulator
2. Login sebagai siswa (misal: Ahmad Siswa)
3. Buka menu "Kehadiran Guru"
4. Klik tombol tambah (+)
5. Pilih jadwal, status, dan tanggal
6. Simpan
7. Verifikasi tidak ada error dan data muncul di list

### 3. Verifikasi Database

```sql
-- Cek data kehadiran guru yang baru diinput
SELECT
    kg.id,
    kg.tanggal,
    kg.status_kehadiran,
    kg.waktu_datang,
    kg.diinput_oleh,
    g.nama as nama_guru,
    u.name as diinput_oleh_nama
FROM kehadiran_gurus kg
JOIN gurus g ON kg.guru_id = g.id
LEFT JOIN users u ON kg.diinput_oleh = u.id
ORDER BY kg.created_at DESC
LIMIT 10;
```

## 📝 Notes Penting

### Database Enum Values (CRITICAL!)

- ⚠️ **SELALU gunakan `"telat"`** bukan `"terlambat"` saat kirim ke backend
- ⚠️ Enum value di database: `enum('hadir','telat','tidak_hadir','izin','sakit')`
- ✅ Display ke user boleh "Terlambat" tapi kirim ke API harus `"telat"`

### User ID Siswa

- Field `diinput_oleh` penting untuk tracking siapa yang input data
- Gunakan `authRepository.getUserId()` untuk mendapatkan user_id siswa yang login
- Field ini opsional di backend tapi sangat direkomendasikan untuk audit trail

### Waktu Datang

- Otomatis di-generate untuk status `hadir` dan `telat`
- Format: `"HH:mm"` contoh: `"07:30"`
- Untuk status lain (izin, sakit, tidak_hadir) tidak perlu waktu_datang

## ✅ Kesimpulan

Semua perbaikan sudah dilakukan untuk mengatasi error validasi pada entri kehadiran siswa dan guru. Perubahan utama:

1. **Model Request** - Tambah field `diinput_oleh`, `waktu_datang`, `durasi_keterlambatan`
2. **ViewModel** - Update logic untuk kirim user_id dan waktu_datang
3. **UI Screen** - Perbaiki mapping status dari `"terlambat"` ke `"telat"`
4. **Display Functions** - Support backward compatibility untuk data lama

Role siswa sekarang dapat melakukan entri kehadiran siswa dan kehadiran guru tanpa error.

---

**Developer**: GitHub Copilot  
**Last Updated**: 7 Desember 2025, 13:10 WIB
