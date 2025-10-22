# PANDUAN QUICK START & TESTING

## WavesOfFood & AdminWOF Apps

---

## 🚀 BUILD & RUN

### 1. Build WavesOfFood App (User App)

```powershell
cd "c:\Akbar\Waves Of Food\WavesOfFood"
.\gradlew clean assembleDebug
```

### 2. Build AdminWOF App (Admin App)

```powershell
cd "c:\Akbar\Waves Of Food\AdminWOF"
.\gradlew clean assembleDebug
```

---

## 🧪 TESTING SCENARIO

### PART A: WISHLIST FEATURE (WavesOfFood App)

#### Test 1: Toggle Favorite dari Product Detail

1. ✅ Buka WavesOfFood app
2. ✅ Login dengan user account
3. ✅ Browse products di MainActivity
4. ✅ Klik product untuk buka ProductDetailActivity
5. ✅ **Klik icon star (top-right corner)**
   - Icon harus berubah dari outline → filled
   - Product ID ditambahkan ke Firestore `users/{uid}/favorites`
6. ✅ Klik star lagi untuk unfavorite
   - Icon berubah dari filled → outline
   - Product ID dihapus dari favorites

**Expected Result:**

- Icon toggle dengan smooth
- Status persist di Firebase
- Toast tidak muncul (silent operation)

---

#### Test 2: Lihat Daftar Favorites

1. ✅ Dari MainActivity, navigate ke FavoritesActivity
   - _Note: Anda perlu menambahkan menu item atau button untuk ini_
   - _Alternative: Start activity manual via Intent_
2. ✅ FavoritesActivity menampilkan grid 2 kolom
3. ✅ Semua produk yang difavoritkan muncul dengan:
   - Product image
   - Product name
   - Product price
4. ✅ Klik product untuk buka ProductDetailActivity

**Expected Result:**

- Grid layout dengan 2 columns
- Data fetch dari Firestore
- Loading indicator saat fetch
- Empty state jika belum ada favorit

---

#### Test 3: Empty State

1. ✅ Unfavorite semua products
2. ✅ Buka FavoritesActivity
3. ✅ **Verify empty state muncul:**
   ```
   "No favorites yet.
   Start adding your favorite products!"
   ```

---

#### Test 4: Multi-Device Sync

1. ✅ Login dengan user yang sama di 2 devices
2. ✅ Device 1: Add product ke favorites
3. ✅ Device 2: Pull to refresh / reopen FavoritesActivity
4. ✅ **Verify product muncul di device 2**

**Expected Result:**

- Favorites sync via Firebase Firestore
- Real-time atau on-refresh sync

---

### PART B: ORDER MANAGEMENT (AdminWOF App)

#### Test 1: Login Admin

1. ✅ Buka AdminWOF app
2. ✅ Login dengan admin email:
   - `admin@wavesoffood.com`
   - `admin@komputerkit.com`
3. ✅ Navigate ke AdminMainActivity

**Expected Result:**

- Only whitelisted emails dapat login
- Non-admin emails ditolak

---

#### Test 2: View All Orders

1. ✅ Dari AdminMainActivity, buka OrdersManagementActivity
   - _Note: Anda perlu menambahkan button/menu untuk ini_
2. ✅ **Verify orders ditampilkan:**
   - Sorted by date descending (terbaru di atas)
   - Setiap order card menampilkan:
     - Order ID (8 characters)
     - Date & Time
     - Item count
     - Status dengan warna:
       - Ordered (Blue)
       - Processing (Orange)
       - Shipped (Purple)
       - Delivered (Green)
       - Cancelled (Red)

**Expected Result:**

- RecyclerView dengan list orders
- Loading indicator saat fetch
- Empty state jika belum ada order

---

#### Test 3: View Order Detail

1. ✅ Dari OrdersManagementActivity, klik salah satu order
2. ✅ OrderDetailActivity terbuka
3. ✅ **Verify semua data muncul:**

**Customer Information:**

```
Name: [Customer Name]
Email: [customer@email.com]
Address: [Customer Address]
```

**Order Information:**

```
Order #: [12345678]
Date: [15 Oct 2025, 14:30]
```

**Order Items:**

```
- Product 1 Name        x2    Rp 50.000   → Subtotal: Rp 100.000
- Product 2 Name        x1    Rp 30.000   → Subtotal: Rp 30.000
───────────────────────────────────────────────────────────────
Total Price:                                 Rp 130.000
```

**Order Status:**

```
[Spinner with current status selected]
[Save Changes Button]
```

**Expected Result:**

- Semua data loaded dari Firestore
- User info displayed (name, email, address)
- Product details fetched dan displayed
- Total price calculated correctly
- Current status pre-selected di spinner

---

#### Test 4: Update Order Status

1. ✅ Di OrderDetailActivity, buka Spinner
2. ✅ Pilih status baru (contoh: "Processing" → "Shipped")
3. ✅ Klik "Save Changes"
4. ✅ **Verify:**
   - Button disabled & text change: "Updating..."
   - Toast muncul: "Status Updated to Shipped"
   - Activity close otomatis
   - Kembali ke OrdersManagementActivity
5. ✅ **Verify di list:**
   - Order status updated
   - Warna status berubah sesuai (Shipped = Purple)

**Expected Result:**

- Status update di Firestore
- Real-time update terlihat
- Toast confirmation
- Activity close setelah success

---

#### Test 5: Status Validation

1. ✅ Di OrderDetailActivity
2. ✅ Pastikan spinner hanya menampilkan status valid:
   - Ordered
   - Processing
   - Shipped
   - Delivered
   - Cancelled
3. ✅ Tidak ada status invalid

**Expected Result:**

- Spinner hanya populated dari `Constants.ORDER_STATUSES`
- Backend validation di `AdminRepository.validateOrderStatus()`

---

#### Test 6: Multiple Orders

1. ✅ Create 5+ orders dari WavesOfFood app (user side)
2. ✅ Refresh OrdersManagementActivity
3. ✅ **Verify:**
   - Semua orders muncul
   - Sorted by date (terbaru di atas)
   - Dapat scroll smooth
   - Click any order untuk detail

---

## 🔍 FIREBASE VERIFICATION

### Check Firestore Data

#### 1. Users Collection

```javascript
users/
  {userId}/
    uid: "..."
    email: "..."
    name: "..."
    address: "..."
    cartItems: { ... }
    favorites: ["productId1", "productId2", "productId3"] // ← Check ini
```

#### 2. Products Collection

```javascript
products/
  {productId}/
    id: "..."
    title: "..."
    price: 50000
    category: "..."
    imageUrl: "..."
    description: "..."
```

#### 3. Orders Collection

```javascript
orders/
  {orderId}/
    id: "..."
    userID: "userId"
    date: Timestamp(...)
    items: {
      "productId1": 2,
      "productId2": 1
    }
    status: "Processing" // ← Check updates
    address: "..."
```

---

## ⚠️ TROUBLESHOOTING

### Issue: Favorites tidak update

**Solution:**

- Check internet connection
- Verify Firebase rules allow read/write
- Check Logcat for errors
- Verify userId is correct

### Issue: Orders tidak muncul

**Solution:**

- Verify google-services.json configured
- Check Firebase Console untuk data
- Verify admin login dengan whitelisted email
- Check collection name: "orders"

### Issue: User info tidak muncul di OrderDetail

**Solution:**

- Verify userID exists di orders
- Check users collection has data
- Verify `fetchUserById()` di AdminRepository
- Check Logcat for network errors

### Issue: Status update gagal

**Solution:**

- Verify status string match `Constants.ORDER_STATUSES`
- Check Firebase write permissions
- Verify orderId is correct
- Check UiState observe for error message

---

## 📊 TESTING CHECKLIST

### WavesOfFood (User App)

- [ ] Login berhasil
- [ ] Products displayed di MainActivity
- [ ] ProductDetailActivity load correctly
- [ ] Favorite icon toggle works
- [ ] Favorites persist di Firebase
- [ ] FavoritesActivity load favorites
- [ ] Empty state displayed when no favorites
- [ ] Navigate dari FavoritesActivity ke ProductDetail

### AdminWOF (Admin App)

- [ ] Admin login berhasil
- [ ] Non-admin login ditolak
- [ ] OrdersManagementActivity load all orders
- [ ] Orders sorted by date descending
- [ ] Order status colors correct
- [ ] OrderDetailActivity opens correctly
- [ ] Customer info displayed (name, email, address)
- [ ] Order items displayed dengan quantity & subtotal
- [ ] Total price calculated correctly
- [ ] Spinner populated dengan ORDER_STATUSES
- [ ] Current status pre-selected
- [ ] Status update works
- [ ] Toast confirmation muncul
- [ ] Activity close after update
- [ ] List refresh shows new status

---

## 🔥 QUICK TEST COMMANDS

### Run WavesOfFood App

```powershell
cd "c:\Akbar\Waves Of Food\WavesOfFood"
.\gradlew installDebug
adb shell am start -n com.komputerkit.wavesoffood/.view.splash.SplashActivity
```

### Run AdminWOF App

```powershell
cd "c:\Akbar\Waves Of Food\AdminWOF"
.\gradlew installDebug
adb shell am start -n com.komputerkit.adminwof/.view.auth.AdminLoginActivity
```

### Check Logcat

```powershell
adb logcat | Select-String "WavesOfFood|AdminWOF|Firebase"
```

### Clear App Data (Fresh Start)

```powershell
# WavesOfFood
adb shell pm clear com.komputerkit.wavesoffood

# AdminWOF
adb shell pm clear com.komputerkit.adminwof
```

---

## ✅ SUCCESS CRITERIA

### WavesOfFood:

- [x] User dapat favorite/unfavorite products
- [x] Favorites displayed di FavoritesActivity
- [x] Data sync dengan Firebase
- [x] Empty state works

### AdminWOF:

- [x] Admin dapat lihat semua orders
- [x] Orders sorted correctly
- [x] Order detail menampilkan customer info
- [x] Admin dapat update order status
- [x] Status update reflect real-time
- [x] Validation works

---

**Ready for Testing!** 🚀

_Last updated: 15 Oktober 2025_
