# 📱 FRONTEND DEVELOPMENT CHECKLIST

**SI-UMKM Android App - Implementation Guide**

**Last Updated**: 1 Februari 2026  
**Status**: Ready to Implement

---

## 📋 WEEK 1: PROJECT SETUP & ARCHITECTURE

### Day 1: Initial Setup

#### ✅ Create Project

- [ ] Create new Compose project in Android Studio
- [ ] Configure package: `com.umkm.business`
- [ ] Min SDK: 24 (Android 7.0)
- [ ] Target SDK: 34 (Android 14)
- [ ] Kotlin version: 1.9+

#### ✅ Dependencies Setup

**build.gradle.kts (Module: app)**:

```kotlin
dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Retrofit & OkHttp (API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Room Database (offline)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // DataStore (preferences)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Coil (image loading)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Chart library
    implementation("com.patrykandpatrick.vico:compose:1.13.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

### Day 2: Architecture Setup

#### ✅ Create Package Structure

```
app/src/main/java/com/umkm/business/
├── data/
│   ├── local/              # Room Database
│   │   ├── dao/
│   │   ├── entities/
│   │   └── database/
│   ├── remote/             # API
│   │   ├── api/
│   │   ├── dto/
│   │   └── interceptors/
│   ├── repository/         # Repository Pattern
│   └── preferences/        # DataStore
├── domain/
│   ├── model/              # Domain Models ⭐ UPDATED (dengan Customer)
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── theme/
│   ├── components/
│   ├── navigation/
│   └── screens/
│       ├── auth/
│       ├── production/
│       ├── sales/
│       ├── finance/
│       ├── product/
│       └── customer/       # ⭐ NEW
└── util/
    ├── Constants.kt
    ├── Extensions.kt
    └── Result.kt
```

#### ✅ Create Base Classes

- [ ] `BaseViewModel.kt` - ViewModel base class
- [ ] `Result.kt` - sealed class untuk API response
- [ ] `Resource.kt` - sealed class untuk UI state
- [ ] `Constants.kt` - app constants
- [ ] `Extensions.kt` - Kotlin extensions

### Day 3: Theme & Design System

#### ✅ Material 3 Theme

- [ ] `Color.kt` - Brand colors (Primary Green, Amber, Blue, Red, etc.)
- [ ] `Typography.kt` - Inter font family
- [ ] `Theme.kt` - Light/Dark theme
- [ ] `Shapes.kt` - Rounded corners

#### ✅ Reusable Components

- [ ] `PrimaryButton.kt`
- [ ] `OutlinedTextField.kt`
- [ ] `AppBar.kt`
- [ ] `LoadingIndicator.kt`
- [ ] `ErrorMessage.kt`
- [ ] `EmptyState.kt`
- [ ] `ConfirmDialog.kt`
- [ ] `SuccessDialog.kt`

### Day 4-5: Network Layer

#### ✅ Retrofit Setup

**ApiService.kt**:

```kotlin
interface ApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Products
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<Product>

    // Materials
    @GET("materials")
    suspend fun getMaterials(@Query("kategori") kategori: String?): Response<List<Material>>  // ⭐ UPDATED

    // Productions
    @POST("productions")
    suspend fun createProduction(@Body request: ProductionRequest): Response<Production>

    // Sales (POS)
    @POST("sales/checkout")
    suspend fun checkout(@Body request: CheckoutRequest): Response<Sale>  // ⭐ UPDATED dengan customer

    @GET("sales/today")
    suspend fun getTodaySales(): Response<List<Sale>>

    // Customers ⭐ NEW
    @GET("customers")
    suspend fun getCustomers(@Query("segment") segment: String?): Response<List<Customer>>

    @POST("customers")
    suspend fun createCustomer(@Body request: CustomerRequest): Response<Customer>

    @GET("customers/{id}")
    suspend fun getCustomer(@Path("id") id: Int): Response<Customer>

    @GET("customers/{id}/history")
    suspend fun getCustomerHistory(@Path("id") id: Int): Response<List<Sale>>

    // Finance
    @GET("finance/dashboard")
    suspend fun getFinanceDashboard(): Response<FinanceDashboard>

    @POST("finance/incomes")
    suspend fun createIncome(@Body request: IncomeRequest): Response<Income>

    @POST("finance/expenses")
    suspend fun createExpense(@Body request: ExpenseRequest): Response<Expense>
}
```

#### ✅ Interceptors

- [ ] `AuthInterceptor.kt` - Add auth token
- [ ] `LoggingInterceptor.kt` - Log requests
- [ ] `ErrorInterceptor.kt` - Handle errors

#### ✅ Repository Pattern

- [ ] `ProductRepository.kt`
- [ ] `MaterialRepository.kt`
- [ ] `ProductionRepository.kt`
- [ ] `SalesRepository.kt`
- [ ] `CustomerRepository.kt` ⭐ NEW
- [ ] `FinanceRepository.kt`

---

## 📋 WEEK 2: DATA MODELS & LOCAL DATABASE

### Day 1: Domain Models

#### ✅ Create All Models (lihat docs/01-MODELS.md)

- [ ] `User.kt` (Model 1)
- [ ] `Material.kt` (Model 2) - dengan kategori ⭐ UPDATED
- [ ] `Product.kt` (Model 3) - dengan sku ⭐ UPDATED
- [ ] `Production.kt` (Model 4)
- [ ] `POSProduct.kt` (Model 5)
- [ ] `Customer.kt` (Model 6) ⭐ NEW
- [ ] `Sale.kt` (Model 7) - dengan customerId ⭐ UPDATED
- [ ] `SaleDetail.kt` (Model 7b) ⭐ NEW
- [ ] `Transaction.kt` (Model 8)
- [ ] `RecentSale.kt` (Model 9)
- [ ] `SalesHistoryItem.kt` (Model 10)
- [ ] `BottomNavItem.kt` (Model 11)

### Day 2: Room Database Setup

#### ✅ Create Entities

```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val nama: String,
    val sku: String,  // ⭐ NEW
    val kategori: String,
    val harga_jual: Double,
    val stok: Int,
    val stok_minimum: Int,  // ⭐ NEW
    val gambar_url: String?,
    @ColumnInfo(name = "last_synced") val lastSynced: Long
)

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey val id: Int,
    val nama: String,
    val kategori: String?,  // ⭐ NEW
    val satuan: String,
    val harga_beli: Double,
    val stok: Double,
    @ColumnInfo(name = "last_synced") val lastSynced: Long
)

@Entity(tableName = "customers")  // ⭐ NEW
data class CustomerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val phone: String?,
    val email: String?,
    val segment: String,
    val total_purchases: Double,
    @ColumnInfo(name = "last_synced") val lastSynced: Long
)

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey val id: Int,
    val invoice_number: String,
    val customer_id: Int?,  // ⭐ NEW
    val total_amount: Double,
    val payment_method: String,
    val sale_date: String,
    @ColumnInfo(name = "synced") val synced: Boolean = false
)
```

#### ✅ Create DAOs

- [ ] `ProductDao.kt` - CRUD operations + offline sync
- [ ] `MaterialDao.kt`
- [ ] `CustomerDao.kt` ⭐ NEW
- [ ] `SaleDao.kt`
- [ ] `ProductionDao.kt`

#### ✅ Database

```kotlin
@Database(
    entities = [
        ProductEntity::class,
        MaterialEntity::class,
        CustomerEntity::class,  // ⭐ NEW
        SaleEntity::class,
        ProductionEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun materialDao(): MaterialDao
    abstract fun customerDao(): CustomerDao  // ⭐ NEW
    abstract fun saleDao(): SaleDao
    abstract fun productionDao(): ProductionDao
}
```

### Day 3: DataStore (Preferences)

#### ✅ UserPreferences

```kotlin
class UserPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    suspend fun clearAuth()
    suspend fun saveUserInfo(userId: Int, name: String, role: String)
    fun getUserRole(): Flow<String?>
}
```

### Day 4-5: Repository Implementation

#### ✅ Implement Repositories

- [ ] `ProductRepositoryImpl.kt`
  - Online: Fetch dari API
  - Offline: Cache di Room
  - Sync strategy
- [ ] `SalesRepositoryImpl.kt` ⭐ UPDATED
  - Offline queue untuk sales
  - Auto-sync when online
  - Customer integration
- [ ] `CustomerRepositoryImpl.kt` ⭐ NEW
  - Fetch customers from API
  - Local cache
  - Search & filter

---

## 📋 WEEK 3: NAVIGATION & AUTH

### Day 1: Navigation Setup

#### ✅ NavGraph

```kotlin
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Production : Screen("production")
    object Sales : Screen("sales")
    object Finance : Screen("finance")
    object Products : Screen("products")
    object Customers : Screen("customers")  // ⭐ NEW
    // ... 34 screens total
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController, startDestination) {
        composable(Screen.Login.route) { LoginScreen() }
        composable(Screen.Dashboard.route) { DashboardScreen() }
        // ... all screens
    }
}
```

#### ✅ Bottom Navigation

- [ ] 5 tabs: Produksi, Penjualan, Keuangan, Produk, Profil
- [ ] Role-based visibility
- [ ] Badge counts

### Day 2-3: Authentication

#### ✅ Login Screen

- [ ] Email/username field
- [ ] Password field
- [ ] Login button
- [ ] Remember me checkbox
- [ ] Loading state
- [ ] Error handling

#### ✅ AuthViewModel

```kotlin
class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(username: String, password: String)
    fun logout()
    fun checkAuthStatus()
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

### Day 4: Role-based Access

#### ✅ Permission Helper

```kotlin
object PermissionHelper {
    fun canAccessProduction(role: String): Boolean {
        return role in listOf("owner", "admin", "manager", "produksi")
    }

    fun canAccessSales(role: String): Boolean {
        return role in listOf("owner", "admin", "manager", "kasir")
    }

    fun canAccessFinance(role: String): Boolean {
        return role in listOf("owner", "admin", "keuangan")
    }

    fun canManageCustomers(role: String): Boolean {  // ⭐ NEW
        return role in listOf("owner", "admin", "manager")
    }
}
```

### Day 5: Splash Screen

- [ ] Check auth status
- [ ] Sync data if online
- [ ] Navigate to Login/Dashboard

---

## 📋 WEEK 4-5: PRODUCTION MODULE (Screens 1-10)

### Day 1-2: Production Dashboard

#### ✅ ProductionDashboardScreen

**Components**:

- [ ] Header dengan current shift
- [ ] Quick stats cards (3)
  - Produksi Hari Ini
  - Total Produk Siap
  - Material Menipis
- [ ] Active productions list
- [ ] FAB untuk create production
- [ ] Bottom navigation

**ViewModel**:

```kotlin
class ProductionViewModel : ViewModel() {
    private val _dashboardState = MutableStateFlow<ProductionDashboardState>(Loading)
    val dashboardState = _dashboardState.asStateFlow()

    fun loadDashboard()
    fun refreshData()
    fun createProduction(data: ProductionRequest)
}
```

### Day 3: Material Management

#### ✅ MaterialListScreen

- [ ] Search bar dengan kategori filter ⭐ UPDATED
- [ ] Material cards with kategori badge ⭐ UPDATED
- [ ] Low stock indicators
- [ ] Pull to refresh
- [ ] FAB untuk add material

#### ✅ MaterialDetailScreen

- [ ] Material info (dengan kategori) ⭐ UPDATED
- [ ] Stock chart
- [ ] Usage history
- [ ] Restock button

#### ✅ AddMaterialScreen

- [ ] Nama field
- [ ] Kategori dropdown ⭐ NEW (Bahan Utama, Bumbu, Kemasan, dll)
- [ ] Satuan field
- [ ] Harga beli field
- [ ] Stok awal field
- [ ] Form validation

### Day 4: Production Process

#### ✅ CreateProductionScreen

- [ ] Product selector (dropdown)
- [ ] Quantity input
- [ ] Material availability check
- [ ] Cost calculation preview
- [ ] Confirm button

#### ✅ ProductionDetailScreen

- [ ] Production info
- [ ] Materials used list
- [ ] Status (Planning/InProgress/Completed)
- [ ] Complete button (if in progress)

### Day 5: Product & Composition

#### ✅ ProductListScreen

- [ ] Product grid/list
- [ ] Stock badge dengan stok_minimum ⭐ UPDATED
- [ ] SKU display ⭐ NEW
- [ ] Low stock filter
- [ ] Category filter

#### ✅ ProductDetailScreen

- [ ] Product info (SKU, kategori, dll) ⭐ UPDATED
- [ ] Composition breakdown
- [ ] HPP analysis
- [ ] Profit margin
- [ ] Edit button

#### ✅ AddProductScreen

- [ ] Nama field
- [ ] Auto-generate SKU ⭐ NEW
- [ ] Kategori field
- [ ] Harga jual field
- [ ] Stok minimum field ⭐ NEW
- [ ] Composition builder
- [ ] Image upload

---

## 📋 WEEK 6-7: SALES MODULE (Screens 11-20)

### Day 1-2: POS System

#### ✅ SalesDashboardScreen (POS)

**Components**:

- [ ] Product grid (POSProduct cards)
- [ ] Search bar
- [ ] Category filter
- [ ] Shopping cart preview (badge count)
- [ ] Cart button → navigate to checkout

**ViewModel**:

```kotlin
class POSViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<POSProduct>>(emptyList())
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    fun loadProducts()
    fun addToCart(product: POSProduct)
    fun updateQuantity(productId: Int, quantity: Int)
    fun removeFromCart(productId: Int)
    fun getCartTotal(): Double
}
```

#### ✅ CheckoutScreen ⭐ UPDATED

**Components**:

- [ ] Cart items list dengan quantity controls
- [ ] Customer selector (optional) ⭐ NEW
  - Quick search customer
  - "Walk-in customer" option
  - Create new customer button
- [ ] Subtotal, tax, total
- [ ] Payment method selector (Cash, Transfer, QRIS)
- [ ] Cash amount input (if Cash selected)
- [ ] Change calculation
- [ ] Confirm button
- [ ] Print receipt checkbox

**Flow dengan Customer**:

1. Review cart items
2. Select customer (optional untuk walk-in)
3. Choose payment method
4. Process payment
5. Auto-update customer stats (jika ada customer)
6. Generate invoice
7. Show success dialog

### Day 3: Customer Management ⭐ NEW

#### ✅ CustomerListScreen

**Components**:

- [ ] Search bar (nama/phone)
- [ ] Segment filter (VIP/Regular/New)
- [ ] Customer cards dengan badges
  - VIP badge untuk segment VIP
  - Transaction count
  - Total purchases
- [ ] Sort options (nama, total belanja, transaksi terakhir)
- [ ] FAB untuk add customer

**ViewModel**:

```kotlin
class CustomerViewModel : ViewModel() {
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    private val _filterSegment = MutableStateFlow<String?>(null)

    fun loadCustomers()
    fun filterBySegment(segment: String?)
    fun searchCustomer(query: String)
    fun createCustomer(data: CustomerRequest)
}
```

#### ✅ CustomerDetailScreen

**Components**:

- [ ] Customer info card
  - Name, phone, email
  - Segment badge (VIP/Regular/New)
  - Member since date
- [ ] Quick stats
  - Total purchases (Rp)
  - Transaction count
  - Average purchase
  - Last transaction date
- [ ] Purchase history list (expandable)
- [ ] Edit button
- [ ] Delete button (dengan konfirmasi)

**Features**:

- [ ] View full transaction history
- [ ] Customer analytics chart
- [ ] Send promotional message (future)

#### ✅ AddCustomerScreen

**Form Fields**:

- [ ] Nama lengkap (required)
- [ ] Nomor HP (required, validation)
- [ ] Email (optional, validation)
- [ ] Alamat (optional, text area)
- [ ] Notes (optional)
- [ ] Auto-set segment = "new"
- [ ] Auto-set total_purchases = 0

**Validation**:

- [ ] Phone format validation (08xxx atau +62xxx)
- [ ] Email format validation
- [ ] Duplicate phone check
- [ ] Error handling

### Day 4: Sales History & Reports

#### ✅ SalesHistoryScreen

- [ ] Date range picker
- [ ] Sales list dengan customer info ⭐ UPDATED
  - Invoice number
  - Customer name (atau "Walk-in")
  - Items summary
  - Total amount
  - Payment method
- [ ] Filter by customer ⭐ NEW
- [ ] Export to Excel

#### ✅ SaleDetailScreen

- [ ] Invoice header (dengan customer) ⭐ UPDATED
- [ ] Customer info card (jika ada)
- [ ] Items list
- [ ] Payment info
- [ ] Reprint receipt

### Day 5: Analytics & Reports

#### ✅ SalesAnalyticsScreen

- [ ] Today/Week/Month tabs
- [ ] Sales trend chart
- [ ] Best selling products
- [ ] Customer analytics ⭐ NEW
  - Top customers by value
  - VIP customer growth
  - New vs returning customers
- [ ] Revenue breakdown

---

## 📋 WEEK 8-9: FINANCE MODULE (Screens 21-28)

### Day 1-2: Finance Dashboard

#### ✅ FinanceDashboardScreen

**Components**:

- [ ] Period selector (Today/Week/Month)
- [ ] Summary cards (4)
  - Total Pemasukan
  - Total Pengeluaran
  - Laba Bersih
  - Cash Flow
- [ ] Quick actions grid (4)
  - Catat Pemasukan
  - Catat Pengeluaran
  - Kelola Hutang
  - Kelola Piutang
- [ ] Recent transactions (5 items)
- [ ] Chart: Income vs Expense

**ViewModel**:

```kotlin
class FinanceViewModel : ViewModel() {
    private val _dashboardData = MutableStateFlow<FinanceDashboard?>(null)
    private val _period = MutableStateFlow(Period.TODAY)

    fun loadDashboard(period: Period)
    fun refreshData()
}
```

### Day 3: Income & Expense

#### ✅ IncomeListScreen

- [ ] Date filter
- [ ] Category filter
- [ ] Income items list
- [ ] Total summary
- [ ] FAB untuk add income

#### ✅ AddIncomeScreen

- [ ] Kategori (Penjualan, Lain-lain)
- [ ] Jumlah
- [ ] Deskripsi
- [ ] Tanggal
- [ ] Bukti (foto, optional)
- [ ] Save button

#### ✅ ExpenseListScreen

- [ ] Date filter
- [ ] Category filter (Produksi, Operasional, Gaji, Lain-lain)
- [ ] Expense items list
- [ ] Total summary
- [ ] FAB untuk add expense

#### ✅ AddExpenseScreen

- [ ] Kategori dropdown
- [ ] Jumlah
- [ ] Deskripsi
- [ ] Tanggal
- [ ] Bukti (foto, upload)
- [ ] Save button

### Day 4: Debt & Receivable

#### ✅ DebtListScreen

- [ ] Active debts list
- [ ] Paid debts (collapsed)
- [ ] Overdue indicator
- [ ] Total debt summary
- [ ] FAB untuk add debt

#### ✅ DebtDetailScreen

- [ ] Debt info
- [ ] Payment history
- [ ] Make payment button
- [ ] Payment form (amount, date)

#### ✅ ReceivableListScreen

- [ ] Active receivables list
- [ ] Paid receivables (collapsed)
- [ ] Overdue indicator
- [ ] Total receivable summary
- [ ] FAB untuk add receivable

#### ✅ ReceivableDetailScreen

- [ ] Receivable info
- [ ] Payment history
- [ ] Receive payment button
- [ ] Payment form

### Day 5: Reports & BEP

#### ✅ FinanceReportScreen

- [ ] Date range selector
- [ ] Report type tabs
  - Cash Flow
  - Laba Rugi
  - BEP Analysis
- [ ] Charts & graphs
- [ ] Export to PDF

#### ✅ BEPAnalysisScreen

- [ ] Fixed costs input
- [ ] Variable costs input
- [ ] BEP calculation
- [ ] BEP chart
- [ ] Recommendations

---

## 📋 WEEK 10: PRODUCT MODULE (Screens 29-32)

### Day 1-2: Product Catalog

#### ✅ ProductCatalogScreen

- [ ] Search bar
- [ ] Filter options (kategori, stok)
- [ ] Sort options (nama, harga, stok)
- [ ] Product grid dengan badges
  - SKU display ⭐ NEW
  - Low stock badge (stok < stok_minimum) ⭐ UPDATED
  - Out of stock badge
- [ ] View mode toggle (grid/list)

#### ✅ ProductDetailScreen (Enhanced)

- [ ] Hero image
- [ ] Product info
  - SKU ⭐ NEW
  - Kategori
  - Harga
  - Stok / Stok Minimum ⭐ UPDATED
  - Margin
- [ ] Composition chart
- [ ] HPP breakdown
- [ ] Production history
- [ ] Sales history
- [ ] Edit/Delete actions

### Day 3: Product Form

#### ✅ AddEditProductScreen

**Form Sections**:

1. **Basic Info**
   - Nama produk
   - Auto-generate SKU (editable) ⭐ NEW
   - Kategori dropdown
   - Upload gambar

2. **Pricing & Stock**
   - Harga jual
   - Stok awal
   - Stok minimum ⭐ NEW (default 5)

3. **Composition**
   - Material selector
   - Quantity input
   - Add material button
   - Composition list (removable)
   - Auto-calculate HPP

4. **Actions**
   - Save button
   - Cancel button

**Validation**:

- [ ] Nama tidak boleh kosong
- [ ] SKU unique validation ⭐ NEW
- [ ] Harga jual > 0
- [ ] Stok minimum >= 0 ⭐ NEW
- [ ] Minimal 1 material

### Day 4-5: Material Management (Enhanced)

#### ✅ MaterialCatalogScreen

- [ ] Search bar
- [ ] Kategori filter chips ⭐ NEW
  - All, Bahan Utama, Bumbu, Kemasan, dll
- [ ] Sort options
- [ ] Material cards dengan kategori badge ⭐ UPDATED
- [ ] Low stock alerts
- [ ] Add material FAB

#### ✅ MaterialDetailScreen

- [ ] Material info (dengan kategori) ⭐ UPDATED
- [ ] Stock info
- [ ] Usage in products (list produk yang pakai)
- [ ] Restock history
- [ ] Usage analytics
- [ ] Edit/Delete actions

---

## 📋 WEEK 11: PROFILE & SETTINGS (Screens 33-34)

### Day 1: Profile Screen

#### ✅ ProfileScreen

**Sections**:

1. **User Info Card**
   - Avatar
   - Nama
   - Role badge
   - Email

2. **Business Info** (if owner/admin)
   - Nama usaha
   - Alamat
   - Telepon
   - Edit button

3. **Quick Stats**
   - Total products
   - Total customers ⭐ NEW
   - Active productions
   - Today's sales

4. **Menu Items**
   - Pengaturan Akun
   - Pengaturan Aplikasi
   - Bantuan
   - Tentang Aplikasi
   - Logout

### Day 2: Settings Screen

#### ✅ SettingsScreen

**Sections**:

1. **Akun**
   - Ubah password
   - Ubah email
   - Ubah foto profil

2. **Aplikasi**
   - Tema (Light/Dark/System)
   - Bahasa (Indonesia/English)
   - Notifikasi toggle
   - Clear cache

3. **Sinkronisasi**
   - Auto-sync toggle
   - Sync interval
   - Last sync time
   - Manual sync button

4. **Data & Storage**
   - Download data
   - Backup database
   - Restore database
   - Clear offline data

5. **Advanced** (owner only)
   - User management
   - Role permissions
   - Audit log

---

## 📋 WEEK 12: TESTING & POLISH

### Day 1: Unit Testing

#### ✅ ViewModel Tests

```kotlin
@Test
fun `login with valid credentials returns success`() = runTest {
    // Given
    val viewModel = AuthViewModel(fakeRepository)

    // When
    viewModel.login("admin", "password")

    // Then
    val state = viewModel.authState.value
    assertTrue(state is AuthState.Success)
}
```

- [ ] Test all ViewModels
- [ ] Test use cases
- [ ] Test repositories
- [ ] Test utilities

### Day 2: UI Testing

#### ✅ Compose UI Tests

```kotlin
@Test
fun loginScreen_displaysCorrectly() {
    composeTestRule.setContent {
        LoginScreen()
    }

    composeTestRule.onNodeWithText("Username").assertExists()
    composeTestRule.onNodeWithText("Password").assertExists()
    composeTestRule.onNodeWithText("Login").assertExists()
}
```

- [ ] Test critical user flows
- [ ] Test navigation
- [ ] Test form validation

### Day 3: Integration Testing

- [ ] Test API integration
- [ ] Test offline mode
- [ ] Test data sync
- [ ] Test role-based access

### Day 4: Bug Fixes & Polish

- [ ] Fix reported bugs
- [ ] Improve error messages
- [ ] Add loading states
- [ ] Improve animations
- [ ] Optimize performance

### Day 5: Documentation

- [ ] Code documentation
- [ ] User manual
- [ ] API integration guide
- [ ] Deployment guide

---

## 📋 DEPLOYMENT CHECKLIST

### Pre-Release

- [ ] All tests passing
- [ ] No critical bugs
- [ ] Performance optimized
- [ ] ProGuard rules configured
- [ ] Code obfuscation enabled

### Release Build

- [ ] Update version code & name
- [ ] Generate signed APK/AAB
- [ ] Test on multiple devices
- [ ] Test on different Android versions

### Play Store (Optional)

- [ ] Create store listing
- [ ] Upload screenshots
- [ ] Write description
- [ ] Set pricing & distribution
- [ ] Submit for review

---

## 🎯 PROGRESS TRACKING

| Week     | Module                   | Screens | Status | Completion |
| -------- | ------------------------ | ------- | ------ | ---------- |
| Week 1   | Setup & Architecture     | -       | ⏳     | 0%         |
| Week 2   | Data Layer               | -       | ⏳     | 0%         |
| Week 3   | Navigation & Auth        | 1-2     | ⏳     | 0%         |
| Week 4-5 | Production Module        | 3-10    | ⏳     | 0%         |
| Week 6-7 | Sales Module + Customers | 11-20   | ⏳     | 0%         |
| Week 8-9 | Finance Module           | 21-28   | ⏳     | 0%         |
| Week 10  | Product Module           | 29-32   | ⏳     | 0%         |
| Week 11  | Profile & Settings       | 33-34   | ⏳     | 0%         |
| Week 12  | Testing & Polish         | -       | ⏳     | 0%         |

**Total Screens**: 34  
**Estimated Completion**: 12 Weeks  
**Status**: ⭐ Updated with Customer Management & Enhanced Fields

---

## 📚 RESOURCES

### Documentation

- Jetpack Compose: https://developer.android.com/jetpack/compose
- Material 3: https://m3.material.io/
- Navigation: https://developer.android.com/guide/navigation
- Room: https://developer.android.com/training/data-storage/room
- Retrofit: https://square.github.io/retrofit/

### Libraries

- Coil: https://coil-kt.github.io/coil/
- Vico Charts: https://github.com/patrykandpatrick/vico
- DataStore: https://developer.android.com/topic/libraries/architecture/datastore

### Tools

- Android Studio: Latest stable
- Postman: API testing
- Figma: Design reference

---

**Last Updated**: 1 Februari 2026  
**Ready to implement with enhanced customer management and improved field tracking**
