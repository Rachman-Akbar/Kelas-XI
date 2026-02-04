# 🎯 Overview SI-UMKM Mobile

## Tentang Aplikasi

**SI-UMKM Mobile** adalah aplikasi Android native untuk mengelola operasional usaha kecil menengah, khususnya bisnis produksi. Aplikasi ini dibangun menggunakan **Jetpack Compose** dengan arsitektur modern Android.

### Informasi Proyek

- **Platform**: Android (Min SDK 24, Target SDK 34)
- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose + Material Design 3
- **Arsitektur**: MVVM Pattern
- **Navigation**: Jetpack Navigation Compose

---

## 🎯 Tujuan Aplikasi

1. **Multi-Role Dashboard** - Dashboard khusus untuk setiap role (Produksi, Keuangan, Penjualan)
2. **Real-time Monitoring** - Pantau stok, produksi, dan penjualan secara real-time
3. **Offline-First** - Tetap bisa digunakan tanpa koneksi internet
4. **Modern UI/UX** - Interface yang clean, simple, dan mudah digunakan

---

## 🏗️ Arsitektur Aplikasi

### Layered Architecture

```
┌─────────────────────────────────────────────┐
│         PRESENTATION LAYER                  │
│  ┌──────────────────────────────────────┐   │
│  │  Screens (Composables)               │   │
│  │  - ProductionDashboardScreen         │   │
│  │  - SalesDashboardScreen              │   │
│  │  - FinanceDashboardScreen            │   │
│  │  - MaterialListScreen                │   │
│  │  - ProductListScreen                 │   │
│  └────────────┬─────────────────────────┘   │
└───────────────┼─────────────────────────────┘
                │
┌───────────────▼─────────────────────────────┐
│         NAVIGATION LAYER                    │
│  ┌──────────────────────────────────────┐   │
│  │  Navigation Graph                    │   │
│  │  - Screen.kt (Route definitions)     │   │
│  │  - AppNavigator.kt (NavHost)         │   │
│  └──────────────────────────────────────┘   │
└───────────────┬─────────────────────────────┘
                │
┌───────────────▼─────────────────────────────┐
│         COMPONENT LAYER                     │
│  ┌──────────────────────────────────────┐   │
│  │  Reusable Components                 │   │
│  │  - AppBottomNavigation               │   │
│  │  - MaterialCard                      │   │
│  │  - ProductCard                       │   │
│  │  - SalesProductCard                  │   │
│  └──────────────────────────────────────┘   │
└───────────────┬─────────────────────────────┘
                │
┌───────────────▼─────────────────────────────┐
│         DATA LAYER                          │
│  ┌──────────────────────────────────────┐   │
│  │  Models & Data Classes               │   │
│  │  - Material                          │   │
│  │  - Product                           │   │
│  │  - Production                        │   │
│  │  - Customer                          │   │
│  │  - Transaction                       │   │
│  └──────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

---

## 📱 Struktur Role

### 1. Role Produksi

**Bottom Navigation** (4 icons):

- 🏠 **Home** → Dashboard utama produksi
- 📦 **Bahan Baku** → Manajemen bahan baku & restock
- 📋 **Produk** → List produk & komposisi
- 🏭 **Produksi** → Jadwal & riwayat produksi

**Fitur Utama**:

- Monitor stok bahan baku real-time
- Restock bahan baku dengan form validasi
- Lihat komposisi produk dan analisis biaya
- Jadwal produksi dengan tracking material usage
- Riwayat produksi lengkap dengan filter

---

### 2. Role Keuangan

**Bottom Navigation** (4 icons):

- 🏠 **Home** → Dashboard keuangan
- 📊 **HPP-BEP** → Analisis biaya & break-even point
- 💰 **Utang** → Manajemen utang & piutang
- 🧾 **Transaksi** → Riwayat transaksi lengkap

**Fitur Utama**:

- Dashboard dengan total saldo, pemasukan, pengeluaran
- Analisis HPP (Harga Pokok Penjualan) per produk
- Perhitungan BEP (Break Even Point) otomatis
- Manajemen utang dan piutang dengan reminder
- Input transaksi dengan multiple payment methods
- Laporan keuangan dengan filter periode

---

### 3. Role Penjualan ⭐ NEW

**Bottom Navigation** (3 icons):

- 🏠 **Home** → Dashboard penjualan
- 💳 **Kasir** → Point of Sale (POS)
- 👥 **Pelanggan** → Manajemen pelanggan

**Fitur Utama**:

- Dashboard dengan statistik penjualan
- POS dengan cart management & checkout
- Customer management dengan purchase history
- Sales history dengan filter & search
- Multiple payment methods support

---

## 🎨 Design System

### Color Palette

```kotlin
Primary: Color(0xFF197FE6)      // Blue - Main actions
Secondary: Color(0xFF0D4C8C)    // Dark Blue - Gradients
Success: Color(0xFF10B981)      // Green - Success states
Warning: Color(0xFFF59E0B)      // Orange - Warnings
Error: Color(0xFFEF4444)        // Red - Errors/Delete
Purple: Color(0xFF8B5CF6)       // Purple - Special features
Background: Color(0xFFF8FAFC)   // Light Gray - Background
Surface: Color.White            // White - Cards
Outline: Color(0xFFE2E8F0)      // Gray - Borders
```

### Typography

```kotlin
Display: 32sp, Bold              // Page titles
Headline: 24sp, Bold             // Section headers
Title: 20sp, SemiBold            // Card headers
Body Large: 16sp, Regular        // Main content
Body Medium: 14sp, Regular       // Secondary content
Body Small: 12sp, Regular        // Captions
Label: 11sp, SemiBold, UpperCase // Labels
```

### Spacing System

```kotlin
XS: 4.dp     // Tight spacing
S: 8.dp      // Small spacing
M: 12.dp     // Medium spacing
L: 16.dp     // Large spacing (standard)
XL: 20.dp    // Extra large spacing
XXL: 24.dp   // Section spacing
```

### Component Sizes

```kotlin
// Icons
Icon Small: 18.dp
Icon Medium: 24.dp
Icon Large: 36-40.dp

// Buttons
Button Height: 48.dp
Icon Button: 36.dp circle

// Input Fields
TextField Height: 52.dp
TextField Border: 1-2.dp

// Cards
Card Border: 1.dp
Card Border Radius: 12-16.dp
Card Elevation: 1-4.dp
```

---

## 🧩 Prinsip Desain

### 1. Consistency First

Semua screen mengikuti pattern yang sama:

- Header dengan statusBarsPadding()
- Search bar dengan height 52.dp
- Bottom navigation dengan role-based filtering
- Icon dengan circular background & colored borders

### 2. Clean & Simple

- Minimal clutter, fokus pada fungsi utama
- White space yang cukup untuk readability
- Icon yang jelas dan bermakna
- Typography hierarchy yang konsisten

### 3. Feedback Visual

- Loading states untuk async operations
- Success/error messages yang jelas
- Disabled states untuk unavailable actions
- Badge counts untuk notifications

### 4. Mobile-First

- Touch targets minimal 48.dp
- Swipe gestures untuk common actions
- Bottom sheet untuk quick actions
- Responsive layout untuk berbagai screen sizes

---

## 📂 Struktur Folder

```
app/src/main/java/com/komputerkit/business/
├── components/
│   ├── common/
│   │   └── AppBottomNavigation.kt
│   ├── bahan_baku/
│   │   └── MaterialCard.kt
│   ├── produk/
│   │   └── ProductCard.kt
│   └── penjualan/
│       └── SalesProductCard.kt
│
├── screens/
│   ├── auth/
│   │   ├── SplashScreen.kt
│   │   └── LoginScreen.kt
│   │
│   ├── produksi/
│   │   ├── ProductionDashboardScreen.kt
│   │   ├── ProductionListScreen.kt
│   │   ├── ProductionHistoryScreen.kt
│   │   ├── ProductionFormScreen.kt
│   │   ├── ProductionDetailScreen.kt
│   │   └── ProductionEditScreen.kt
│   │
│   ├── bahan_baku/
│   │   ├── MaterialListScreen.kt
│   │   ├── MaterialHistoryScreen.kt
│   │   ├── MaterialDetailScreen.kt
│   │   ├── RestockMaterialScreen.kt
│   │   └── MaterialSpecificHistoryScreen.kt
│   │
│   ├── produk/
│   │   ├── ProductListScreen.kt
│   │   ├── ProductDetailScreen.kt
│   │   ├── ProductCompositionScreen.kt
│   │   └── RestockProductScreen.kt
│   │
│   ├── keuangan/
│   │   ├── FinanceDashboardScreen.kt
│   │   ├── HppBepAnalysisScreen.kt
│   │   ├── TransactionHistoryScreen.kt
│   │   ├── TransactionFormScreen.kt
│   │   ├── DebtReceivableScreen.kt
│   │   ├── DebtReceivableHistoryScreen.kt
│   │   └── DebtFormScreen.kt
│   │
│   ├── penjualan/
│   │   ├── SalesDashboardScreen.kt
│   │   ├── SalesPOSScreen.kt
│   │   ├── CustomerListScreen.kt
│   │   ├── CustomerFormScreen.kt (TODO)
│   │   ├── CustomerDetailScreen.kt (TODO)
│   │   ├── SalesCheckoutScreen.kt
│   │   ├── SalesHistoryScreen.kt
│   │   ├── SalesCatalogScreen.kt
│   │   ├── SalesFormScreen.kt
│   │   └── SalesListScreen.kt
│   │
│   └── profile/
│       └── ProfileScreen.kt
│
├── navigation/
│   ├── Screen.kt
│   └── AppNavigator.kt
│
├── models/
│   ├── Material.kt
│   ├── Product.kt
│   ├── Production.kt
│   ├── Customer.kt
│   └── Transaction.kt
│
├── data/
│   └── CartManager.kt
│
└── utils/
    └── Formatter.kt
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK 34
- Gradle 8.0+

### Build & Run

```bash
# Clone repository
git clone [repository-url]

# Open project di Android Studio
# File → Open → Pilih folder project

# Sync Gradle
# Build → Make Project

# Run app
# Run → Run 'app'
```

### Build Variants

- **debug**: Development build dengan logging
- **release**: Production build dengan ProGuard

---

## 📊 Fitur Utama Aplikasi

### ✅ Implemented Features

1. **Authentication**
   - Splash screen dengan auto-redirect
   - Login dengan role selection
   - Role-based navigation

2. **Produksi Role**
   - Dashboard dengan quick stats
   - Material management dengan restock
   - Product list dengan composition view
   - Production scheduling & tracking
   - History dengan filter tanggal & kategori

3. **Keuangan Role**
   - Dashboard keuangan dengan saldo
   - HPP-BEP analysis
   - Transaction management
   - Debt & receivable tracking

4. **Penjualan Role** ⭐ NEW
   - Sales dashboard dengan statistik
   - Point of Sale (POS) dengan cart
   - Customer management
   - Sales history

### 🚧 Upcoming Features

1. **Offline Mode**
   - Local database dengan Room
   - Sync queue untuk pending operations
   - Conflict resolution

2. **Advanced Analytics**
   - Interactive charts
   - Export to PDF/Excel
   - Custom date range filters

3. **Notifications**
   - Stock alerts
   - Payment reminders
   - Production schedule notifications

4. **User Management**
   - Profile settings
   - Change password
   - Activity logs

---

## 📖 Dokumentasi Terkait

- [01-MODELS.md](./01-MODELS.md) - Data models & entities
- [02-SCREENS.md](./02-SCREENS.md) - Screen documentation
- [03-COMPONENTS.md](./03-COMPONENTS.md) - Reusable components
- [04-NAVIGATION.md](./04-NAVIGATION.md) - Navigation flow
- [05-FEATURES-PRODUKSI.md](./05-FEATURES-PRODUKSI.md) - Role Produksi features
- [06-FEATURES-KEUANGAN.md](./06-FEATURES-KEUANGAN.md) - Role Keuangan features
- [07-FEATURES-PENJUALAN.md](./07-FEATURES-PENJUALAN.md) - Role Penjualan features
- [08-UI-FLOWS.md](./08-UI-FLOWS.md) - User flows per role
- [09-DESIGN-SYSTEM.md](./09-DESIGN-SYSTEM.md) - Design guidelines

---

## 🤝 Contributing

Untuk kontribusi, pastikan mengikuti:

- Kotlin coding conventions
- Jetpack Compose best practices
- Material Design 3 guidelines
- Existing architecture patterns

---

## 📝 License

Copyright © 2026 SI-UMKM Mobile Team
