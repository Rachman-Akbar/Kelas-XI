# UMKM Business App - Aplikasi Manajemen UMKM

Aplikasi mobile berbasis Android untuk mengelola operasional UMKM (Usaha Mikro, Kecil, dan Menengah) dengan fitur lengkap untuk manajemen Produk, Bahan Baku, Produksi, Penjualan, dan Keuangan.

## 🎯 Fitur Utama

### 1. **Dashboard Eksekutif**
- Ringkasan KPI harian (Penjualan, Produksi, Laba)
- Akses cepat ke semua modul
- Notifikasi stok rendah
- Grafik performa bisnis

### 2. **Manajemen Produk**
- Daftar produk dengan pencarian dan filter
- Detail produk lengkap (stok, harga, HPP, margin)
- Tracking riwayat produksi dan penjualan
- Grid dan List view

### 3. **Bahan Baku (Inventory)**
- Monitoring stok bahan baku
- Alert stok rendah/habis
- Riwayat restock
- Kategorisasi bahan baku

### 4. **Produksi**
- Input produksi baru
- Tracking penggunaan bahan baku
- Status produksi (Pending, In Progress, Completed)
- Kalkulasi biaya produksi otomatis

### 5. **Penjualan**
- Katalog produk untuk penjualan
- Checkout dengan multiple items
- Riwayat transaksi penjualan
- Tracking profit per transaksi

### 6. **Keuangan**
- Dashboard keuangan
- Tracking pemasukan & pengeluaran
- Laporan laba rugi
- Analisis margin keuntungan

### 7. **Profil & Pengaturan**
- Manajemen user profile
- Role-based access (Owner, Manager, Staff)
- Pengaturan aplikasi
- Logout

## 🏗️ Arsitektur Aplikasi

Aplikasi ini dibangun dengan **Clean Architecture** dan **Separation of Concerns**:

```
app/src/main/java/com/komputerkit/business/
│
├── models/                  # Data Models
│   ├── User.kt
│   ├── Product.kt
│   ├── Material.kt
│   ├── Production.kt
│   ├── Sale.kt
│   └── Finance.kt
│
├── components/              # Reusable UI Components
│   ├── common/
│   │   ├── AppButton.kt
│   │   ├── AppInput.kt
│   │   └── AppCard.kt
│   ├── produk/
│   │   └── ProductCard.kt
│   ├── bahan_baku/
│   │   └── MaterialCard.kt
│   ├── penjualan/
│   │   └── SalesCard.kt
│   └── keuangan/
│       └── FinanceCard.kt
│
├── screens/                 # Screen/Page Components
│   ├── auth/
│   │   ├── SplashScreen.kt
│   │   └── LoginScreen.kt
│   ├── dashboard/
│   │   └── DashboardScreen.kt
│   ├── produk/
│   │   ├── ProductListScreen.kt
│   │   └── ProductDetailScreen.kt
│   ├── bahan_baku/
│   │   └── MaterialListScreen.kt
│   ├── produksi/
│   │   └── ProductionListScreen.kt
│   ├── penjualan/
│   │   ├── SalesCatalogScreen.kt
│   │   └── SalesListScreen.kt
│   ├── keuangan/
│   │   └── FinanceSummaryScreen.kt
│   └── profile/
│       └── ProfileScreen.kt
│
├── navigation/
│   ├── Screen.kt           # Route definitions
│   └── AppNavigator.kt     # Navigation graph
│
├── utils/
│   ├── Formatter.kt        # Currency, date formatting
│   └── Constants.kt        # App constants
│
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Clean Architecture with MVVM pattern
- **Navigation**: Compose Navigation
- **Material Design**: Material 3 (Material You)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36

## 📱 UI/UX Design Principles

1. **Material Design 3** - Modern dan konsisten
2. **Responsive Layout** - Adaptif untuk berbagai ukuran layar
3. **Dark/Light Theme** - Mendukung kedua mode
4. **Intuitive Navigation** - Navigasi yang mudah dipahami
5. **Performance Optimized** - Lazy loading dan efficient rendering

## 🎨 Color Palette

- **Primary**: `#2A526F` (Blue)
- **Secondary**: `#10B981` (Green)
- **Accent**: `#3B82F6` (Light Blue)
- **Warning**: `#F59E0B` (Orange)
- **Error**: `#EF4444` (Red)
- **Success**: `#10B981` (Green)

## 📊 Data Flow

```
┌─────────────┐
│   Screen    │ ← User Interaction
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Component  │ ← Reusable UI
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Model     │ ← Data Structure
└─────────────┘
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog atau lebih baru
- JDK 11 atau lebih baru
- Android SDK API 24+

### Installation

1. Clone repository
```bash
git clone <repository-url>
```

2. Open project di Android Studio

3. Sync Gradle files

4. Run aplikasi
```bash
./gradlew installDebug
```

## 📝 Konsep Bisnis & Alur Data

### Alur Bisnis UMKM:

1. **Bahan Baku** → Input sekali, digunakan untuk banyak produk
2. **Produk** → Mengambil bahan baku, menghitung HPP otomatis
3. **Produksi** → Mengurangi stok bahan baku, menambah stok produk
4. **Penjualan** → Mengurangi stok produk, menambah pemasukan
5. **Keuangan** → Menghitung laba rugi, margin per produk

### Kalkulasi:
- **HPP (Harga Pokok Penjualan)** = Total biaya bahan baku / Jumlah produk
- **Profit** = Harga Jual - HPP
- **Margin** = (Profit / HPP) × 100%

## 🔐 Role-Based Access

- **Owner**: Full access ke semua fitur
- **Manager**: Access ke operasional dan laporan
- **Staff**: Access terbatas untuk operasional harian

## 📈 Future Enhancements

- [ ] Backend integration dengan REST API
- [ ] Real-time sync dengan Firebase
- [ ] Barcode scanner untuk produk
- [ ] Export laporan ke PDF/Excel
- [ ] Multi-branch support
- [ ] Analytics & insights AI-powered
- [ ] Integrasi payment gateway
- [ ] Push notifications
- [ ] Offline mode dengan local database

## 👥 Contributors

- Developer: UMKM Business Team

## 📄 License

Copyright © 2024 UMKM Business App. All rights reserved.

---

**Note**: Aplikasi ini adalah frontend prototype tanpa logic backend yang kompleks. Fokus pada design dan user experience berdasarkan referensi dari folder `stitch`.
