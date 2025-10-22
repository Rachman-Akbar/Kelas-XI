# 🏗️ ARCHITECTURE DIAGRAM

## WavesOfFood & AdminWOF Apps

---

## 📱 PART A: WISHLIST ARCHITECTURE (WavesOfFood)

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER INTERFACE                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────┐        ┌──────────────────────┐     │
│  │ ProductDetailActivity│        │  FavoritesActivity   │     │
│  ├──────────────────────┤        ├──────────────────────┤     │
│  │ • Product Image      │        │ • RecyclerView       │     │
│  │ • Product Info       │        │   (Grid 2 cols)      │     │
│  │ • btnFavorite ⭐     │◄───────┤ • ProductAdapter     │     │
│  │ • btnAddToCart       │        │ • Empty State        │     │
│  └──────────┬───────────┘        └──────────┬───────────┘     │
│             │                               │                  │
└─────────────┼───────────────────────────────┼──────────────────┘
              │                               │
              ▼                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                       VIEWMODEL LAYER                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │            FavoritesViewModel                          │   │
│  ├────────────────────────────────────────────────────────┤   │
│  │ LiveData:                                              │   │
│  │  • favoriteProducts: LiveData<List<ProductModel>>     │   │
│  │  • isFavorite: LiveData<Boolean>                      │   │
│  │  • isLoading: LiveData<Boolean>                       │   │
│  │  • error: LiveData<String?>                           │   │
│  ├────────────────────────────────────────────────────────┤   │
│  │ Methods:                                               │   │
│  │  • fetchFavoriteProducts()                            │   │
│  │  • toggleFavorite(productId, currentStatus)           │   │
│  │  • checkIsFavorite(productId)                         │   │
│  │  • removeFromFavorites(productId)                     │   │
│  └────────────────────────┬───────────────────────────────┘   │
│                           │                                    │
└───────────────────────────┼────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                      REPOSITORY LAYER                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │          EcommerceRepository                           │   │
│  ├────────────────────────────────────────────────────────┤   │
│  │ Favorites Methods:                                     │   │
│  │  • toggleFavorite(userId, productId, isFavorite)      │   │
│  │    → Update Firestore users/{uid}/favorites           │   │
│  │                                                        │   │
│  │  • getFavorites(userId): Result<List<String>>         │   │
│  │    → Fetch user.favorites array                       │   │
│  │                                                        │   │
│  │  • getFavoriteProducts(userId): Result<List<Product>> │   │
│  │    → Fetch favorites IDs                              │   │
│  │    → Loop & fetch each product detail                 │   │
│  │    → Return complete ProductModel list                │   │
│  │                                                        │   │
│  │  • isFavorite(userId, productId): Result<Boolean>     │   │
│  │    → Check if productId in favorites array            │   │
│  └────────────────────────┬───────────────────────────────┘   │
│                           │                                    │
└───────────────────────────┼────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                      FIREBASE FIRESTORE                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  users/                                                         │
│    {userId}/                                                    │
│      uid: "abc123"                                              │
│      email: "user@email.com"                                    │
│      name: "User Name"                                          │
│      address: "User Address"                                    │
│      cartItems: { "prod1": 2, "prod2": 1 }                     │
│      favorites: ["prod1", "prod3", "prod5"] ◄── NEW FIELD      │
│                                                                 │
│  products/                                                      │
│    {productId}/                                                 │
│      id: "prod1"                                                │
│      title: "Product Name"                                      │
│      price: 50000                                               │
│      category: "Food"                                           │
│      imageUrl: "https://..."                                    │
│      description: "..."                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ PART B: ORDER MANAGEMENT ARCHITECTURE (AdminWOF)

```
┌─────────────────────────────────────────────────────────────────┐
│                    ADMIN USER INTERFACE                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────┐        ┌──────────────────────┐     │
│  │OrdersManagementActivity       │ OrderDetailActivity  │     │
│  ├──────────────────────┤        ├──────────────────────┤     │
│  │ • RecyclerView       │        │ ┌─ Customer Info ─┐ │     │
│  │   - Order Cards      │        │ │ • Name          │ │     │
│  │   - Order ID         │        │ │ • Email         │ │     │
│  │   - Date & Time      │        │ │ • Address       │ │     │
│  │   - Item Count       │────────┤ └─────────────────┘ │     │
│  │   - Status (colored) │   Click│ ┌─ Order Info ───┐ │     │
│  │ • Empty State        │        │ │ • Order ID      │ │     │
│  └──────────────────────┘        │ │ • Date & Time   │ │     │
│             │                    │ └─────────────────┘ │     │
│             │                    │ ┌─ Order Items ──┐ │     │
│             │                    │ │ • RecyclerView  │ │     │
│             │                    │ │ • Total Price   │ │     │
│             │                    │ └─────────────────┘ │     │
│             │                    │ ┌─ Status Update ─┐ │     │
│             │                    │ │ • Spinner       │ │     │
│             │                    │ │ • Save Button   │ │     │
│             │                    │ └─────────────────┘ │     │
│             │                    └──────────┬───────────┘     │
│             │                               │                  │
└─────────────┼───────────────────────────────┼──────────────────┘
              │                               │
              ▼                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                       VIEWMODEL LAYER                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │               OrderViewModel                           │   │
│  ├────────────────────────────────────────────────────────┤   │
│  │ LiveData:                                              │   │
│  │  • orders: LiveData<List<OrderModel>>                 │   │
│  │  • order: LiveData<OrderModel?>                       │   │
│  │  • user: LiveData<UserModel?>                         │   │
│  │  • orderItems: LiveData<List<OrderItemDetail>>        │   │
│  │  • totalPrice: LiveData<Double>                       │   │
│  │  • uiState: LiveData<UiState<String>>                 │   │
│  │  • isLoading / error                                   │   │
│  ├────────────────────────────────────────────────────────┤   │
│  │ Methods:                                               │   │
│  │  • fetchAllOrders()                                   │   │
│  │    → Call repo.fetchAllOrders()                       │   │
│  │                                                        │   │
│  │  • fetchOrderDetail(orderId)                          │   │
│  │    → Call repo.fetchOrderById()                       │   │
│  │    → Call repo.fetchUserById(order.userID)            │   │
│  │    → Call fetchProductDetails(order.items)            │   │
│  │                                                        │   │
│  │  • updateOrderStatus(orderId, newStatus)              │   │
│  │    → Validate with repo.validateOrderStatus()         │   │
│  │    → Call repo.updateOrderStatus()                    │   │
│  │    → Update uiState (Success/Error)                   │   │
│  └────────────────────────┬───────────────────────────────┘   │
│                           │                                    │
└───────────────────────────┼────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                      REPOSITORY LAYER                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │             AdminRepository                            │   │
│  ├────────────────────────────────────────────────────────┤   │
│  │ Order Methods:                                         │   │
│  │                                                        │   │
│  │  • fetchAllOrders(): Result<List<OrderModel>>         │   │
│  │    → Query: orders.orderBy("date", DESC)              │   │
│  │    → Return sorted list                               │   │
│  │                                                        │   │
│  │  • fetchOrderById(orderId): Result<OrderModel>        │   │
│  │    → Get orders/{orderId}                             │   │
│  │                                                        │   │
│  │  • fetchUserById(userId): Result<UserModel>           │   │
│  │    → Get users/{userId}                               │   │
│  │    → Return (id, name, email, address)                │   │
│  │                                                        │   │
│  │  • fetchProductById(productId): Result<ProductModel>  │   │
│  │    → Get products/{productId}                         │   │
│  │                                                        │   │
│  │  • updateOrderStatus(orderId, status): Result<Unit>   │   │
│  │    → Update orders/{orderId}.status                   │   │
│  │                                                        │   │
│  │  • validateOrderStatus(status): Boolean               │   │
│  │    → Check against Constants.ORDER_STATUSES           │   │
│  │                                                        │   │
│  └────────────────────────┬───────────────────────────────┘   │
│                           │                                    │
└───────────────────────────┼────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                      FIREBASE FIRESTORE                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  orders/                                                        │
│    {orderId}/                                                   │
│      id: "order123"                                             │
│      userID: "user456"                                          │
│      date: Timestamp(2025-10-15 14:30:00)                      │
│      items: {                                                   │
│        "prod1": 2,   // productId: quantity                    │
│        "prod2": 1                                               │
│      }                                                          │
│      status: "Processing"  ◄── UPDATED BY ADMIN               │
│      address: "Customer Address"                                │
│                                                                 │
│  users/                                                         │
│    {userId}/                                                    │
│      id: "user456"                                              │
│      name: "Customer Name"  ◄── DISPLAYED IN ORDER DETAIL     │
│      email: "customer@email.com"  ◄── FOR CONTACT             │
│      address: "Full Address"  ◄── SHIPPING INFO               │
│                                                                 │
│  products/                                                      │
│    {productId}/                                                 │
│      id: "prod1"                                                │
│      title: "Product Name"  ◄── DISPLAYED IN ORDER ITEMS      │
│      price: 50000  ◄── FOR SUBTOTAL CALCULATION                │
│      ...                                                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 DATA FLOW DIAGRAM

### Favorites Toggle Flow

```
User Clicks ⭐
    │
    ▼
ProductDetailActivity.btnFavorite.onClick()
    │
    ▼
FavoritesViewModel.toggleFavorite(productId, currentStatus)
    │
    ▼
EcommerceRepository.toggleFavorite(userId, productId, !currentStatus)
    │
    ├─► Get current user.favorites from Firestore
    ├─► Add/Remove productId from list
    └─► Update Firestore: users/{uid}/favorites
    │
    ▼
Result<Unit>.onSuccess
    │
    ├─► Update LiveData: isFavorite = !currentStatus
    └─► Trigger UI update: updateFavoriteIcon(isFavorite)
```

### Order Status Update Flow

```
Admin Selects Status from Spinner
    │
    ▼
OrderDetailActivity.btnSaveChanges.onClick()
    │
    ▼
OrderViewModel.updateOrderStatus(orderId, selectedStatus)
    │
    ├─► Validate: repository.validateOrderStatus(status)
    │   └─► Check against Constants.ORDER_STATUSES
    │
    ▼ [Valid]
AdminRepository.updateOrderStatus(orderId, status)
    │
    ├─► Update Firestore: orders/{orderId}.status = newStatus
    │
    ▼
Result<Unit>.onSuccess
    │
    ├─► Update UiState: Success("Status updated to $status")
    ├─► Show Toast: "Status Updated to Processing"
    ├─► Call finish() → Close activity
    │
    ▼
OrdersManagementActivity.onResume()
    │
    └─► Auto-refresh: fetchAllOrders() → See updated status
```

---

## 🎨 ADAPTER HIERARCHY

### WavesOfFood

```
ProductAdapter (Existing)
    └─► Used by:
        ├─► MainActivity (grid layout)
        └─► FavoritesActivity (grid layout) ◄── REUSED
```

### AdminWOF

```
OrderAdapter (NEW)
    ├─► ViewHolder: OrderViewHolder
    ├─► Layout: item_order.xml
    ├─► DiffUtil: OrderDiffCallback
    └─► Used by: OrdersManagementActivity

OrderItemAdapter (NEW)
    ├─► ViewHolder: OrderItemViewHolder
    ├─► Layout: item_order_item.xml
    ├─► DiffUtil: OrderItemDiffCallback
    └─► Used by: OrderDetailActivity
```

---

## 📊 DEPENDENCY GRAPH

```
┌─────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                     │
│  ┌──────────────────┐        ┌──────────────────┐      │
│  │ WavesOfFoodApp   │        │  AdminWOFApp     │      │
│  ├──────────────────┤        ├──────────────────┤      │
│  │ • authRepository │        │ • authRepository │      │
│  │ • ecommerceRepo  │        │ • adminRepo      │      │
│  │ • viewModelFactory│       │ • viewModelFactory│     │
│  └────────┬─────────┘        └────────┬─────────┘      │
│           │                           │                 │
└───────────┼───────────────────────────┼─────────────────┘
            │                           │
            ▼                           ▼
┌─────────────────────────────────────────────────────────┐
│                 VIEWMODEL FACTORY                       │
│  ┌─────────────────────────────────────────────────┐   │
│  │  create(modelClass) {                           │   │
│  │    when (modelClass) {                          │   │
│  │      FavoritesViewModel → new(ecommerceRepo)    │   │
│  │      OrderViewModel → new(adminRepo)            │   │
│  │      ...                                        │   │
│  │    }                                            │   │
│  │  }                                              │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## 🏗️ LAYER ARCHITECTURE

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                   │
│  Activities, Fragments, Adapters, View Binding         │
└────────────────────┬────────────────────────────────────┘
                     │ Observes LiveData
                     ▼
┌─────────────────────────────────────────────────────────┐
│                    VIEWMODEL LAYER                      │
│  ViewModels, LiveData, Coroutines, Business Logic      │
└────────────────────┬────────────────────────────────────┘
                     │ Calls Repository
                     ▼
┌─────────────────────────────────────────────────────────┐
│                   REPOSITORY LAYER                      │
│  Single Source of Truth, Data Operations               │
└────────────────────┬────────────────────────────────────┘
                     │ Firebase Calls
                     ▼
┌─────────────────────────────────────────────────────────┐
│                    DATA LAYER                           │
│  Firebase Firestore, Firebase Storage, Firebase Auth   │
└─────────────────────────────────────────────────────────┘
```

---

## 📐 MVVM PATTERN

```
        View                ViewModel              Repository
    (Activity/Fragment)                           (Data Source)
         │                      │                      │
    ┌────▼────┐           ┌────▼────┐           ┌────▼────┐
    │         │           │         │           │         │
    │  User   │◄─Observe──│LiveData │◄──Result──│Firebase │
    │ Actions │           │         │           │Firestore│
    │         │           │         │           │         │
    └────┬────┘           └────┬────┘           └────┬────┘
         │                     │                     │
         │                     │                     │
    Click/Input           Coroutine              suspend fun
         │                 Launch                    │
         └──────Call───────►│                       │
                            └─────────Call──────────►│
                                                     │
                            ◄────Result<T>──────────┘
```

---

## 🔗 NAVIGATION FLOW

### WavesOfFood

```
SplashActivity
    │
    ├─► [Not Logged In] → AuthActivity
    │                       └─► LoginActivity
    │                       └─► RegisterActivity
    │
    └─► [Logged In] → MainActivity
                        ├─► ProductDetailActivity
                        │   └─► Toggle Favorite ⭐
                        │
                        ├─► CartActivity
                        │   └─► CheckoutActivity
                        │       └─► OrderConfirmationActivity
                        │
                        ├─► ProfileActivity
                        │
                        ├─► OrdersActivity
                        │   └─► OrderDetailActivity
                        │
                        └─► FavoritesActivity ◄── NEW
                            └─► ProductDetailActivity
```

### AdminWOF

```
AdminLoginActivity
    │
    └─► [Admin Email Verified] → AdminMainActivity
                                    │
                                    ├─► OrdersManagementActivity ◄── NEW
                                    │   └─► OrderDetailActivity ◄── NEW
                                    │       └─► Update Status
                                    │           └─► Back to List
                                    │
                                    └─► [Future: ProductFormActivity]
```

---

**END OF ARCHITECTURE DIAGRAM** 🏗️

_Generated: 15 Oktober 2025_
