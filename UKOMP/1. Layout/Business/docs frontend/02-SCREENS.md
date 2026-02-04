# 📱 Screen Documentation

Dokumentasi lengkap semua screen dalam aplikasi SI-UMKM Mobile, dikelompokkan berdasarkan module.

---

## 📂 Screen Organization

```
Screens/
├── Auth (2 screens)
├── Produksi (6 screens)
├── Bahan Baku (5 screens)
├── Produk (4 screens)
├── Keuangan (7 screens)
├── Penjualan (13 screens) ⭐ +4 Pre-Order
└── Profile (1 screen)

Total: 38 screens
```

---

## 🔐 AUTH SCREENS

### 1. SplashScreen

**Path**: `screens/auth/SplashScreen.kt`

**Purpose**: Entry point aplikasi dengan delay 2 detik sebelum redirect ke Login.

**UI Components**:

- Logo aplikasi (centered)
- App name
- Loading indicator
- Gradient background

**Flow**:

```
SplashScreen (2s delay)
    ↓
LoginScreen
```

**Code Structure**:

```kotlin
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToLogin()
    }

    // UI: Centered logo + loading
}
```

---

### 2. LoginScreen

**Path**: `screens/auth/LoginScreen.kt`

**Purpose**: Login dengan pemilihan role pengguna.

**UI Components**:

- Welcome text
- Role selector (3 options):
  - 👷 Produksi
  - 💰 Keuangan
  - 🛒 Penjualan
- Login button
- Remember me checkbox (optional)

**Flow**:

```
LoginScreen
    ├── Select "Produksi" → ProductionDashboardScreen
    ├── Select "Keuangan" → FinanceDashboardScreen
    └── Select "Penjualan" → SalesDashboardScreen
```

**Navigation Logic**:

```kotlin
val destination = when (selectedRole) {
    "Keuangan" -> Screen.FinanceDashboard.route
    "Penjualan" -> Screen.SalesDashboard.route
    "Produksi" -> Screen.Dashboard.route
    else -> Screen.Dashboard.route
}
```

---

## 🏭 PRODUKSI SCREENS

### 3. ProductionDashboardScreen

**Path**: `screens/produksi/ProductionDashboardScreen.kt`

**Purpose**: Dashboard utama role produksi dengan overview dan quick access.

**UI Components**:

- **Header**:
  - Profile icon (top-left)
  - Title "DASHBOARD PRODUKSI"
  - Date icon (top-right)
- **Stats Cards** (3 cards):
  - 📦 Total Bahan Baku
  - 📋 Total Produk
  - 🏭 Produksi Hari Ini

- **Quick Access Menu** (3 buttons):
  - 📦 Bahan Baku → MaterialListScreen
  - 📋 Produk → ProductListScreen
  - 🏭 Produksi → ProductionListScreen

- **Bottom Navigation**: 4 icons (Home | Bahan Baku | Produk | Produksi)

**State Management**:

```kotlin
var showStockAlert by remember { mutableStateOf(false) }
val lowStockMaterials = materials.filter { it.isLowStock() }
```

**Navigation**:

```kotlin
onNavigateToMaterials: () -> Unit  // → MaterialListScreen
onNavigateToProducts: () -> Unit   // → ProductListScreen
onNavigateToProduction: () -> Unit // → ProductionListScreen
onNavigateToProfile: () -> Unit    // → ProfileScreen
```

---

### 4. ProductionListScreen

**Path**: `screens/produksi/ProductionListScreen.kt`

**Purpose**: List jadwal produksi dengan search & filter.

**UI Components**:

- **Header**:
  - Back button (jika dari dashboard)
  - Title "Jadwal Produksi"
  - 3 icons: Date | Category | History
- **Search Bar**: 52dp height, rounded corners

- **Production Cards**:
  - Product name & batch number
  - Quantity & date
  - Status badge (Planned/InProgress/Completed)
  - Three-dot menu (Edit | Delete)

- **Dropdown Menu** (modern circular background):
  - ✏️ Edit → ProductionEditScreen
  - 🗑️ Hapus (dengan konfirmasi)

**Features**:

- Search by product name atau batch number
- Filter by date
- Filter by category
- Sort by date (newest/oldest)

**Card Status Colors**:

```kotlin
when (status) {
    "Planned" -> Color(0xFF197FE6)      // Blue
    "InProgress" -> Color(0xFFF59E0B)   // Orange
    "Completed" -> Color(0xFF10B981)    // Green
    else -> Color.Gray
}
```

---

### 5. ProductionHistoryScreen

**Path**: `screens/produksi/ProductionHistoryScreen.kt`

**Purpose**: Riwayat produksi dengan filter lengkap.

**UI Components**:

- **Header**:
  - Back button
  - Title "Riwayat Produksi"
  - Date icon | Category icon

- **Filter Chips**:
  - Semua | Minggu Ini | Bulan Ini | Custom Range

- **History Cards**:
  - Product name
  - Batch number
  - Quantity produced
  - Production date
  - Operator name
  - Status badge

**Filter Logic**:

```kotlin
val filteredHistory = when (selectedFilter) {
    0 -> allHistory
    1 -> allHistory.filter { isThisWeek(it.date) }
    2 -> allHistory.filter { isThisMonth(it.date) }
    3 -> allHistory.filter { isInRange(it.date, startDate, endDate) }
    else -> allHistory
}
```

---

### 6. ProductionFormScreen

**Path**: `screens/produksi/ProductionFormScreen.kt`

**Purpose**: Form tambah jadwal produksi baru.

**UI Components**:

- **Header**: Title "Tambah Produksi" + save icon

- **Form Fields**:
  - Dropdown "Pilih Produk" (required)
  - TextField "Jumlah Produksi" (number, required)
  - DatePicker "Tanggal Produksi" (default: today)
  - Dropdown "Status" (Planned/InProgress/Completed)
  - TextField "Catatan" (optional, multiline)
  - TextField "Operator" (optional)

- **Bottom Bar**:
  - Cancel button (outlined)
  - Save button (filled, primary color)

**Validation**:

```kotlin
fun validateForm(): Boolean {
    return selectedProduct.isNotEmpty() &&
           quantity > 0 &&
           productionDate.isNotEmpty()
}
```

**Form Submission**:

```kotlin
onSave = {
    if (validateForm()) {
        val production = Production(
            productId = selectedProduct,
            quantity = quantity.toInt(),
            productionDate = productionDate,
            status = selectedStatus,
            notes = notes,
            operator = operator
        )
        saveProduction(production)
        onNavigateBack()
    } else {
        showValidationError = true
    }
}
```

---

### 7. ProductionDetailScreen

**Path**: `screens/produksi/ProductionDetailScreen.kt`

**Purpose**: Detail lengkap satu jadwal produksi.

**UI Components**:

- **Header**: Back button + "Detail Produksi"

- **Info Section**:
  - Batch number (large, bold)
  - Product name
  - Status badge
  - Production date
  - Operator name

- **Materials Used**:
  - List of materials with quantities
  - Total material cost

- **Timeline** (jika InProgress/Completed):
  - Start time
  - End time
  - Duration

- **Action Buttons**:
  - Edit (jika status Planned)
  - Mark as Complete (jika InProgress)
  - Print Label

---

### 8. ProductionEditScreen

**Path**: `screens/produksi/ProductionEditScreen.kt`

**Purpose**: Edit jadwal produksi yang sudah ada.

**UI Components**: Sama seperti ProductionFormScreen, tapi pre-filled.

**Additional Features**:

- Load existing data
- Disable product selection (tidak bisa ganti produk)
- Enable semua field lainnya

**Bottom Bar**:

- Cancel (discard changes)
- Update (save changes)

---

## 📦 BAHAN BAKU SCREENS

### 9. MaterialListScreen

**Path**: `screens/bahan_baku/MaterialListScreen.kt`

**Purpose**: List semua bahan baku dengan search & filter.

**UI Components**:

- **Header**:
  - Title "Bahan Baku"
  - 3 icons: Date | Category | History

- **Search Bar**: 52dp height

- **Material Cards**:
  - Material name & supplier
  - Current stock (with unit)
  - Stock status badge (Aman/Menipis)
  - Price per unit
  - Three-dot menu

**Stock Status Logic**:

```kotlin
val status = if (material.stokSaatIni <= material.batasMinimum) {
    "Menipis" to Color(0xFFEF4444)  // Red
} else {
    "Aman" to Color(0xFF10B981)     // Green
}
```

**Three-Dot Menu**:

- ✏️ Edit Material
- 📦 Restock → RestockMaterialScreen
- 📊 Lihat Riwayat → MaterialSpecificHistoryScreen
- 🗑️ Hapus

---

### 10. MaterialHistoryScreen

**Path**: `screens/bahan_baku/MaterialHistoryScreen.kt`

**Purpose**: Riwayat penggunaan semua bahan baku.

**UI Components**:

- **Header**:
  - Back button
  - Title "Riwayat Bahan Baku"
  - Date icon | Category icon

- **Filter Options**:
  - By date range
  - By material
  - By activity type (Usage/Restock)

- **History Items**:
  - Material name
  - Activity type (icon)
  - Quantity (+ for restock, - for usage)
  - Date & time
  - Related production (if usage)

---

### 11. MaterialDetailScreen

**Path**: `screens/bahan_baku/MaterialDetailScreen.kt`

**Purpose**: Detail lengkap satu bahan baku.

**UI Components**:

- **Header**: Back button + Material name

- **Info Cards**:
  - 📊 Stok Info:
    - Current stock
    - Minimum threshold
    - Stock status
  - 💰 Price Info:
    - Price per unit
    - Total inventory value
  - 🏪 Supplier Info:
    - Supplier name
    - Category

- **Usage Chart**:
  - Line chart showing stock over time
  - Usage trend analysis

- **Action Buttons**:
  - Restock
  - Edit
  - View History

---

### 12. RestockMaterialScreen

**Path**: `screens/bahan_baku/RestockMaterialScreen.kt`

**Purpose**: Form restock bahan baku.

**UI Components**:

- **Header**: "Restock Bahan Baku"

- **Form Fields**:
  - Material name (read-only, if from detail)
  - OR Dropdown "Pilih Bahan" (if from list)
  - TextField "Jumlah Restock" (number, required)
  - Current stock (info only)
  - New stock after restock (calculated)
  - DatePicker "Tanggal Restock"
  - TextField "Supplier" (optional)
  - TextField "Harga per Unit" (optional, to update)
  - TextField "Catatan" (optional)

- **Stock Preview**:

```kotlin
Row {
    Text("Stok Saat Ini: ${currentStock} ${unit}")
    Icon(Icons.Default.ArrowForward)
    Text("Stok Baru: ${currentStock + restockQty} ${unit}")
}
```

**Validation**:

```kotlin
fun validate(): Boolean {
    return restockQty > 0 &&
           restockDate.isNotEmpty()
}
```

---

### 13. MaterialSpecificHistoryScreen

**Path**: `screens/bahan_baku/MaterialSpecificHistoryScreen.kt`

**Purpose**: Riwayat spesifik satu bahan baku.

**UI Components**:

- Header with material name
- Filter by date range
- Timeline view of all activities:
  - Restock events
  - Usage in productions
  - Stock adjustments

---

## 📋 PRODUK SCREENS

### 14. ProductListScreen

**Path**: `screens/produk/ProductListScreen.kt`

**Purpose**: List semua produk dengan search.

**UI Components**:

- **Header**:
  - Title "Produk"
  - Search icon | History icon

- **Search Bar**: 52dp height

- **Product Cards** (NON-CLICKABLE):
  - Product name & SKU
  - Category
  - Stock quantity
  - Selling price
  - Three-dot menu

**Three-Dot Menu**:

- 👁️ Detail → ProductDetailScreen
- 🔧 Komposisi → ProductCompositionScreen
- 📦 Restock → RestockProductScreen
- ✏️ Edit Product
- 🗑️ Hapus

---

### 15. ProductDetailScreen

**Path**: `screens/produk/ProductDetailScreen.kt`

**Purpose**: Detail lengkap produk dengan statistik.

**UI Components**:

- **Header**: Back button + Product name

- **Product Info**:
  - Product image placeholder
  - Name, SKU, Category
  - Current stock
  - Selling price
  - Cost of goods (HPP)
  - Profit margin (calculated)

- **Stats Cards**:
  - Total sold (all time)
  - Revenue generated
  - Average monthly sales

- **Action Buttons**:
  - View Composition
  - Restock
  - Edit
  - View Sales History

---

### 16. ProductCompositionScreen

**Path**: `screens/produk/ProductCompositionScreen.kt`

**Purpose**: Lihat komposisi bahan untuk satu produk.

**UI Components**:

- **Header**: Back button + "Komposisi Produk"

- **Product Info**:
  - Product name
  - Batch size (jumlah per produksi)

- **Composition List**:
  - Material name
  - Quantity needed
  - Unit
  - Cost per unit
  - Subtotal cost

- **Summary Card**:
  - Total material cost
  - Selling price
  - Profit per unit
  - Profit margin (%)

**Cost Calculation**:

```kotlin
val totalMaterialCost = compositions.sumOf {
    it.quantity * it.costPerUnit
}
val profitPerUnit = sellingPrice - totalMaterialCost
val marginPercent = (profitPerUnit / sellingPrice) * 100
```

---

### 17. RestockProductScreen

**Path**: `screens/produk/RestockProductScreen.kt`

**Purpose**: Form restock produk jadi.

**UI Components**:

- Header "Restock Produk"
- Product name (read-only)
- Current stock
- Restock quantity input
- New stock preview
- Date picker
- Notes (optional)
- Save button

---

## 💰 KEUANGAN SCREENS

### 18. FinanceDashboardScreen

**Path**: `screens/keuangan/FinanceDashboardScreen.kt`

**Purpose**: Dashboard keuangan dengan overview finansial.

**UI Components**:

- **Header**:
  - Profile icon
  - "DASHBOARD KEUANGAN"
  - Date icon

- **Period Selector**:
  - Filter chips: Harian | Mingguan | Bulanan

- **Balance Card** (gradient blue):
  - "TOTAL SALDO AKTIF"
  - Amount (with visibility toggle)
  - Last updated

- **Income & Expense Cards** (2 columns):
  - 📈 Pemasukan: amount + percentage
  - 📉 Pengeluaran: amount + percentage

- **Quick Access Menu** (4 buttons):
  - 📊 Analisis HPP-BEP
  - 💰 Utang & Piutang
  - 🧾 Transaksi
  - 📋 Laporan

- **Recent Transactions**:
  - List of last 5 transactions
  - "Lihat Semua" button

**Balance Visibility Toggle**:

```kotlin
var isBalanceVisible by remember { mutableStateOf(true) }

Text(
    text = if (isBalanceVisible) {
        "Rp 45.750.000"
    } else {
        "Rp ••••••••"
    }
)
```

---

### 19. HppBepAnalysisScreen

**Path**: `screens/keuangan/HppBepAnalysisScreen.kt`

**Purpose**: Analisis HPP (Harga Pokok Penjualan) & BEP (Break Even Point).

**UI Components**:

- **Header**: Back + "Analisis HPP-BEP"

- **Tab Selector**:
  - Tab HPP | Tab BEP

- **HPP Tab**:
  - Product selector
  - HPP breakdown:
    - Material costs (per item)
    - Labor costs (optional)
    - Overhead costs (optional)
    - Total HPP
  - Selling price
  - Profit per unit
  - Profit margin %

- **BEP Tab**:
  - Fixed costs input
  - Variable cost per unit
  - Selling price per unit
  - BEP calculation results:
    - BEP in units
    - BEP in Rupiah
    - Margin of safety
  - Chart visualization

**HPP Formula**:

```kotlin
HPP = Material Cost + Labor Cost + Overhead Cost
Profit = Selling Price - HPP
Margin = (Profit / Selling Price) × 100%
```

**BEP Formula**:

```kotlin
BEP (units) = Fixed Costs / (Selling Price - Variable Cost)
BEP (Rp) = BEP (units) × Selling Price
```

---

### 20. TransactionHistoryScreen

**Path**: `screens/keuangan/TransactionHistoryScreen.kt`

**Purpose**: Riwayat semua transaksi keuangan.

**UI Components**:

- Header with date filter
- Filter chips: Semua | Pemasukan | Pengeluaran
- Search bar
- Transaction list:
  - Title
  - Date & time
  - Category
  - Amount (green for income, red for expense)
  - Icon
- FAB "+" untuk add transaction

---

### 21. TransactionFormScreen

**Path**: `screens/keuangan/TransactionFormScreen.kt`

**Purpose**: Form input transaksi baru.

**UI Components**:

- **Transaction Type Selector**:
  - Radio button: Pemasukan | Pengeluaran

- **Form Fields**:
  - TextField "Judul Transaksi"
  - Dropdown "Kategori"
  - TextField "Jumlah" (number, currency format)
  - DatePicker "Tanggal"
  - TimePicker "Waktu"
  - Dropdown "Metode Pembayaran"
  - TextField "Keterangan" (multiline)

- **Category Options**:
  - Pemasukan: Penjualan, Piutang Diterima, Lainnya
  - Pengeluaran: Bahan Baku, Gaji, Utilitas, Utang, Lainnya

- **Payment Methods**:
  - Cash, Transfer Bank, E-Wallet, Kartu Kredit/Debit

**Amount Input Formatting**:

```kotlin
var amount by remember { mutableStateOf("") }

TextField(
    value = amount,
    onValueChange = { newValue ->
        // Only accept numbers
        if (newValue.all { it.isDigit() }) {
            amount = newValue
        }
    },
    visualTransformation = CurrencyVisualTransformation(),
    prefix = { Text("Rp ") }
)
```

---

### 22. DebtReceivableScreen

**Path**: `screens/keuangan/DebtReceivableScreen.kt`

**Purpose**: Manajemen utang dan piutang.

**UI Components**:

- **Header**: "Utang & Piutang"

- **Tab Selector**:
  - Utang | Piutang

- **Summary Cards** (per tab):
  - Total amount
  - Number of entries
  - Overdue count

- **List Items**:
  - Creditor/Debtor name
  - Amount
  - Due date
  - Status (Belum Lunas/Lunas)
  - Progress bar

- **Filter Options**:
  - Semua | Belum Lunas | Sudah Lunas | Jatuh Tempo

**Status Badge**:

```kotlin
val (status, color) = when {
    isPaid -> "Lunas" to Color(0xFF10B981)
    isOverdue -> "Jatuh Tempo" to Color(0xFFEF4444)
    else -> "Belum Lunas" to Color(0xFFF59E0B)
}
```

---

### 23. DebtReceivableHistoryScreen

**Path**: `screens/keuangan/DebtReceivableHistoryScreen.kt`

**Purpose**: Riwayat pembayaran utang/piutang.

**UI Components**:

- Header with filter
- Timeline of payments
- Each payment shows:
  - Payment date
  - Amount paid
  - Remaining balance
  - Payment method

---

### 24. DebtFormScreen

**Path**: `screens/keuangan/DebtFormScreen.kt`

**Purpose**: Form tambah/edit utang atau piutang.

**UI Components**:

- Type selector (Utang/Piutang)
- Creditor/Debtor name
- Total amount
- Due date
- Description
- Payment schedule (optional)
- Save button

---

## 🛒 PENJUALAN SCREENS

### 25. SalesDashboardScreen ⭐ NEW

**Path**: `screens/penjualan/SalesDashboardScreen.kt`

**Purpose**: Dashboard penjualan dengan statistik & quick actions.

**UI Components**:

- **Header**:
  - Profile icon
  - "DASHBOARD PENJUALAN"
  - Date icon

- **Period Selector**:
  - Harian | Mingguan | Bulanan

- **Stats Card** (gradient blue):
  - "TOTAL PENJUALAN"
  - Amount
  - Transaction count
  - Items sold

- **Quick Access Menu** (3 buttons):
  - 💳 POS → SalesPOSScreen
  - 👥 Pelanggan → CustomerListScreen
  - 📊 Riwayat → SalesHistoryScreen

- **Recent Transactions**:
  - Last 5 sales
  - Customer name
  - Items count
  - Total amount
  - Payment method

**Stats Calculation**:

```kotlin
val totalSales = sales.sumOf { it.total }
val transactionCount = sales.size
val itemsSold = sales.sumOf { it.itemCount }
```

---

### 26. SalesPOSScreen ⭐ NEW

**Path**: `screens/penjualan/SalesPOSScreen.kt`

**Purpose**: Point of Sale untuk transaksi penjualan.

**UI Components**:

- **Header**:
  - "POINT OF SALE"
  - Cart icon with badge (item count)

- **Search Bar**: Cari produk

- **Category Chips**:
  - Semua | Minuman | Makanan | Snack | Lainnya

- **Product Grid** (2 columns):
  - Product image placeholder
  - Product name
  - Stock info
  - Price
  - Quantity badge (if in cart)
  - Add/Remove buttons (if in cart)

- **Cart Summary** (bottom, if not empty):
  - Total items
  - Total price (green, large)
  - Checkout button

**Cart Management**:

```kotlin
val cartItems = remember { mutableStateListOf<CartItem>() }

fun addToCart(product: POSProduct) {
    val existingItem = cartItems.find { it.product.id == product.id }
    if (existingItem != null) {
        if (existingItem.quantity < product.stock) {
            existingItem.quantity++
        }
    } else {
        cartItems.add(CartItem(product, 1))
    }
}

fun removeFromCart(productId: String) {
    val item = cartItems.find { it.product.id == productId }
    if (item != null) {
        if (item.quantity > 1) {
            item.quantity--
        } else {
            cartItems.remove(item)
        }
    }
}
```

**Product Card Border**:

```kotlin
border = androidx.compose.foundation.BorderStroke(
    width = if (quantityInCart > 0) 2.dp else 1.dp,
    color = if (quantityInCart > 0) Color(0xFF10B981) else Color(0xFFE2E8F0)
)
```

---

### 27. CustomerListScreen ⭐ NEW

**Path**: `screens/penjualan/CustomerListScreen.kt`

**Purpose**: Manajemen data pelanggan.

**UI Components**:

- **Header**:
  - "MANAJEMEN PELANGGAN"
  - Sort icon | Add icon

- **Search Bar**: Cari nama, telepon, email

- **Stats Summary Card**:
  - Total pelanggan
  - Total transaksi

- **Customer Cards**:
  - Avatar (first letter)
  - Name
  - Phone number
  - Transaction count badge
  - Last purchase date
  - Total purchases amount

- **FAB**: Add customer

- **Sort Options**:
  - Nama (A-Z)
  - Total Pembelian (high to low)
  - Terakhir Beli (recent first)

**Customer Segmentation Color**:

```kotlin
val avatarColor = when {
    customer.totalPurchases > 5_000_000 -> Color(0xFFFFD700) // Gold - VIP
    customer.totalPurchases > 1_000_000 -> Color(0xFF8B5CF6) // Purple - Regular
    else -> Color(0xFF94A3B8) // Gray - New
}
```

---

### 28. SalesCheckoutScreen

**Path**: `screens/penjualan/SalesCheckoutScreen.kt`

**Purpose**: Checkout & payment processing.

**UI Components**:

- Header "Checkout"
- Cart items summary
- Customer selector (optional)
- Payment method selector:
  - Cash
  - Transfer
  - E-Wallet
  - Credit/Debit
- Amount received input (for cash)
- Change calculation
- Notes (optional)
- Process payment button

---

### 29. SalesHistoryScreen

**Path**: `screens/penjualan/SalesHistoryScreen.kt`

**Purpose**: Riwayat penjualan lengkap.

**UI Components**:

- Header with filter
- Search bar
- Filter chips: Semua | Hari Ini | Minggu Ini | Bulan Ini
- Sales history cards:
  - Customer name
  - Transaction ID
  - Date & time
  - Amount
  - Status badge (Paid/Pending)
- FAB to add new sale

---

### 30. CustomerListScreen ⭐ NEW

**Path**: `screens/penjualan/CustomerListScreen.kt`

**Purpose**: List semua customer dengan filter segment.

**UI Components**:

- **Header**: "MANAJEMEN PELANGGAN"
- **Search Bar**: Cari customer (nama/phone)
- **Segment Filter Chips**:
  - Semua | VIP | Regular | New
- **Customer Cards**:
  - Customer name
  - Phone number
  - Segment badge (VIP/Regular/New)
  - Transaction count
  - Total purchases (Rp)
  - Last transaction date
  - Tap to navigate to detail
- **FAB**: Add new customer

**ViewModel**:

```kotlin
class CustomerViewModel : ViewModel() {
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers = _customers.asStateFlow()

    private val _selectedSegment = MutableStateFlow<String?>(null)

    fun loadCustomers()
    fun filterBySegment(segment: String?)
    fun searchCustomer(query: String)
}
```

---

### 31. CustomerDetailScreen ⭐ NEW

**Path**: `screens/penjualan/CustomerDetailScreen.kt`

**Purpose**: Detail customer dengan purchase history & analytics.

**UI Components**:

- **Header**: Back button + Edit icon
- **Customer Info Card**:
  - Name
  - Phone
  - Email
  - Segment badge (VIP/Regular/New)
  - Member since date
- **Quick Stats** (3 cards):
  - Total Purchases (Rp)
  - Transaction Count
  - Average Purchase
- **Tabs**:
  - **Purchase History**: List of sales
  - **Pre-Orders**: Active pre-orders
  - **Analytics**: Charts & trends
- **Purchase History List**:
  - Invoice number
  - Date
  - Items summary
  - Total amount
  - Tap to view sale detail
- **Bottom Actions**:
  - Create Pre-Order button
  - Delete Customer button (with confirmation)

---

### 32. CustomerFormScreen ⭐ NEW

**Path**: `screens/penjualan/CustomerFormScreen.kt`

**Purpose**: Form untuk add/edit customer.

**UI Components**:

- **Header**: "Tambah Pelanggan" / "Edit Pelanggan"
- **Form Fields**:
  - Nama lengkap (required)
  - Nomor HP (required, validation)
  - Email (optional, validation)
  - Alamat (optional, text area)
  - Notes (optional)
- **Validation**:
  - Phone format: 08xxx atau +62xxx
  - Email format validation
  - Duplicate phone check
- **Bottom Actions**:
  - Cancel button
  - Save button

**Validation Example**:

```kotlin
fun validatePhone(phone: String): Boolean {
    val phoneRegex = "^(08|\\+62)[0-9]{9,12}$".toRegex()
    return phone.matches(phoneRegex)
}

fun validateEmail(email: String): Boolean {
    if (email.isBlank()) return true // Optional
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return email.matches(emailRegex)
}
```

---

### 33. PreOrderListScreen ⭐ NEW

**Path**: `screens/penjualan/PreOrderListScreen.kt`

**Purpose**: List semua pre-order dengan filter status.

**UI Components**:

- **Header**: "PRE-ORDER MANAGEMENT"
- **Status Filter Chips**:
  - Semua | Pending | DP Paid | Ready | Completed
- **PreOrder Cards**:
  - Pre-order ID (e.g., "PO-001")
  - Customer name
  - Product name
  - Quantity
  - Total amount
  - DP amount & percentage
  - Remaining amount
  - Pickup date & time
  - Status badge dengan warna:
    - PENDING: Gray
    - DP_PAID: Blue
    - READY: Green
    - COMPLETED: Dark Green
    - CANCELLED: Red
  - Overdue indicator (jika lewat pickup date)
  - Tap to navigate to detail
- **FAB**: Create new pre-order

**Status Badge**:

```kotlin
val statusBadge = when (preOrder.status) {
    PreOrderStatus.PENDING -> "Menunggu DP" to Color(0xFF9CA3AF)
    PreOrderStatus.DP_PAID -> "DP Dibayar" to Color(0xFF197FE6)
    PreOrderStatus.READY -> "Siap Diambil" to Color(0xFF10B981)
    PreOrderStatus.COMPLETED -> "Selesai" to Color(0xFF059669)
    PreOrderStatus.CANCELLED -> "Dibatalkan" to Color(0xFFEF4444)
}
```

---

### 34. PreOrderDetailScreen ⭐ NEW

**Path**: `screens/penjualan/PreOrderDetailScreen.kt`

**Purpose**: Detail pre-order dengan actions untuk payment & pickup.

**UI Components**:

- **Header**: Back button + Status badge

- **Pre-Order Info Card**:
  - Pre-order ID
  - Created date
  - Status badge (large)
- **Customer Info Card**:
  - Customer name (clickable to customer detail)
  - Phone number
  - Segment badge
- **Product Info Card**:
  - Product name
  - Quantity
  - Unit price
  - Total amount (bold, large)
- **Payment Info Card**:
  - DP Amount dengan percentage
  - Remaining Amount (highlighted if > 0)
  - Progress bar (DP percentage)
- **Pickup Info Card**:
  - Pickup date
  - Pickup time
  - Days until pickup (or "Overdue" if past)
- **Notes Section** (if exists):
  - Customer notes/special requests
- **Bottom Actions** (conditional based on status):

  **If PENDING**:
  - Pay DP button → Dialog untuk input DP amount
  - Cancel Pre-Order button

  **If DP_PAID**:
  - Mark as Ready button (untuk staff produksi)
  - Cancel Pre-Order button

  **If READY**:
  - Complete Pickup button → Navigate to payment
  - Cancel Pre-Order button

  **If COMPLETED**:
  - View Sale button → Navigate to sale detail

  **If CANCELLED**:
  - Cancelled reason text
  - No actions available

**Payment Dialog**:

```kotlin
@Composable
fun PayDPDialog(
    preOrder: PreOrder,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var dpAmount by remember { mutableStateOf("") }

    val minDP = preOrder.totalAmount * 0.30
    val maxDP = preOrder.totalAmount

    AlertDialog(
        title = { Text("Bayar DP Pre-Order") },
        text = {
            Column {
                Text("Minimal DP: ${formatCurrency(minDP)} (30%)")
                Spacer(height = 8.dp)
                OutlinedTextField(
                    value = dpAmount,
                    onValueChange = { dpAmount = it },
                    label = { Text("Jumlah DP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(dpAmount.toDoubleOrNull() ?: 0.0) },
                enabled = dpAmount.toDoubleOrNull() in minDP..maxDP
            ) {
                Text("Konfirmasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        onDismissRequest = onDismiss
    )
}
```

---

### 35. CreatePreOrderScreen ⭐ NEW

**Path**: `screens/penjualan/CreatePreOrderScreen.kt`

**Purpose**: Form untuk create pre-order baru.

**UI Components**:

- **Header**: "Buat Pre-Order Baru"

- **Form Sections**:

  **1. Customer Selection**:
  - Customer dropdown (dengan search)
  - "+ Tambah Customer Baru" button

  **2. Product Selection**:
  - Product dropdown (dengan search)
  - Selected product info (image, name, price)

  **3. Quantity & Pricing**:
  - Quantity input (number)
  - Unit price (editable, default from product)
  - Total amount (auto-calculated, read-only)

  **4. Down Payment**:
  - DP amount input
  - DP percentage (auto-calculated)
  - Remaining amount (auto-calculated)
  - Validation message: "DP minimal 30%"

  **5. Pickup Schedule**:
  - Pickup date picker (min H+1, max H+30)
  - Pickup time picker (optional)

  **6. Notes**:
  - Text area untuk catatan khusus

- **Bottom Actions**:
  - Cancel button
  - Create Pre-Order button

**Validation**:

```kotlin
fun validatePreOrder(): String? {
    if (selectedCustomer == null) return "Pilih customer"
    if (selectedProduct == null) return "Pilih produk"
    if (quantity <= 0) return "Quantity harus > 0"

    val minDP = totalAmount * 0.30
    if (dpAmount > 0 && dpAmount < minDP) {
        return "DP minimal 30% (${formatCurrency(minDP)})"
    }

    if (dpAmount > totalAmount) return "DP tidak boleh > total"

    val minPickupDate = LocalDate.now().plusDays(1)
    if (pickupDate < minPickupDate) {
        return "Pickup date minimal besok"
    }

    return null // Valid
}
```

**Flow**:

```
1. User select customer (atau create new)
2. User select product
3. User input quantity → auto-calculate total
4. User input DP → validate >= 30%
5. User pilih pickup date & time
6. User tambah notes (optional)
7. Click "Create Pre-Order"
   → Validation
   → API call
   → Success: Navigate to PreOrderDetailScreen
   → Error: Show error message
```

---

### 36. PreOrderCalendarScreen ⭐ NEW

**Path**: `screens/penjualan/PreOrderCalendarScreen.kt`

**Purpose**: Calendar view untuk jadwal pickup pre-order.

**UI Components**:

- **Header**: "Jadwal Pickup Pre-Order"
- **Month Selector**: Previous/Next month buttons
- **Calendar View**:
  - Date grid dengan indicator:
    - Dot badge untuk tanggal yang ada pre-order
    - Color coding by status (blue=dp_paid, green=ready)
  - Selected date highlighted
- **Selected Date Pre-Orders**:
  - List pre-orders untuk tanggal terpilih
  - Sorted by pickup time
  - Card dengan:
    - Time
    - Customer name
    - Product name
    - Status badge
    - Tap to navigate to detail
- **Quick Stats** (untuk selected date):
  - Total pre-orders
  - Total amount to collect
  - Ready count vs total

**Calendar Implementation**:

```kotlin
@Composable
fun PreOrderCalendar(
    preOrders: List<PreOrder>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Group pre-orders by date
    val preOrdersByDate = preOrders.groupBy { it.pickupDate }

    // Calendar grid implementation
    // Show dots/badges on dates with pre-orders
    // Highlight selected date
}
```

---

## 👤 PROFILE SCREEN

### 37. ProfileScreen

**Path**: `screens/profile/ProfileScreen.kt`

**Purpose**: User profile & settings.

**UI Components**:

- Profile header with avatar
- User info (name, role)
- Settings options:
  - Change password
  - Language
  - Theme (Light/Dark)
  - Notifications
- About app
- Logout button

---

## 🎯 Screen Navigation Summary

### Bottom Navigation Routes

**Produksi Role** (4 tabs):

```
dashboard → production → product_list → bahan_baku
   ↓            ↓            ↓             ↓
 Home        Produksi     Produk      Bahan Baku
```

**Keuangan Role** (4 tabs):

```
finance_dashboard → hpp_bep_analysis → debt_receivable → transaction_history
       ↓                  ↓                  ↓                    ↓
     Home              HPP-BEP             Utang              Transaksi
```

**Penjualan Role** (3 tabs):

```
sales_dashboard → sales_pos → customer_list
      ↓              ↓             ↓
    Home          Kasir       Pelanggan
```

---

## 📱 Screen States

### Common States

```kotlin
// Loading state
var isLoading by remember { mutableStateOf(false) }

// Error state
var errorMessage by remember { mutableStateOf<String?>(null) }

// Success state
var showSuccessDialog by remember { mutableStateOf(false) }

// Search query
var searchQuery by remember { mutableStateOf("") }

// Filter state
var selectedFilter by remember { mutableStateOf(0) }
```

### Validation States

```kotlin
// Form validation
var isFormValid by remember { mutableStateOf(false) }
var showValidationErrors by remember { mutableStateOf(false) }

// Field errors
var nameError by remember { mutableStateOf<String?>(null) }
var emailError by remember { mutableStateOf<String?>(null) }
```

---

## 🎨 Screen Design Patterns

### 1. Header Pattern

Semua screen menggunakan header konsisten:

```kotlin
Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        // Content...
    )
}
```

### 2. Search Bar Pattern

Search bar dengan height 52dp:

```kotlin
OutlinedTextField(
    value = searchQuery,
    onValueChange = { searchQuery = it },
    modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .padding(horizontal = 16.dp),
    shape = RoundedCornerShape(16.dp),
    colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedBorderColor = Color(0xFF197FE6)
    )
)
```

### 3. Card Pattern

Cards dengan border & elevation:

```kotlin
Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    shadowElevation = 1.dp
) {
    // Card content...
}
```

---

## 📝 Notes

- Semua screen menggunakan `@OptIn(ExperimentalMaterial3Api::class)`
- State management menggunakan `remember` & `mutableStateOf`
- Navigation menggunakan lambda callbacks
- Loading states untuk async operations
- Error handling dengan Snackbar atau Dialog
- Consistent spacing: 8dp, 12dp, 16dp, 20dp, 24dp
