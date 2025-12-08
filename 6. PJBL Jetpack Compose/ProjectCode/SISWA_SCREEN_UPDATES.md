# Panduan Update Screen Siswa

## Format Referensi dari Kepsek

### Struktur Card Modern:

1. **Header Row**: Title (bold) + Badge/Status di kanan
2. **Divider**: Garis pemisah setelah header
3. **Content Section**: Icon + Text dalam column dengan spacing 8dp
4. **Footer**: Badge tambahan jika diperlukan (di kanan)

### Pattern Konsisten:

- Title: `MaterialTheme.typography.titleMedium`, `FontWeight.Bold`, `ModernColors.TextPrimary`
- Icon: size 16.dp dengan warna sesuai konteks (PrimaryBlue, InfoBlue, WarningAmber, SuccessGreen)
- Text Content: `MaterialTheme.typography.bodyMedium` atau `bodySmall`
- Badge: Surface dengan shape small, padding (horizontal = 12.dp, vertical = 6.dp)
- Spacing: 8dp antar elemen, 12dp antar card

### Warna Icon:

- Book (Mata Pelajaran): `ModernColors.PrimaryBlue`
- Person (Nama): `ModernColors.InfoBlue`
- Schedule/Time: `ModernColors.WarningAmber`
- Class: `ModernColors.InfoBlue`
- Status: `ModernColors.SuccessGreen` atau sesuai status
- Date/Calendar: `ModernColors.SuccessGreen`

## File yang Perlu Diupdate:

1. ✅ SiswaStudentAttendanceScreen.kt - **UPDATED dengan semua informasi lengkap** (tanggal, nama siswa, kelas, mata pelajaran, guru, keterangan)
2. ✅ SiswaScheduleScreen.kt - **UPDATED dengan format kepsek** (kelas, hari, mata pelajaran, guru, jam)
3. ✅ SiswaTeacherPermissionListScreen.kt - **UPDATED dengan semua informasi lengkap** (tanggal, nama guru, kelas, mata pelajaran, waktu, keterangan)
4. ✅ SiswaSubstituteTeacherScreen.kt - **UPDATED dengan semua informasi lengkap** (tanggal, kelas, mata pelajaran, guru asli, guru pengganti, keterangan, catatan)
5. ✅ SiswaTeacherAttendanceScreen.kt - **UPDATED dengan semua informasi lengkap** (tanggal, nama guru, kelas, mata pelajaran, waktu datang, durasi keterlambatan, keterangan)
6. ⏳ SiswaHomeScreen.kt - **Check if needs update**

## Ringkasan Perubahan:

### ✅ SEMUA SCREEN SISWA SUDAH DIUPDATE!

Semua card di screen siswa sekarang menampilkan informasi yang **sama persis** dengan role kepsek:

1. **Format Header**: Tanggal (bold) + Nama (subtitle) + Status Badge
2. **Divider**: Garis pemisah setelah header
3. **Content Section**: Icon 16dp + Text dengan spacing 8dp
4. **Warna Konsisten**:
   - Book (Mata Pelajaran) = PrimaryBlue
   - Person (Nama) = InfoBlue
   - Schedule (Waktu) = WarningAmber
   - Class (Kelas) = InfoBlue
   - Timer (Keterlambatan) = ErrorRed
   - Status = SuccessGreen/sesuai status
5. **Keterangan**: Menggunakan StandardInfoCard yang sama

### Informasi Lengkap yang Ditampilkan:

#### 1. Kehadiran Siswa

- Tanggal & Nama Siswa
- Kelas
- Mata Pelajaran
- Guru
- Keterangan (jika ada)

#### 2. Jadwal Pelajaran

- Nama Kelas & Hari
- Mata Pelajaran
- Guru
- Waktu/Jam
- Jam ke-X

#### 3. Izin Guru

- Tanggal & Nama Guru
- Kelas
- Mata Pelajaran
- Waktu
- Keterangan (jika ada)

#### 4. Guru Pengganti

- Tanggal & Status
- Kelas
- Mata Pelajaran
- Guru Asli (icon merah)
- Guru Pengganti (icon hijau)
- Keterangan (jika ada)
- Catatan Approval (jika ada)

#### 5. Kehadiran Guru

- Tanggal & Nama Guru
- Kelas
- Mata Pelajaran
- Waktu Datang
- Durasi Keterlambatan (jika ada, warna merah)
- Keterangan (jika ada)

## Title Format:

- Tambahkan nama kelas di title: `"[Screen Name] - [Kelas Name]"`
- Fallback ke Kelas ID jika nama tidak tersedia
- Contoh: `"Jadwal Pelajaran - XII RPL 1"` atau `"Jadwal Pelajaran - Kelas ID: 1"`
