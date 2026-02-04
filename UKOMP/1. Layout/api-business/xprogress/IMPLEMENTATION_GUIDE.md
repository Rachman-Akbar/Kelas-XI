# 🚀 PANDUAN IMPLEMENTASI BACKEND SI-UMKM

## ✅ Apa Yang Sudah Selesai

### 1. Models (100% Complete)

- ✅ Material.php - Lengkap dengan relationships, scopes, methods
- ✅ Product.php - Lengkap dengan HPP calculation, profitability
- ✅ ProductComposition.php - Sudah ada
- ✅ Production.php - Sudah ada
- ✅ ProductionDetail.php - Sudah ada
- ✅ ActivityLog.php - Sudah ada
- ✅ User.php, Customer.php, Order.php, Transaction.php - Sudah ada

### 2. Controllers

- ✅ MaterialController - **PRODUCTION READY**
  - ✅ index() - dengan filter, search, pagination, stats
  - ✅ create() - form create
  - ✅ store() - validasi + activity log
  - ✅ show() - detail + usage statistics
  - ✅ edit() - form edit
  - ✅ update() - validasi + activity log
  - ✅ destroy() - soft delete dengan validation
  - ✅ export() - export to CSV
  - ✅ import() - placeholder untuk Excel import

### 3. Routes

- ✅ Material routes - Complete resource routes + custom (export/import)
- ✅ Route structure sudah terorganisir dengan grouping

### 4. Views (Material)

- ⚠️ Perlu update dengan data dari controller

---

## 🎯 Yang Perlu Dilanjutkan

### PRIORITY 1: Core CRUD Controllers (Master Data & Operational)

#### 1. ProductController

**File**: `app/Http/Controllers/ProductController.php`

**Methods yang perlu dibuat**:

```php
public function index(Request $request)
{
    // Filter by kategori, status, stock_level
    // Search by nama, sku
    // Pagination
    // Stats: total, low_stock, total_value
}

public function create()
{
    // Get categories
    // Return view produk.create
}

public function store(Request $request)
{
    // Validate
    // Create product
    // Log activity
    // Return redirect with success message
}

public function show($id)
{
    // Load product with compositions
    // Get stats: sales count, revenue, margin
    // Get sales trend (6 months)
    // Return view produk.show
}

public function edit($id)
{
    // Get product
    // Get categories
    // Return view produk.edit
}

public function update(Request $request, $id)
{
    // Validate
    // Update product
    // Log activity
    // Return redirect
}

public function destroy($id)
{
    // Check if product is used in sales/orders
    // Soft delete
    // Log activity
    // Return JSON response
}

public function calculateHPP($id)
{
    // Calculate HPP from compositions
    // Update product
    // Return JSON with new HPP
}

public function export(Request $request)
{
    // Export products to CSV
}
```

**Validation Rules**:

```php
$validated = $request->validate([
    'nama' => 'required|string|max:255',
    'sku' => 'nullable|string|max:100|unique:products,sku,' . $id,
    'kategori' => 'required|string|max:50',
    'harga_jual' => 'required|numeric|min:0',
    'hpp' => 'nullable|numeric|min:0',
    'stok' => 'required|integer|min:0',
    'stok_minimum' => 'required|integer|min:0',
    'satuan' => 'required|string|max:50',
    'deskripsi' => 'nullable|string',
    'status' => 'required|in:aktif,nonaktif',
]);
```

**Update Routes** (`routes/web.php`):

```php
// Produk Routes
Route::prefix('produk')->name('produk.')->group(function () {
    Route::get('/', [ProductController::class, 'index'])->name('index');
    Route::get('/create', [ProductController::class, 'create'])->name('create');
    Route::post('/', [ProductController::class, 'store'])->name('store');
    Route::get('/{id}', [ProductController::class, 'show'])->name('show');
    Route::get('/{id}/edit', [ProductController::class, 'edit'])->name('edit');
    Route::put('/{id}', [ProductController::class, 'update'])->name('update');
    Route::delete('/{id}', [ProductController::class, 'destroy'])->name('destroy');

    // Custom routes
    Route::post('/{id}/calculate-hpp', [ProductController::class, 'calculateHPP'])->name('calculate-hpp');
    Route::get('/export/excel', [ProductController::class, 'export'])->name('export');
});
```

---

#### 2. ProductCompositionController

**File**: `app/Http/Controllers/ProductCompositionController.php`

**Methods**:

```php
public function index(Request $request)
{
    // Load products with compositions and materials
    // Filter by product
    // Return view komposisi-produk.index
}

public function store(Request $request)
{
    // Add material to product composition
    // Recalculate HPP
    // Log activity
}

public function update(Request $request, $id)
{
    // Update composition quantity
    // Recalculate HPP
}

public function destroy($id)
{
    // Remove material from composition
    // Recalculate HPP
}

public function breakdown($productId)
{
    // Get detailed cost breakdown
    // Pie chart data
    // Return view komposisi-produk.breakdown
}

public function bulkCalculate(Request $request)
{
    // Planning produksi massal
    // Calculate material requirements for multiple products
    // Return JSON with aggregated materials needed
}
```

**Key Features**:

- Bulk Calculate Modal: Hitung kebutuhan bahan untuk produksi multi-produk
- Real-time HPP recalculation
- Material availability check

---

#### 3. ProductionController

**File**: `app/Http/Controllers/ProductionController.php`

**Methods**:

```php
public function index(Request $request)
{
    // Filter by date range, product, status
    // Search
    // Load with details, user, products
    // Stats: total production, total cost
}

public function create()
{
    // Get all active products
    // Generate nomor produksi
    // Return view produksi.create
}

public function store(Request $request)
{
    // Validate materials availability
    // Create production + details
    // Reduce material stock
    // Add product stock
    // Log activity
}

public function show($id)
{
    // Load production with details, products, materials
    // Get cost breakdown
    // Get timeline
}

public function validateMaterials(Request $request)
{
    // AJAX endpoint
    // Check if materials available for production
    // Return JSON with availability status
}

public function cancel($id)
{
    // Reverse stock changes
    // Update status to cancelled
    // Log activity
}
```

**Key Logic**:

```php
// In store method
DB::beginTransaction();

$production = Production::create([...]);

foreach ($request->items as $item) {
    // Create production detail
    ProductionDetail::create([...]);

    // Get product compositions
    $compositions = Product::find($item['product_id'])->compositions;

    foreach ($compositions as $comp) {
        // Reduce material stock
        $requiredQty = $comp->jumlah * $item['quantity'];
        $comp->material->reduceStock($requiredQty);
    }

    // Add product stock
    Product::find($item['product_id'])->addStock($item['quantity']);
}

DB::commit();
```

---

#### 4. SaleController

**File**: `app/Http/Controllers/SaleController.php`

**Methods**:

```php
public function index(Request $request)
{
    // Filter: status, date range, payment_method, customer
    // Search by invoice number, customer
    // Stats: total sales, revenue
}

public function create()
{
    // Get active products
    // Get customers
    // Generate invoice number
}

public function store(Request $request)
{
    // Validate stock availability
    // Create sale + sale details
    // Reduce product stock
    // Create transaction (income)
    // Log activity
}

public function show($id)
{
    // Load sale with details, customer
    // Calculate profit
    // Get payment info
}

public function updatePaymentStatus($id)
{
    // Mark as paid/pending
    // Update transaction
}

public function printInvoice($id)
{
    // Generate printable invoice
    // Return PDF view
}
```

---

### PRIORITY 2: Financial Management

#### 5. TransactionController

**File**: `app/Http/Controllers/TransactionController.php`

**Konsep**: Unified finance system (income, expense, debt, receivable dalam 1 table)

**Methods**:

```php
public function index(Request $request)
{
    // Filter by type, category, status, date range, payment method
    // Stats per type
    // Cash flow summary
}

public function store(Request $request)
{
    // Dynamic validation based on type
    // Create transaction
    // Handle file upload (bukti)
}

public function show($id)
{
    // Transaction detail
    // Payment history (for debt/receivable)
    // Attachment display
}

public function recordPayment(Request $request, $id)
{
    // Record payment for debt/receivable
    // Update paid_amount
    // Update status if fully paid
}
```

**Transaction Types**:

- `income`: Pemasukan (otomatis dari penjualan + manual)
- `expense`: Pengeluaran (bahan baku, gaji, operasional, dll)
- `debt`: Hutang (supplier, bank, dll)
- `receivable`: Piutang (customer, dll)

---

#### 6. CustomerController

**File**: `app/Http/Controllers/CustomerController.php`

**Methods**:

```php
public function index(Request $request)
{
    // Filter by segment, status, total purchases, last purchase
    // Search by name, phone, email
    // Stats: total customers, segments distribution
}

public function create()
{
    // Return view customers.create
}

public function store(Request $request)
{
    // Validate
    // Create customer
}

public function show($id)
{
    // Customer profile
    // Purchase statistics
    // Purchase history with pagination
    // Favorite products
    // Purchase trend chart data
    // Activity timeline
}

public function update(Request $request, $id)
{
    // Update customer
}

public function destroy($id)
{
    // Check if customer has orders
    // Soft delete
}
```

**Customer Segmentation**:

```php
public function getSegmentAttribute()
{
    $totalPurchase = $this->orders()->sum('total');
    $totalOrders = $this->orders()->count();

    if ($totalPurchase > 10000000 || $totalOrders > 50) {
        return 'VIP';
    } elseif ($totalPurchase > 1000000) {
        return 'Regular';
    }
    return 'New';
}
```

---

### PRIORITY 3: Analysis & Reports

#### 7. DashboardController

**File**: `app/Http/Controllers/DashboardController.php`

**Method**: `dashboard()`

**Data yang perlu ditampilkan**:

```php
public function dashboard()
{
    // Widget Stats
    $stats = [
        'produksi_bulan_ini' => Production::whereMonth('tanggal_produksi', now()->month)->sum('total_biaya'),
        'penjualan_bulan_ini' => Sale::whereMonth('tanggal_penjualan', now()->month)->count(),
        'pemasukan_bulan_ini' => Transaction::where('type', 'income')->whereMonth('tanggal', now()->month)->sum('jumlah'),
        'pengeluaran_bulan_ini' => Transaction::where('type', 'expense')->whereMonth('tanggal', now()->month)->sum('jumlah'),
        'stok_menipis' => Material::lowStock()->count(),
        'hutang_belum_lunas' => Transaction::where('type', 'debt')->where('status', 'pending')->sum('jumlah'),
    ];

    // Chart: Penjualan & Produksi Bulanan (12 bulan terakhir)
    $salesChart = [];
    $productionChart = [];
    for ($i = 11; $i >= 0; $i--) {
        $date = now()->subMonths($i);
        $salesChart[] = [
            'month' => $date->format('M Y'),
            'value' => Sale::whereYear('tanggal_penjualan', $date->year)
                          ->whereMonth('tanggal_penjualan', $date->month)
                          ->count()
        ];
        $productionChart[] = [
            'month' => $date->format('M Y'),
            'value' => ProductionDetail::whereHas('production', function($q) use ($date) {
                $q->whereYear('tanggal_produksi', $date->year)
                  ->whereMonth('tanggal_produksi', $date->month);
            })->sum('jumlah')
        ];
    }

    // Pie Chart: Kategori Produk Terlaris
    $topCategories = SaleDetail::join('products', 'sale_details.product_id', '=', 'products.id')
        ->selectRaw('products.kategori, SUM(sale_details.jumlah) as total')
        ->groupBy('products.kategori')
        ->get();

    // Recent Activities (10 terakhir)
    $recentActivities = ActivityLog::with('user')->latest()->take(10)->get();

    // Stock Alerts
    $stockAlerts = Material::lowStock()->get();

    return view('dashboard', compact('stats', 'salesChart', 'productionChart', 'topCategories', 'recentActivities', 'stockAlerts'));
}
```

---

#### 8. AnalysisController

**File**: `app/Http/Controllers/AnalysisController.php`

**Methods**:

```php
public function bep(Request $request)
{
    // BEP Analysis
    $periode = $request->input('periode', now()->format('Y-m'));

    // Fixed Cost: Gaji, Sewa, dll (dari expenses dengan kategori tertentu)
    $fixedCost = Transaction::where('type', 'expense')
        ->whereIn('kategori', ['Gaji Karyawan', 'Sewa', 'Utilitas'])
        ->whereMonth('tanggal', '=', date('m', strtotime($periode)))
        ->sum('jumlah');

    // Variable Cost: Bahan baku (dari production costs)
    $variableCost = Production::whereMonth('tanggal_produksi', '=', date('m', strtotime($periode)))
        ->sum('total_biaya');

    // Total Sales
    $totalSales = Sale::whereMonth('tanggal_penjualan', '=', date('m', strtotime($periode)))
        ->sum('total');

    // BEP Calculation
    $bepRupiah = ($fixedCost / (1 - ($variableCost / $totalSales)));
    $bepUnit = $bepRupiah / (SaleDetail::avg('harga_satuan'));

    $marginOfSafety = (($totalSales - $bepRupiah) / $totalSales) * 100;

    return view('analisis.bep', compact('fixedCost', 'variableCost', 'totalSales', 'bepRupiah', 'bepUnit', 'marginOfSafety'));
}

public function hpp(Request $request)
{
    // HPP Analysis
    $productId = $request->input('product_id');

    if ($productId) {
        $product = Product::with('compositions.material')->find($productId);

        // HPP Breakdown
        $breakdown = $product->compositions->map(function($comp) {
            return [
                'material' => $comp->material->nama,
                'quantity' => $comp->jumlah,
                'unit' => $comp->material->satuan,
                'price' => $comp->material->harga_per_satuan,
                'subtotal' => $comp->subtotal,
            ];
        });

        $hppTotal = $breakdown->sum('subtotal');
        $margin = $product->profit_margin;
        $markup = $product->markup_percentage;
        $profit = $product->profit_per_unit;

        return view('analisis.hpp', compact('product', 'breakdown', 'hppTotal', 'margin', 'markup', 'profit'));
    }

    // List all products for selection
    $products = Product::where('status', 'aktif')->get();
    return view('analisis.hpp', compact('products'));
}

public function simulatePrice(Request $request)
{
    // AJAX: Simulasi perubahan harga jual
    $productId = $request->product_id;
    $newPrice = $request->new_price;

    $product = Product::find($productId);
    $hpp = $product->hpp;

    $newMargin = (($newPrice - $hpp) / $newPrice) * 100;
    $newMarkup = (($newPrice - $hpp) / $hpp) * 100;
    $newProfit = $newPrice - $hpp;

    // Estimasi profit per bulan
    $avgMonthlySales = SaleDetail::where('product_id', $productId)
        ->whereMonth('created_at', now()->month)
        ->sum('jumlah');
    $estimatedMonthlyProfit = $avgMonthlySales * $newProfit;

    return response()->json([
        'margin' => $newMargin,
        'markup' => $newMarkup,
        'profit_per_unit' => $newProfit,
        'estimated_monthly_profit' => $estimatedMonthlyProfit,
    ]);
}
```

---

#### 9. ReportController

**File**: `app/Http/Controllers/ReportController.php`

**Methods**:

```php
public function penjualan(Request $request)
{
    // Laporan Penjualan
    $startDate = $request->input('start_date', now()->startOfMonth());
    $endDate = $request->input('end_date', now()->endOfMonth());

    $sales = Sale::with(['details.product', 'customer'])
        ->whereBetween('tanggal_penjualan', [$startDate, $endDate])
        ->get();

    $stats = [
        'total_sales' => $sales->count(),
        'total_revenue' => $sales->sum('total'),
        'avg_transaction' => $sales->avg('total'),
    ];

    // Top Products
    $topProducts = SaleDetail::whereBetween('created_at', [$startDate, $endDate])
        ->select('product_id', DB::raw('SUM(jumlah) as total_sold'))
        ->groupBy('product_id')
        ->orderBy('total_sold', 'desc')
        ->take(10)
        ->with('product')
        ->get();

    return view('laporan.penjualan', compact('sales', 'stats', 'topProducts', 'startDate', 'endDate'));
}

public function produksi(Request $request)
{
    // Laporan Produksi
    $startDate = $request->input('start_date', now()->startOfMonth());
    $endDate = $request->input('end_date', now()->endOfMonth());

    $productions = Production::with(['details.product', 'user'])
        ->whereBetween('tanggal_produksi', [$startDate, $endDate])
        ->get();

    $stats = [
        'total_production' => $productions->count(),
        'total_cost' => $productions->sum('total_biaya'),
        'total_units' => $productions->sum(function($p) {
            return $p->details->sum('jumlah');
        }),
    ];

    return view('laporan.produksi', compact('productions', 'stats', 'startDate', 'endDate'));
}

public function keuangan(Request $request)
{
    // Laporan Keuangan (Cash Flow)
    $periode = $request->input('periode', now()->format('Y-m'));

    $pemasukan = Transaction::where('type', 'income')
        ->whereMonth('tanggal', '=', date('m', strtotime($periode)))
        ->get();

    $pengeluaran = Transaction::where('type', 'expense')
        ->whereMonth('tanggal', '=', date('m', strtotime($periode)))
        ->get();

    $totalPemasukan = $pemasukan->sum('jumlah');
    $totalPengeluaran = $pengeluaran->sum('jumlah');
    $saldo = $totalPemasukan - $totalPengeluaran;

    // Group by category
    $pemasukanByCategory = $pemasukan->groupBy('kategori');
    $pengeluaranByCategory = $pengeluaran->groupBy('kategori');

    return view('laporan.keuangan', compact('totalPemasukan', 'totalPengeluaran', 'saldo', 'pemasukanByCategory', 'pengeluaranByCategory'));
}

public function stok(Request $request)
{
    // Laporan Stok Bahan Baku
    $materials = Material::where('status', 'aktif')->get();

    $stats = [
        'total_materials' => $materials->count(),
        'safe_stock' => $materials->filter(fn($m) => $m->stock_status == 'aman')->count(),
        'low_stock' => $materials->filter(fn($m) => $m->stock_status == 'menipis')->count(),
        'critical_stock' => $materials->filter(fn($m) => $m->stock_status == 'kritis')->count(),
        'total_value' => $materials->sum('total_value'),
    ];

    // Materials that need reorder
    $needReorder = $materials->filter(fn($m) => $m->isLowStock());

    return view('laporan.stok', compact('materials', 'stats', 'needReorder'));
}

public function exportPDF($type, Request $request)
{
    // Export laporan ke PDF
    // TODO: Implement dengan package dompdf atau mpdf
}
```

---

### PRIORITY 4: Settings & User Management

#### 10. UserController

**File**: `app/Http/Controllers/UserController.php`

**Methods**: Standard CRUD + role management

```php
public function index(Request $request)
{
    // Filter by role, status
    // Search by name, email
}

public function store(Request $request)
{
    // Hash password
    // Assign role
}

public function resetPassword($id)
{
    // Reset user password
    // Send email
}

public function toggleStatus($id)
{
    // Activate/Deactivate user
}
```

---

#### 11. SettingController

**File**: `app/Http/Controllers/SettingController.php`

**Methods**:

```php
public function index()
{
    // Show settings tabs
    $settings = Setting::first() ?? new Setting();
    return view('settings.index', compact('settings'));
}

public function updateProfile(Request $request)
{
    // Update company profile
    // Upload logo
}

public function updateNotification(Request $request)
{
    // Update notification settings
}

public function backup()
{
    // Create database backup
    // Download .sql file
}

public function restore(Request $request)
{
    // Upload and restore from .sql file
}
```

---

#### 12. ActivityLogController

**File**: `app/Http/Controllers/ActivityLogController.php`

```php
public function index(Request $request)
{
    // Filter by user, action, module, date range
    // Search
    $logs = ActivityLog::with('user')
        ->byUser($request->user_id)
        ->byAction($request->action)
        ->byModule($request->module)
        ->dateRange($request->start_date, $request->end_date)
        ->search($request->search)
        ->latest()
        ->paginate(50);

    return view('activity-logs.index', compact('logs'));
}

public function show($id)
{
    // Show detail in modal
    $log = ActivityLog::with('user')->findOrFail($id);
    return response()->json($log);
}

public function clearOldLogs()
{
    // Delete logs older than 6 months
    ActivityLog::where('created_at', '<', now()->subMonths(6))->delete();

    return redirect()->back()->with('success', 'Log lama berhasil dihapus');
}
```

---

## 📋 Checklist Implementasi

### Controllers (14 total)

- [x] MaterialController ✅
- [ ] ProductController
- [ ] ProductCompositionController
- [ ] ProductionController
- [ ] SaleController
- [ ] OrderController (Pre-orders)
- [ ] TransactionController (Unified finance)
- [ ] CustomerController
- [ ] DashboardController
- [ ] AnalysisController
- [ ] ReportController
- [ ] UserController
- [ ] SettingController
- [ ] ActivityLogController

### Routes

- [x] Material routes ✅
- [ ] Update remaining routes dari closure ke controller methods

### Views (52 screens total)

- [x] Material index (perlu update dengan real data)
- [ ] Material create, edit, show
- [ ] Product index, create, edit, show
- [ ] Komposisi index, breakdown
- [ ] Production index, create, show
- [ ] Sale index, create, show
- [ ] Transaction index, create, show
- [ ] Customer index, create, edit, show
- [ ] Dashboard dengan widgets & charts
- [ ] Analysis BEP, HPP
- [ ] Reports 4x
- [ ] User Management 4x
- [ ] Settings 4x
- [ ] Activity Log 2x

### Sidebar

- [x] Basic structure ✅
- [ ] Tambah Customer Management menu
- [ ] Tambah Settings menu dengan submenu
- [ ] Tambah Activity Log menu
- [ ] Tambah Pre-Orders menu

---

## 🛠️ Tools & Packages yang Direkomendasikan

### 1. Laravel Excel (untuk Export/Import)

```bash
composer require maatwebsite/excel
```

### 2. Laravel PDF (untuk Generate PDF)

```bash
composer require barryvdh/laravel-dompdf
```

### 3. Chart.js (untuk Charts di Dashboard)

Sudah include via CDN di layout

### 4. DataTables (untuk Advanced Tables)

```html
<!-- Sudah bisa pakai via CDN -->
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css" />
<script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>
```

---

## 🎨 Template View Blade

### Contoh Index Page dengan DataTable:

```blade
@extends('layouts.master')

@section('title', 'Nama Module')

@section('content')
<div class="page-header">
    <div class="row align-items-center">
        <div class="col">
            <h1><i class="fas fa-icon me-2"></i>Nama Module</h1>
        </div>
        <div class="col-auto">
            <div class="btn-group">
                <a href="{{ route('module.export') }}" class="btn btn-info">
                    <i class="fas fa-file-excel me-2"></i>Export
                </a>
                <a href="{{ route('module.create') }}" class="btn btn-primary">
                    <i class="fas fa-plus me-2"></i>Tambah
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Stats Cards -->
<div class="row mb-4">
    <div class="col-md-3">
        <div class="card">
            <div class="card-body">
                <h6 class="text-muted mb-2">Total</h6>
                <h2>{{ $stats['total'] }}</h2>
            </div>
        </div>
    </div>
    <!-- More stat cards -->
</div>

<!-- Main Card -->
<div class="card">
    <div class="card-header">
        <h5><i class="fas fa-list me-2"></i>Daftar Data</h5>
    </div>
    <div class="card-body">
        <table id="dataTable" class="table table-hover">
            <thead>
                <tr>
                    <th>Column 1</th>
                    <th>Column 2</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                @foreach($items as $item)
                <tr>
                    <td>{{ $item->field }}</td>
                    <td>{{ $item->field2 }}</td>
                    <td>
                        <a href="{{ route('module.show', $item->id) }}" class="btn btn-sm btn-info">
                            <i class="fas fa-eye"></i>
                        </a>
                        <a href="{{ route('module.edit', $item->id) }}" class="btn btn-sm btn-warning">
                            <i class="fas fa-edit"></i>
                        </a>
                        <button class="btn btn-sm btn-danger btn-delete" data-id="{{ $item->id }}">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
                @endforeach
            </tbody>
        </table>
    </div>
</div>

@push('scripts')
<script>
$(document).ready(function() {
    $('#dataTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/id.json'
        }
    });

    $('.btn-delete').click(function() {
        if (confirm('Yakin ingin menghapus?')) {
            // AJAX delete
        }
    });
});
</script>
@endpush
@endsection
```

---

## ⚡ Quick Commands

```bash
# Create controller
php artisan make:controller ProductController --resource

# Create model
php artisan make:model Product -m

# Create migration
php artisan make:migration create_products_table

# Run migrations
php artisan migrate

# Clear cache
php artisan cache:clear
php artisan config:clear
php artisan route:clear
php artisan view:clear
```

---

## 🔥 Next Steps

1. **Buat ProductController** mengikuti pattern MaterialController
2. **Update routes** untuk ProductController
3. **Buat views** untuk Product (index, create, edit, show)
4. **Test** di browser, pastikan data muncul
5. **Ulangi** untuk controller lainnya

**Estimasi**: 1-2 hari untuk menyelesaikan semua controller + views dasar

---

**NOTE**: MaterialController sudah production-ready, tinggal copy pattern-nya untuk controller lainnya! 🚀
