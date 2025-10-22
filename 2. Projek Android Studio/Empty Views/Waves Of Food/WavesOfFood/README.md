# Waves of Food - E-Commerce Android App

Aplikasi e-commerce Android lengkap yang dibangun menggunakan Kotlin, Firebase, dan arsitektur MVVM (Model-View-ViewModel).

## 🚀 Fitur Utama

### ✅ FASE 1: Otentikasi (Completed)

- **Firebase Authentication** - Login & Register dengan email/password
- **Splash Screen** - Auto-login untuk pengguna yang sudah masuk
- **User Management** - Data pengguna disimpan di Firestore

### ✅ FASE 2: Tampilan Produk (Completed)

- **Daftar Produk** - Grid layout dengan 2 kolom
- **Filter Kategori** - Filter produk berdasarkan kategori (All, Food, Drink, Snack)
- **Detail Produk** - Tampilan lengkap informasi produk
- **Image Loading** - Glide untuk memuat gambar produk

### ✅ FASE 3: Keranjang Belanja (Completed)

- **Add to Cart** - Tambah produk ke keranjang
- **Manage Quantity** - Increase/decrease jumlah item
- **Cart Summary** - Total harga otomatis terhitung
- **Checkout** - Proses checkout sederhana

### ✅ FASE 4: Checkout & Pembayaran (Completed)

- **Checkout Flow** - Review order dengan detail lengkap
- **Address Management** - Input dan validasi alamat pengiriman
- **Tax Calculation** - Perhitungan pajak 10% otomatis
- **Payment Simulation** - Mock payment dengan delay 3 detik
- **Order Creation** - Pembuatan order dengan UUID dan Timestamp
- **Order Confirmation** - Screen konfirmasi dengan Order ID

### ✅ FASE 5: Profil & Riwayat Pesanan (Completed)

- **User Profile** - Tampilan profil user (nama, email, alamat)
- **Edit Address** - Update alamat pengiriman
- **Order History** - Daftar semua pesanan user dengan status
- **Order Detail** - Detail lengkap pesanan dengan breakdown produk
- **Sign Out** - Logout dengan konfirmasi dialog
- **Profile Menu** - Akses profile dari MainActivity menu

## 🏗️ Arsitektur

Aplikasi ini menggunakan **MVVM (Model-View-ViewModel)** architecture pattern:

```
app/
├── model/              # Data classes
│   ├── ProductModel
│   ├── UserModel
│   └── OrderModel
├── view/               # Activities & UI
│   ├── auth/          # Login, Register, Auth
│   ├── splash/        # Splash screen
│   ├── product/       # Product details
│   ├── cart/          # Shopping cart
│   ├── checkout/      # Checkout, Order Confirmation
│   ├── profile/       # User profile
│   └── orders/        # Order history, Order detail
├── viewmodel/          # Business logic
│   ├── AuthViewModel
│   ├── HomeViewModel
│   ├── ProductDetailViewModel
│   ├── CartViewModel
│   ├── CheckoutViewModel
│   ├── ProfileViewModel
│   ├── OrdersViewModel
│   └── OrderDetailViewModel
└── adapter/            # RecyclerView adapters
    ├── ProductAdapter
    ├── CartAdapter
    ├── OrderAdapter
    └── OrderDetailAdapter
```

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI**: XML with Material Design 3
- **Backend**: Firebase
  - Firebase Authentication
  - Cloud Firestore
  - Firebase Storage
- **Architecture**: MVVM
- **Image Loading**: Glide
- **View Binding**: Enabled
- **Networking**: Retrofit (untuk future API calls)

## 📦 Dependencies

```gradle
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// Glide
implementation("com.github.bumptech.glide:glide:4.16.0")

// Lifecycle & ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
implementation("androidx.fragment:fragment-ktx:1.8.5")
```

## 🔧 Setup & Installation

### 1. Clone Repository

```bash
git clone <repository-url>
cd WavesOfFood
```

### 2. Firebase Configuration

**PENTING**: Anda harus mengkonfigurasi Firebase untuk aplikasi ini!

1. Buat project baru di [Firebase Console](https://console.firebase.google.com/)
2. Tambahkan aplikasi Android dengan package name: `com.komputerkit.wavesoffood`
3. Download file `google-services.json`
4. Replace file `app/google-services.json` dengan file yang Anda download
5. Enable Firebase Authentication (Email/Password)
6. Buat Firestore Database dengan struktur berikut:

#### Firestore Structure:

**Collection: `users`**

```json
{
  "uid": "user_id",
  "email": "user@email.com",
  "name": "User Name",
  "address": "User Address",
  "cartItems": {
    "product_id_1": 2,
    "product_id_2": 1
  }
}
```

**Collection: `products`**

```json
{
  "id": "auto_generated",
  "title": "Nasi Goreng",
  "description": "Nasi goreng spesial dengan bumbu rahasia",
  "price": 25000.0,
  "imageUrl": "https://example.com/image.jpg",
  "category": "Food"
}
```

**Collection: `orders`**

```json
{
  "id": "uuid_order_id",
  "userID": "user_uid",
  "date": "Timestamp(2024, 1, 15, 10, 30)",
  "items": {
    "product_id_1": 2,
    "product_id_2": 1
  },
  "status": "Pending",
  "address": "Jl. Pengiriman No. 123, Jakarta"
}
```

### 3. Add Sample Products

Gunakan Firebase Console untuk menambahkan beberapa produk sample:

```json
// Product 1
{
  "title": "Nasi Goreng Special",
  "description": "Nasi goreng dengan telur, ayam, dan sayuran segar",
  "price": 25000,
  "imageUrl": "URL_GAMBAR",
  "category": "Food"
}

// Product 2
{
  "title": "Es Teh Manis",
  "description": "Teh manis dingin yang menyegarkan",
  "price": 5000,
  "imageUrl": "URL_GAMBAR",
  "category": "Drink"
}

// Product 3
{
  "title": "Keripik Singkong",
  "description": "Keripik singkong renyah dengan berbagai rasa",
  "price": 15000,
  "imageUrl": "URL_GAMBAR",
  "category": "Snack"
}
```

### 4. Build & Run

1. Open project di Android Studio
2. Sync Gradle files
3. Run aplikasi di emulator atau device fisik

## 📱 Flow Aplikasi

1. **Splash Screen** → Check user login status
2. Jika belum login → **Auth Screen** → Login/Register
3. Jika sudah login → **Home Screen** (Product List)
4. Browse products dengan filter kategori (All, Food, Drink, Snack)
5. Klik produk → **Product Detail**
6. Add to Cart → **Cart Icon** (FAB)
7. Klik Cart → **Cart Activity** → Manage quantities
8. Checkout → **Checkout Activity** → Isi alamat
9. Pay Now → Loading 3 detik → **Order Confirmation**
10. View Orders History → **Orders Activity**
11. Klik order → **Order Detail** → Lihat breakdown produk
12. Menu Profile → **Profile Activity** → Edit address, Sign out

## 🎨 Features Detail

### Home Screen

- Grid layout dengan 2 kolom
- Category filter chips (All, Food, Drink, Snack)
- Floating Action Button untuk Cart
- Profile & Logout menu
- Empty state untuk produk kosong

### Product Detail

- Full screen product image
- Product name, category, price
- Description
- Add to Cart button
- Back button

### Shopping Cart

- List semua item di cart
- Increase/Decrease quantity per item
- Auto-calculate subtotal per item
- Total price di bottom
- Checkout button
- Empty state ketika cart kosong

### Checkout & Payment

- Order summary dengan breakdown items
- Subtotal, Tax (10%), dan Total calculation
- Input alamat pengiriman (required)
- Mock payment processing dengan loading 3 detik
- Order creation di Firestore dengan UUID
- Clear cart setelah order berhasil

### Order Confirmation

- Display Order ID (shortened untuk UI)
- Status chip menunjukkan status "Pending"
- Navigation ke Order History atau Home
- Prevent back navigation (order sudah dibuat)

### User Profile

- View nama, email (read-only)
- Edit alamat pengiriman
- Button "View My Orders" ke Order History
- Sign Out dengan confirmation dialog
- Auto-logout setelah sign out

### Order History

- List semua orders user (sorted by date desc)
- Show order ID, tanggal, jumlah items, status
- Status chip dengan warna berbeda per status
- Click untuk lihat detail order
- Empty state ketika belum ada order

### Order Detail

- Order information card (ID, date, status, address)
- List produk dalam order dengan:
  - Product image (Glide)
  - Product name
  - Price per unit
  - Quantity
  - Subtotal per item
- Total price calculation
- Back button ke Order History

## 🔐 Security Notes

- Firebase Security Rules harus dikonfigurasi dengan benar
- Jangan commit file `google-services.json` ke public repository
- Gunakan environment variables untuk sensitive data

## 📝 Firestore Security Rules (Recommended)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Products collection
    match /products/{productId} {
      allow read: if true; // Public read
      allow write: if false; // Admin only
    }

    // Orders collection
    match /orders/{orderId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 🚀 Next Steps (Future Enhancements)

- [ ] Real Payment Gateway Integration (Midtrans, etc.)
- [ ] Admin Panel untuk manage products & update order status
- [ ] Product Search dengan SearchView
- [ ] Push Notifications untuk order updates
- [ ] Product Reviews & Ratings
- [ ] Wishlist/Favorites
- [ ] Multiple Payment Methods
- [ ] Order Tracking dengan timeline UI
- [ ] Promo Codes & Discounts
- [ ] Upload product images ke Firebase Storage
- [ ] Order status filters (Pending, Processing, Completed, Cancelled)
- [ ] Export order history ke PDF

## 👨‍💻 Developer

Dibuat dengan ❤️ menggunakan Kotlin & Firebase

## 📄 License

MIT License - Feel free to use this project for learning purposes.
