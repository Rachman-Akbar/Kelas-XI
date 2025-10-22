# Admin Waves Of Food - Setup Guide

## ✅ FASE 1 COMPLETED: Authentication & Project Setup

### 📦 Dependencies Added

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// Lifecycle & ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
implementation("androidx.activity:activity-ktx:1.9.3")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

// Image Loading - Glide
implementation("com.github.bumptech.glide:glide:4.16.0")
```

### 📁 Project Structure Created

```
AdminWOF/
├── data/
│   ├── model/
│   │   ├── ProductModel.kt
│   │   ├── OrderModel.kt
│   │   └── UserModel.kt
│   └── repository/
│       ├── AuthRepository.kt
│       ├── FirebaseAuthRepository.kt (with admin validation)
│       ├── ProductRepository.kt (CRUD + Firebase Storage)
│       └── OrderRepository.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── ProductViewModel.kt
│   ├── OrderViewModel.kt
│   └── ViewModelFactory.kt
├── view/
│   ├── auth/
│   │   └── AdminLoginActivity.kt ✅
│   ├── main/
│   │   └── AdminMainActivity.kt (partially complete)
│   ├── product/
│   │   ├── ProductFormActivity.kt (TODO)
│   │   └── ProductListActivity.kt (TODO)
│   └── order/
│       ├── OrdersManagementActivity.kt (TODO)
│       └── OrderDetailActivity.kt (TODO)
├── adapter/
│   ├── ProductAdapter.kt (TODO)
│   └── OrderAdapter.kt (TODO)
├── utils/
│   ├── Constants.kt ✅
│   ├── UiState.kt ✅
│   └── Extensions.kt ✅
└── AdminWOFApp.kt ✅
```

### 🔐 Admin Authentication

**Admin Emails (Hardcoded in Constants.kt):**

```kotlin
val ADMIN_EMAILS = setOf(
    "admin@wavesoffood.com",
    "admin@komputerkit.com"
)
```

**Features:**

- ✅ Admin email validation before login
- ✅ Firebase Authentication integration
- ✅ MVVM architecture
- ✅ Error handling with sealed classes
- ✅ ViewModelFactory for DI

### 📋 What's Completed

#### ✅ Data Layer:

1. **Models:** ProductModel, OrderModel, UserModel
2. **Repositories:**
   - AuthRepository (admin validation)
   - ProductRepository (CRUD + image upload)
   - OrderRepository (fetch & update status)

#### ✅ ViewModel Layer:

1. AuthViewModel (login with admin check)
2. ProductViewModel (CRUD operations)
3. OrderViewModel (fetch orders, update status)
4. ViewModelFactory (DI)

#### ✅ Utils:

1. Constants (collections, categories, statuses)
2. UiState sealed classes
3. Extension functions (toRupiah, toFormattedDate, etc.)

#### ✅ Views:

1. AdminLoginActivity (complete with layout)
2. AdminMainActivity (layout created)

### 📝 Next Steps - TODO

#### 🔧 Implement Product Management (FASE 2):

**Files to Create:**

1. **ProductAdapter.kt** - RecyclerView adapter untuk product list
2. **ProductFormActivity.kt** - Form untuk Create/Update product
   - Layout: `activity_product_form.xml`
   - Features:
     - Upload image dari galeri
     - Input fields: title, description, price, category
     - Save/Update logic
     - Delete button (update mode only)
3. **AdminMainActivity.kt** (complete implementation)
   - Setup RecyclerView dengan ProductAdapter
   - Navigate to ProductFormActivity
   - Handle FAB click for new product

**Product Form Fields:**

```xml
- ImageView (with picker button)
- EditText: Product Name
- EditText: Description (multiline)
- EditText: Price (number decimal)
- Spinner: Category
- Button: Save/Update
- Button: Delete (update mode only)
```

#### 🔧 Implement Order Management (FASE 3):

**Files to Create:**

1. **OrderAdapter.kt** - RecyclerView adapter untuk order list
2. **item_order_admin.xml** - Layout untuk order list item
3. **OrdersManagementActivity.kt**
   - Display all orders (all users)
   - Show: OrderID, Date, UserID, Status, Total items
4. **OrderDetailActivity.kt**
   - Show full order details
   - Display user info
   - Show order items with products
   - **Spinner/RadioGroup** untuk update status:
     - Ordered
     - Processing
     - Shipped
     - Delivered
     - Cancelled
5. **OrderItemAdapter.kt** - Adapter untuk items dalam order
6. **Layouts:**
   - `activity_orders_management.xml`
   - `activity_order_detail.xml`
   - `item_order_admin.xml`
   - `item_order_item_detail.xml`

### 🚀 How to Continue Development

#### Step 1: Copy google-services.json

```powershell
Copy-Item "c:\Akbar\Waves Of Food\WavesOfFood\app\google-services.json" `
    -Destination "c:\Akbar\Waves Of Food\AdminWOF\app\google-services.json"
```

#### Step 2: Update AndroidManifest.xml

```xml
<application
    android:name=".AdminWOFApp"
    ...>

    <activity
        android:name=".view.auth.AdminLoginActivity"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>

    <activity
        android:name=".view.main.AdminMainActivity"
        android:exported="false" />

    <!-- Add other activities as you create them -->
</application>
```

#### Step 3: Build Project

```powershell
cd "c:\Akbar\Waves Of Food\AdminWOF"
.\gradlew.bat assembleDebug
```

### 📱 Admin Login Credentials

**To test admin login, create these users in Firebase Console:**

1. Email: `admin@wavesoffood.com`
   Password: (your choice, min 6 chars)
2. Email: `admin@komputerkit.com`
   Password: (your choice, min 6 chars)

### 🎨 UI Features

**AdminLoginActivity:**

- Material Design components
- Email & password validation
- Admin access restriction
- Loading indicator
- Error messages

**AdminMainActivity:**

- Toolbar with title
- Quick action buttons (Manage Orders, Sign Out)
- Product list with RecyclerView
- FAB for adding new product
- Empty state message

### 🔥 Firebase Collections Structure

**Products Collection:**

```javascript
products/
  {productId}/
    - title: String
    - description: String
    - price: Double
    - category: String
    - imageUrl: String
```

**Orders Collection:**

```javascript
orders/
  {orderId}/
    - userID: String
    - date: Timestamp
    - items: Map<String, Int> // productId -> quantity
    - status: String
    - address: String
```

**Firebase Storage:**

```
products/
  - product_{uuid}.jpg
```

### ⚙️ Key Repository Methods

**ProductRepository:**

- `fetchAllProducts()` - Get all products
- `fetchProductById(id)` - Get single product
- `uploadProductImage(uri)` - Upload to Storage
- `createProduct(product)` - Add new product
- `updateProduct(id, product)` - Update existing
- `deleteProduct(id)` - Delete product & image

**OrderRepository:**

- `fetchAllOrders()` - Get all orders
- `fetchOrderById(id)` - Get single order
- `fetchUserById(id)` - Get user info
- `updateOrderStatus(id, status)` - Update order status

### 📊 Order Status Flow

```
Ordered → Processing → Shipped → Delivered
                    ↓
                Cancelled
```

### 🎯 Implementation Priority

**High Priority (Complete FASE 2):**

1. ✅ ProductAdapter
2. ✅ ProductFormActivity (Create/Edit)
3. ✅ Image picker implementation
4. ✅ Complete AdminMainActivity

**Medium Priority (FASE 3):**

1. ✅ OrderAdapter
2. ✅ OrdersManagementActivity
3. ✅ OrderDetailActivity
4. ✅ Status update UI (Spinner/RadioGroup)

**Nice to Have:**

- Search functionality
- Filter by category/status
- Statistics dashboard
- Date range filtering
- Export orders to CSV

### 💡 Tips & Notes

1. **Admin Validation:** Only emails in `Constants.ADMIN_EMAILS` can login
2. **Image Upload:** Use Glide for image loading
3. **Error Handling:** All repository methods return `Result<T>`
4. **Coroutines:** All Firebase operations use `suspend` functions
5. **ViewModelScope:** Lifecycle-aware coroutine scope
6. **LiveData:** Observe data changes in Activities

### 🐛 Common Issues

**Issue:** Build error with google-services.json
**Solution:** Copy file from WavesOfFood project

**Issue:** Admin can't login
**Solution:** Check email is in ADMIN_EMAILS set (lowercase)

**Issue:** Image upload fails
**Solution:** Check Firebase Storage rules and permissions

### 📚 Resources

- [Firebase Android Documentation](https://firebase.google.com/docs/android/setup)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Material Components](https://material.io/develop/android)

---

## 🎉 Current Status: FASE 1 COMPLETE ✅

**Ready for:**

- Product CRUD implementation
- Order Management implementation
- Testing with Firebase

**Architecture:** Clean MVVM with Repository Pattern
**State Management:** LiveData + Sealed Classes
**Async:** Kotlin Coroutines
**DI:** Custom ViewModelFactory
