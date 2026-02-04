# 🛒 Features - Role Penjualan

Dokumentasi lengkap fitur-fitur untuk **Role Penjualan** dalam aplikasi SI-UMKM Mobile.

---

## 🎯 Overview Role Penjualan

Role Penjualan dirancang untuk staff yang menangani transaksi penjualan langsung (cashier), manajemen pelanggan, dan tracking sales history.

### Bottom Navigation (3 Icons)

```
🏠 Home → SalesDashboardScreen
💳 Kasir → SalesPOSScreen
👥 Pelanggan → CustomerListScreen
```

### Akses Menu Lain (dari Header)

- 📊 Riwayat Penjualan → SalesHistoryScreen (dari dashboard)
- 🧾 Checkout → SalesCheckoutScreen (dari POS)
- 📝 Detail Pelanggan → CustomerDetailScreen (dari list)
- ➕ Tambah Pelanggan → CustomerFormScreen (dari FAB)

---

## 📱 FITUR UTAMA

### 1. Sales Dashboard

**Screen**: `SalesDashboardScreen.kt`

**Purpose**: Overview penjualan dengan statistik dan quick actions.

#### Features

✅ **Period Selector**

- Filter data berdasarkan periode:
  - Harian (hari ini)
  - Mingguan (7 hari terakhir)
  - Bulanan (30 hari terakhir)

✅ **Stats Card (Gradient Blue)**

- Total Penjualan (Rp)
- Total Transaksi (count)
- Produk Terjual (items)

✅ **Quick Access Menu (3 Buttons)**

- 💳 POS → Langsung ke Point of Sale
- 👥 Pelanggan → Manajemen customer
- 📊 Riwayat → Sales history

✅ **Recent Transactions (Last 5)**

- Customer name
- Date & time
- Items count
- Total amount (Rp)
- Payment method icon

#### UI Elements

| Element              | Type                | Behavior                       |
| -------------------- | ------------------- | ------------------------------ |
| Period Chips         | FilterChip          | Single selection, update stats |
| Stats Card           | Gradient Surface    | Display only, no interaction   |
| Quick Action Buttons | Clickable Card      | Navigate to screens            |
| Transaction Cards    | Clickable List Item | Show transaction detail        |
| "Lihat Semua"        | TextButton          | Navigate to full history       |

#### Calculations

```kotlin
// Total penjualan
val totalSales = sales.sumOf { it.total }

// Total transaksi
val transactionCount = sales.size

// Produk terjual
val itemsSold = sales.sumOf { it.itemCount }
```

---

### 2. Point of Sale (POS)

**Screen**: `SalesPOSScreen.kt`

**Purpose**: Interface kasir untuk proses transaksi penjualan.

#### Features

✅ **Product Search**

- Real-time search by product name
- Clear button untuk reset search

✅ **Category Filter**

- Chips: Semua | Minuman | Makanan | Snack | Lainnya
- Single selection filter
- Auto-update product grid

✅ **Product Grid (2 Columns)**

- Product image placeholder (by category)
- Product name
- Stock info
- Price (formatted Rp)
- Quantity badge (if in cart)
- Add/Remove buttons (if in cart)

✅ **Cart Management**

- Add product to cart (tap card)
- Increase quantity (+)
- Decrease quantity (−)
- Auto-remove if quantity = 0
- Stock validation (max = available stock)

✅ **Cart Badge (Header)**

- Badge dengan total items
- Clickable to view cart summary

✅ **Cart Summary Bar (Bottom)**

- Show only if cart not empty
- Display: Total items + Total price
- Checkout button (green, prominent)

✅ **Responsive States**

- Empty cart: No summary bar
- Has items: Show summary + checkout
- Product in cart: Highlighted border (green, 2dp)
- Stock limit: Disable add button

#### Cart Logic

```kotlin
// State management
val cartItems = remember { mutableStateListOf<CartItem>() }

// Add to cart
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

// Remove from cart
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

// Calculate totals
val totalItems = cartItems.sumOf { it.quantity }
val totalPrice = cartItems.sumOf { it.product.price * it.quantity }
```

#### Product Card States

```kotlin
val quantityInCart = cartItems.find { it.product.id == product.id }?.quantity ?: 0

// Border
border = BorderStroke(
    width = if (quantityInCart > 0) 2.dp else 1.dp,
    color = if (quantityInCart > 0) Color(0xFF10B981) else Color(0xFFE2E8F0)
)

// Elevation
shadowElevation = if (quantityInCart > 0) 4.dp else 1.dp
```

#### Category Icons

```kotlin
val icon = when (product.category) {
    "Minuman" -> Icons.Default.LocalCafe
    "Makanan" -> Icons.Default.Restaurant
    "Snack" -> Icons.Default.Fastfood
    else -> Icons.Default.ShoppingBag
}
```

---

### 3. Customer Management

**Screen**: `CustomerListScreen.kt`

**Purpose**: Manajemen database pelanggan.

#### Features

✅ **Search Functionality**

- Search by: Name, Phone, Email
- Real-time filtering
- Case-insensitive

✅ **Sort Options** (Dropdown menu)

- Nama (A-Z)
- Total Pembelian (high to low)
- Terakhir Beli (recent first)

✅ **Stats Summary Card**

- Total pelanggan (count)
- Total transaksi (sum all customers)
- Divider between stats

✅ **Customer Cards**

- Avatar with first letter (colored by segment)
- Name (bold)
- Phone number (icon + text)
- Transaction count badge
- Last purchase date
- Total purchases (Rp, blue, bold)

✅ **FAB (Add Customer)**

- Floating action button (green)
- Navigate to CustomerFormScreen

✅ **Empty State**

- Show if no customers
- Icon + message
- "Tambahkan pelanggan pertama" text

#### Sort Logic

```kotlin
val filteredCustomers = customers
    .filter { customer ->
        searchQuery.isEmpty() ||
        customer.name.contains(searchQuery, ignoreCase = true) ||
        customer.phone.contains(searchQuery) ||
        customer.email.contains(searchQuery, ignoreCase = true)
    }
    .let { list ->
        when (sortBy) {
            "purchases" -> list.sortedByDescending { it.totalPurchases }
            "recent" -> list.sortedByDescending { it.lastPurchase }
            else -> list.sortedBy { it.name }
        }
    }
```

#### Customer Segmentation

```kotlin
// Avatar background color berdasarkan total pembelian
val avatarColor = when {
    customer.totalPurchases > 5_000_000 -> Color(0xFFFFD700) // Gold - VIP
    customer.totalPurchases > 1_000_000 -> Color(0xFF8B5CF6) // Purple - Regular
    else -> Color(0xFF94A3B8) // Gray - New
}
```

#### Transaction Count Badge

```kotlin
Surface(
    shape = RoundedCornerShape(6.dp),
    color = Color(0xFF10B981).copy(alpha = 0.1f)
) {
    Text(
        text = "${customer.transactionCount}x",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF10B981),
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
```

---

### 4. Checkout Process

**Screen**: `SalesCheckoutScreen.kt`

**Purpose**: Process payment & complete transaction.

#### Features

✅ **Cart Items Summary**

- List of selected products
- Quantity × Price each
- Edit quantity (optional)
- Remove item (optional)

✅ **Customer Selection**

- Dropdown or search customer
- Option: "Walk-in Customer" (no customer selected)
- Quick add new customer

✅ **Payment Method Selector**

- Radio buttons or chips:
  - 💵 Cash
  - 🏦 Transfer Bank
  - 📱 E-Wallet
  - 💳 Credit/Debit Card

✅ **Cash Payment Fields** (if Cash selected)

- TextField "Jumlah Diterima" (amount received)
- Auto-calculate change:
  - Kembalian = Diterima − Total
  - Show warning if < total

✅ **Notes Field**

- Optional notes
- Multiline TextField

✅ **Total Summary**

- Subtotal
- Discount (optional, future)
- Tax (optional, future)
- **Grand Total** (large, bold)

✅ **Action Buttons**

- Cancel (outlined, gray)
- Process Payment (filled, green)

#### Payment Validation

```kotlin
fun validateCheckout(): Boolean {
    // Customer not required for walk-in
    if (paymentMethod.isEmpty()) {
        showError("Pilih metode pembayaran")
        return false
    }

    if (paymentMethod == "Cash") {
        if (amountReceived < grandTotal) {
            showError("Jumlah diterima kurang")
            return false
        }
    }

    if (cartItems.isEmpty()) {
        showError("Keranjang kosong")
        return false
    }

    return true
}
```

#### Success Flow

```
Process Payment
    ↓
[Validation]
    ↓
Create Sale Record
    ↓
Update Product Stock
    ↓
Update Customer History (if not walk-in)
    ↓
Show Success Dialog / Receipt
    ↓
Clear Cart
    ↓
Navigate Back to POS / Dashboard
```

---

### 5. Sales History

**Screen**: `SalesHistoryScreen.kt`

**Purpose**: Riwayat transaksi penjualan lengkap.

#### Features

✅ **Filter by Period**

- Chips: Semua | Hari Ini | Minggu Ini | Bulan Ini
- Custom date range (future)

✅ **Search**

- By customer name
- By transaction ID

✅ **History Cards**

- Customer name
- Transaction ID (#TX-xxxx)
- Date & time
- Total amount
- Status badge:
  - ✅ Paid (green)
  - ⏳ Pending (orange)
  - ❌ Cancelled (red)

✅ **FAB (Add New Sale)**

- Navigate to SalesCheckoutScreen

✅ **Card Actions**

- Tap to view detail
- Long press for options:
  - View Receipt
  - Refund (if paid)
  - Cancel (if pending)

#### Status Color Coding

```kotlin
val (statusText, statusColor) = when (status) {
    "Paid" -> "Lunas" to Color(0xFF10B981)
    "Pending" -> "Pending" to Color(0xFFF59E0B)
    "Cancelled" -> "Batal" to Color(0xFFEF4444)
    else -> "Unknown" to Color.Gray
}
```

---

## 🎨 UI/UX Patterns

### 1. Consistent Header

Semua screen menggunakan header pattern yang sama:

```kotlin
// Sales role screens header
Row(
    modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Column {
        Text(
            text = "DASHBOARD PENJUALAN",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = "Staff Penjualan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }

    // Action icons...
}
```

### 2. Price Formatting

```kotlin
// Consistent Rupiah formatting
fun formatCurrency(amount: Double): String {
    return "Rp ${String.format("%,d", amount.toLong())}"
}

// Usage
Text(
    text = formatCurrency(totalPrice),
    style = MaterialTheme.typography.titleLarge,
    fontWeight = FontWeight.Bold,
    color = Color(0xFF10B981)
)
```

### 3. Payment Method Icons

```kotlin
val (icon, color) = when (paymentMethod) {
    "Cash" -> Icons.Default.Payments to Color(0xFF10B981)
    "Transfer" -> Icons.Default.AccountBalance to Color(0xFF197FE6)
    "E-Wallet" -> Icons.Default.Wallet to Color(0xFF8B5CF6)
    "Credit/Debit" -> Icons.Default.CreditCard to Color(0xFFF59E0B)
    else -> Icons.Default.Payment to Color.Gray
}
```

---

## 🚀 Business Logic

### Cart Manager

**File**: `data/CartManager.kt` (if exists)

```kotlin
object CartManager {
    private val items = mutableStateListOf<CartItem>()

    fun addItem(product: POSProduct, quantity: Int = 1) {
        val existing = items.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            items.add(CartItem(product, quantity))
        }
    }

    fun removeItem(productId: String) {
        items.removeIf { it.product.id == productId }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        val item = items.find { it.product.id == productId }
        if (item != null) {
            if (newQuantity > 0) {
                item.quantity = newQuantity
            } else {
                items.remove(item)
            }
        }
    }

    fun clear() {
        items.clear()
    }

    fun getTotalItems(): Int = items.sumOf { it.quantity }
    fun getTotalPrice(): Double = items.sumOf { it.product.price * it.quantity }
    fun getItems(): List<CartItem> = items.toList()
}
```

### Stock Validation

```kotlin
fun isStockAvailable(product: POSProduct, requestedQty: Int): Boolean {
    return product.stock >= requestedQty
}

fun getMaxQuantity(product: POSProduct, currentInCart: Int): Int {
    return product.stock - currentInCart
}
```

---

## 📊 Analytics & Metrics

### Sales Metrics

```kotlin
data class SalesMetrics(
    val totalRevenue: Double,
    val transactionCount: Int,
    val itemsSold: Int,
    val averageTransaction: Double,
    val topProducts: List<Product>,
    val topCustomers: List<Customer>
)

fun calculateMetrics(sales: List<Sale>): SalesMetrics {
    val totalRevenue = sales.sumOf { it.total }
    val transactionCount = sales.size
    val itemsSold = sales.sumOf { it.items.sumOf { it.quantity } }
    val averageTransaction = if (transactionCount > 0)
        totalRevenue / transactionCount else 0.0

    return SalesMetrics(
        totalRevenue = totalRevenue,
        transactionCount = transactionCount,
        itemsSold = itemsSold,
        averageTransaction = averageTransaction,
        topProducts = getTopProducts(sales),
        topCustomers = getTopCustomers(sales)
    )
}
```

---

## 🔒 Permissions & Access

### Role-Based Access

| Feature       | View           | Add | Edit | Delete |
| ------------- | -------------- | --- | ---- | ------ |
| Dashboard     | ✅             | -   | -    | -      |
| POS           | ✅             | ✅  | ✅   | ✅     |
| Customers     | ✅             | ✅  | ✅   | ✅     |
| Sales History | ✅             | -   | ❌   | ❌     |
| Products      | ✅ (read-only) | ❌  | ❌   | ❌     |
| Pricing       | ❌             | ❌  | ❌   | ❌     |

### Screen Access Matrix

```kotlin
when (userRole) {
    "Penjualan" -> {
        // Allowed
        - SalesDashboardScreen
        - SalesPOSScreen
        - CustomerListScreen
        - SalesCheckoutScreen
        - SalesHistoryScreen

        // Denied
        - MaterialListScreen
        - ProductionListScreen
        - FinanceDashboardScreen
    }
}
```

---

## 🎯 Future Enhancements

### Phase 2 (In Development) ⭐

- [x] **Customer Management** ✅ DONE
  - Customer list dengan segmentation (VIP/Regular/New)
  - Customer detail dengan purchase history
  - Add/Edit customer form
  - Customer analytics

- [x] **Pre-Order System** ⭐ **IMPLEMENTED**
  - Create pre-order dengan customer & product selection
  - Down Payment (DP) minimal 30%
  - Pickup scheduling (date & time)
  - Pre-order list dengan status filter:
    - PENDING: Menunggu DP
    - DP_PAID: DP Sudah Dibayar
    - READY: Siap Diambil
    - COMPLETED: Selesai (sudah diambil)
    - CANCELLED: Dibatalkan
  - Pre-order detail dengan payment actions
  - Calendar view untuk jadwal pickup
  - Auto-convert to sale saat pickup
  - Reminder H-1 pickup date
  - Customer stats auto-update setelah complete

- [ ] **Discount Management**
  - Percentage discount
  - Fixed amount discount
  - Promo codes
  - Member discounts

- [ ] **Receipt Generation**
  - Print receipt (Bluetooth printer)
  - Email receipt
  - WhatsApp receipt
  - PDF export

- [ ] **Return/Refund**
  - Product return process
  - Refund approval
  - Stock adjustment

### Phase 3 (Future)

- [ ] **Analytics Dashboard**
  - Sales trends chart
  - Best selling products
  - Peak hours analysis
  - Customer segments
  - Pre-order analytics
  - Customer lifetime value

- [ ] **Integration**
  - Sync with backend API
  - Real-time inventory
  - Payment gateway
  - Accounting software
  - WhatsApp notifications untuk pre-order

- [ ] **Advanced Features**
  - Loyalty points system
  - Membership tiers
  - Automated marketing
  - Delivery management

---

## 📝 Notes

- **Simple & Fast**: Interface dirancang untuk transaksi cepat
- **Minimal Clicks**: Reduce steps untuk complete transaction
- **Visual Feedback**: Jelas untuk setiap action (add cart, payment success)
- **Error Prevention**: Validation di setiap step
- **Offline Ready**: Can work without internet (future)
- **Print Ready**: Support untuk thermal printer (future)
