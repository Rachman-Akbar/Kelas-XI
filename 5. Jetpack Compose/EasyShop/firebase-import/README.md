# 🔥 EasyShop Firebase Data Import Tool

Tool untuk mengimpor data sampel ke Firebase Firestore untuk aplikasi EasyShop e-commerce.

## 📋 Prerequisites

1. **Node.js** (versi 16 atau lebih tinggi)
2. **Firebase Service Account Key** (`service-account-key.json`)
3. **Firebase Project** yang sudah dikonfigurasi

## 🚀 Setup

### 1. Install Dependencies

```bash
cd firebase-import
npm install
```

### 2. Add Service Account Key

- Letakkan file `service-account-key.json` di folder `firebase-import/`
- File ini berisi credentials untuk Firebase Admin SDK

### 3. Test Connection

```bash
npm run test
```

## 📦 Available Commands

### Import All Data (Recommended)

```bash
npm run import
# atau
node import-data.js --all
```

### Import Specific Data

```bash
# Import produk saja
npm run import-products

# Import kategori saja
npm run import-categories

# Import user documents saja
npm run import-users

# Test koneksi Firebase
npm run test
```

### Interactive Mode

```bash
node import-data.js
```

Akan menampilkan menu interaktif untuk memilih jenis import.

## 📊 Data yang Diimpor

### 📂 Categories (6 items)

- Electronics
- Fashion
- Food & Beverage
- Beauty
- Home & Living
- Sports

### 📦 Products (10 items)

- iPhone 15 Pro Max
- Samsung Galaxy S24 Ultra
- MacBook Air M3
- Nike Air Jordan 1
- Adidas Ultraboost 23
- Uniqlo Basic T-Shirt
- Kopi Arabica Premium
- Minyak Zaitun Extra Virgin
- Skincare Set Vitamin C
- Serum Hyaluronic Acid

### 👥 Users (3 sample documents)

- Sample user documents untuk testing
- **Note**: Ini hanya dokumen Firestore, bukan user Auth

## 🔧 Struktur File

```
firebase-import/
├── package.json              # Dependencies dan scripts
├── firebase-config.js        # Konfigurasi Firebase Admin
├── test-connection.js        # Test koneksi Firebase
├── import-data.js            # Main import script
├── import-products.js        # Import produk
├── import-categories.js      # Import kategori
├── import-users.js           # Import user documents
├── service-account-key.json  # Firebase credentials (ANDA TAMBAHKAN)
└── README.md                # Dokumentasi ini
```

## ✅ Verification

Setelah import berhasil, Anda dapat:

1. **Cek Firebase Console**:

   - Buka Firebase Console → Firestore Database
   - Verifikasi collections: `categories`, `products`, `users`

2. **Test di Android App**:
   - Jalankan aplikasi EasyShop
   - Lihat produk di halaman Home
   - Test fitur favorites, orders, dll.

## 🔒 Security Notes

- ⚠️ **JANGAN commit** file `service-account-key.json` ke repository
- File ini berisi credentials sensitif
- Tambahkan ke `.gitignore` jika perlu

## 📱 Firebase Collections

### Products Collection

```javascript
{
  name: string,
  description: string,
  price: number,
  imageUrl: string,
  category: string,
  rating: number,
  reviewCount: number,
  stock: number,
  isAvailable: boolean,
  createdAt: timestamp
}
```

### Categories Collection

```javascript
{
  name: string,
  description: string,
  imageUrl: string,
  productCount: number,
  isActive: boolean,
  createdAt: timestamp
}
```

### Users Collection

```javascript
{
  id: string,
  name: string,
  email: string,
  phone: string,
  address: string,
  favorites: array,
  createdAt: timestamp
}
```

## 🆘 Troubleshooting

### Error: "service-account-key.json not found"

- Pastikan file `service-account-key.json` ada di folder `firebase-import/`
- Check nama file (harus persis sama)

### Error: "Permission denied"

- Pastikan Service Account memiliki role `Firebase Admin`
- Check Firebase project settings

### Error: "Project not found"

- Pastikan project ID di `service-account-key.json` benar
- Check Firebase console untuk project ID

## 📞 Support

Jika ada masalah:

1. Check error message di console
2. Verify Firebase project settings
3. Ensure service account permissions
4. Test connection dengan `npm run test`

---

**Happy Importing! 🚀**
