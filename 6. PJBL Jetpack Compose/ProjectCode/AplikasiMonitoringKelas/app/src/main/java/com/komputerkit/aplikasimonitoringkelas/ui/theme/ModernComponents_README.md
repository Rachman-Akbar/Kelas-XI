# Modern UI Components - Panduan Penggunaan

## Komponen yang Tersedia

### 1. StandardPageTitle

Judul halaman yang konsisten untuk semua screen list

```kotlin
StandardPageTitle(
    title = "Jadwal Pelajaran",
    icon = Icons.Default.CalendarToday
)
```

### 2. StandardDashboardCard

Card untuk menu dashboard (home screen)

```kotlin
StandardDashboardCard(
    title = "Jadwal Pelajaran",
    description = "Lihat jadwal",
    icon = Icons.Default.CalendarToday,
    onClick = { }
)
```

### 3. StandardStatCard

Card statistik dengan icon dan nilai

```kotlin
StandardStatCard(
    label = "Total Kelas",
    value = "12",
    icon = Icons.Default.Class
)
```

### 4. StandardDataCard

Card untuk menampilkan list item data

```kotlin
StandardDataCard {
    // Content here
}
```

### 5. StandardFilterButton

Tombol filter yang modern

```kotlin
StandardFilterButton(
    label = "Status",
    selectedValue = "Semua",
    icon = Icons.Default.FilterList,
    onClick = { }
)
```

### 6. StandardStatusBadge

Badge untuk menampilkan status

```kotlin
StandardStatusBadge(
    status = "Disetujui" // atau "Pending", "Ditolak"
)
```

### 7. ModernFilterDialog

Dialog filter yang cantik

```kotlin
ModernFilterDialog(
    title = "Pilih Status",
    options = listOf("Semua", "Hadir", "Izin"),
    selectedOption = selectedStatus,
    onDismiss = { },
    onSelect = { selected -> }
)
```

### 8. ModernFAB

Floating Action Button untuk entri (guru role)

```kotlin
ModernFAB(
    onClick = { },
    icon = Icons.Default.Add,
    text = "Tambah Izin"
)
```

### 9. ModernEmptyState

Tampilan ketika tidak ada data

```kotlin
ModernEmptyState(
    icon = Icons.Default.EventBusy,
    title = "Tidak Ada Data",
    message = "Belum ada jadwal",
    onResetFilters = { }
)
```

## Palet Warna Modern

- **PrimaryBlue**: #2196F3 (Soft Blue)
- **LightBlue**: #E3F2FD (Background filter/info)
- **CardWhite**: #FFFFFF (Card background)
- **BackgroundWhite**: #FAFAFA (Screen background)

## Aturan Konsistensi

1. ✅ Semua halaman list menggunakan `StandardPageTitle`
2. ✅ Semua filter menggunakan `StandardFilterButton` + `ModernFilterDialog`
3. ✅ Semua card data menggunakan `StandardDataCard`
4. ✅ Semua status badge menggunakan `StandardStatusBadge`
5. ✅ Tidak ada warna merah/pink, hanya biru, hijau, amber
6. ✅ FAB untuk entri data di guru role menggunakan `ModernFAB`
7. ✅ Empty state menggunakan `ModernEmptyState`
