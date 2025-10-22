# 🔄 FASE 6 - REFACTORING GUIDE

## Panduan Migrasi ke Arsitektur MVVM dengan Repository Pattern

### 📋 Overview

Aplikasi WavesOfFood telah di-refactor dengan arsitektur yang lebih proper:

- ✅ Repository Pattern untuk abstraksi data layer
- ✅ Dependency Injection dengan ViewModelFactory
- ✅ Kotlin Coroutines untuk operasi asinkron
- ✅ Proper package structure
- ✅ Separation of concerns

---

## 📁 Struktur Package Baru

```
com.komputerkit.wavesoffood/
├── WavesOfFoodApp.kt              # Application class
├── MainActivity.kt                 # Main Activity
│
├── data/
│   ├── model/                     # Data Models
│   │   ├── ProductModel.kt
│   │   ├── UserModel.kt
│   │   └── OrderModel.kt
│   │
│   └── repository/                # Repository Layer
│       ├── AuthRepository.kt      # Interface
│       ├── FirebaseAuthRepository.kt  # Implementation
│       └── EcommerceRepository.kt # Firestore operations
│
├── viewmodel/                     # ViewModels (BARU dengan Repository)
│   ├── ViewModelFactory.kt        # Factory untuk DI
│   ├── NewAuthViewModel.kt
│   ├── NewHomeViewModel.kt
│   ├── NewCartViewModel.kt
│   ├── NewProductDetailViewModel.kt
│   ├── NewCheckoutViewModel.kt
│   ├── NewProfileViewModel.kt
│   ├── NewOrdersViewModel.kt
│   └── NewOrderDetailViewModel.kt
│
├── view/                          # UI Layer (Activities)
│   ├── auth/
│   ├── splash/
│   ├── product/
│   ├── cart/
│   ├── checkout/
│   ├── profile/
│   └── orders/
│
├── adapter/                       # RecyclerView Adapters
│   ├── ProductAdapter.kt
│   ├── CartAdapter.kt
│   ├── OrderAdapter.kt
│   └── OrderDetailAdapter.kt
│
└── utils/                         # Helper Classes
    ├── Constants.kt
    ├── Extensions.kt
    └── UiState.kt
```

---

## 🔥 File-File Baru yang Dibuat

### 1. Repository Layer

#### **AuthRepository.kt** (Interface)

```kotlin
package com.komputerkit.wavesoffood.data.repository

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signUp(email: String, password: String, name: String): Result<FirebaseUser>
    fun getCurrentUserId(): String?
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
    fun isLoggedIn(): Boolean
}
```

#### **FirebaseAuthRepository.kt** (Implementation)

- Implements `AuthRepository`
- Handles Firebase Authentication operations
- Creates user document in Firestore after signup
- Uses Kotlin Coroutines with `suspend` functions

#### **EcommerceRepository.kt**

Menangani semua operasi Firestore:

- **User Operations:** fetchUser, updateUserField, updateUserAddress
- **Product Operations:** fetchAllProducts, fetchProductsByCategory, fetchProductById
- **Cart Operations:** getCartItems, addToCart, removeFromCart, updateCartQuantity, clearCart
- **Order Operations:** createOrder, fetchUserOrders, fetchOrderById, updateOrderStatus

### 2. Data Models (Relocated)

Dipindahkan dari `model/` ke `data/model/`:

- `ProductModel.kt`
- `UserModel.kt` (cartItems: Map<String, **Int**> - changed from Long)
- `OrderModel.kt` (items: Map<String, **Int**> - changed from Long)

### 3. Utils Package

#### **Constants.kt**

```kotlin
object Constants {
    const val COLLECTION_USERS = "users"
    const val COLLECTION_PRODUCTS = "products"
    const val COLLECTION_ORDERS = "orders"

    const val EXTRA_PRODUCT_ID = "PRODUCT_ID"
    const val EXTRA_ORDER_ID = "ORDER_ID"
    const val EXTRA_SUBTOTAL = "SUBTOTAL"

    const val CATEGORY_FOOD = "Food"
    const val TAX_RATE = 0.10
    const val PAYMENT_DELAY_MS = 3000L
    // ... dll
}
```

#### **Extensions.kt**

```kotlin
fun Double.toRupiah(): String
fun Date.toFormattedDate(): String
fun String.shortened(length: Int): String
fun String.isValidEmail(): Boolean
```

#### **UiState.kt**

```kotlin
sealed class UiState<out T>
sealed class AuthState
sealed class PaymentState
```

### 4. ViewModels (Refactored)

Semua ViewModel sekarang:

- ✅ Menerima Repository via constructor
- ✅ Menggunakan `viewModelScope.launch` untuk coroutines
- ✅ Menggunakan `Result<T>` untuk error handling
- ✅ LiveData untuk observable state

**File baru:**

- `NewAuthViewModel.kt`
- `NewHomeViewModel.kt`
- `NewCartViewModel.kt`
- `NewProductDetailViewModel.kt`
- `NewCheckoutViewModel.kt`
- `NewProfileViewModel.kt`
- `NewOrdersViewModel.kt`
- `NewOrderDetailViewModel.kt`

### 5. ViewModelFactory

```kotlin
class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val ecommerceRepository: EcommerceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Returns appropriate ViewModel with injected repositories
    }
}
```

### 6. Application Class

**WavesOfFoodApp.kt**

```kotlin
class WavesOfFoodApp : Application() {
    val authRepository by lazy { FirebaseAuthRepository() }
    val ecommerceRepository by lazy { EcommerceRepository() }
    val viewModelFactory by lazy { ViewModelFactory(...) }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
```

---

## 🔨 Step-by-Step Migration

### Step 1: Update AndroidManifest.xml ✅ DONE

```xml
<application
    android:name=".WavesOfFoodApp"
    ...>
```

### Step 2: Rename Old ViewModels (Backup)

File lama yang perlu di-backup atau delete setelah migrasi:

```
viewmodel/AuthViewModel.kt → (akan diganti dengan NewAuthViewModel.kt)
viewmodel/HomeViewModel.kt → (akan diganti dengan NewHomeViewModel.kt)
viewmodel/CartViewModel.kt → (akan diganti dengan NewCartViewModel.kt)
viewmodel/ProductDetailViewModel.kt → (akan diganti dengan NewProductDetailViewModel.kt)
viewmodel/CheckoutViewModel.kt → (akan diganti dengan NewCheckoutViewModel.kt)
viewmodel/ProfileViewModel.kt → (akan diganti dengan NewProfileViewModel.kt)
viewmodel/OrdersViewModel.kt → (akan diganti dengan NewOrdersViewModel.kt)
viewmodel/OrderDetailViewModel.kt → (akan diganti dengan NewOrderDetailViewModel.kt)
```

**ACTION REQUIRED:**

1. Rename semua file `New*ViewModel.kt` menjadi nama aslinya (hapus prefix "New")
2. Delete file ViewModel lama

### Step 3: Update Import Statements di Activities

Setiap Activity yang menggunakan ViewModel perlu update:

#### Before:

```kotlin
package com.komputerkit.wavesoffood.view.auth

import com.komputerkit.wavesoffood.model.UserModel
import com.komputerkit.wavesoffood.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    // ...
}
```

#### After:

```kotlin
package com.komputerkit.wavesoffood.view.auth

import com.komputerkit.wavesoffood.data.model.UserModel
import com.komputerkit.wavesoffood.viewmodel.AuthViewModel
import com.komputerkit.wavesoffood.viewmodel.ViewModelFactory
import com.komputerkit.wavesoffood.WavesOfFoodApp

class LoginActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels {
        (application as WavesOfFoodApp).viewModelFactory
    }
    // ...
}
```

### Step 4: Update Activities dengan ViewModelFactory

#### Activities yang perlu di-update:

1. **LoginActivity**

```kotlin
private val viewModel: AuthViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

2. **RegisterActivity**

```kotlin
private val viewModel: AuthViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

3. **MainActivity**

```kotlin
private val homeViewModel: HomeViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
private val cartViewModel: CartViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

4. **ProductDetailActivity**

```kotlin
private val viewModel: ProductDetailViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

5. **CartActivity**

```kotlin
private val viewModel: CartViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

6. **CheckoutActivity**

```kotlin
private val viewModel: CheckoutViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

7. **ProfileActivity**

```kotlin
private val viewModel: ProfileViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

8. **OrdersActivity**

```kotlin
private val viewModel: OrdersViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

9. **OrderDetailActivity**

```kotlin
private val viewModel: OrderDetailViewModel by viewModels {
    (application as WavesOfFoodApp).viewModelFactory
}
```

### Step 5: Update Adapters

Update import statements dari:

```kotlin
import com.komputerkit.wavesoffood.model.ProductModel
```

Menjadi:

```kotlin
import com.komputerkit.wavesoffood.data.model.ProductModel
```

Files yang perlu di-update:

- `adapter/ProductAdapter.kt`
- `adapter/CartAdapter.kt`
- `adapter/OrderAdapter.kt`
- `adapter/OrderDetailAdapter.kt`

### Step 6: Update RegisterActivity

Hapus parameter `address` dari signUp call:

#### Before:

```kotlin
viewModel.signUpWithEmail(email, password, name, address)
```

#### After:

```kotlin
viewModel.signUpWithEmail(email, password, name)
```

Address sekarang bisa di-set nanti via ProfileActivity.

---

## 🎯 Key Changes Summary

### 1. Repository Pattern

- Semua Firebase operations sekarang di Repository layer
- ViewModels tidak langsung akses Firebase
- Easier untuk testing dan maintenance

### 2. Kotlin Coroutines

- Semua async operations menggunakan `suspend` functions
- `viewModelScope.launch` untuk lifecycle-aware coroutines
- No more callbacks hell

### 3. Result<T> Pattern

```kotlin
suspend fun fetchProducts(): Result<List<ProductModel>>

// Usage:
result.onSuccess { products ->
    // Handle success
}.onFailure { exception ->
    // Handle error
}
```

### 4. Dependency Injection

- ViewModelFactory manages ViewModel creation
- Repositories injected via constructor
- Centralized di WavesOfFoodApp

### 5. Clean Architecture

```
UI (Activity/Fragment)
     ↓
ViewModel (Business Logic)
     ↓
Repository (Data Source Abstraction)
     ↓
Firebase (Remote Data Source)
```

---

## ✅ Testing Checklist

Setelah migrasi, test semua flow:

- [ ] Register user baru
- [ ] Login dengan user existing
- [ ] Browse products (all & filtered)
- [ ] View product detail
- [ ] Add to cart
- [ ] View cart & update quantities
- [ ] Checkout & payment
- [ ] View order confirmation
- [ ] View profile
- [ ] Edit address
- [ ] View order history
- [ ] View order detail
- [ ] Sign out

---

## 🐛 Common Issues & Solutions

### Issue 1: "Unresolved reference: model"

**Solution:** Update import dari `com.komputerkit.wavesoffood.model.*` ke `com.komputerkit.wavesoffood.data.model.*`

### Issue 2: "Type mismatch: Map<String, Long> vs Map<String, Int>"

**Solution:** UserModel dan OrderModel sekarang menggunakan `Int` untuk quantity, bukan `Long`

### Issue 3: "Cannot create an instance of ViewModel"

**Solution:** Pastikan menggunakan ViewModelFactory:

```kotlin
by viewModels { (application as WavesOfFoodApp).viewModelFactory }
```

### Issue 4: "AuthState not found"

**Solution:** Import dari utils:

```kotlin
import com.komputerkit.wavesoffood.utils.AuthState
```

---

## 📚 Next Steps

1. **Rename New\*ViewModel files** - Remove "New" prefix
2. **Update all Activities** - Add ViewModelFactory
3. **Update all Adapters** - Fix imports
4. **Delete old ViewModel files** - After testing
5. **Delete old model files** - From old `model/` package
6. **Test thoroughly** - Ensure everything works

---

## 📖 Documentation

Lihat dokumentasi lengkap di:

- `README.md` - Project overview
- `FIREBASE_SETUP.md` - Firebase configuration
- `DOCUMENTATION.md` - Technical documentation

---

**Status:** Repository layer & ViewModels created ✅  
**Next:** Update Activities dengan ViewModelFactory  
**Version:** 2.0.0 (MVVM + Repository Pattern)
