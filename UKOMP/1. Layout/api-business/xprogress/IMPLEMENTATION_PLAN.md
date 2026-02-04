# 📋 IMPLEMENTATION PLAN - SI-UMKM Backend

## 🎯 Summary dari Analisis

### Total Screens: 52

Berdasarkan [08-FEATURES.md](api-business/docs%20backend/08-FEATURES.md), sistem memiliki 52 screens yang terbagi dalam 10 kategori:

### 1. **Dashboard** (1 screen)

- `/dashboard` - Main dashboard dengan widgets, charts, dan stats

### 2. **Master Data** (10 screens)

- `/bahan-baku` - Index, Create, Edit, Show
- `/produk` - Index, Create, Edit, Show
- `/komposisi-produk` - Index, Breakdown

### 3. **Operational** (12 screens)

- `/produksi` - Index, Create, Show
- `/penjualan` - Index, Create, Show
- `/pre-orders` - Index, Create, Show, Calendar View

### 4. **Financial** (7 screens)

- `/transactions` - Index (unified: income, expense, debt, receivable)
- `/transactions/create` - Create transaction modal
- `/transactions/{id}` - Detail transaction

### 5. **Analysis** (4 screens)

- `/analisis/bep` - BEP Analysis
- `/analisis/hpp` - HPP Analysis & Simulation

### 6. **Reports** (4 screens)

- `/laporan/penjualan` - Sales Report
- `/laporan/produksi` - Production Report
- `/laporan/keuangan` - Financial Report
- `/laporan/stok` - Stock Report

### 7. **User Management** (4 screens)

- `/user-management` - Index, Create, Edit, Show

### 8. **Customer Management** (4 screens)

- `/customers` - Index, Create, Edit, Show

### 9. **Settings** (4 screens)

- `/settings/profil` - Company Profile
- `/settings/notifikasi` - Notification Settings
- `/settings/backup` - Backup & Restore
- `/settings/sistem` - System Settings

### 10. **Activity Log** (2 screens)

- `/activity-logs` - Index
- `/activity-logs/{id}` - Detail Modal

---

## 📊 Status Existing Implementation

### ✅ Yang Sudah Ada:

- ✅ Routes dasar (20% complete)
- ✅ Models: Material, Product, ProductComposition, Production, Sale, User, Customer, dll
- ✅ Views: Basic struktur untuk bahan-baku, produk, keuangan, analisis, laporan, user-management
- ✅ Layouts: master.blade.php, sidebar.blade.php, navbar.blade.php, footer.blade.php
- ✅ AuthController dengan login/logout
- ✅ MaterialController (empty methods)

### ❌ Yang Belum Ada / Belum Lengkap:

- ❌ Controllers kosong (hanya MaterialController yang ada, tapi method kosong)
- ❌ Routes masih banyak yang menggunakan closure, bukan controller
- ❌ Sidebar belum lengkap (tidak ada menu Customer, Settings, Activity Log)
- ❌ Models belum lengkap relationships-nya
- ❌ Views masih kosong (belum ada data dari database)
- ❌ Tidak ada middleware untuk role/permission
- ❌ Pre-Order management belum ada
- ❌ Transaction management belum ada (unified finance)
- ❌ Customer management belum ada
- ❌ Settings belum ada
- ❌ Activity Log belum ada

---

## 🚀 Implementation Steps

### STEP 1: Models & Relationships ✅

**File**: Semua models di `app/Models/`

**Actions**:

1. ✅ Material.php - Add relationships
2. ✅ Product.php - Add relationships
3. ✅ ProductComposition.php - Create new
4. ✅ Production.php - Create/update
5. ✅ ProductionDetail.php - Create/update
6. ✅ Sale.php - Update relationships
7. ✅ SaleDetail.php - Update relationships
8. ✅ Order.php - Create (for pre-orders)
9. ✅ OrderDetail.php - Create
10. ✅ Transaction.php - Create (unified finance)
11. ✅ Customer.php - Update
12. ✅ User.php - Update
13. ✅ ActivityLog.php - Create

### STEP 2: Controllers 🔨

**Folder**: `app/Http/Controllers/`

**Create Controllers**:

1. DashboardController - Stats, charts, widgets
2. MaterialController - Complete CRUD
3. ProductController - Complete CRUD + HPP calculation
4. ProductCompositionController - CRUD + Bulk Calculate
5. ProductionController - CRUD + Material validation
6. SaleController - CRUD + Stock validation
7. OrderController - Pre-order management
8. TransactionController - Unified finance
9. AnalysisController - BEP & HPP
10. ReportController - All reports
11. CustomerController - CRUD
12. UserController - CRUD + Role management
13. SettingController - Settings management
14. ActivityLogController - Logs

### STEP 3: Routes 🛣️

**File**: `routes/web.php`

**Actions**:

- Replace all closures with controller methods
- Add resource routes untuk semua CRUD
- Add custom routes untuk special features (bulk calculate, export, import, etc)
- Group routes by module dengan middleware

### STEP 4: Sidebar 📋

**File**: `resources/views/layouts/sidebar.blade.php`

**Add Missing Menus**:

1. Customer Management
2. Settings (with submenu)
3. Activity Log
4. Pre-Orders (under Operational)

### STEP 5: Views 🎨

**Folder**: `resources/views/`

**Create/Update Views**:

- All index pages dengan DataTables
- All create/edit forms
- All show/detail pages
- Dashboard dengan real data
- Reports dengan charts
- Settings pages

### STEP 6: Middleware & Permissions 🔐

**Create**:

- RoleMiddleware
- PermissionMiddleware
- Integrate with routes & sidebar

### STEP 7: Testing & Debugging 🐛

- Test all routes
- Verify data display
- Fix errors
- Test filters & search
- Test pagination

---

## 📝 Detail Action Plan

### Priority 1: Core CRUD (Master Data & Operational)

1. MaterialController + Views
2. ProductController + Views
3. ProductCompositionController + Views
4. ProductionController + Views
5. SaleController + Views

### Priority 2: Financial & Customer

6. CustomerController + Views
7. TransactionController + Views (unified finance)
8. OrderController + Views (pre-orders)

### Priority 3: Analysis & Reports

9. DashboardController (dengan real stats)
10. AnalysisController (BEP & HPP)
11. ReportController (all reports)

### Priority 4: User Management & Settings

12. UserController + Views
13. SettingController + Views
14. ActivityLogController + Views

---

## 🎯 Expected Result

Setelah implementasi selesai:

- ✅ Semua 52 screens bisa diakses
- ✅ Sidebar lengkap dengan 9 kategori menu
- ✅ Routes tersambung ke controller
- ✅ Data muncul dari database (bukan dummy)
- ✅ Filter, search, pagination berjalan
- ✅ Role & permission terimplementasi
- ✅ Tidak ada error 404/403/500
- ✅ Backend siap dihubungkan ke mobile app

---

## 📅 Timeline Estimate

**Total: 52 screens**

- Models & Relationships: 2-3 jam
- Controllers (14 controllers): 4-5 jam
- Routes: 1 jam
- Sidebar: 30 menit
- Views (52 screens): 8-10 jam
- Middleware & Permission: 1-2 jam
- Testing & Debugging: 2-3 jam

**Total Estimate: 18-24 jam** (untuk implementasi penuh)

---

Mari kita mulai implementasi! 🚀
