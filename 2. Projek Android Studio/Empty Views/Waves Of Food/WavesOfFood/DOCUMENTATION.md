# 📚 Dokumentasi Struktur Project - Waves of Food

## 📁 Struktur Folder

```
WavesOfFood/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/komputerkit/wavesoffood/
│   │   │   │   │
│   │   │   │   ├── 📦 model/              # Data Models
│   │   │   │   │   ├── ProductModel.kt    # Model untuk produk
│   │   │   │   │   ├── UserModel.kt       # Model untuk user
│   │   │   │   │   └── OrderModel.kt      # Model untuk order
│   │   │   │   │
│   │   │   │   ├── 🎨 view/               # UI Layer (Activities)
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── AuthActivity.kt      # Landing page untuk auth
│   │   │   │   │   │   ├── LoginActivity.kt     # Login screen
│   │   │   │   │   │   └── RegisterActivity.kt  # Register screen
│   │   │   │   │   │
│   │   │   │   │   ├── splash/
│   │   │   │   │   │   └── SplashActivity.kt    # Splash screen
│   │   │   │   │   │
│   │   │   │   │   ├── product/
│   │   │   │   │   │   └── ProductDetailActivity.kt  # Detail produk
│   │   │   │   │   │
│   │   │   │   │   └── cart/
│   │   │   │   │       └── CartActivity.kt       # Shopping cart
│   │   │   │   │
│   │   │   │   ├── 🧠 viewmodel/          # Business Logic Layer
│   │   │   │   │   ├── AuthViewModel.kt         # Logic untuk auth
│   │   │   │   │   ├── HomeViewModel.kt         # Logic untuk home/products
│   │   │   │   │   ├── ProductDetailViewModel.kt # Logic untuk detail
│   │   │   │   │   └── CartViewModel.kt         # Logic untuk cart
│   │   │   │   │
│   │   │   │   ├── 🔄 adapter/            # RecyclerView Adapters
│   │   │   │   │   ├── ProductAdapter.kt        # Adapter untuk list produk
│   │   │   │   │   └── CartAdapter.kt           # Adapter untuk cart items
│   │   │   │   │
│   │   │   │   └── MainActivity.kt              # Home screen
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/                # XML Layouts
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_auth.xml
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── activity_register.xml
│   │   │   │   │   ├── activity_splash.xml
│   │   │   │   │   ├── activity_product_detail.xml
│   │   │   │   │   ├── activity_cart.xml
│   │   │   │   │   ├── item_product.xml          # Layout untuk product card
│   │   │   │   │   └── item_cart.xml             # Layout untuk cart item
│   │   │   │   │
│   │   │   │   ├── menu/
│   │   │   │   │   └── menu_main.xml             # Menu untuk logout
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   │
│   │   │   │   └── drawable/               # Icons & Images
│   │   │   │
│   │   │   ├── AndroidManifest.xml        # App configuration
│   │   │   └── google-services.json       # Firebase config (DO NOT COMMIT!)
│   │   │
│   │   └── build.gradle.kts               # Module dependencies
│   │
│   ├── build.gradle.kts                   # App build config
│   └── google-services.json               # ⚠️ JANGAN COMMIT FILE INI!
│
├── gradle/                                 # Gradle wrapper files
├── build.gradle.kts                        # Project build config
├── settings.gradle.kts                     # Project settings
├── README.md                               # Dokumentasi utama
├── FIREBASE_SETUP.md                       # Panduan setup Firebase
└── .gitignore                              # Git ignore rules
```

## 🏗️ Penjelasan Arsitektur MVVM

### 📦 Model (Data Layer)

Berisi data classes yang merepresentasikan struktur data aplikasi.

**ProductModel.kt**

- Merepresentasikan produk
- Fields: id, title, description, price, imageUrl, category

**UserModel.kt**

- Merepresentasikan user
- Fields: uid, email, name, address, cartItems

**OrderModel.kt**

- Merepresentasikan order
- Fields: id, userID, date, items, status, address

### 🎨 View (UI Layer)

Berisi semua Activities dan Fragments yang menampilkan UI.

**AuthActivity** → Landing page dengan tombol Login/Register
**LoginActivity** → Form login
**RegisterActivity** → Form registrasi
**SplashActivity** → Splash screen dengan auto-login check
**MainActivity** → Home screen dengan list produk
**ProductDetailActivity** → Detail produk
**CartActivity** → Shopping cart

### 🧠 ViewModel (Business Logic Layer)

Berisi business logic dan berkomunikasi dengan Firebase.

**AuthViewModel**

- `signUpWithEmail()` - Registrasi user baru
- `signInWithEmail()` - Login user
- `signOut()` - Logout
- `isUserLoggedIn()` - Check login status

**HomeViewModel**

- `fetchProducts()` - Ambil semua produk
- `fetchProductsByCategory()` - Filter produk by category

**ProductDetailViewModel**

- `fetchProductDetail()` - Ambil detail produk by ID

**CartViewModel**

- `addToCart()` - Tambah produk ke cart
- `removeFromCart()` - Kurangi/hapus produk dari cart
- `fetchCartItems()` - Ambil semua item di cart
- `getTotalPrice()` - Hitung total harga
- `clearCart()` - Kosongkan cart

### 🔄 Adapter (RecyclerView)

Berisi adapter untuk menampilkan data di RecyclerView.

**ProductAdapter**

- Menampilkan list produk dalam grid (2 kolom)
- Handle click pada produk dan tombol Add to Cart

**CartAdapter**

- Menampilkan list item di cart
- Handle increase/decrease quantity

## 🔄 Data Flow

### Login Flow

```
LoginActivity
    ↓ (user input email & password)
AuthViewModel.signInWithEmail()
    ↓ (Firebase Auth)
Firebase Authentication
    ↓ (success)
Navigate to MainActivity
```

### Register Flow

```
RegisterActivity
    ↓ (user input data)
AuthViewModel.signUpWithEmail()
    ↓ (create auth)
Firebase Authentication
    ↓ (create user doc)
Firestore users collection
    ↓ (success)
Navigate to MainActivity
```

### Product List Flow

```
MainActivity.onCreate()
    ↓
HomeViewModel.fetchProducts()
    ↓
Firestore products collection
    ↓ (LiveData update)
ProductAdapter.submitList()
    ↓
RecyclerView displays products
```

### Add to Cart Flow

```
User clicks "Add to Cart"
    ↓
CartViewModel.addToCart(productId)
    ↓
Firestore users/{userId}/cartItems
    ↓ (update field)
increment quantity or add new item
```

### Checkout Flow

```
CartActivity
    ↓ (display cart items)
User clicks "Checkout"
    ↓
Show confirmation dialog
    ↓ (confirm)
CartViewModel.clearCart()
    ↓
Firestore update cartItems = {}
    ↓
Show success message
```

## 🎯 Fitur yang Sudah Diimplementasikan

### ✅ Authentication

- [x] Splash screen dengan auto-login
- [x] Login dengan email/password
- [x] Register dengan validasi
- [x] Logout
- [x] User data di Firestore

### ✅ Product Management

- [x] Fetch products dari Firestore
- [x] Display products dalam grid layout
- [x] Filter by category (All, Food, Drink, Snack)
- [x] Product detail page
- [x] Image loading dengan Glide

### ✅ Shopping Cart

- [x] Add product to cart
- [x] Remove product from cart
- [x] Increase/decrease quantity
- [x] Real-time cart sync dengan Firestore
- [x] Calculate total price
- [x] Checkout (basic)
- [x] Empty state

## 🚀 Cara Menjalankan Aplikasi

### Prerequisites

1. Android Studio Arctic Fox atau lebih baru
2. JDK 11
3. Android SDK API 24+
4. Firebase project (ikuti FIREBASE_SETUP.md)

### Steps

1. Clone repository
2. Setup Firebase (lihat FIREBASE_SETUP.md)
3. Replace google-services.json
4. Sync Gradle
5. Run aplikasi

## 🔧 Cara Menambah Fitur Baru

### Menambah Screen Baru

1. **Buat Layout XML**

   ```xml
   <!-- res/layout/activity_new.xml -->
   ```

2. **Buat Activity**

   ```kotlin
   class NewActivity : AppCompatActivity() {
       private lateinit var binding: ActivityNewBinding

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           binding = ActivityNewBinding.inflate(layoutInflater)
           setContentView(binding.root)
       }
   }
   ```

3. **Tambah ke AndroidManifest.xml**
   ```xml
   <activity android:name=".view.NewActivity" />
   ```

### Menambah ViewModel Baru

```kotlin
class NewViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _data = MutableLiveData<DataType>()
    val data: LiveData<DataType> = _data

    fun fetchData() {
        firestore.collection("collection_name")
            .get()
            .addOnSuccessListener { result ->
                _data.value = result.toObjects(DataType::class.java)
            }
    }
}
```

## 📝 Best Practices

### 1. ViewBinding

Selalu gunakan ViewBinding untuk akses view:

```kotlin
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
}
```

### 2. LiveData Observer

Observe LiveData di Activity/Fragment:

```kotlin
viewModel.data.observe(this) { data ->
    // Update UI
}
```

### 3. Error Handling

Selalu handle error dari Firebase:

```kotlin
.addOnFailureListener { exception ->
    _error.value = exception.message ?: "Unknown error"
}
```

### 4. Loading State

Tampilkan loading indicator:

```kotlin
viewModel.isLoading.observe(this) { isLoading ->
    progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
}
```

## 🐛 Common Issues & Solutions

### Issue: Products tidak muncul

**Solution**:

- Check Firestore collection name: `products`
- Check Firestore security rules
- Check Logcat untuk error

### Issue: Add to Cart tidak bekerja

**Solution**:

- Check user sudah login
- Check field name di Firestore: `cartItems`
- Check Firestore security rules untuk users collection

### Issue: Image tidak load

**Solution**:

- Check internet permission di Manifest
- Check image URL valid
- Check Glide dependency

## 📞 Support

Jika ada pertanyaan atau issue:

1. Check documentation ini terlebih dahulu
2. Check FIREBASE_SETUP.md untuk Firebase issues
3. Check Logcat untuk error messages
4. Search di Stack Overflow

## 📄 License

MIT License - Free to use for learning purposes
