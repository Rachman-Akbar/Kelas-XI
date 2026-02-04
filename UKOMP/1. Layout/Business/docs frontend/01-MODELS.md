# 📦 Data Models & Entities

Dokumentasi lengkap semua data model yang digunakan dalam aplikasi SI-UMKM Mobile.

---

## 📋 Model Structure Overview

```
Models/
├── Core Business Models
│   ├── Material (Bahan Baku)
│   ├── Product (Produk)
│   ├── Production (Produksi)
│   └── ProductComposition (Komposisi)
│
├── Sales Models
│   ├── Customer (Pelanggan)
│   ├── POSProduct (POS Item)
│   ├── CartItem (Keranjang)
│   ├── Sale (Penjualan)
│   └── SalesHistoryItem (Riwayat)
│
├── Finance Models
│   ├── Transaction (Transaksi)
│   ├── Debt (Utang)
│   ├── Receivable (Piutang)
│   └── HppBepData (Analisis)
│
└── UI Models
    ├── RecentSale (Dashboard)
    ├── BottomNavItem (Navigation)
    └── FilterState (UI State)
```

---

## 1️⃣ Material (Bahan Baku)

### Data Class

```kotlin
data class Material(
    val id: String,
    val nama: String,              // Nama bahan baku
    val satuan: String,            // kg, liter, pcs, dll
    val stokSaatIni: Double,       // Stok aktual
    val batasMinimum: Double,      // Batas minimum stok
    val hargaPerSatuan: Double,    // Harga per unit
    val supplier: String = "",     // Nama supplier
    val kategori: String = "",     // Kategori bahan
    val lastUpdate: String = ""    // Timestamp terakhir update
)
```

### Field Explanation

| Field            | Type   | Description                      | Example         |
| ---------------- | ------ | -------------------------------- | --------------- |
| `id`             | String | Unique identifier                | "MAT-001"       |
| `nama`           | String | Nama bahan baku                  | "Tepung Terigu" |
| `satuan`         | String | Unit of measurement              | "kg"            |
| `stokSaatIni`    | Double | Current stock quantity           | 25.5            |
| `batasMinimum`   | Double | Minimum stock threshold          | 10.0            |
| `hargaPerSatuan` | Double | Price per unit (Rp)              | 12000.0         |
| `supplier`       | String | Supplier name                    | "Toko Bahan A"  |
| `kategori`       | String | Category (Bahan Dasar, Tambahan) | "Bahan Dasar"   |
| `lastUpdate`     | String | Last modified timestamp          | "2026-01-31"    |

### Usage Example

```kotlin
val material = Material(
    id = "MAT-001",
    nama = "Tepung Terigu",
    satuan = "kg",
    stokSaatIni = 25.5,
    batasMinimum = 10.0,
    hargaPerSatuan = 12000.0,
    supplier = "Toko Bahan A",
    kategori = "Bahan Dasar",
    lastUpdate = "2026-01-31"
)

// Check if stock is low
val isLowStock = material.stokSaatIni <= material.batasMinimum

// Calculate total value
val totalValue = material.stokSaatIni * material.hargaPerSatuan
```

### Business Rules

1. **Stock Alert**:
   - Status "Menipis" jika `stokSaatIni <= batasMinimum`
   - Status "Aman" jika `stokSaatIni > batasMinimum`

2. **Validation**:
   - `stokSaatIni` tidak boleh negatif
   - `batasMinimum` harus > 0
   - `hargaPerSatuan` harus > 0

---

## 2️⃣ Product (Produk)

### Data Class

```kotlin
data class Product(
    val id: String,
    val name: String,                    // Nama produk
    val sku: String,                     // Stock Keeping Unit
    val category: String,                // Kategori produk
    val stock: Int,                      // Stok tersedia
    val sellingPrice: Double,            // Harga jual
    val costOfGoodsPrice: Double = 0.0,  // HPP (Harga Pokok Penjualan)
    val minimumStock: Int = 0,           // Batas minimum stok
    val description: String = "",        // Deskripsi produk
    val imageUrl: String = ""            // URL gambar produk
)
```

### Field Explanation

| Field              | Type   | Description              | Example           |
| ------------------ | ------ | ------------------------ | ----------------- |
| `id`               | String | Unique identifier        | "PRD-001"         |
| `name`             | String | Product name             | "Kopi Latte"      |
| `sku`              | String | Stock Keeping Unit       | "SKU-001"         |
| `category`         | String | Product category         | "Minuman"         |
| `stock`            | Int    | Available stock quantity | 45                |
| `sellingPrice`     | Double | Selling price (Rp)       | 25000.0           |
| `costOfGoodsPrice` | Double | Cost of goods sold (HPP) | 12000.0           |
| `minimumStock`     | Int    | Minimum stock threshold  | 10                |
| `description`      | String | Product description      | "Kopi susu latte" |
| `imageUrl`         | String | Product image URL        | "https://..."     |

### Usage Example

```kotlin
val product = Product(
    id = "PRD-001",
    name = "Kopi Latte",
    sku = "SKU-001",
    category = "Minuman",
    stock = 45,
    sellingPrice = 25000.0,
    costOfGoodsPrice = 12000.0,
    minimumStock = 10
)

// Calculate profit margin
val profitMargin = ((product.sellingPrice - product.costOfGoodsPrice) /
                    product.sellingPrice) * 100

// Calculate total inventory value
val inventoryValue = product.stock * product.costOfGoodsPrice
```

### Business Rules

1. **Profit Calculation**:

   ```kotlin
   Profit per unit = sellingPrice - costOfGoodsPrice
   Margin (%) = (Profit / sellingPrice) * 100
   ```

2. **Stock Status**:
   - "Habis" jika `stock == 0`
   - "Menipis" jika `stock <= minimumStock`
   - "Aman" jika `stock > minimumStock`

---

## 3️⃣ ProductComposition (Komposisi Produk)

### Data Class

```kotlin
data class ProductComposition(
    val id: String,
    val productId: String,        // ID produk
    val materialId: String,       // ID bahan baku
    val materialName: String,     // Nama bahan baku
    val quantity: Double,         // Jumlah yang dibutuhkan
    val unit: String,             // Satuan
    val costPerUnit: Double       // Biaya per satuan
)
```

### Usage Example

```kotlin
val composition = ProductComposition(
    id = "COMP-001",
    productId = "PRD-001",
    materialId = "MAT-001",
    materialName = "Tepung Terigu",
    quantity = 0.2,
    unit = "kg",
    costPerUnit = 12000.0
)

// Calculate material cost for this composition
val materialCost = composition.quantity * composition.costPerUnit
// Result: 0.2 * 12000 = Rp 2.400
```

---

## 4️⃣ Production (Produksi)

### Data Class

```kotlin
data class Production(
    val id: String,
    val productId: String,           // ID produk yang diproduksi
    val productName: String,         // Nama produk
    val batchNumber: String,         // Nomor batch
    val quantity: Int,               // Jumlah produksi
    val productionDate: String,      // Tanggal produksi
    val status: String,              // Status: Planned, InProgress, Completed
    val notes: String = "",          // Catatan produksi
    val operator: String = "",       // Nama operator
    val totalCost: Double = 0.0      // Total biaya produksi
)
```

### Status Flow

```
Planned → InProgress → Completed
   ↓           ↓            ↓
 (Blue)    (Orange)     (Green)
```

### Usage Example

```kotlin
val production = Production(
    id = "PROD-001",
    productId = "PRD-001",
    productName = "Kopi Latte",
    batchNumber = "BATCH-20260131-001",
    quantity = 50,
    productionDate = "2026-01-31",
    status = "Completed",
    operator = "Ahmad",
    totalCost = 600000.0
)

// Calculate cost per unit
val costPerUnit = production.totalCost / production.quantity
// Result: 600000 / 50 = Rp 12.000 per unit
```

---

## 5️⃣ Customer (Pelanggan)

### Data Class

```kotlin
data class Customer(
    val id: String,
    val name: String,                  // Nama pelanggan
    val phone: String,                 // Nomor telepon
    val email: String,                 // Email
    val totalPurchases: Double,        // Total pembelian (Rp)
    val transactionCount: Int,         // Jumlah transaksi
    val lastPurchase: String,          // Tanggal pembelian terakhir
    val address: String = "",          // Alamat
    val notes: String = ""             // Catatan
)
```

### Usage Example

```kotlin
val customer = Customer(
    id = "CUST-001",
    name = "Ahmad Wijaya",
    phone = "081234567890",
    email = "ahmad.wijaya@email.com",
    totalPurchases = 2450000.0,
    transactionCount = 12,
    lastPurchase = "14 Jan 2026"
)

// Calculate average purchase
val averagePurchase = customer.totalPurchases / customer.transactionCount
// Result: 2450000 / 12 = Rp 204.166 per transaksi
```

### Business Rules

1. **Customer Segmentation**:
   - VIP: `totalPurchases > 5.000.000`
   - Regular: `totalPurchases > 1.000.000`
   - New: `transactionCount < 5`

---

## 7️⃣ PreOrder (Pre-Order / Pesanan)

### Data Class

```kotlin
data class PreOrder(
    val id: String,
    val customerId: String,          // ID pelanggan
    val customerName: String,        // Nama pelanggan
    val productId: String,           // ID produk
    val productName: String,         // Nama produk
    val quantity: Int,               // Jumlah pesanan
    val unitPrice: Double,           // Harga satuan
    val totalAmount: Double,         // Total harga
    val dpAmount: Double,            // Down Payment
    val remainingAmount: Double,     // Sisa pembayaran
    val pickupDate: String,          // Tanggal pengambilan
    val pickupTime: String? = null,  // Jam pengambilan (optional)
    val status: PreOrderStatus,      // Status pre-order
    val notes: String = "",           // Catatan pesanan
    val createdDate: String,         // Tanggal dibuat
    val cancelledReason: String? = null  // Alasan batal (jika cancelled)
)

enum class PreOrderStatus {
    PENDING,      // Menunggu DP
    DP_PAID,      // DP Sudah Dibayar
    READY,        // Siap Diambil
    COMPLETED,    // Selesai (sudah diambil)
    CANCELLED     // Dibatalkan
}
```

### Field Explanation

| Field             | Type           | Description                | Example            |
| ----------------- | -------------- | -------------------------- | ------------------ |
| `id`              | String         | Unique identifier          | "PO-001"           |
| `customerId`      | String         | ID pelanggan               | "CUST-001"         |
| `customerName`    | String         | Nama pelanggan             | "Budi Santoso"     |
| `productId`       | String         | ID produk                  | "PRD-001"          |
| `productName`     | String         | Nama produk                | "Roti Ulang Tahun" |
| `quantity`        | Int            | Jumlah pesanan             | 1                  |
| `unitPrice`       | Double         | Harga satuan               | 500000.0           |
| `totalAmount`     | Double         | Total harga                | 500000.0           |
| `dpAmount`        | Double         | Down payment (minimal 30%) | 150000.0           |
| `remainingAmount` | Double         | Sisa pembayaran            | 350000.0           |
| `pickupDate`      | String         | Tanggal ambil              | "2026-02-05"       |
| `pickupTime`      | String?        | Jam ambil (optional)       | "14:00"            |
| `status`          | PreOrderStatus | Status pre-order           | `DP_PAID`          |
| `notes`           | String         | Catatan                    | "Topping coklat"   |
| `createdDate`     | String         | Tanggal dibuat             | "2026-01-31"       |
| `cancelledReason` | String?        | Alasan batal (optional)    | null               |

### Status Flow

```
PENDING → DP_PAID → READY → COMPLETED
   ↓        ↓        ↓
CANCELLED ← CANCELLED ← CANCELLED
```

### Usage Example

```kotlin
val preOrder = PreOrder(
    id = "PO-001",
    customerId = "CUST-001",
    customerName = "Budi Santoso",
    productId = "PRD-005",
    productName = "Kue Ulang Tahun Custom",
    quantity = 1,
    unitPrice = 750000.0,
    totalAmount = 750000.0,
    dpAmount = 225000.0,  // 30% DP
    remainingAmount = 525000.0,
    pickupDate = "2026-02-10",
    pickupTime = "15:00",
    status = PreOrderStatus.DP_PAID,
    notes = "Tema Frozen, tulisan 'Happy Birthday Luna'",
    createdDate = "2026-01-31",
    cancelledReason = null
)

// Check DP percentage
val dpPercentage = (preOrder.dpAmount / preOrder.totalAmount) * 100
// Result: 30.0

// Check if overdue
val isOverdue = LocalDate.parse(preOrder.pickupDate) < LocalDate.now()
              && preOrder.status !in listOf(PreOrderStatus.COMPLETED, PreOrderStatus.CANCELLED)
```

### Business Rules

1. **DP Requirements**:
   - Minimal DP: 30% dari total amount
   - Maksimal DP: 100% (full payment)

   ```kotlin
   val minDP = totalAmount * 0.30
   val maxDP = totalAmount
   require(dpAmount in minDP..maxDP) { "DP harus 30-100% dari total" }
   ```

2. **Pickup Date**:
   - Minimal H+1 dari tanggal order
   - Maksimal 30 hari dari tanggal order

   ```kotlin
   val minPickupDate = LocalDate.now().plusDays(1)
   val maxPickupDate = LocalDate.now().plusDays(30)
   ```

3. **Status Transitions**:
   - PENDING → DP_PAID: Setelah bayar DP
   - DP_PAID → READY: Produk selesai diproduksi
   - READY → COMPLETED: Pelanggan ambil & bayar sisa
   - Any → CANCELLED: Bisa dibatalkan kapan saja (kecuali COMPLETED)

4. **Auto-Complete**:
   - Saat status COMPLETED, otomatis create Sale record
   - Customer stats (total_purchases, transaction_count) auto-update

### Helper Functions

```kotlin
fun PreOrder.getDpPercentage(): Double {
    return if (totalAmount > 0) (dpAmount / totalAmount) * 100 else 0.0
}

fun PreOrder.isOverdue(): Boolean {
    val pickupLocalDate = LocalDate.parse(pickupDate)
    return pickupLocalDate < LocalDate.now()
           && status !in listOf(PreOrderStatus.COMPLETED, PreOrderStatus.CANCELLED)
}

fun PreOrder.getStatusBadge(): Pair<String, Color> {
    return when (status) {
        PreOrderStatus.PENDING -> "Menunggu DP" to Color(0xFF9CA3AF)
        PreOrderStatus.DP_PAID -> "DP Dibayar" to Color(0xFF197FE6)
        PreOrderStatus.READY -> "Siap Diambil" to Color(0xFF10B981)
        PreOrderStatus.COMPLETED -> "Selesai" to Color(0xFF059669)
        PreOrderStatus.CANCELLED -> "Dibatalkan" to Color(0xFFEF4444)
    }
}
```

---

## 8️⃣ Sale (Penjualan)

### Data Class

```kotlin
data class Sale(
    val id: String,
    val invoiceNumber: String,           // Nomor invoice
    val saleDate: String,                // Tanggal penjualan
    val customerId: String? = null,      // ID pelanggan (optional)
    val customerName: String,            // Nama pelanggan
    val subtotal: Double,                // Subtotal sebelum diskon/pajak
    val discount: Double = 0.0,          // Diskon
    val tax: Double = 0.0,               // Pajak
    val totalAmount: Double,             // Total akhir
    val paymentMethod: String,           // Metode pembayaran
    val paymentStatus: String,           // Status pembayaran
    val notes: String = "",              // Catatan
    val items: List<SaleDetail> = emptyList()  // Detail items
)
```

### Field Explanation

| Field           | Type    | Description          | Example              |
| --------------- | ------- | -------------------- | -------------------- |
| `id`            | String  | Unique identifier    | "1"                  |
| `invoiceNumber` | String  | Nomor invoice        | "INV/2026/01/001"    |
| `saleDate`      | String  | Tanggal penjualan    | "2026-01-31"         |
| `customerId`    | String? | ID pelanggan         | "CUST-001" atau null |
| `customerName`  | String  | Nama pelanggan       | "Ahmad Wijaya"       |
| `subtotal`      | Double  | Subtotal             | 500000.0             |
| `discount`      | Double  | Diskon               | 50000.0              |
| `tax`           | Double  | Pajak                | 0.0                  |
| `totalAmount`   | Double  | Total akhir          | 450000.0             |
| `paymentMethod` | String  | Cash/Transfer/Kredit | "Cash"               |
| `paymentStatus` | String  | lunas/pending        | "lunas"              |
| `notes`         | String  | Catatan              | "Pembelian rutin"    |

### SaleDetail (Item Penjualan)

```kotlin
data class SaleDetail(
    val id: String,
    val saleId: String,           // ID penjualan
    val productId: String,        // ID produk
    val productName: String,      // Nama produk
    val quantity: Int,            // Jumlah
    val unitPrice: Double,        // Harga satuan
    val subtotal: Double,         // Subtotal item
    val hpp: Double = 0.0,        // HPP satuan
    val profit: Double = 0.0      // Profit item
)
```

### Usage Example

```kotlin
val saleDetails = listOf(
    SaleDetail(
        id = "1",
        saleId = "SALE-001",
        productId = "PRD-001",
        productName = "Roti Tawar",
        quantity = 20,
        unitPrice = 12000.0,
        subtotal = 240000.0,
        hpp = 7500.0,
        profit = 90000.0
    ),
    SaleDetail(
        id = "2",
        saleId = "SALE-001",
        productId = "PRD-002",
        productName = "Brownies",
        quantity = 5,
        unitPrice = 45000.0,
        subtotal = 225000.0,
        hpp = 32000.0,
        profit = 65000.0
    )
)

val sale = Sale(
    id = "SALE-001",
    invoiceNumber = "INV/2026/01/001",
    saleDate = "2026-01-20",
    customerId = "CUST-001",
    customerName = "Toko A",
    subtotal = 465000.0,
    discount = 15000.0,
    tax = 0.0,
    totalAmount = 450000.0,
    paymentMethod = "Transfer",
    paymentStatus = "lunas",
    items = saleDetails
)

// Calculate total profit
val totalProfit = sale.items.sumOf { it.profit }
// Result: 155000.0
```

### Business Rules

1. **Calculation Flow**:

   ```kotlin
   subtotal = items.sumOf { it.subtotal }
   totalAmount = subtotal - discount + tax
   ```

2. **Payment Status**:
   - "lunas": Sudah dibayar penuh
   - "pending": Menunggu pembayaran
   - "cancelled": Transaksi dibatalkan

3. **Payment Methods**:
   - "Cash": Tunai
   - "Transfer": Bank transfer
   - "Kredit": Pembayaran kredit

---

## 9️⃣ POSProduct (Point of Sale Product)

### Data Class

```kotlin
data class POSProduct(
    val id: String,
    val name: String,      // Nama produk
    val price: Double,     // Harga jual
    val stock: Int,        // Stok tersedia
    val category: String,  // Kategori
    val image: String = "" // URL gambar
)
```

### CartItem (Item di Keranjang)

```kotlin
data class CartItem(
    val product: POSProduct,
    var quantity: Int      // Jumlah yang dibeli
)
```

### Usage Example

```kotlin
val posProduct = POSProduct(
    id = "1",
    name = "Kopi Latte",
    price = 25000.0,
    stock = 45,
    category = "Minuman"
)

val cartItem = CartItem(
    product = posProduct,
    quantity = 2
)

// Calculate subtotal
val subtotal = cartItem.product.price * cartItem.quantity
// Result: 25000 * 2 = Rp 50.000
```

---

## 1️⃣0️⃣ Transaction (Transaksi Keuangan)

### Data Class

```kotlin
data class Transaction(
    val id: String,
    val title: String,              // Judul transaksi
    val date: String,               // Tanggal transaksi
    val category: String,           // Kategori transaksi
    val amount: String,             // Jumlah (formatted)
    val type: String,               // "income" atau "expense"
    val icon: ImageVector,          // Icon transaksi
    val paymentMethod: String = "", // Metode pembayaran
    val description: String = ""    // Deskripsi
)
```

### Usage Example

```kotlin
val transaction = Transaction(
    id = "TRX-001",
    title = "Penjualan Produk A",
    date = "14 Okt 2023 • 10:30 • Penjualan",
    category = "Penjualan",
    amount = "+Rp 5.000.000",
    type = "income",
    icon = Icons.Default.Payments,
    paymentMethod = "Cash"
)
```

---

## 1️⃣1️⃣ RecentSale (Recent Sale Item)

### Data Class

```kotlin
data class RecentSale(
    val id: String,
    val customer: String,      // Nama pelanggan
    val date: String,          // Tanggal & waktu
    val items: String,         // Jumlah items
    val total: String,         // Total harga (formatted)
    val paymentMethod: String, // Metode pembayaran
    val icon: ImageVector      // Icon payment
)
```

### Usage Example

```kotlin
val recentSale = RecentSale(
    id = "1",
    customer = "Ahmad Wijaya",
    date = "14 Jan 2026 • 10:30",
    items = "3 items",
    total = "Rp 150.000",
    paymentMethod = "Cash",
    icon = Icons.Default.Payments
)
```

---

## 1️⃣2️⃣ SalesHistoryItem

### Data Class

```kotlin
data class SalesHistoryItem(
    val id: String,
    val customer: String,      // Nama pelanggan
    val date: String,          // Tanggal penjualan
    val transactionId: String, // ID transaksi
    val amount: String,        // Total amount
    val status: String         // "Paid", "Pending", "Cancelled"
)
```

### Status Types

| Status    | Color  | Meaning              |
| --------- | ------ | -------------------- |
| Paid      | Green  | Pembayaran lunas     |
| Pending   | Orange | Menunggu pembayaran  |
| Cancelled | Red    | Transaksi dibatalkan |

---

## 1️⃣3️⃣ BottomNavItem (Navigation Item)

### Data Class

```kotlin
data class BottomNavItem(
    val route: String,      // Route destination
    val icon: ImageVector,  // Icon to display
    val label: String       // Label text
)
```

### Usage Example

```kotlin
val navItem = BottomNavItem(
    route = "sales_dashboard",
    icon = Icons.Default.Home,
    label = "Home"
)
```

---

## 📊 Model Relationships

### Production Flow

```
Material ──┐
           ├──► ProductComposition ──► Product ──► Production
Material ──┘
```

### Sales Flow

```
Product ──► POSProduct ──► CartItem ──► Sale
                              ↓
                           Customer
```

### Finance Flow

```
Sale ──┐
       ├──► Transaction
Production ──┘
```

---

## 🎯 Best Practices

### 1. Immutability

Semua data class bersifat immutable (`val` instead of `var`):

```kotlin
// ❌ Wrong
data class Product(
    var stock: Int  // Mutable
)

// ✅ Correct
data class Product(
    val stock: Int  // Immutable
)
```

### 2. Default Values

Gunakan default values untuk optional fields:

```kotlin
data class Material(
    val id: String,
    val nama: String,
    val supplier: String = "",  // Optional with default
    val kategori: String = ""   // Optional with default
)
```

### 3. Type Safety

Gunakan tipe data yang sesuai:

```kotlin
// ❌ Wrong
val price: String = "25000"

// ✅ Correct
val price: Double = 25000.0
```

### 4. Formatting

Format nilai untuk display di UI layer, bukan di model:

```kotlin
// Model (raw data)
data class Product(
    val price: Double = 25000.0
)

// UI layer (formatted)
Text(text = "Rp ${String.format("%,d", product.price.toLong())}")
```

---

## 🔄 Model Extensions

Contoh extension functions untuk models:

```kotlin
// Material extensions
fun Material.isLowStock(): Boolean = stokSaatIni <= batasMinimum
fun Material.totalValue(): Double = stokSaatIni * hargaPerSatuan

// Product extensions
fun Product.profitMargin(): Double =
    ((sellingPrice - costOfGoodsPrice) / sellingPrice) * 100
fun Product.isOutOfStock(): Boolean = stock == 0

// Customer extensions
fun Customer.averagePurchase(): Double =
    totalPurchases / transactionCount.coerceAtLeast(1)
fun Customer.isVIP(): Boolean = totalPurchases > 5_000_000
```

---

## 📝 Notes

1. Semua model menggunakan `String` untuk ID untuk fleksibilitas
2. Harga menggunakan `Double` untuk akurasi decimal
3. Tanggal menggunakan `String` (akan diupgrade ke `LocalDateTime`)
4. Icon menggunakan `ImageVector` dari Material Icons
5. Status menggunakan `String` (consider enum untuk type safety)
