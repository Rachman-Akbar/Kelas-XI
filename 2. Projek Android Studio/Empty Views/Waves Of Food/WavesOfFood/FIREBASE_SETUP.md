# 🔥 Panduan Setup Firebase untuk Waves of Food

## Langkah 1: Buat Project Firebase

1. Kunjungi [Firebase Console](https://console.firebase.google.com/)
2. Klik **"Add project"** atau **"Tambah project"**
3. Masukkan nama project: `WavesOfFood` (atau nama lain sesuai keinginan)
4. Aktifkan Google Analytics (optional)
5. Klik **"Create project"**

## Langkah 2: Tambahkan Aplikasi Android

1. Di Firebase Console, klik icon Android atau **"Add app"** → **Android**
2. Masukkan informasi berikut:
   - **Android package name**: `com.komputerkit.wavesoffood`
   - **App nickname**: `Waves of Food` (optional)
   - **Debug signing certificate SHA-1**: (optional untuk development)
3. Klik **"Register app"**

## Langkah 3: Download google-services.json

1. Download file `google-services.json`
2. Copy file tersebut ke folder: `WavesOfFood/app/`
3. **PENTING**: Replace file `google-services.json` yang sudah ada dengan file baru ini
4. Klik **"Next"** → **"Continue to console"**

## Langkah 4: Enable Firebase Authentication

1. Di Firebase Console, pilih menu **"Authentication"** di sidebar
2. Klik tab **"Sign-in method"**
3. Enable **"Email/Password"**:
   - Klik pada "Email/Password"
   - Toggle **"Enable"**
   - Klik **"Save"**

## Langkah 5: Setup Cloud Firestore

1. Di Firebase Console, pilih menu **"Firestore Database"**
2. Klik **"Create database"**
3. Pilih mode:
   - **Test mode** (untuk development) - semua orang bisa read/write
   - **Production mode** (untuk production) - harus setup security rules
4. Pilih location/region terdekat (misalnya: `asia-southeast1`)
5. Klik **"Enable"**

## Langkah 6: Buat Collections dan Sample Data

### A. Collection: users

Akan dibuat otomatis saat user pertama kali register. Struktur:

```
users (collection)
  └── {userId} (document)
      ├── uid: "user_firebase_uid"
      ├── email: "user@email.com"
      ├── name: "Nama User"
      ├── address: "Alamat User"
      └── cartItems: {}
```

### B. Collection: products

1. Klik **"Start collection"**
2. Collection ID: `products`
3. Tambahkan document dengan data berikut:

⚠️ **PENTING**: Jangan tambahkan field `id` manual, gunakan Auto-generated Document ID

**Product 1: Nasi Goreng**

```
Document ID: (Auto-generated)

Fields:
- title (string): "Nasi Goreng Special"
- description (string): "Nasi goreng dengan telur, ayam, dan sayuran segar. Dibuat dengan bumbu rahasia yang membuat rasanya istimewa."
- price (number): 25000
- imageUrl (string): "https://images.unsplash.com/photo-1603133872878-684f208fb84b"
- category (string): "Food"
```

**Product 2: Mie Goreng**

```
Document ID: (Auto-generated)

Fields:
- title (string): "Mie Goreng Jawa"
- description (string): "Mie goreng khas Jawa dengan bumbu kecap manis dan sayuran"
- price (number): 20000
- imageUrl (string): "https://images.unsplash.com/photo-1585032226651-759b368d7246"
- category (string): "Food"
```

**Product 3: Es Teh Manis**

```
Document ID: (Auto-generated)

Fields:
- title (string): "Es Teh Manis"
- description (string): "Teh manis dingin yang menyegarkan, cocok untuk menemani makan"
- price (number): 5000
- imageUrl (string): "https://images.unsplash.com/photo-1556881286-fc6915169721"
- category (string): "Drink"
```

**Product 4: Jus Jeruk**

```
Document ID: (Auto-generated)

Fields:
- title (string): "Jus Jeruk Segar"
- description (string): "Jus jeruk murni tanpa gula tambahan, kaya vitamin C"
- price (number): 12000
- imageUrl (string): "https://images.unsplash.com/photo-1600271886742-f049cd451bba"
- category (string): "Drink"
```

**Product 5: Keripik Singkong**

```
Document ID: (Auto-generated)

Fields:
- title (string): "Keripik Singkong"
- description (string): "Keripik singkong renyah dengan berbagai pilihan rasa"
- price (number): 15000
- imageUrl (string): "https://images.unsplash.com/photo-1566478989037-eec170784d0b"
- category (string): "Snack"
```

**Product 6: Kue Cubit**

```
Document ID: (Auto-generated)

Fields:
- title (string): "Kue Cubit Rainbow"
- description (string): "Kue cubit lembut dengan topping coklat dan keju"
- price (number): 10000
- imageUrl (string): "https://images.unsplash.com/photo-1587241321921-91a834d82ffc"
- category (string): "Snack"
```

### C. Collection: orders

Collection ini akan dibuat otomatis saat user melakukan checkout pertama kali. Struktur:

```
orders (collection)
  └── {orderId} (UUID)
      ├── id: "same_as_document_id"
      ├── userID: "user_firebase_uid"
      ├── date: Timestamp(2024, 1, 15, 10, 30)
      ├── items: {
      │   "product_id_1": 2,
      │   "product_id_2": 1
      │ }
      ├── status: "Pending"
      └── address: "Jl. Pengiriman No. 123, Jakarta"
```

**Status Types:**

- `Pending` - Order baru dibuat
- `Processing` - Order sedang diproses
- `Completed` - Order selesai/delivered
- `Cancelled` - Order dibatalkan

## Langkah 7: Setup Firestore Security Rules (PENTING!)

1. Di Firestore, klik tab **"Rules"**
2. Replace rules dengan kode berikut:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Users collection - hanya owner yang bisa read/write
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Products collection - semua orang bisa read, hanya admin yang bisa write
    match /products/{productId} {
      allow read: if true;
      allow write: if false; // Hanya admin yang bisa tambah/edit produk
    }

    // Orders collection - user hanya bisa read orders mereka sendiri, create order baru
    match /orders/{orderId} {
      allow read: if request.auth != null && resource.data.userID == request.auth.uid;
      allow create: if request.auth != null && request.resource.data.userID == request.auth.uid;
      allow update, delete: if false; // Orders tidak bisa diupdate/delete oleh user
    }
  }
}
```

3. Klik **"Publish"**

## Langkah 8: Setup Firebase Storage (Optional)

Jika ingin upload gambar produk dari aplikasi:

1. Di Firebase Console, pilih menu **"Storage"**
2. Klik **"Get started"**
3. Pilih mode security rules:
   - Start in **test mode** untuk development
   - Start in **production mode** untuk production
4. Pilih location yang sama dengan Firestore
5. Klik **"Done"**

## Langkah 9: Verifikasi Setup

### Test Authentication:

1. Run aplikasi di emulator/device
2. Register dengan email dan password baru
3. Cek di Firebase Console → Authentication → Users
4. User baru harus muncul di daftar

### Test Firestore:

1. Login ke aplikasi
2. Lihat daftar produk di Home screen
3. Produk yang ditambahkan di Firestore harus muncul

### Test Cart:

1. Add produk ke cart
2. Cek di Firestore Console → users → {userId}
3. Field `cartItems` harus ter-update

### Test Checkout & Orders:

1. Add beberapa produk ke cart
2. Klik Checkout, isi alamat pengiriman
3. Klik "Pay Now" dan tunggu 3 detik
4. Cek di Firestore Console → orders
5. Order baru harus muncul dengan status "Pending"
6. Cek field `items` berisi product IDs dan quantities
7. Cart user harus kosong setelah checkout

### Test Profile & Order History:

1. Klik menu Profile di MainActivity
2. Edit alamat dan klik Save
3. Cek di Firestore Console → users → address ter-update
4. Klik "View My Orders"
5. Semua orders user harus muncul
6. Klik salah satu order untuk lihat detail
7. Produk breakdown harus muncul dengan gambar dan harga

## 🎉 Selesai!

Firebase sudah siap digunakan. Sekarang Anda bisa:

- ✅ Register/Login user baru
- ✅ Melihat daftar produk dengan filter kategori
- ✅ View product details
- ✅ Menambahkan produk ke cart
- ✅ Manage cart quantities
- ✅ Checkout dengan alamat pengiriman
- ✅ Mock payment processing
- ✅ View order confirmation
- ✅ View & edit user profile
- ✅ View order history
- ✅ View detailed order breakdown
- ✅ Sign out

## 📝 Tips & Catatan

### Menambah Produk Lebih Banyak

- Gunakan Firestore Console untuk menambah produk manual
- Atau buat admin panel untuk manage produk dari aplikasi

### Menggunakan Gambar Sendiri

- Upload gambar ke Firebase Storage
- Atau gunakan URL gambar dari internet (Unsplash, Pexels, dll)

### Test Account

Untuk testing, buat beberapa user account:

- Email: `test@wavesoffood.com`
- Password: `test123` (min. 6 karakter)

### Troubleshooting

**Error: Default FirebaseApp is not initialized**

- Pastikan file `google-services.json` sudah di folder `app/`
- Sync Gradle files

**Error: Network error**

- Cek koneksi internet
- Pastikan emulator/device bisa akses internet

**Products tidak muncul**

- Cek Firestore Console, pastikan collection `products` ada
- Cek Logcat untuk error messages

**Login failed**

- Pastikan Email/Password authentication sudah enabled
- Cek format email valid
- Password minimal 6 karakter

## 🔒 Security Best Practices

1. **Jangan share google-services.json** di public repository
2. **Update Security Rules** sebelum production
3. **Enable App Check** untuk production
4. **Setup billing alerts** untuk monitor usage
5. **Regular backup** data Firestore

## 📞 Need Help?

- [Firebase Documentation](https://firebase.google.com/docs)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/firebase)
- [Firebase Support](https://firebase.google.com/support)
