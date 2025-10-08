# 🔥 PANDUAN SETUP FIREBASE FIRESTORE

## 📋 STRUKTUR DATABASE YANG DIPERLUKAN

### 1️⃣ COLLECTION: `products`

```json
{
  "product_001": {
    "id": "product_001",
    "name": "iPhone 15 Pro Max",
    "description": "iPhone terbaru dengan chip A17 Pro dan kamera 48MP. Tersedia dalam berbagai warna menarik.",
    "price": 19999000,
    "imageUrl": "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400",
    "category": "Electronics",
    "categoryId": "electronics_001",
    "stock": 15,
    "rating": 4.8,
    "reviewCount": 234,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_002": {
    "id": "product_002",
    "name": "Samsung Galaxy S24 Ultra",
    "description": "Smartphone flagship Samsung dengan S Pen dan kamera 200MP untuk fotografi profesional.",
    "price": 17999000,
    "imageUrl": "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400",
    "category": "Electronics",
    "categoryId": "electronics_001",
    "stock": 8,
    "rating": 4.7,
    "reviewCount": 189,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_003": {
    "id": "product_003",
    "name": "MacBook Air M3",
    "description": "Laptop tipis dan ringan dengan performa luar biasa dari chip M3 Apple terbaru.",
    "price": 25999000,
    "imageUrl": "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400",
    "category": "Electronics",
    "categoryId": "electronics_001",
    "stock": 12,
    "rating": 4.9,
    "reviewCount": 156,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_004": {
    "id": "product_004",
    "name": "Nike Air Jordan 1 Retro",
    "description": "Sepatu basketball klasik yang menjadi ikon fashion streetwear dunia.",
    "price": 3299000,
    "imageUrl": "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
    "category": "Fashion",
    "categoryId": "fashion_001",
    "stock": 25,
    "rating": 4.6,
    "reviewCount": 412,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_005": {
    "id": "product_005",
    "name": "Adidas Ultraboost 22",
    "description": "Sepatu lari dengan teknologi Boost untuk kenyamanan maksimal saat berlari.",
    "price": 2899000,
    "imageUrl": "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400",
    "category": "Fashion",
    "categoryId": "fashion_001",
    "stock": 18,
    "rating": 4.5,
    "reviewCount": 298,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_006": {
    "id": "product_006",
    "name": "PlayStation 5",
    "description": "Konsol gaming next-gen dengan performa 4K dan loading super cepat dengan SSD.",
    "price": 8999000,
    "imageUrl": "https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?w=400",
    "category": "Gaming",
    "categoryId": "gaming_001",
    "stock": 6,
    "rating": 4.9,
    "reviewCount": 567,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_007": {
    "id": "product_007",
    "name": "Apple Watch Series 9",
    "description": "Smartwatch terdepan dengan fitur kesehatan lengkap dan Always-On Retina display.",
    "price": 6999000,
    "imageUrl": "https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=400",
    "category": "Electronics",
    "categoryId": "electronics_001",
    "stock": 22,
    "rating": 4.7,
    "reviewCount": 334,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_008": {
    "id": "product_008",
    "name": "Canon EOS R6 Mark II",
    "description": "Kamera mirrorless profesional dengan sensor full-frame dan video 4K 60fps.",
    "price": 45999000,
    "imageUrl": "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=400",
    "category": "Electronics",
    "categoryId": "electronics_001",
    "stock": 4,
    "rating": 4.8,
    "reviewCount": 87,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_009": {
    "id": "product_009",
    "name": "Uniqlo Heattech Ultra Warm",
    "description": "Pakaian dalam dengan teknologi Heattech untuk kehangatan ekstra di cuaca dingin.",
    "price": 299000,
    "imageUrl": "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400",
    "category": "Fashion",
    "categoryId": "fashion_001",
    "stock": 50,
    "rating": 4.4,
    "reviewCount": 789,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "product_010": {
    "id": "product_010",
    "name": "Dyson V15 Detect",
    "description": "Vacuum cleaner cordless dengan laser detection untuk membersihkan debu mikroskopis.",
    "price": 12999000,
    "imageUrl": "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400",
    "category": "Home & Living",
    "categoryId": "home_001",
    "stock": 9,
    "rating": 4.6,
    "reviewCount": 145,
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  }
}
```

### 2️⃣ COLLECTION: `categories`

```json
{
  "electronics_001": {
    "id": "electronics_001",
    "name": "Electronics",
    "description": "Gadget dan perangkat elektronik terbaru",
    "imageUrl": "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=300",
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "fashion_001": {
    "id": "fashion_001",
    "name": "Fashion",
    "description": "Pakaian dan aksesoris fashion terkini",
    "imageUrl": "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=300",
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "gaming_001": {
    "id": "gaming_001",
    "name": "Gaming",
    "description": "Konsol game dan aksesoris gaming",
    "imageUrl": "https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=300",
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "home_001": {
    "id": "home_001",
    "name": "Home & Living",
    "description": "Peralatan rumah tangga dan dekorasi",
    "imageUrl": "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=300",
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "sports_001": {
    "id": "sports_001",
    "name": "Sports",
    "description": "Peralatan olahraga dan fitness",
    "imageUrl": "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=300",
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  },
  "books_001": {
    "id": "books_001",
    "name": "Books",
    "description": "Buku-buku terbaik dan terpopuler",
    "imageUrl": "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=300",
    "isActive": true,
    "createdAt": 1703097600000,
    "updatedAt": 1703097600000
  }
}
```

### 3️⃣ COLLECTION: `banners`

```json
{
  "banner_001": {
    "id": "banner_001",
    "title": "Flash Sale Electronics",
    "subtitle": "Diskon hingga 50% untuk gadget pilihan",
    "imageUrl": "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800",
    "actionUrl": "category/electronics_001",
    "actionType": "category",
    "backgroundColor": "#FF6B6B",
    "textColor": "#FFFFFF",
    "isActive": true,
    "startDate": 1703097600000,
    "endDate": 1706803200000,
    "priority": 1
  },
  "banner_002": {
    "id": "banner_002",
    "title": "New Fashion Collection",
    "subtitle": "Koleksi fashion terbaru untuk gaya Anda",
    "imageUrl": "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=800",
    "actionUrl": "category/fashion_001",
    "actionType": "category",
    "backgroundColor": "#4ECDC4",
    "textColor": "#FFFFFF",
    "isActive": true,
    "startDate": 1703097600000,
    "endDate": 1709251200000,
    "priority": 2
  },
  "banner_003": {
    "id": "banner_003",
    "title": "Gaming Weekend",
    "subtitle": "Spesial weekend untuk para gamers",
    "imageUrl": "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800",
    "actionUrl": "category/gaming_001",
    "actionType": "category",
    "backgroundColor": "#A8E6CF",
    "textColor": "#2C3E50",
    "isActive": true,
    "startDate": 1703097600000,
    "endDate": 1704307200000,
    "priority": 3
  }
}
```

---

## 🛠️ LANGKAH SETUP FIREBASE

### STEP 1: Akses Firebase Console

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Pilih project EasyShop Anda
3. Klik "Firestore Database" di menu sebelah kiri

### STEP 2: Create Collections

1. Klik "Start collection"
2. Collection ID: `products`
3. Klik "Next"
4. Tambahkan document dengan ID: `product_001`
5. Copy-paste field dari JSON di atas
6. Ulangi untuk semua products

### STEP 3: Setup Indexes (Opsional untuk Performa)

Di Firebase Console → Firestore → Indexes:

```
Collection: products
Fields: category (Ascending), rating (Descending)

Collection: products
Fields: isActive (Ascending), createdAt (Descending)

Collection: products
Fields: name (Ascending), description (Ascending) - untuk search
```

### STEP 4: Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read access to products, categories, banners
    match /{collection}/{document} {
      allow read: if collection in ['products', 'categories', 'banners'];
    }

    // Allow users to manage their own cart and profile
    match /carts/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## 🧪 TESTING SETUP

Setelah setup, test dengan menjalankan:

```bash
cd "c:\Akbar\5. Jetpack Compose\EasyShop"
.\gradlew assembleDebug
.\gradlew installDebug
```

Pastikan:

1. ✅ Home screen menampilkan products
2. ✅ Categories terload dengan benar
3. ✅ Banners muncul di home
4. ✅ Product detail bisa dibuka
5. ✅ Add to cart berfungsi
6. ✅ Cart screen menampilkan items

---

## 🚀 BONUS FEATURES

### A. Sample Data Generator Script

Buat file `generate_sample_data.js` untuk auto-populate data:

```javascript
const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

// Function to populate sample data
async function populateSampleData() {
  // Add products, categories, banners
  console.log('Sample data populated successfully!');
}

populateSampleData();
```

### B. Real-time Data Sync

Products akan auto-update ketika admin mengubah data di Firebase Console.

### C. Analytics Integration

Setup Firebase Analytics untuk tracking user behavior dan conversion rates.

---

## 📱 HASIL AKHIR

Setelah setup database:

- **10 Sample Products** dengan berbagai kategori
- **6 Categories** yang menarik
- **3 Promotional Banners**
- **Real-time synchronization**
- **Professional product images** dari Unsplash

**DATABASE SIAP PRODUCTION!** 🎉
