<?php

use Illuminate\Support\Facades\Route;
use App\Models\User;
use App\Http\Controllers\UserController;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\MaterialController;
use App\Http\Controllers\ProductController;
use App\Http\Controllers\ProductCompositionController;
use App\Http\Controllers\ProductionController;
use App\Http\Controllers\SaleController;
use App\Http\Controllers\CustomerController;
use App\Http\Controllers\PreOrderController;
use App\Http\Controllers\IncomeController;
use App\Http\Controllers\ExpenseController;
use App\Http\Controllers\DebtController;
use App\Http\Controllers\ReceivableController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\ReportController;
use App\Http\Controllers\TransaksiController;

// Redirect root to login
Route::get('/', function () {
    return redirect()->route('login');
})->name('home');

// ========================================
// PUBLIC ROUTES (No Authentication)
// ========================================

// Authentication Routes
Route::get('/login', [AuthController::class, 'showLogin'])->name('login');
Route::post('/login', [AuthController::class, 'login'])->name('login.post');

// ========================================
// PROTECTED ROUTES (Admin Only)
// ========================================

Route::middleware(['admin'])->group(function () {

    // Logout
    Route::post('/logout', [AuthController::class, 'logout'])->name('logout');

    // Redirect /pembelian to pengeluaran (pembelian bahan is part of expenses)
    Route::get('/pembelian', function () {
        return redirect()->route('keuangan.pengeluaran.index');
    });

    // Dashboard
    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard');
    Route::get('/dashboard/stats', [DashboardController::class, 'getStats'])->name('dashboard.stats');

    // ========================================
    // MASTER DATA
    // ========================================

    // Bahan Baku Routes (Resource + Custom Routes)
    Route::prefix('bahan-baku')->name('bahan-baku.')->group(function () {
        Route::get('/', [MaterialController::class, 'index'])->name('index');
        Route::get('/create', [MaterialController::class, 'create'])->name('create');
        Route::post('/', [MaterialController::class, 'store'])->name('store');
        Route::get('/{id}', [MaterialController::class, 'show'])->name('show');
        Route::get('/{id}/edit', [MaterialController::class, 'edit'])->name('edit');
        Route::put('/{id}', [MaterialController::class, 'update'])->name('update');
        Route::delete('/{id}', [MaterialController::class, 'destroy'])->name('destroy');

        // Custom routes
        Route::get('/export/excel', [MaterialController::class, 'export'])->name('export');
        Route::post('/import/excel', [MaterialController::class, 'import'])->name('import');
    });

    // Produk Routes (Resource + Custom Routes)
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

    // Komposisi Produk Routes (Resource + Custom Routes)
    Route::prefix('komposisi-produk')->name('komposisi-produk.')->group(function () {
        Route::get('/', [ProductCompositionController::class, 'index'])->name('index');
        Route::get('/create', [ProductCompositionController::class, 'create'])->name('create');
        Route::post('/', [ProductCompositionController::class, 'store'])->name('store');
        Route::get('/{id}', [ProductCompositionController::class, 'show'])->name('show');
        Route::get('/{id}/edit', [ProductCompositionController::class, 'edit'])->name('edit');
        Route::put('/{id}', [ProductCompositionController::class, 'update'])->name('update');
        Route::delete('/{id}', [ProductCompositionController::class, 'destroy'])->name('destroy');

        // Custom routes
        Route::match(['GET', 'POST'], '/bulk-calculate', [ProductCompositionController::class, 'bulkCalculate'])->name('bulk-calculate');
        Route::get('/export/excel', [ProductCompositionController::class, 'export'])->name('export');
    });

    // ========================================
    // OPERATIONAL
    // ========================================

    // Produksi Routes (Resource + Custom Routes)
    Route::prefix('produksi')->name('produksi.')->group(function () {
        Route::get('/', [ProductionController::class, 'index'])->name('index');
        Route::get('/create', [ProductionController::class, 'create'])->name('create');
        Route::post('/', [ProductionController::class, 'store'])->name('store');
        Route::get('/{production}', [ProductionController::class, 'show'])->name('show');
        Route::get('/{production}/edit', [ProductionController::class, 'edit'])->name('edit');
        Route::put('/{production}', [ProductionController::class, 'update'])->name('update');
        Route::delete('/{production}', [ProductionController::class, 'destroy'])->name('destroy');

        // Custom routes
        Route::get('/export/excel', [ProductionController::class, 'export'])->name('export');
    });

    // Activity Logs Routes
    Route::get('/activity-logs', function () {
        return view('activity-logs.index', [
            'activities' => collect([
                (object)[
                    'id' => 1,
                    'user_name' => 'Admin System',
                    'action' => 'Login',
                    'description' => 'User berhasil login ke sistem',
                    'ip_address' => '127.0.0.1',
                    'created_at' => now()->subMinutes(5),
                ],
                (object)[
                    'id' => 2,
                    'user_name' => 'Admin System',
                    'action' => 'View Dashboard',
                    'description' => 'User mengakses halaman dashboard',
                    'ip_address' => '127.0.0.1',
                    'created_at' => now()->subMinutes(3),
                ],
                (object)[
                    'id' => 3,
                    'user_name' => 'Admin System',
                    'action' => 'Access Settings',
                    'description' => 'User mengakses halaman pengaturan',
                    'ip_address' => '127.0.0.1',
                    'created_at' => now()->subMinute(),
                ]
            ]),
            'totalLogs' => 3,
            'todayLogs' => 3,
            'uniqueUsers' => 1
        ]);
    })->name('activity-logs');

    // Settings Routes
    Route::get('/pengaturan', function () {
        return view('pengaturan.index', [
            'totalUsers' => 1,
            'activeUsers' => 1,
            'lastBackup' => now()->format('d/m/Y H:i'),
            'systemVersion' => '1.0.0'
        ]);
    })->name('pengaturan');

    // Penjualan Routes (Resource + Custom Routes)
    Route::prefix('penjualan')->name('penjualan.')->group(function () {
        Route::get('/', [SaleController::class, 'index'])->name('index');
        Route::get('/create', [SaleController::class, 'create'])->name('create');
        Route::post('/', [SaleController::class, 'store'])->name('store');
        Route::get('/{sale}', [SaleController::class, 'show'])->name('show');
        Route::get('/{sale}/edit', [SaleController::class, 'edit'])->name('edit');
        Route::put('/{sale}', [SaleController::class, 'update'])->name('update');
        Route::delete('/{sale}', [SaleController::class, 'destroy'])->name('destroy');

        // Custom routes
        Route::get('/export/excel', [SaleController::class, 'export'])->name('export');
    });

    // ========================================
    // RELASI & PELANGGAN
    // ========================================

    // Pelanggan Routes (Resource + Custom Routes)
    Route::prefix('pelanggan')->name('pelanggan.')->group(function () {
        Route::get('/', [CustomerController::class, 'index'])->name('index');
        Route::get('/create', [CustomerController::class, 'create'])->name('create');
        Route::post('/', [CustomerController::class, 'store'])->name('store');
        Route::get('/{pelanggan}', [CustomerController::class, 'show'])->name('show');
        Route::get('/{pelanggan}/edit', [CustomerController::class, 'edit'])->name('edit');
        Route::put('/{pelanggan}', [CustomerController::class, 'update'])->name('update');
        Route::delete('/{pelanggan}', [CustomerController::class, 'destroy'])->name('destroy');

        // Custom routes
        Route::post('/{pelanggan}/upgrade-vip', [CustomerController::class, 'upgradeVip'])->name('upgrade-vip');
        Route::get('/export/excel', [CustomerController::class, 'export'])->name('export');
    });

    // Pesanan/Pre-Order Routes (Resource + Custom Routes)
    Route::prefix('pesanan')->name('pesanan.')->group(function () {
        Route::get('/', [PreOrderController::class, 'index'])->name('index');
        Route::get('/create', [PreOrderController::class, 'create'])->name('create');
        Route::post('/', [PreOrderController::class, 'store'])->name('store');
        Route::get('/{pesanan}', [PreOrderController::class, 'show'])->name('show');
        Route::get('/{pesanan}/edit', [PreOrderController::class, 'edit'])->name('edit');
        Route::put('/{pesanan}', [PreOrderController::class, 'update'])->name('update');
        Route::delete('/{pesanan}', [PreOrderController::class, 'destroy'])->name('destroy');

        // Custom routes
        Route::post('/{pesanan}/update-status', [PreOrderController::class, 'updateStatus'])->name('update-status');
        Route::post('/{pesanan}/add-payment', [PreOrderController::class, 'addPayment'])->name('add-payment');
        Route::get('/export/excel', [PreOrderController::class, 'export'])->name('export');
    });

    // ========================================
    // FINANCE
    // ========================================

    // Transaksi Routes (Unified Finance Page)
    Route::prefix('transaksi')->name('transaksi.')->group(function () {
        Route::get('/', [TransaksiController::class, 'index'])->name('index');
        Route::get('/create', [TransaksiController::class, 'create'])->name('create');
        Route::post('/', [TransaksiController::class, 'store'])->name('store');
        Route::get('/{id}/edit', [TransaksiController::class, 'edit'])->name('edit')->where('id', '[0-9]+');
        Route::put('/{id}', [TransaksiController::class, 'update'])->name('update')->where('id', '[0-9]+');
        Route::delete('/{id}', [TransaksiController::class, 'destroy'])->name('destroy')->where('id', '[0-9]+');
        Route::get('/{id}', [TransaksiController::class, 'show'])->name('show')->where('id', '[0-9]+');
    });

    // ========================================
    // ANALYSIS & REPORTS
    // ========================================

    // Analisis Routes
    Route::prefix('analisis')->name('analisis.')->group(function () {
        Route::get('/', [App\Http\Controllers\AnalysisController::class, 'index'])->name('index');
        Route::get('/bep', [App\Http\Controllers\AnalysisController::class, 'bep'])->name('bep');
        Route::get('/hpp', [App\Http\Controllers\AnalysisController::class, 'hpp'])->name('hpp');
    });

    // Laporan Routes
    Route::prefix('laporan')->name('laporan.')->group(function () {
        Route::get('/', [ReportController::class, 'index'])->name('index');
    });

    // ========================================
    // USER MANAGEMENT
    // ========================================

    // User Management Routes
    Route::prefix('user-management')->name('user-management.')->group(function () {
        Route::get('/', function () {
            // Calculate user statistics by role
            $stats = [
                'total' => \App\Models\User::count(),
                'admin' => \App\Models\User::where('role', 'admin')->count(),
                'manager' => \App\Models\User::where('role', 'manager')->count(),
                'staff' => \App\Models\User::where('role', 'staff')->count(),
            ];

            // Fetch users with pagination
            $users = \App\Models\User::orderBy('created_at', 'desc')->paginate(15);

            return view('user-management.index', compact('stats', 'users'));
        })->name('index');

        Route::get('/create', function () {
            return view('user-management.create');
        })->name('create');

        Route::get('/{id}', function () {
            return view('user-management.show');
        })->name('show');

        Route::get('/{id}/edit', function () {
            return view('user-management.edit');
        })->name('edit');
    });
}); // Keuangan Routes
Route::prefix('keuangan')->name('keuangan.')->group(function () {
    Route::get('/pemasukan', function () {
        return view('keuangan.pemasukan');
    })->name('pemasukan');

    Route::get('/pengeluaran', function () {
        return view('keuangan.pengeluaran');
    })->name('pengeluaran');

    Route::get('/hutang', function () {
        return view('keuangan.hutang');
    })->name('hutang');

    Route::get('/piutang', function () {
        return view('keuangan.piutang');
    })->name('piutang');

    Route::get('/laporan', function () {
        return view('keuangan.laporan');
    })->name('laporan');
});

// End of routes
