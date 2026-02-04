# 🗺️ UI Flows & Navigation

Dokumentasi alur penggunaan aplikasi untuk setiap role pengguna dalam SI-UMKM Mobile.

---

## 🎭 Role & Screen Access Matrix

| Screen                    | Produksi | Keuangan | Penjualan      |
| ------------------------- | -------- | -------- | -------------- |
| SplashScreen              | ✅       | ✅       | ✅             |
| LoginScreen               | ✅       | ✅       | ✅             |
| ProductionDashboardScreen | ✅       | ❌       | ❌             |
| MaterialListScreen        | ✅       | ❌       | ❌             |
| ProductListScreen         | ✅       | ❌       | ✅ (read-only) |
| ProductionListScreen      | ✅       | ❌       | ❌             |
| FinanceDashboardScreen    | ❌       | ✅       | ❌             |
| HppBepAnalysisScreen      | ❌       | ✅       | ❌             |
| TransactionHistoryScreen  | ❌       | ✅       | ❌             |
| SalesDashboardScreen      | ❌       | ❌       | ✅             |
| SalesPOSScreen            | ❌       | ❌       | ✅             |
| CustomerListScreen        | ❌       | ❌       | ✅             |
| ProfileScreen             | ✅       | ✅       | ✅             |

---

## 🏭 FLOW: ROLE PRODUKSI

### App Start Flow

```
SplashScreen (2s)
    ↓
LoginScreen
    ↓
Select "Produksi"
    ↓
ProductionDashboardScreen
```

### Daily Workflow

#### Morning Routine (08:00)

```
1. ProductionDashboardScreen
   - Lihat overview stats
   - Check "Produksi Hari Ini" card

2. MaterialListScreen (via bottom nav atau quick access)
   - Scan stock status
   - Identifikasi material "Menipis"

3. RestockMaterialScreen (jika ada yang menipis)
   - Input jumlah restock
   - Update stock
   - Save

4. ProductionListScreen (via bottom nav)
   - Review jadwal hari ini
   - Lihat production yang "Planned"
```

#### Production Process

```
ProductionListScreen
    ↓
Tap production card
    ↓
ProductionDetailScreen
    ↓
Click "Mark as InProgress"
    ↓
[Produksi berlangsung...]
    ↓
Update status "InProgress" → "Completed"
    ↓
Stock produk otomatis bertambah
Material stock otomatis berkurang
    ↓
Back to ProductionListScreen
```

#### Add New Production Schedule

```
ProductionListScreen
    ↓
Tap three-dot menu "Tambah Jadwal" atau FAB
    ↓
ProductionFormScreen
    ↓
[Fill form]
- Pilih Produk: "Kopi Latte"
- Jumlah: 50
- Tanggal: 31 Jan 2026
- Status: Planned
- Operator: "Ahmad"
    ↓
Tap "Simpan"
    ↓
[Validation]
    ↓
Create production record
    ↓
Navigate back
    ↓
ProductionListScreen (updated list)
```

#### View Product Composition

```
ProductListScreen
    ↓
Tap product three-dot menu
    ↓
Select "Komposisi"
    ↓
ProductCompositionScreen
    ↓
Lihat:
- Material list dengan quantities
- Total material cost
- Profit margin
```

#### Check Material History

```
MaterialListScreen
    ↓
Tap material three-dot menu
    ↓
Select "Lihat Riwayat"
    ↓
MaterialSpecificHistoryScreen
    ↓
View timeline:
- Restock events (+)
- Production usage (-)
- Stock adjustments
```

---

## 💰 FLOW: ROLE KEUANGAN

### App Start Flow

```
SplashScreen (2s)
    ↓
LoginScreen
    ↓
Select "Keuangan"
    ↓
FinanceDashboardScreen
```

### Daily Workflow

#### Morning Routine (08:00)

```
1. FinanceDashboardScreen
   - Check total saldo (toggle visibility)
   - Review pemasukan vs pengeluaran
   - Lihat transaksi terakhir

2. DebtReceivableScreen (via quick access atau bottom nav)
   - Check utang jatuh tempo hari ini
   - Check piutang yang perlu ditagih

3. TransactionHistoryScreen (via bottom nav)
   - Review transaksi kemarin
   - Verify semua sudah tercatat
```

#### Record New Transaction

```
TransactionHistoryScreen
    ↓
Tap FAB "+"
    ↓
TransactionFormScreen
    ↓
[Fill form]
1. Select type: Pemasukan / Pengeluaran
2. Title: "Pembelian Tepung"
3. Kategori: "Bahan Baku"
4. Jumlah: Rp 500.000
5. Tanggal & Waktu: 31 Jan 2026, 10:30
6. Metode: Transfer Bank
7. Keterangan: "Invoice #INV-001"
    ↓
Tap "Simpan"
    ↓
[Validation]
    ↓
Create transaction record
Update saldo
    ↓
Navigate back
    ↓
TransactionHistoryScreen (updated)
```

#### HPP-BEP Analysis

```
FinanceDashboardScreen
    ↓
Tap "Analisis HPP-BEP"
    ↓
HppBepAnalysisScreen
    ↓
[Tab HPP]
1. Select product: "Kopi Latte"
2. View breakdown:
   - Material cost: Rp 12.000
   - Labor cost: Rp 2.000
   - Overhead: Rp 1.000
   - Total HPP: Rp 15.000
   - Selling price: Rp 25.000
   - Profit: Rp 10.000
   - Margin: 40%

[Tab BEP]
1. Input fixed costs: Rp 10.000.000
2. Input variable cost/unit: Rp 15.000
3. Input selling price: Rp 25.000
4. View results:
   - BEP (units): 1.000 pcs
   - BEP (Rp): Rp 25.000.000
   - Current sales: 500 pcs
   - Margin of safety: 50% below BEP
```

#### Manage Debt/Receivable

```
DebtReceivableScreen
    ↓
[Tab Utang]
    ↓
View list utang
    ↓
Tap "Tambah Utang"
    ↓
DebtFormScreen
    ↓
[Fill form]
- Type: Utang
- Kreditor: "Supplier Bahan A"
- Jumlah: Rp 5.000.000
- Jatuh Tempo: 28 Feb 2026
- Deskripsi: "Pembelian bahan baku"
    ↓
Save
    ↓
Back to DebtReceivableScreen
    ↓
[Bayar utang]
Tap utang item
    ↓
DebtReceivableHistoryScreen
    ↓
Tap "Bayar"
    ↓
Input jumlah pembayaran
    ↓
Update status & saldo
```

#### Weekly Report

```
FinanceDashboardScreen
    ↓
Set period filter: "Mingguan"
    ↓
View updated stats:
- Total pemasukan minggu ini
- Total pengeluaran minggu ini
- Net profit/loss
    ↓
Tap "Lihat Semua" transaksi
    ↓
TransactionHistoryScreen (filtered weekly)
    ↓
Export to Excel/PDF (future feature)
```

---

## 🛒 FLOW: ROLE PENJUALAN

### App Start Flow

```
SplashScreen (2s)
    ↓
LoginScreen
    ↓
Select "Penjualan"
    ↓
SalesDashboardScreen
```

### Daily Workflow

#### Morning Routine (08:00)

```
1. SalesDashboardScreen
   - Set filter: "Harian"
   - Lihat total penjualan kemarin
   - Check transaksi terakhir

2. CustomerListScreen (via bottom nav)
   - Review customer baru (jika ada)
   - Check customer dengan pending payment
```

#### POS Transaction Flow (Complete)

```
SalesDashboardScreen
    ↓
Tap "POS" quick action (atau bottom nav)
    ↓
SalesPOSScreen
    ↓
[Step 1: Product Selection]
1. Search "Kopi" atau filter "Minuman"
2. Product grid muncul
3. Tap "Kopi Latte" card
   → Product added to cart (qty = 1)
   → Card border berubah hijau
   → Cart badge shows "1"

4. Tap "+" button
   → Quantity jadi 2
   → Cart badge shows "2"

5. Tap "Cappuccino" card
   → Added to cart
   → Cart badge shows "3"

6. Cart summary bar muncul di bottom:
   "3 items - Rp 78.000"
   [Checkout →]
    ↓
[Step 2: Checkout]
Tap "Checkout" button
    ↓
SalesCheckoutScreen
    ↓
[Fill checkout form]
1. Cart items summary (read-only):
   - 2× Kopi Latte = Rp 50.000
   - 1× Cappuccino = Rp 28.000

2. Customer:
   - Option A: Select from dropdown
   - Option B: "Walk-in Customer"

3. Payment method:
   Select "Cash"

4. Amount received: Rp 100.000
   Auto-calculate:
   → Kembalian: Rp 22.000

5. Notes (optional): "-"
    ↓
[Step 3: Process]
Tap "Process Payment"
    ↓
[Validation]
✓ Payment method selected
✓ Amount >= Total
✓ Cart not empty
    ↓
[Backend Process]
1. Create sale record
2. Update product stock:
   - Kopi Latte: 45 → 43
   - Cappuccino: 32 → 31
3. Update customer history (if not walk-in)
4. Generate transaction ID: #TX-9022
    ↓
[Success]
Show success dialog:
"Pembayaran Berhasil"
Transaction ID: #TX-9022
Kembalian: Rp 22.000
[Print Receipt] [Selesai]
    ↓
Clear cart
Navigate to SalesDashboardScreen
```

#### Add New Customer

```
CustomerListScreen
    ↓
Tap FAB "+" atau header "Add" icon
    ↓
CustomerFormScreen (future)
    ↓
[Fill form]
- Name: "Ahmad Wijaya"
- Phone: "081234567890"
- Email: "ahmad@email.com" (optional)
- Address: "Jl. Merdeka No. 10" (optional)
- Notes: "-"
    ↓
Tap "Simpan"
    ↓
[Validation]
✓ Name not empty
✓ Phone valid format
    ↓
Create customer record
    ↓
Navigate back
    ↓
CustomerListScreen (updated)
```

#### View Sales History

```
SalesDashboardScreen
    ↓
Tap "Riwayat" quick action atau "Lihat Semua"
    ↓
SalesHistoryScreen
    ↓
[Filter options]
- Semua
- Hari Ini ← selected
- Minggu Ini
- Bulan Ini
    ↓
View list transaksi hari ini
    ↓
Tap transaction card
    ↓
SalesDetailScreen (future)
    ↓
View details:
- Transaction ID
- Customer info
- Items list
- Payment details
- Timestamp
[Print Receipt] [Refund]
```

#### Customer Search & Sort

```
CustomerListScreen
    ↓
[Search]
Type "Ahmad" in search bar
    ↓
List filtered real-time:
→ Shows only customers with "Ahmad" in name
    ↓
Clear search
    ↓
[Sort]
Tap sort icon
    ↓
Dropdown menu:
- Nama (A-Z) ← default
- Total Pembelian
- Terakhir Beli
    ↓
Select "Total Pembelian"
    ↓
List re-sorted (highest to lowest)
```

---

## 🔀 Cross-Role Workflows

### Scenario: Restock → Production → Sales

```
[PRODUKSI ROLE]
MaterialListScreen
    ↓
Restock "Tepung Terigu" +10 kg
    ↓
ProductionListScreen
    ↓
Create production: 50× Kopi Latte
    ↓
Mark as completed
    ↓
Product stock updated: +50

[PENJUALAN ROLE]
SalesPOSScreen
    ↓
Sell 2× Kopi Latte
    ↓
Product stock updated: -2
```

### Scenario: Sales → Finance Recording

```
[PENJUALAN ROLE]
SalesPOSScreen
    ↓
Complete sale: Rp 78.000
    ↓
Transaction created

[KEUANGAN ROLE]
FinanceDashboardScreen
    ↓
Auto-update:
- Pemasukan +Rp 78.000
- Saldo +Rp 78.000
    ↓
TransactionHistoryScreen
    ↓
New entry appears:
"Penjualan #TX-9022"
```

---

## 📱 Navigation Patterns

### Bottom Navigation Behavior

```kotlin
// Tap bottom nav item
onNavigate(route)
    ↓
Check if same route as current
    ↓
if (same) {
    // Scroll to top
    scrollToTop()
} else {
    // Navigate to new screen
    navController.navigate(route) {
        popUpTo(startDestination) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
```

### Back Navigation

```kotlin
// Screens with back button
Header {
    IconButton(onClick = onNavigateBack) {
        Icon(Icons.Default.ArrowBack)
    }
}

// Implementation
onNavigateBack = {
    navController.popBackStack()
}
```

### Deep Linking (Future)

```
// Direct to specific screen
si-umkm://production/detail/123
si-umkm://material/restock/456
si-umkm://sales/checkout
```

---

## 🎯 User Journey Maps

### New User First Time

```
Install App
    ↓
Open App
    ↓
SplashScreen → LoginScreen
    ↓
Select Role: "Penjualan"
    ↓
[First time tips/tutorial] (future)
    ↓
SalesDashboardScreen
    ↓
Explore:
1. Try POS (add dummy product)
2. Check customer list (empty)
3. Add first customer
4. Complete first transaction
```

### Power User (Cashier)

```
Open App
    ↓
Quick login (remember me)
    ↓
SalesDashboardScreen
    ↓
Immediately to POS (muscle memory)
    ↓
Fast transaction flow:
- Tap products (no search needed)
- Quick checkout
- Repeat
```

---

## 🚦 Error Flows

### Network Error

```
Action (e.g., save transaction)
    ↓
Network request
    ↓
[Error: No internet]
    ↓
Show Snackbar:
"Tidak ada koneksi internet"
[Retry] [Cancel]
    ↓
if (Retry) {
    Try again
} else {
    Stay on current screen
}
```

### Validation Error

```
ProductionFormScreen
    ↓
Fill form incompletely
    ↓
Tap "Simpan"
    ↓
[Validation fails]
    ↓
Highlight error fields (red border)
Show error messages below fields
Scroll to first error
    ↓
User fixes errors
    ↓
Retry save
```

### Stock Insufficient

```
SalesPOSScreen
    ↓
Add product to cart (qty = 50)
    ↓
[Check stock: only 45 available]
    ↓
Show dialog:
"Stok tidak cukup"
"Tersedia: 45 pcs"
"Anda ingin: 50 pcs"
[Adjust] [Cancel]
    ↓
if (Adjust) {
    Set quantity to max (45)
} else {
    Don't add to cart
}
```

---

## 📊 Navigation Metrics

### Screen Hierarchy Depth

```
Level 0: Splash, Login
Level 1: Dashboard screens (bottom nav)
Level 2: List screens
Level 3: Detail screens
Level 4: Form screens
```

### Average Clicks to Complete Task

| Task               | Clicks | Time   |
| ------------------ | ------ | ------ |
| POS Transaction    | 3-5    | 30s    |
| Add Customer       | 2      | 1min   |
| Restock Material   | 3      | 45s    |
| Create Production  | 4      | 2min   |
| Record Transaction | 4      | 1.5min |

---

## 🎨 Navigation UI Elements

### Bottom Nav Icons & Labels

**Produksi**:

- 🏠 Home
- 📦 Bahan Baku
- 📋 Produk
- 🏭 Produksi

**Keuangan**:

- 🏠 Home
- 📊 HPP-BEP
- 💰 Utang
- 🧾 Transaksi

**Penjualan**:

- 🏠 Home
- 💳 Kasir
- 👥 Pelanggan

### Header Icons

- ← Back button (left)
- ⋮ More menu (right)
- 📅 Date filter
- 🔍 Search
- ➕ Add action

---

## 📝 Notes

- **Flat Navigation**: Minimize hierarchy depth
- **Consistent Patterns**: Same flows across similar features
- **Quick Access**: Common actions always 1-2 taps away
- **State Preservation**: Bottom nav maintains screen state
- **Clear Indicators**: Active tab, loading states, errors
- **Responsive**: Handle orientation changes gracefully
