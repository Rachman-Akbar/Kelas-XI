# 🔧 BACKEND DEVELOPMENT CHECKLIST

**SI-UMKM Laravel Backend - Implementation Guide**

**Last Updated**: 1 Februari 2026  
**Status**: Ready to Implement

---

## 📋 WEEK 1: DATABASE & MIGRATIONS

### Day 1-2: Create Migrations

#### ✅ Basic Tables

- [ ] `create_materials_table` (dengan field `kategori`)
- [ ] `create_products_table` (dengan `sku` dan `stok_minimum`)
- [ ] `create_product_compositions_table`
- [ ] `create_customers_table` ⭐ NEW
- [ ] `create_users_table` (modify default)

#### ✅ Transaction Tables

- [ ] `create_productions_table`
- [ ] `create_production_details_table`
- [ ] `create_sales_table` (dengan `customer_id` FK)
- [ ] `create_sale_details_table`

#### ✅ Finance Tables

- [ ] `create_incomes_table`
- [ ] `create_expenses_table`
- [ ] `create_debts_table`
- [ ] `create_receivables_table`

#### ✅ System Tables

- [ ] `create_activity_logs_table`

**Migration Commands**:

```bash
# Create all migrations
php artisan make:migration create_materials_table
php artisan make:migration create_products_table
php artisan make:migration create_product_compositions_table
php artisan make:migration create_customers_table
php artisan make:migration create_productions_table
php artisan make:migration create_production_details_table
php artisan make:migration create_sales_table
php artisan make:migration create_sale_details_table
php artisan make:migration create_incomes_table
php artisan make:migration create_expenses_table
php artisan make:migration create_debts_table
php artisan make:migration create_receivables_table
php artisan make:migration create_activity_logs_table
php artisan make:migration modify_users_table

# Run migrations
php artisan migrate
```

### Day 3-4: Create Seeders

#### ✅ Master Data Seeders

- [ ] `UserSeeder` (Admin, Manager, Staff, Kasir, Keuangan, Produksi)
- [ ] `MaterialSeeder` (15 sample bahan baku dengan kategori)
- [ ] `ProductSeeder` (10 sample produk dengan SKU dan stok_minimum)
- [ ] `ProductCompositionSeeder`
- [ ] `CustomerSeeder` (10 sample customers) ⭐ NEW

#### ✅ Transaction Seeders

- [ ] `ProductionSeeder` (sample produksi data)
- [ ] `SaleSeeder` (sample penjualan dengan customer relations) ⭐ UPDATED

**Seeder Commands**:

```bash
php artisan make:seeder UserSeeder
php artisan make:seeder MaterialSeeder
php artisan make:seeder ProductSeeder
php artisan make:seeder ProductCompositionSeeder
php artisan make:seeder CustomerSeeder
php artisan make:seeder ProductionSeeder
php artisan make:seeder SaleSeeder

# Update DatabaseSeeder.php
# Run seeders
php artisan db:seed
```

### Day 5: Testing & Verification

- [ ] Verify all tables created
- [ ] Check foreign key constraints
- [ ] Verify indexes created
- [ ] Test sample data
- [ ] Document database schema

**Testing Commands**:

```bash
# Check tables
php artisan db:show

# Check specific table
php artisan db:table materials

# Fresh migration with seed
php artisan migrate:fresh --seed
```

---

## 📋 WEEK 2: MODELS & RELATIONSHIPS

### Day 1: Core Models

#### ✅ Create Models

```bash
php artisan make:model Material
php artisan make:model Product
php artisan make:model ProductComposition
php artisan make:model Customer     # ⭐ NEW
```

#### ✅ Implement Features

- [ ] Material Model
  - Fillable fields (tambah `kategori`) ⭐ UPDATED
  - Relationships (compositions, productionDetails)
  - Accessors (totalValue, isLowStock, isCriticalStock, stockStatus)
  - Scopes (lowStock, criticalStock, inStock)
  - Methods (deductStock, addStock)

- [ ] Product Model
  - Fillable fields (tambah `sku`, `stok_minimum`) ⭐ UPDATED
  - Relationships (compositions, productions, saleDetails)
  - Accessors (margin, marginPercentage, totalValue, isLowStock)
  - Scopes (lowStock, inStock, byCategory)
  - Methods (calculateHPP, addStock, deductStock)

- [ ] ProductComposition Model
  - Fillable fields
  - Relationships (product, material)
  - Accessors (cost, costPercentage)
  - Methods (checkMaterialAvailability)

- [ ] Customer Model ⭐ NEW
  - Fillable fields (name, phone, email, address, etc.)
  - Relationships (sales)
  - Accessors (averagePurchase, segment, isVip)
  - Scopes (vip, active, bySegment)
  - Methods (recordPurchase)

### Day 2: Transaction Models

```bash
php artisan make:model Production
php artisan make:model ProductionDetail
php artisan make:model Sale
php artisan make:model SaleDetail
```

#### ✅ Implement Features

- [ ] Production & ProductionDetail
  - Relationships
  - Accessors
  - Scopes (today, thisMonth)
  - Model events (auto-calculate)

- [ ] Sale & SaleDetail ⭐ UPDATED
  - Add customer relationship
  - Update invoice generation
  - Auto-update customer stats
  - Model events

### Day 3: Finance Models

```bash
php artisan make:model Income
php artisan make:model Expense
php artisan make:model Debt
php artisan make:model Receivable
```

#### ✅ Implement Features

- [ ] Income/Expense Models
  - Fillable, casts
  - Scopes (thisMonth, byCategory)

- [ ] Debt/Receivable Models
  - Fillable, casts
  - Accessors (isOverdue, paymentPercentage)
  - Methods (makePayment, receivePayment)

### Day 4: System Models

```bash
php artisan make:model ActivityLog
```

- [ ] ActivityLog Model
  - Relationships (user)
  - Scopes (byUser, byModel, recent)

- [ ] Update User Model
  - Add roles & permissions
  - Relationships (activityLogs, productions, sales)
  - Accessors (isOwner, isAdmin)
  - Methods (hasRole, can)

### Day 5: Model Testing

- [ ] Unit tests untuk semua models
- [ ] Test relationships
- [ ] Test accessors & scopes
- [ ] Test methods

**Testing Commands**:

```bash
php artisan make:test MaterialModelTest --unit
php artisan make:test ProductModelTest --unit
php artisan make:test CustomerModelTest --unit

# Run tests
php artisan test
```

---

## 📋 WEEK 3: SERVICES LAYER

### Day 1: Material & Product Services

```bash
php artisan make:service MaterialService
php artisan make:service ProductService
```

#### ✅ MaterialService

- [ ] `getAllMaterials($filters)` - dengan filter kategori ⭐ UPDATED
- [ ] `getLowStockAlerts()`
- [ ] `createMaterial($data)` - validasi kategori ⭐ UPDATED
- [ ] `updateMaterial($material, $data)`
- [ ] `updateStock($material, $quantity, $type)`
- [ ] `getMaterialUsageStats($material, $startDate, $endDate)`

#### ✅ ProductService

- [ ] `getAllProducts($filters)`
- [ ] `createProduct($data)` - dengan SKU auto-generate ⭐ UPDATED
- [ ] `updateCompositions($product, $compositions)`
- [ ] `getProfitabilityAnalysis($product)`
- [ ] `checkStockAlert($product)` - menggunakan stok_minimum ⭐ UPDATED

### Day 2: Production & Sales Services

```bash
php artisan make:service ProductionService
php artisan make:service SalesService
php artisan make:service CustomerService  # ⭐ NEW
```

#### ✅ ProductionService

- [ ] `validateMaterialAvailability($productId, $quantity)`
- [ ] `createProduction($data)`
- [ ] `getProductionStats($startDate, $endDate)`

#### ✅ SalesService ⭐ UPDATED

- [ ] `createSale($data)` - dengan customer handling
- [ ] `getSalesStats($startDate, $endDate)`
- [ ] `getBestSellingProducts($startDate, $endDate, $limit)`
- [ ] `updateCustomerStats($customerId, $saleAmount)` - auto-update

#### ✅ CustomerService ⭐ NEW

- [ ] `getAllCustomers($filters)` - dengan segment filter
- [ ] `createCustomer($data)`
- [ ] `updateCustomer($customer, $data)`
- [ ] `getPurchaseHistory($customer, $limit)`
- [ ] `getCustomerStats($customer)` - analytics
- [ ] `getVipCustomers()`
- [ ] `getGrowthStats($startDate, $endDate)`

### Day 3: Finance Services

```bash
php artisan make:service FinanceService
php artisan make:service ReportService
```

#### ✅ FinanceService

- [ ] `getCashFlow($startDate, $endDate)`
- [ ] `calculateBEP($period)`
- [ ] `getIncomeExpenseSummary($startDate, $endDate)`
- [ ] `getDebtReceivableSummary()`

#### ✅ ReportService

- [ ] `generateBusinessReport($startDate, $endDate)`
- [ ] `getSalesReport($startDate, $endDate)`
- [ ] `getProductionReport($startDate, $endDate)`
- [ ] `getInventoryReport()`
- [ ] `getFinanceReport($startDate, $endDate)`
- [ ] `getCustomerReport($startDate, $endDate)` ⭐ NEW

### Day 4: Helper Services

```bash
php artisan make:service BulkCalculateService
php artisan make:service NotificationService
```

#### ✅ BulkCalculateService

- [ ] `calculateMaterialNeeds($productQuantities)`
- [ ] `recalculateAllHPP()`

#### ✅ NotificationService

- [ ] `sendStockAlert($material)`
- [ ] `sendLowStockNotification()`
- [ ] `sendDebtReminder($debt)`

### Day 5: Service Testing

- [ ] Unit tests untuk semua services
- [ ] Integration tests
- [ ] Transaction testing

---

## 📋 WEEK 4: API CONTROLLERS & ROUTES

### Day 1: Authentication API

```bash
composer require laravel/sanctum
php artisan vendor:publish --provider="Laravel\Sanctum\SanctumServiceProvider"
php artisan migrate

php artisan make:controller Api/AuthController
```

#### ✅ AuthController

- [ ] `POST /api/auth/register`
- [ ] `POST /api/auth/login`
- [ ] `POST /api/auth/logout`
- [ ] `GET /api/auth/me`
- [ ] `POST /api/auth/refresh`

### Day 2: Resource Controllers

```bash
php artisan make:controller Api/MaterialController --api
php artisan make:controller Api/ProductController --api
php artisan make:controller Api/ProductionController --api
php artisan make:controller Api/SaleController --api
php artisan make:controller Api/CustomerController --api  # ⭐ NEW
```

#### ✅ MaterialController

- [ ] `GET /api/materials` - index dengan filter kategori ⭐ UPDATED
- [ ] `POST /api/materials`
- [ ] `GET /api/materials/{id}`
- [ ] `PUT /api/materials/{id}`
- [ ] `DELETE /api/materials/{id}`
- [ ] `GET /api/materials/low-stock`
- [ ] `POST /api/materials/{id}/restock`

#### ✅ ProductController

- [ ] `GET /api/products`
- [ ] `POST /api/products` - dengan SKU validation ⭐ UPDATED
- [ ] `GET /api/products/{id}`
- [ ] `PUT /api/products/{id}`
- [ ] `DELETE /api/products/{id}`
- [ ] `GET /api/products/{id}/compositions`
- [ ] `POST /api/products/{id}/compositions`
- [ ] `GET /api/products/{id}/hpp-analysis`

#### ✅ ProductionController

- [ ] `GET /api/productions`
- [ ] `POST /api/productions`
- [ ] `GET /api/productions/{id}`
- [ ] `PUT /api/productions/{id}`
- [ ] `DELETE /api/productions/{id}`
- [ ] `GET /api/productions/history`
- [ ] `POST /api/productions/{id}/complete`

#### ✅ SaleController ⭐ UPDATED

- [ ] `GET /api/sales`
- [ ] `POST /api/sales` - dengan customer handling
- [ ] `GET /api/sales/{id}`
- [ ] `POST /api/sales/checkout` - POS checkout
- [ ] `GET /api/sales/history`
- [ ] `GET /api/sales/today`
- [ ] `GET /api/sales/stats`

#### ✅ CustomerController ⭐ NEW

- [ ] `GET /api/customers` - dengan filter segment
- [ ] `POST /api/customers`
- [ ] `GET /api/customers/{id}`
- [ ] `PUT /api/customers/{id}`
- [ ] `DELETE /api/customers/{id}`
- [ ] `GET /api/customers/{id}/history`
- [ ] `GET /api/customers/{id}/stats`
- [ ] `GET /api/customers/vip`

### Day 3: Finance & Dashboard Controllers

```bash
php artisan make:controller Api/FinanceController
php artisan make:controller Api/DashboardController
```

#### ✅ FinanceController

- [ ] `GET /api/finance/transactions`
- [ ] `GET /api/finance/incomes`
- [ ] `POST /api/finance/incomes`
- [ ] `GET /api/finance/expenses`
- [ ] `POST /api/finance/expenses`
- [ ] `GET /api/finance/debts`
- [ ] `POST /api/finance/debts`
- [ ] `PUT /api/finance/debts/{id}/payment`
- [ ] `GET /api/finance/receivables`
- [ ] `POST /api/finance/receivables`
- [ ] `PUT /api/finance/receivables/{id}/payment`
- [ ] `GET /api/finance/dashboard`
- [ ] `GET /api/finance/bep-analysis`
- [ ] `GET /api/finance/cash-flow`

#### ✅ DashboardController

- [ ] `GET /api/dashboard/production`
- [ ] `GET /api/dashboard/sales`
- [ ] `GET /api/dashboard/finance`
- [ ] `GET /api/dashboard/stats`
- [ ] `GET /api/dashboard/alerts`
- [ ] `GET /api/dashboard/customer-analytics` ⭐ NEW

### Day 4: API Resources & Requests

```bash
# Resources (for API responses)
php artisan make:resource MaterialResource
php artisan make:resource ProductResource
php artisan make:resource CustomerResource  # ⭐ NEW
php artisan make:resource SaleResource

# Requests (for validation)
php artisan make:request StoreMaterialRequest
php artisan make:request UpdateMaterialRequest
php artisan make:request StoreProductRequest
php artisan make:request StoreCustomerRequest  # ⭐ NEW
php artisan make:request StoreSaleRequest
```

#### ✅ API Resources

- [ ] Transform model data to API response format
- [ ] Include relationships
- [ ] Hide sensitive data

#### ✅ Form Requests

- [ ] Validation rules
- [ ] Authorization logic
- [ ] Custom error messages

### Day 5: API Testing & Documentation

- [ ] API integration tests
- [ ] Postman collection
- [ ] API documentation (Swagger/OpenAPI)

**Testing Commands**:

```bash
php artisan make:test Api/MaterialControllerTest
php artisan make:test Api/CustomerControllerTest  # ⭐ NEW

# Run API tests
php artisan test --filter Api
```

---

## 📋 ADDITIONAL FEATURES

### File Upload & Management

```bash
# Configure storage
php artisan storage:link

# Create controller
php artisan make:controller Api/FileController
```

- [ ] Image upload for products
- [ ] Receipt upload for expenses
- [ ] File validation & compression

### Import/Export

```bash
composer require maatwebsite/excel
```

- [ ] Excel import untuk materials
- [ ] Excel import untuk products
- [ ] Excel export untuk reports
- [ ] PDF export untuk invoices

### Notifications

```bash
composer require laravel-notification-channels/fcm
```

- [ ] Stock alert notifications
- [ ] Debt reminder notifications
- [ ] Daily summary notifications

---

## 📋 TESTING & DEPLOYMENT

### Testing Checklist

- [ ] All unit tests passing
- [ ] All feature tests passing
- [ ] API tests with Postman
- [ ] Manual testing all endpoints
- [ ] Load testing critical endpoints

### Deployment Checklist

- [ ] Environment configuration
- [ ] Database migration on production
- [ ] Seeder for production data
- [ ] API keys & secrets
- [ ] CORS configuration
- [ ] Rate limiting
- [ ] Logging & monitoring

---

## 🎯 PROGRESS TRACKING

| Week   | Tasks                  | Status | Completion |
| ------ | ---------------------- | ------ | ---------- |
| Week 1 | Database & Migrations  | ⏳     | 0%         |
| Week 2 | Models & Relationships | ⏳     | 0%         |
| Week 3 | Services Layer         | ⏳     | 0%         |
| Week 4 | API Controllers        | ⏳     | 0%         |

---

## 📚 RESOURCES

### Documentation

- Laravel Docs: https://laravel.com/docs
- Laravel Sanctum: https://laravel.com/docs/sanctum
- API Resources: https://laravel.com/docs/eloquent-resources

### Tools

- Postman: API testing
- TablePlus/phpMyAdmin: Database management
- Laravel Telescope: Debugging

---

**Last Updated**: 1 Februari 2026  
**Estimated Completion**: 4 Weeks  
**Status**: ⭐ Updated with Customer Management & Enhanced Fields
