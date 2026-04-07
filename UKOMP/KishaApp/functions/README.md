# Firebase Cloud Functions - KishaApp Marketplace

Backend di Node.js untuk KishaApp Marketplace menggunakan Firebase Cloud Functions.

## Setup

### Prerequisites

- Node.js 18+ LTS
- npm atau yarn
- Firebase CLI: `npm install -g firebase-tools`

### Installation

```bash
cd functions
npm install
```

## Import Data ke Firestore (Node.js)

Script import tersedia di `scripts/import-firestore-data.js`.

### 1) Siapkan kredensial Firebase Admin

- Opsi A: set `GOOGLE_APPLICATION_CREDENTIALS` ke file service account JSON.
- Opsi B: set `FIREBASE_SERVICE_ACCOUNT_JSON` (isi JSON credential dalam 1 env var) dan optional `FIREBASE_PROJECT_ID`.

### 2) Jalankan import data seed default

```bash
cd functions
npm run import:data
```

File default seed: `seeds/products.json` (berisi collection `products` dan `categories`).

### 3) Overwrite data (opsional)

```bash
npm run import:data:overwrite
```

### 4) Import file custom

```bash
node scripts/import-firestore-data.js --file ./seeds/nama-file.json --mode merge
```

Format JSON yang didukung:

```json
{
  "products": [{ "title": "...", "price": 1000 }],
  "categories": [{ "name": "Sayur" }]
}
```

atau array langsung (otomatis ke collection `products`):

```json
[{ "title": "Beras Organik", "price": 12000 }]
```

### Build

```bash
npm run build
```

## Deployment

### Deploy ke Firebase

```bash
firebase deploy --only functions
```

### Deploy dengan emulator local

```bash
firebase emulators:start --only functions
```

## Available Cloud Functions

### 1. User Functions

#### `validateUserProfile(name, email, role)`

Validasi dan update user profile di server

- **Input**: name, email, role
- **Auth**: Required (UID dari Firebase Auth)
- **Output**: success, message, uid

```kotlin
val result = cloudFunctionsRepository.validateUserProfile(
    name = "John Doe",
    email = "john@example.com",
    role = "customer"
)
```

---

### 2. Product Functions

#### `validateProduct(name, description, price, category, stock)`

Validasi produk sebelum create (server-side)

- **Input**: name, description, price, category, stock
- **Auth**: Required (must be seller or admin)
- **Output**: success, message, isValid

```kotlin
val result = cloudFunctionsRepository.validateProduct(
    name = "Laptop",
    description = "Gaming laptop dengan specs tinggi",
    price = 10000000.0,
    category = "Elektronik",
    stock = 5
)
```

#### `createProduct(name, description, price, category, stock, imageUrl)`

Buat produk baru dengan automatic seller assignment

- **Input**: name, description, price, category, stock, imageUrl
- **Auth**: Required (must be seller or admin)
- **Output**: success, message, productId, product object

```kotlin
val result = cloudFunctionsRepository.createProduct(
    name = "Laptop",
    description = "Gaming laptop dengan specs tinggi",
    price = 10000000.0,
    category = "Elektronik",
    stock = 5,
    imageUrl = "https://..."
)
```

---

### 3. Order Functions

#### `createOrder(productId, quantity, totalPrice)`

Buat order baru dengan validasi inventory

- **Input**: productId, quantity, totalPrice
- **Auth**: Required (buyer)
- **Output**: success, message, orderId, order object
- **Validation**:
  - Check product exists
  - Check stock availability
  - Automatic stock deduction

```kotlin
val result = cloudFunctionsRepository.createOrder(
    productId = "prod_123",
    quantity = 2,
    totalPrice = 50000.0
)
```

---

### 4. Admin Functions

#### `getStatistics()`

Dapatkan marketplace statistics

- **Input**: None
- **Auth**: Required (must be admin)
- **Output**: success, statistics (totalUsers, totalProducts, totalOrders, totalRevenue)

```kotlin
val result = cloudFunctionsRepository.getStatistics()
```

---

### 5. Trigger Functions

#### `onOrderCompleted` (Firestore Trigger)

Trigger ketika order status berubah menjadi "completed"

- **Trigger**: Firestore document update pada `orders/{orderId}`
- **Action**: Update product salesCount
- **No manual call needed** - runs automatically

#### `onAuthUserDeleted` (Auth Trigger)

Trigger ketika akun Firebase Authentication dihapus.

- **Trigger**: Firebase Auth user delete
- **Action**: Hapus dokumen `users/{uid}` di Firestore
- **No manual call needed** - runs automatically

---

## Usage di Android App

### 1. Inject CloudFunctionsRepository

```kotlin
class SellerViewModel(
    private val cloudFunctionsRepo: CloudFunctionsRepository = CloudFunctionsRepository()
) : ViewModel() {

    fun createProduct(name: String, description: String, price: Double) {
        viewModelScope.launch {
            cloudFunctionsRepo.createProduct(
                name = name,
                description = description,
                price = price,
                category = "Umum",
                stock = 10
            )
                .onSuccess { result ->
                    Log.d("SellerVM", "Product created: ${result["productId"]}")
                }
                .onFailure { error ->
                    Log.e("SellerVM", "Error: ${error.message}")
                }
        }
    }
}
```

### 2. Error Handling

Cloud Functions throw specific HTTP errors:

```kotlin
try {
    result.onFailure { error ->
        when {
            error.message?.contains("unauthenticated") == true -> {
                // Handle: User not logged in
            }
            error.message?.contains("permission-denied") == true -> {
                // Handle: User doesn't have permission (not seller, not admin, etc)
            }
            error.message?.contains("invalid-argument") == true -> {
                // Handle: Invalid input data
            }
            error.message?.contains("not-found") == true -> {
                // Handle: Product/resource not found
            }
            error.message?.contains("failed-precondition") == true -> {
                // Handle: Prerequisites not met (e.g., low stock)
            }
            else -> {
                // Handle: Internal server error
            }
        }
    }
}
```

---

## Security Rules

### Authentication

- Semua callable functions require `context.auth` (user harus login)
- UID diambil otomatis dari Firebase Auth

### Authorization

- **Seller Functions**: Check `users.role === "seller" || "admin"`
- **Admin Functions**: Check `users.role === "admin"`
- **Product Stock**: Tidak bisa negative

---

## Firestore Collections Structure

### users

```
{
  uid: string,
  name: string,
  email: string,
  role: "customer" | "seller" | "admin",
  createdAt: timestamp,
  updatedAt: timestamp
}
```

### products

```
{
  id: string,
  name: string,
  description: string,
  price: number,
  category: string,
  stock: number,
  imageUrl: string,
  sellerId: string (uid),
  sellerName: string,
  status: "active" | "inactive",
  ratings: number,
  reviewCount: number,
  createdAt: timestamp,
  updatedAt: timestamp
}
```

### orders

```
{
  id: string,
  buyerId: string (uid),
  productId: string,
  productName: string,
  sellerId: string (uid),
  quantity: number,
  totalPrice: number,
  status: "pending" | "completed" | "cancelled",
  createdAt: timestamp,
  updatedAt: timestamp
}
```

---

## Local Testing dengan Emulator

### Start Emulator

```bash
firebase emulators:start
```

Ini akan mulai:

- Firestore Emulator (port 8080)
- Functions Emulator (port 5001)
- Auth Emulator (port 9099)
- Storage Emulator (port 9199)
- Emulator UI (port 4000)

### Update Android App untuk Emulator

```kotlin
// In MainActivity.kt atau KishaApp.kt
FirebaseFunctions.getInstance().useEmulator("10.0.2.2", 5001)
FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
```

---

## Logs & Debugging

### View Firebase Functions Logs

```bash
firebase functions:log
```

### Real-time logs

```bash
firebase functions:log --follow
```

---

## Performance Tips

1. **Batch Operations**: Gunakan batch write untuk multiple documents
2. **Caching**: Implementasi caching di Android app untuk mengurangi function calls
3. **Indexes**: Firestore akan auto-create indexes untuk filtering queries
4. **Function Timeout**: Default 60 seconds (configurable)

---

## Next Steps

1. ✅ Deploy functions: `firebase deploy --only functions`
2. ✅ Update Firestore Security Rules untuk cloud functions
3. ✅ Integrate CloudFunctionsRepository ke ViewModels
4. ✅ Test dengan real Firebase project

---

## Troubleshooting

### "Permission denied" error

- Check user role di Firestore
- Pastikan user sudah login
- Verify Firestore security rules

### "Product not found" error

- Pastikan productId correct
- Check product sudah ada di Firestore

### "Stock not enough" error

- Product stock < quantity yang diminta
- Check stock di Firestore console

### Functions tidak ter-deploy

- Run `npm run build` untuk check TypeScript errors
- Run `firebase deploy --only functions` lagi
- Check Firebase Console > Functions > Logs
