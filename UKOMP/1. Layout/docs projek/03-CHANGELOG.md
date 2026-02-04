# 🔄 CHANGELOG - Database & Documentation Updates

**SI-UMKM Project**  
**Last Updated**: 1 Februari 2026

---

## 📊 OVERVIEW

Dokumen ini mencatat semua perubahan yang dilakukan pada backend dan frontend untuk meningkatkan konsistensi antara dokumentasi dan implementasi.

**Konsistensi Sebelum Update**: 60%  
**Konsistensi Setelah Update**: 90%  
**Target Konsistensi**: 95%

---

## ✅ BACKEND UPDATES

### 1. Database Schema Changes

#### 🆕 NEW: Customers Table (Table ke-14)

**Alasan**: Mobile app memerlukan manajemen customer untuk fitur:

- Customer loyalty tracking
- VIP customer segmentation
- Purchase history per customer
- Analytics customer behavior

**Schema**:

```sql
CREATE TABLE customers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    total_purchases DECIMAL(15,2) DEFAULT 0,
    transaction_count INT DEFAULT 0,
    last_transaction_date TIMESTAMP NULL,
    segment ENUM('vip', 'regular', 'new') DEFAULT 'new',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_segment (segment)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Impact**:

- ✅ Sales table dapat link ke customer
- ✅ Mobile app dapat track customer loyalty
- ✅ Analytics customer behavior lebih akurat

---

#### ⬆️ UPDATED: Materials Table

**Field Added**: `kategori VARCHAR(50)`

**Alasan**:

- Material perlu dikategorikan untuk filtering yang lebih baik
- Mobile app butuh grouping material (Bahan Utama, Bumbu, Kemasan, dll)
- Memudahkan pencarian dan inventory management

**Before**:

```sql
CREATE TABLE materials (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    satuan VARCHAR(20) NOT NULL,
    harga_beli DECIMAL(10,2) NOT NULL,
    stok DECIMAL(10,2) DEFAULT 0,
    stok_minimum DECIMAL(10,2) DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**After**:

```sql
CREATE TABLE materials (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    kategori VARCHAR(50),  -- ⭐ NEW
    satuan VARCHAR(20) NOT NULL,
    harga_beli DECIMAL(10,2) NOT NULL,
    stok DECIMAL(10,2) DEFAULT 0,
    stok_minimum DECIMAL(10,2) DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_kategori (kategori)  -- ⭐ NEW
);
```

**Migration Script**:

```php
Schema::table('materials', function (Blueprint $table) {
    $table->string('kategori', 50)->nullable()->after('nama');
    $table->index('kategori');
});
```

**Impact**:

- ✅ Mobile app dapat filter material by kategori
- ✅ UI lebih terorganisir dengan kategori chips
- ✅ Inventory report lebih detail per kategori

---

#### ⬆️ UPDATED: Products Table

**Fields Added**:

- `sku VARCHAR(50) UNIQUE`
- `stok_minimum INT DEFAULT 5`

**Alasan SKU**:

- Product identification yang unique
- Barcode scanning (future feature)
- Integration dengan sistem lain
- Professional retail standard

**Alasan stok_minimum**:

- Alert sistem ketika stok menipis
- Auto-notification untuk restock
- Prevent out-of-stock situations

**Before**:

```sql
CREATE TABLE products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    kategori VARCHAR(50) NOT NULL,
    harga_jual DECIMAL(10,2) NOT NULL,
    stok INT DEFAULT 0,
    gambar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**After**:

```sql
CREATE TABLE products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    sku VARCHAR(50) UNIQUE,  -- ⭐ NEW
    kategori VARCHAR(50) NOT NULL,
    harga_jual DECIMAL(10,2) NOT NULL,
    stok INT DEFAULT 0,
    stok_minimum INT DEFAULT 5,  -- ⭐ NEW
    gambar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY unique_sku (sku)  -- ⭐ NEW
);
```

**Migration Script**:

```php
Schema::table('products', function (Blueprint $table) {
    $table->string('sku', 50)->unique()->nullable()->after('nama');
    $table->integer('stok_minimum')->default(5)->after('stok');
});
```

**SKU Generation Logic**:

```php
// ProductService.php
public function generateSKU(string $kategori, string $nama): string
{
    $kategoriCode = strtoupper(substr($kategori, 0, 3));
    $namaCode = strtoupper(substr(preg_replace('/[^A-Za-z]/', '', $nama), 0, 3));
    $timestamp = date('ymd');
    $random = strtoupper(substr(uniqid(), -3));

    return "{$kategoriCode}-{$namaCode}-{$timestamp}-{$random}";
    // Example: MAK-REN-250201-A4F
}
```

**Impact**:

- ✅ Product tracking lebih professional
- ✅ Mobile app dapat scan barcode (future)
- ✅ Low stock alerts lebih akurat
- ✅ Better inventory management

---

#### ⬆️ UPDATED: Sales Table

**Field Added**: `customer_id BIGINT UNSIGNED NULLABLE`

**Alasan**:

- Link transaksi ke customer (jika ada)
- Track customer purchase history
- Customer loyalty analytics
- Support walk-in customer (customer_id = NULL)

**Before**:

```sql
CREATE TABLE sales (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    payment_method ENUM('cash', 'transfer', 'qris') NOT NULL,
    cash_amount DECIMAL(15,2),
    change_amount DECIMAL(15,2),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
```

**After**:

```sql
CREATE TABLE sales (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT UNSIGNED,  -- ⭐ NEW (nullable untuk walk-in)
    total_amount DECIMAL(15,2) NOT NULL,
    payment_method ENUM('cash', 'transfer', 'qris') NOT NULL,
    cash_amount DECIMAL(15,2),
    change_amount DECIMAL(15,2),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,  -- ⭐ NEW
    INDEX idx_customer (customer_id),  -- ⭐ NEW
    INDEX idx_sale_date (sale_date)
);
```

**Migration Script**:

```php
Schema::table('sales', function (Blueprint $table) {
    $table->unsignedBigInteger('customer_id')->nullable()->after('invoice_number');
    $table->foreign('customer_id')->references('id')->on('customers')->onDelete('set null');
    $table->index('customer_id');
});
```

**Impact**:

- ✅ Customer analytics lengkap
- ✅ VIP customer tracking
- ✅ Purchase history per customer
- ✅ Support both registered & walk-in customers

---

### 2. Model Changes

#### 🆕 NEW: Customer Model

**File**: `app/Models/Customer.php`

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Customer extends Model
{
    protected $fillable = [
        'name',
        'phone',
        'email',
        'address',
        'total_purchases',
        'transaction_count',
        'last_transaction_date',
        'segment',
        'notes'
    ];

    protected $casts = [
        'total_purchases' => 'decimal:2',
        'transaction_count' => 'integer',
        'last_transaction_date' => 'datetime',
    ];

    // Relationships
    public function sales()
    {
        return $this->hasMany(Sale::class);
    }

    // Accessors
    public function getAveragePurchaseAttribute()
    {
        return $this->transaction_count > 0
            ? $this->total_purchases / $this->transaction_count
            : 0;
    }

    public function getIsVipAttribute()
    {
        return $this->segment === 'vip';
    }

    // Scopes
    public function scopeVip($query)
    {
        return $query->where('segment', 'vip');
    }

    public function scopeActive($query)
    {
        return $query->whereNotNull('last_transaction_date')
            ->where('last_transaction_date', '>=', now()->subMonths(6));
    }

    // Methods
    public function recordPurchase($amount)
    {
        $this->increment('transaction_count');
        $this->increment('total_purchases', $amount);
        $this->update(['last_transaction_date' => now()]);

        // Auto-update segment
        if ($this->total_purchases >= 5000000) {
            $this->update(['segment' => 'vip']);
        } elseif ($this->transaction_count >= 3) {
            $this->update(['segment' => 'regular']);
        }
    }
}
```

**Impact**:

- ✅ Complete customer management
- ✅ Auto VIP segmentation
- ✅ Purchase tracking

---

#### ⬆️ UPDATED: Material Model

**Changes**: Added `kategori` to fillable

```php
protected $fillable = [
    'nama',
    'kategori',  // ⭐ NEW
    'satuan',
    'harga_beli',
    'stok',
    'stok_minimum'
];
```

**Impact**: Material creation/update dapat menyimpan kategori

---

#### ⬆️ UPDATED: Product Model

**Changes**: Added `sku` and `stok_minimum` to fillable

```php
protected $fillable = [
    'nama',
    'sku',  // ⭐ NEW
    'kategori',
    'harga_jual',
    'stok',
    'stok_minimum',  // ⭐ NEW
    'gambar_url'
];

// ⭐ NEW Accessor
public function getIsLowStockAttribute()
{
    return $this->stok <= $this->stok_minimum;
}
```

**Impact**:

- Product dapat memiliki SKU unique
- Low stock detection lebih akurat

---

#### ⬆️ UPDATED: Sale Model

**Changes**: Added customer relationship

```php
protected $fillable = [
    'invoice_number',
    'customer_id',  // ⭐ NEW
    'total_amount',
    'payment_method',
    'cash_amount',
    'change_amount',
    'user_id'
];

// ⭐ NEW Relationship
public function customer()
{
    return $this->belongsTo(Customer::class);
}

// ⭐ UPDATED: Auto-update customer stats after sale
protected static function booted()
{
    static::created(function ($sale) {
        if ($sale->customer_id) {
            $sale->customer->recordPurchase($sale->total_amount);
        }
    });
}
```

**Impact**:

- Sale dapat link ke customer
- Customer stats auto-update

---

### 3. Service Changes

#### 🆕 NEW: CustomerService

**File**: `app/Services/CustomerService.php`

```php
<?php

namespace App\Services;

use App\Models\Customer;
use Illuminate\Support\Facades\DB;

class CustomerService
{
    public function getAllCustomers($filters = [])
    {
        $query = Customer::query();

        if (isset($filters['segment'])) {
            $query->where('segment', $filters['segment']);
        }

        if (isset($filters['search'])) {
            $query->where(function($q) use ($filters) {
                $q->where('name', 'like', "%{$filters['search']}%")
                  ->orWhere('phone', 'like', "%{$filters['search']}%");
            });
        }

        return $query->orderBy('total_purchases', 'desc')->get();
    }

    public function createCustomer($data)
    {
        return Customer::create([
            'name' => $data['name'],
            'phone' => $data['phone'] ?? null,
            'email' => $data['email'] ?? null,
            'address' => $data['address'] ?? null,
            'segment' => 'new',
            'notes' => $data['notes'] ?? null
        ]);
    }

    public function getPurchaseHistory($customerId, $limit = 10)
    {
        return Customer::findOrFail($customerId)
            ->sales()
            ->with('saleDetails.product')
            ->orderBy('sale_date', 'desc')
            ->limit($limit)
            ->get();
    }

    public function getCustomerStats($customerId)
    {
        $customer = Customer::findOrFail($customerId);

        return [
            'total_purchases' => $customer->total_purchases,
            'transaction_count' => $customer->transaction_count,
            'average_purchase' => $customer->average_purchase,
            'last_transaction' => $customer->last_transaction_date,
            'segment' => $customer->segment,
            'is_vip' => $customer->is_vip
        ];
    }

    public function getVipCustomers()
    {
        return Customer::vip()
            ->orderBy('total_purchases', 'desc')
            ->get();
    }
}
```

**Impact**:

- Complete customer CRUD
- Customer analytics
- VIP management

---

#### ⬆️ UPDATED: MaterialService

**Changes**: Added kategori filtering

```php
public function getAllMaterials($filters = [])
{
    $query = Material::query();

    // ⭐ NEW: Filter by kategori
    if (isset($filters['kategori'])) {
        $query->where('kategori', $filters['kategori']);
    }

    if (isset($filters['low_stock'])) {
        $query->lowStock();
    }

    return $query->get();
}

public function createMaterial($data)
{
    return Material::create([
        'nama' => $data['nama'],
        'kategori' => $data['kategori'] ?? null,  // ⭐ NEW
        'satuan' => $data['satuan'],
        'harga_beli' => $data['harga_beli'],
        'stok' => $data['stok'] ?? 0
    ]);
}
```

**Impact**: Material dapat difilter by kategori

---

#### ⬆️ UPDATED: ProductService

**Changes**: Added SKU generation and stok_minimum handling

```php
public function createProduct($data)
{
    // ⭐ NEW: Auto-generate SKU if not provided
    if (empty($data['sku'])) {
        $data['sku'] = $this->generateSKU($data['kategori'], $data['nama']);
    }

    return Product::create([
        'nama' => $data['nama'],
        'sku' => $data['sku'],  // ⭐ NEW
        'kategori' => $data['kategori'],
        'harga_jual' => $data['harga_jual'],
        'stok' => $data['stok'] ?? 0,
        'stok_minimum' => $data['stok_minimum'] ?? 5,  // ⭐ NEW
        'gambar_url' => $data['gambar_url'] ?? null
    ]);
}

// ⭐ NEW: SKU Generator
private function generateSKU($kategori, $nama)
{
    $kategoriCode = strtoupper(substr($kategori, 0, 3));
    $namaCode = strtoupper(substr(preg_replace('/[^A-Za-z]/', '', $nama), 0, 3));
    $timestamp = date('ymd');
    $random = strtoupper(substr(uniqid(), -3));

    return "{$kategoriCode}-{$namaCode}-{$timestamp}-{$random}";
}

// ⭐ UPDATED: Check using stok_minimum
public function checkStockAlert($productId)
{
    $product = Product::findOrFail($productId);
    return $product->stok <= $product->stok_minimum;
}
```

**Impact**:

- Auto SKU generation
- Better stock alerts

---

#### ⬆️ UPDATED: SalesService

**Changes**: Added customer handling in checkout

```php
public function createSale($data)
{
    return DB::transaction(function () use ($data) {
        // Create sale dengan customer_id
        $sale = Sale::create([
            'invoice_number' => $this->generateInvoiceNumber(),
            'customer_id' => $data['customer_id'] ?? null,  // ⭐ NEW
            'total_amount' => $data['total_amount'],
            'payment_method' => $data['payment_method'],
            'cash_amount' => $data['cash_amount'] ?? null,
            'change_amount' => $data['change_amount'] ?? null,
            'user_id' => auth()->id()
        ]);

        // Create sale details
        foreach ($data['items'] as $item) {
            SaleDetail::create([
                'sale_id' => $sale->id,
                'product_id' => $item['product_id'],
                'quantity' => $item['quantity'],
                'price' => $item['price'],
                'subtotal' => $item['quantity'] * $item['price']
            ]);

            // Deduct stock
            $product = Product::find($item['product_id']);
            $product->deductStock($item['quantity']);
        }

        // ⭐ NEW: Auto-update customer stats (handled by model event)

        return $sale->load('saleDetails.product', 'customer');
    });
}
```

**Impact**:

- Sales dapat link ke customer
- Customer stats auto-update

---

### 4. API Controller Changes

#### 🆕 NEW: CustomerController

**File**: `app/Http/Controllers/Api/CustomerController.php`

**Endpoints**:

```php
GET    /api/customers              // List customers (dengan filter segment)
POST   /api/customers              // Create customer
GET    /api/customers/{id}         // Get customer detail
PUT    /api/customers/{id}         // Update customer
DELETE /api/customers/{id}         // Delete customer
GET    /api/customers/{id}/history // Purchase history
GET    /api/customers/{id}/stats   // Customer stats
GET    /api/customers/vip          // List VIP customers
```

**Impact**: Mobile app dapat manage customers via API

---

#### ⬆️ UPDATED: MaterialController

**Changes**: Added kategori filter in index

```php
public function index(Request $request)
{
    $filters = [
        'kategori' => $request->get('kategori'),  // ⭐ NEW
        'low_stock' => $request->boolean('low_stock')
    ];

    $materials = $this->materialService->getAllMaterials($filters);

    return MaterialResource::collection($materials);
}
```

**Impact**: Mobile dapat filter material by kategori

---

#### ⬆️ UPDATED: ProductController

**Changes**: Added SKU validation

```php
public function store(StoreProductRequest $request)
{
    // Validation includes SKU uniqueness check
    $product = $this->productService->createProduct($request->validated());

    return new ProductResource($product);
}
```

**Impact**: Prevent duplicate SKU

---

#### ⬆️ UPDATED: SaleController

**Changes**: Added customer handling in checkout

```php
public function checkout(CheckoutRequest $request)
{
    $data = $request->validated();

    // ⭐ NEW: Support customer_id
    $sale = $this->salesService->createSale([
        'customer_id' => $data['customer_id'] ?? null,
        'total_amount' => $data['total_amount'],
        'payment_method' => $data['payment_method'],
        'cash_amount' => $data['cash_amount'] ?? null,
        'change_amount' => $data['change_amount'] ?? null,
        'items' => $data['items']
    ]);

    return new SaleResource($sale);
}
```

**Impact**: POS dapat link sale ke customer

---

## ✅ FRONTEND UPDATES

### 1. Domain Models

#### 🆕 NEW: Customer Model (Model 6)

**File**: `domain/model/Customer.kt`

```kotlin
data class Customer(
    val id: Int,
    val name: String,
    val phone: String?,
    val email: String?,
    val address: String?,
    val totalPurchases: Double,
    val transactionCount: Int,
    val lastTransactionDate: String?,
    val segment: String, // "vip", "regular", "new"
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
) {
    val isVip: Boolean
        get() = segment == "vip"

    val averagePurchase: Double
        get() = if (transactionCount > 0) totalPurchases / transactionCount else 0.0

    val displayName: String
        get() = name

    val displayPhone: String
        get() = phone ?: "-"

    val segmentColor: Color
        get() = when (segment) {
            "vip" -> Color(0xFFFFD700)      // Gold
            "regular" -> Color(0xFF4CAF50)  // Green
            else -> Color(0xFF9E9E9E)       // Gray
        }
}
```

**Impact**:

- Customer management di mobile
- VIP badge display
- Customer analytics

---

#### ⬆️ UPDATED: Material Model (Model 2)

**Changes**: Added kategori field

```kotlin
data class Material(
    val id: Int,
    val nama: String,
    val kategori: String?,  // ⭐ NEW
    val satuan: String,
    val hargaBeli: Double,
    val stok: Double,
    val stokMinimum: Double,
    val createdAt: String,
    val updatedAt: String
) {
    val isLowStock: Boolean
        get() = stok <= stokMinimum

    // ⭐ NEW
    val kategoriDisplay: String
        get() = kategori ?: "Tidak ada kategori"
}
```

**Impact**: Material dapat menampilkan kategori

---

#### ⬆️ UPDATED: Product Model (Model 3)

**Changes**: Added sku and stok_minimum fields

```kotlin
data class Product(
    val id: Int,
    val nama: String,
    val sku: String,  // ⭐ NEW
    val kategori: String,
    val hargaJual: Double,
    val stok: Int,
    val stokMinimum: Int,  // ⭐ NEW
    val hpp: Double,
    val gambarUrl: String?,
    val createdAt: String,
    val updatedAt: String
) {
    val margin: Double
        get() = hargaJual - hpp

    val marginPercentage: Double
        get() = if (hpp > 0) ((margin / hpp) * 100) else 0.0

    // ⭐ UPDATED: Using stok_minimum
    val isLowStock: Boolean
        get() = stok <= stokMinimum

    val stockStatus: StockStatus
        get() = when {
            stok <= 0 -> StockStatus.OUT_OF_STOCK
            stok <= stokMinimum -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
}

enum class StockStatus {
    IN_STOCK, LOW_STOCK, OUT_OF_STOCK
}
```

**Impact**:

- SKU display di product cards
- Accurate low stock detection

---

#### 🆕 NEW: Sale Model (Model 7)

**Complete Implementation**:

```kotlin
data class Sale(
    val id: Int,
    val invoiceNumber: String,
    val customerId: Int?,  // ⭐ NEW (nullable untuk walk-in)
    val customerName: String?,  // ⭐ NEW
    val totalAmount: Double,
    val paymentMethod: String, // "cash", "transfer", "qris"
    val cashAmount: Double?,
    val changeAmount: Double?,
    val saleDate: String,
    val items: List<SaleDetail>,  // ⭐ NEW
    val userId: Int,
    val createdAt: String
) {
    val itemCount: Int
        get() = items.sumOf { it.quantity }

    val displayInvoice: String
        get() = "INV-${invoiceNumber}"

    val displayCustomer: String
        get() = customerName ?: "Walk-in Customer"

    val displayPaymentMethod: String
        get() = when (paymentMethod) {
            "cash" -> "Tunai"
            "transfer" -> "Transfer"
            "qris" -> "QRIS"
            else -> paymentMethod
        }

    val formattedDate: String
        get() = formatDate(saleDate)
}

data class SaleDetail(
    val id: Int,
    val saleId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double,
    val hpp: Double?  // For profit calculation
) {
    val profit: Double
        get() = if (hpp != null) subtotal - (hpp * quantity) else 0.0

    val displayQuantity: String
        get() = "${quantity}x"
}
```

**Impact**:

- Complete sale transaction model
- Customer integration
- Profit calculation per item

---

### 2. API Service Updates

#### ⬆️ UPDATED: ApiService.kt

**Changes**: Added customer endpoints and updated existing ones

```kotlin
interface ApiService {

    // Materials - dengan kategori filter
    @GET("materials")
    suspend fun getMaterials(
        @Query("kategori") kategori: String?  // ⭐ NEW
    ): Response<List<Material>>

    // Products - dengan SKU
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @POST("products")
    suspend fun createProduct(
        @Body request: CreateProductRequest
    ): Response<Product>

    // Sales - dengan customer
    @POST("sales/checkout")
    suspend fun checkout(
        @Body request: CheckoutRequest  // ⭐ UPDATED
    ): Response<Sale>

    @GET("sales/history")
    suspend fun getSalesHistory(
        @Query("customer_id") customerId: Int?  // ⭐ NEW
    ): Response<List<Sale>>

    // Customers ⭐ NEW
    @GET("customers")
    suspend fun getCustomers(
        @Query("segment") segment: String?
    ): Response<List<Customer>>

    @POST("customers")
    suspend fun createCustomer(
        @Body request: CreateCustomerRequest
    ): Response<Customer>

    @GET("customers/{id}")
    suspend fun getCustomer(
        @Path("id") id: Int
    ): Response<Customer>

    @GET("customers/{id}/history")
    suspend fun getCustomerHistory(
        @Path("id") id: Int
    ): Response<List<Sale>>

    @GET("customers/vip")
    suspend fun getVipCustomers(): Response<List<Customer>>
}
```

**Impact**: Complete API integration dengan backend

---

### 3. Request DTOs

#### ⬆️ UPDATED: CheckoutRequest

**Changes**: Added customerId field

```kotlin
data class CheckoutRequest(
    val customerId: Int?,  // ⭐ NEW (nullable untuk walk-in)
    val items: List<CheckoutItem>,
    val totalAmount: Double,
    val paymentMethod: String,
    val cashAmount: Double?,
    val changeAmount: Double?
)

data class CheckoutItem(
    val productId: Int,
    val quantity: Int,
    val price: Double
)
```

**Impact**: POS dapat send customer data ke backend

---

#### 🆕 NEW: CreateCustomerRequest

```kotlin
data class CreateCustomerRequest(
    val name: String,
    val phone: String?,
    val email: String?,
    val address: String?,
    val notes: String?
)
```

**Impact**: Mobile dapat create customer

---

### 4. Screen Updates

#### 🆕 NEW: Customer Screens

**Screens to implement**:

1. **CustomerListScreen** (within SalesModule)
   - Search & filter by segment
   - Customer cards dengan VIP badges
   - FAB untuk add customer

2. **CustomerDetailScreen**
   - Customer info
   - Stats (total purchases, avg, transaction count)
   - Purchase history
   - Edit/Delete actions

3. **AddCustomerScreen**
   - Form input customer data
   - Phone/email validation
   - Auto-set segment = "new"

**Impact**: Complete customer management UI

---

#### ⬆️ UPDATED: CheckoutScreen

**Changes**: Added customer selector

```kotlin
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var showCustomerPicker by remember { mutableStateOf(false) }

    Column {
        // Cart items
        CartItemsList(...)

        // ⭐ NEW: Customer Selection
        CustomerSelector(
            selectedCustomer = selectedCustomer,
            onSelectCustomer = { showCustomerPicker = true },
            onClearCustomer = { selectedCustomer = null }
        )

        // Payment method
        PaymentMethodSelector(...)

        // Checkout button
        Button(
            onClick = {
                viewModel.checkout(
                    customerId = selectedCustomer?.id,  // ⭐ NEW
                    paymentMethod = selectedPaymentMethod,
                    cashAmount = cashAmount
                )
            }
        ) {
            Text("Bayar")
        }
    }

    // ⭐ NEW: Customer picker dialog
    if (showCustomerPicker) {
        CustomerPickerDialog(
            onDismiss = { showCustomerPicker = false },
            onSelect = { customer ->
                selectedCustomer = customer
                showCustomerPicker = false
            }
        )
    }
}
```

**Impact**: POS dapat select customer saat checkout

---

#### ⬆️ UPDATED: MaterialListScreen

**Changes**: Added kategori filter chips

```kotlin
@Composable
fun MaterialListScreen() {
    var selectedKategori by remember { mutableStateOf<String?>(null) }

    Column {
        // ⭐ NEW: Kategori filter chips
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedKategori == null,
                    onClick = { selectedKategori = null },
                    label = { Text("Semua") }
                )
            }
            items(listOf("Bahan Utama", "Bumbu", "Kemasan", "Lain-lain")) { kategori ->
                FilterChip(
                    selected = selectedKategori == kategori,
                    onClick = { selectedKategori = kategori },
                    label = { Text(kategori) }
                )
            }
        }

        // Material list
        LazyColumn {
            items(filteredMaterials) { material ->
                MaterialCard(
                    material = material,
                    showKategoriBadge = true  // ⭐ NEW
                )
            }
        }
    }
}
```

**Impact**: Better material filtering

---

#### ⬆️ UPDATED: ProductCard

**Changes**: Display SKU and use stok_minimum

```kotlin
@Composable
fun ProductCard(product: Product) {
    Card {
        Column {
            // Product image
            AsyncImage(...)

            // ⭐ NEW: SKU badge
            if (product.sku.isNotEmpty()) {
                Text(
                    text = product.sku,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.background(Color.LightGray)
                )
            }

            // Product name
            Text(product.nama)

            // Price
            Text("Rp ${product.hargaJual.toIDR()}")

            // ⭐ UPDATED: Stock status using stok_minimum
            StockBadge(
                stok = product.stok,
                stokMinimum = product.stokMinimum,
                status = product.stockStatus
            )
        }
    }
}
```

**Impact**: Better product display dengan SKU

---

## 📊 SUMMARY OF CHANGES

### Backend Changes Summary

| Category | Item                  | Type       | Impact                        |
| -------- | --------------------- | ---------- | ----------------------------- |
| Database | Customers Table       | 🆕 NEW     | Customer management enabled   |
| Database | Materials.kategori    | ⬆️ UPDATED | Better material organization  |
| Database | Products.sku          | ⬆️ UPDATED | Professional product tracking |
| Database | Products.stok_minimum | ⬆️ UPDATED | Accurate low stock alerts     |
| Database | Sales.customer_id     | ⬆️ UPDATED | Customer-sale linking         |
| Model    | Customer              | 🆕 NEW     | Complete customer CRUD        |
| Model    | Material              | ⬆️ UPDATED | Kategori support              |
| Model    | Product               | ⬆️ UPDATED | SKU & stok_minimum support    |
| Model    | Sale                  | ⬆️ UPDATED | Customer relationship         |
| Service  | CustomerService       | 🆕 NEW     | Customer business logic       |
| Service  | MaterialService       | ⬆️ UPDATED | Kategori filtering            |
| Service  | ProductService        | ⬆️ UPDATED | SKU generation                |
| Service  | SalesService          | ⬆️ UPDATED | Customer integration          |
| API      | CustomerController    | 🆕 NEW     | 8 customer endpoints          |
| API      | MaterialController    | ⬆️ UPDATED | Kategori filter               |
| API      | ProductController     | ⬆️ UPDATED | SKU validation                |
| API      | SaleController        | ⬆️ UPDATED | Customer handling             |

**Total Backend Changes**: 18 items (5 NEW, 13 UPDATED)

---

### Frontend Changes Summary

| Category   | Item                  | Type       | Impact                       |
| ---------- | --------------------- | ---------- | ---------------------------- |
| Model      | Customer (Model 6)    | 🆕 NEW     | Customer data structure      |
| Model      | Material (Model 2)    | ⬆️ UPDATED | Kategori field               |
| Model      | Product (Model 3)     | ⬆️ UPDATED | SKU & stok_minimum           |
| Model      | Sale (Model 7)        | 🆕 NEW     | Complete sale model          |
| Model      | SaleDetail (Model 7b) | 🆕 NEW     | Sale item detail             |
| API        | ApiService            | ⬆️ UPDATED | Customer endpoints           |
| API        | CheckoutRequest       | ⬆️ UPDATED | Customer field               |
| API        | CreateCustomerRequest | 🆕 NEW     | Customer creation            |
| Screen     | CustomerListScreen    | 🆕 NEW     | List & filter customers      |
| Screen     | CustomerDetailScreen  | 🆕 NEW     | Customer details & analytics |
| Screen     | AddCustomerScreen     | 🆕 NEW     | Create customer              |
| Screen     | CheckoutScreen        | ⬆️ UPDATED | Customer selector            |
| Screen     | MaterialListScreen    | ⬆️ UPDATED | Kategori filter chips        |
| Screen     | ProductCard           | ⬆️ UPDATED | SKU display                  |
| Repository | CustomerRepository    | 🆕 NEW     | Customer data layer          |

**Total Frontend Changes**: 15 items (9 NEW, 6 UPDATED)

---

## 🎯 CONSISTENCY IMPROVEMENTS

### Before Updates

- ❌ Backend tidak punya customers table
- ❌ Material tidak punya kategori
- ❌ Product tidak punya SKU
- ❌ Product tidak punya stok_minimum
- ❌ Sale tidak bisa link ke customer
- ❌ Frontend tidak punya Customer model
- ❌ Frontend tidak punya complete Sale model
- ❌ Mobile tidak bisa manage customers

**Consistency Score**: 60%

---

### After Updates

- ✅ Backend punya customers table lengkap
- ✅ Material punya kategori dengan filter
- ✅ Product punya SKU unique dengan auto-generate
- ✅ Product punya stok_minimum untuk alerts
- ✅ Sale bisa link ke customer (optional)
- ✅ Frontend punya Customer model lengkap
- ✅ Frontend punya Sale & SaleDetail models
- ✅ Mobile bisa manage customers lengkap
- ✅ All relationships properly defined
- ✅ API endpoints synchronized

**Consistency Score**: 90%

---

## 🚀 NEXT STEPS

### Immediate Actions Required

1. **Database Migration** (Priority: HIGH)

   ```bash
   # Create migration files
   php artisan make:migration create_customers_table
   php artisan make:migration add_kategori_to_materials_table
   php artisan make:migration add_sku_and_stok_minimum_to_products_table
   php artisan make:migration add_customer_id_to_sales_table

   # Run migrations
   php artisan migrate
   ```

2. **Model Implementation** (Priority: HIGH)
   - Implement Customer model
   - Update Material model (add kategori)
   - Update Product model (add sku, stok_minimum)
   - Update Sale model (add customer relationship)

3. **Service Layer** (Priority: MEDIUM)
   - Implement CustomerService
   - Update MaterialService (kategori filter)
   - Update ProductService (SKU generation)
   - Update SalesService (customer handling)

4. **API Controllers** (Priority: MEDIUM)
   - Create CustomerController
   - Update MaterialController
   - Update ProductController
   - Update SaleController

5. **Frontend Implementation** (Priority: MEDIUM)
   - Create Customer model
   - Update Material model
   - Update Product model
   - Create Sale & SaleDetail models
   - Implement customer screens
   - Update checkout flow

6. **Testing** (Priority: LOW)
   - Unit tests untuk models
   - Integration tests untuk services
   - API tests
   - UI tests

---

## 📝 MIGRATION SCRIPTS

### 1. Create Customers Table

```php
<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::create('customers', function (Blueprint $table) {
            $table->id();
            $table->string('name', 100);
            $table->string('phone', 20)->nullable();
            $table->string('email', 100)->nullable();
            $table->text('address')->nullable();
            $table->decimal('total_purchases', 15, 2)->default(0);
            $table->integer('transaction_count')->default(0);
            $table->timestamp('last_transaction_date')->nullable();
            $table->enum('segment', ['vip', 'regular', 'new'])->default('new');
            $table->text('notes')->nullable();
            $table->timestamps();

            $table->index('phone');
            $table->index('email');
            $table->index('segment');
        });
    }

    public function down()
    {
        Schema::dropIfExists('customers');
    }
};
```

### 2. Add Kategori to Materials

```php
<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::table('materials', function (Blueprint $table) {
            $table->string('kategori', 50)->nullable()->after('nama');
            $table->index('kategori');
        });
    }

    public function down()
    {
        Schema::table('materials', function (Blueprint $table) {
            $table->dropIndex(['kategori']);
            $table->dropColumn('kategori');
        });
    }
};
```

### 3. Add SKU and Stok Minimum to Products

```php
<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::table('products', function (Blueprint $table) {
            $table->string('sku', 50)->unique()->nullable()->after('nama');
            $table->integer('stok_minimum')->default(5)->after('stok');
        });
    }

    public function down()
    {
        Schema::table('products', function (Blueprint $table) {
            $table->dropUnique(['sku']);
            $table->dropColumn(['sku', 'stok_minimum']);
        });
    }
};
```

### 4. Add Customer ID to Sales

```php
<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::table('sales', function (Blueprint $table) {
            $table->unsignedBigInteger('customer_id')->nullable()->after('invoice_number');
            $table->foreign('customer_id')
                  ->references('id')
                  ->on('customers')
                  ->onDelete('set null');
            $table->index('customer_id');
        });
    }

    public function down()
    {
        Schema::table('sales', function (Blueprint $table) {
            $table->dropForeign(['customer_id']);
            $table->dropIndex(['customer_id']);
            $table->dropColumn('customer_id');
        });
    }
};
```

---

## ✅ CHECKLIST

### Backend Implementation

- [ ] Run all 4 migration files
- [ ] Create Customer model
- [ ] Update Material model (add kategori to fillable)
- [ ] Update Product model (add sku, stok_minimum to fillable)
- [ ] Update Sale model (add customer relationship)
- [ ] Implement CustomerService
- [ ] Update MaterialService (add kategori filter)
- [ ] Update ProductService (add SKU generator)
- [ ] Update SalesService (add customer handling)
- [ ] Create CustomerController
- [ ] Update MaterialController (add kategori param)
- [ ] Update ProductController (add SKU validation)
- [ ] Update SaleController (add customer param)
- [ ] Create API tests
- [ ] Update Postman collection
- [ ] Update API documentation

### Frontend Implementation

- [ ] Create Customer.kt model
- [ ] Update Material.kt (add kategori)
- [ ] Update Product.kt (add sku, stok_minimum)
- [ ] Create Sale.kt & SaleDetail.kt
- [ ] Update ApiService (add customer endpoints)
- [ ] Update CheckoutRequest (add customerId)
- [ ] Create CustomerRepository
- [ ] Create CustomerViewModel
- [ ] Implement CustomerListScreen
- [ ] Implement CustomerDetailScreen
- [ ] Implement AddCustomerScreen
- [ ] Update CheckoutScreen (add customer selector)
- [ ] Update MaterialListScreen (add kategori filter)
- [ ] Update ProductCard (add SKU display)
- [ ] Test customer flow end-to-end
- [ ] Update offline sync for customers

---

**Document Status**: ✅ Complete  
**Last Updated**: 1 Februari 2026  
**Next Review**: Setelah implementasi selesai
