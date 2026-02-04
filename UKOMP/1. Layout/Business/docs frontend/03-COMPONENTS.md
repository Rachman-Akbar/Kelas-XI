# 🧩 Reusable Components

Dokumentasi lengkap semua reusable components dalam aplikasi SI-UMKM Mobile.

---

## 📂 Component Organization

```
components/
├── common/
│   └── AppBottomNavigation.kt
├── bahan_baku/
│   └── MaterialCard.kt
├── produk/
│   └── ProductCard.kt
└── penjualan/
    └── SalesProductCard.kt
```

---

## 🎯 COMMON COMPONENTS

### 1. AppBottomNavigation

**Path**: `components/common/AppBottomNavigation.kt`

**Purpose**: Role-based bottom navigation bar yang automatically filtered berdasarkan user role.

**Props**:

```kotlin
@Composable
fun AppBottomNavigation(
    selectedRoute: String,        // Current active route
    onNavigate: (String) -> Unit, // Navigation callback
    userRole: String = "Produksi" // User role for filtering
)
```

**Features**:

- ✅ Auto-filter menu berdasarkan role
- ✅ Icon dan label yang jelas
- ✅ Color change only (no background capsule)
- ✅ Zero elevation (flat design)
- ✅ Transparent indicator

**Role Configuration**:

```kotlin
val items = when (userRole) {
    "Keuangan" -> listOf(
        "finance_dashboard",   // Home
        "hpp_bep_analysis",    // HPP-BEP
        "debt_receivable",     // Utang
        "transaction_history"  // Transaksi
    )

    "Penjualan" -> listOf(
        "sales_dashboard",  // Home
        "sales_pos",        // Kasir
        "customer_list"     // Pelanggan
    )

    "Produksi" -> listOf(
        "dashboard",       // Home
        "bahan_baku",      // Bahan Baku
        "product_list",    // Produk
        "production"       // Produksi
    )
}
```

**Usage Example**:

```kotlin
Scaffold(
    bottomBar = {
        AppBottomNavigation(
            selectedRoute = currentRoute,
            onNavigate = { route -> navController.navigate(route) },
            userRole = "Penjualan"
        )
    }
) { paddingValues ->
    // Screen content
}
```

**Design Specs**:

- **Height**: Auto (system default ~80dp)
- **Elevation**: 0.dp (flat)
- **Background**: MaterialTheme.colorScheme.surface
- **Selected Icon Color**: Primary
- **Unselected Icon Color**: OnSurfaceVariant
- **Indicator**: Transparent
- **Icon Size**: 24.dp
- **Label Font**: labelSmall

**Color States**:

```kotlin
colors = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = Color.Transparent,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)
```

---

## 📦 MATERIAL COMPONENTS

### 2. MaterialCard

**Path**: `components/bahan_baku/MaterialCard.kt`

**Purpose**: Card component untuk menampilkan informasi bahan baku di list.

**Props**:

```kotlin
@Composable
fun MaterialCard(
    material: Material,
    onClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onRestock: () -> Unit = {},
    onViewHistory: () -> Unit = {},
    onDelete: () -> Unit = {}
)
```

**UI Structure**:

```
┌─────────────────────────────────────────┐
│  [Icon]  Tepung Terigu                 │ ← Material name
│          Toko Bahan A                   │ ← Supplier
│                                         │
│  Stok: 25.5 kg    [Aman]    Rp 12.000  │ ← Stock, Status, Price
│                                    [⋮]  │ ← Menu
└─────────────────────────────────────────┘
```

**Features**:

- ✅ Stock status badge (Aman/Menipis)
- ✅ Color-coded status
- ✅ Three-dot menu with actions
- ✅ Clickable for detail view
- ✅ Stock alert indicator

**Status Logic**:

```kotlin
val (statusText, statusColor) = when {
    material.stokSaatIni <= material.batasMinimum ->
        "Menipis" to Color(0xFFEF4444)  // Red
    else ->
        "Aman" to Color(0xFF10B981)     // Green
}
```

**Menu Items**:

- ✏️ Edit Material
- 📦 Restock
- 📊 Lihat Riwayat
- 🗑️ Hapus

**Design Specs**:

- **Shape**: RoundedCornerShape(12.dp)
- **Border**: 1.dp, Color(0xFFE2E8F0)
- **Elevation**: 1.dp
- **Padding**: 16.dp
- **Status Badge**: 6.dp rounded corners

---

## 📋 PRODUCT COMPONENTS

### 3. ProductCard

**Path**: `components/produk/ProductCard.kt`

**Purpose**: Card component untuk menampilkan produk (NON-CLICKABLE).

**Props**:

```kotlin
@Composable
fun ProductCard(
    product: Product,
    onViewDetail: () -> Unit = {},
    onViewComposition: () -> Unit = {},
    onRestock: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
)
```

**UI Structure**:

```
┌─────────────────────────────────────────┐
│  [Img]  Kopi Latte                     │ ← Product name
│         SKU-001 • Minuman               │ ← SKU, Category
│                                         │
│  Stok: 45 pcs                           │ ← Stock
│  Rp 25.000                          [⋮] │ ← Price, Menu
└─────────────────────────────────────────┘
```

**Features**:

- ✅ Product image placeholder
- ✅ SKU & Category display
- ✅ Stock info
- ✅ Price formatting
- ✅ NOT clickable (Surface without onClick)
- ✅ Three-dot menu for actions

**Menu Items**:

- 👁️ Detail
- 🔧 Komposisi
- 📦 Restock
- ✏️ Edit
- 🗑️ Hapus

**Design Specs**:

- **Shape**: RoundedCornerShape(12.dp)
- **Border**: 1.dp, Color(0xFFE2E8F0)
- **Elevation**: 1.dp
- **Image Size**: 80.dp × 80.dp
- **Image Shape**: RoundedCornerShape(8.dp)

**Price Formatting**:

```kotlin
Text(
    text = "Rp ${String.format("%,d", product.sellingPrice.toLong())}",
    style = MaterialTheme.typography.bodyLarge,
    fontWeight = FontWeight.Bold,
    color = Color(0xFF197FE6)
)
```

---

## 🛒 SALES COMPONENTS

### 4. SalesProductCard

**Path**: `components/penjualan/SalesProductCard.kt`

**Purpose**: Card component untuk produk di POS (Point of Sale).

**Props**:

```kotlin
@Composable
fun SalesProductCard(
    product: POSProduct,
    quantityInCart: Int = 0,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit
)
```

**UI Structure**:

```
┌─────────────────────────────────┐
│  ┌───────────────────────────┐  │
│  │     [Product Image]       │  │
│  └───────────────────────────┘  │
│                                 │
│  Kopi Latte                     │ ← Name
│  Stok: 45                       │ ← Stock
│                                 │
│  Rp 25.000            [2]       │ ← Price, Qty badge
│                                 │
│  [−]                      [+]   │ ← Add/Remove buttons
└─────────────────────────────────┘
```

**Features**:

- ✅ Interactive add/remove buttons
- ✅ Quantity badge when in cart
- ✅ Stock validation
- ✅ Border highlight when selected
- ✅ Category icon placeholder

**Border Logic**:

```kotlin
border = androidx.compose.foundation.BorderStroke(
    width = if (quantityInCart > 0) 2.dp else 1.dp,
    color = if (quantityInCart > 0) Color(0xFF10B981) else Color(0xFFE2E8F0)
)
```

**Add/Remove Buttons**:

```kotlin
// Remove button
IconButton(onClick = onRemoveFromCart) {
    Surface(
        shape = CircleShape,
        color = Color(0xFFFEE2E2)  // Light red
    ) {
        Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = "Remove",
            tint = Color(0xFFEF4444)  // Red
        )
    }
}

// Add button
IconButton(
    onClick = onAddToCart,
    enabled = quantityInCart < product.stock
) {
    Surface(
        shape = CircleShape,
        color = Color(0xFFD1FAE5)  // Light green
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color(0xFF10B981)  // Green
        )
    }
}
```

**Design Specs**:

- **Card Shape**: RoundedCornerShape(12.dp)
- **Image Height**: 100.dp
- **Button Size**: 32.dp circle
- **Quantity Badge**: 24.dp × 24.dp, RoundedCornerShape(8.dp)
- **Highlight Border**: 2.dp, Color(0xFF10B981)

---

## 🎨 COMMON UI PATTERNS

### Icon Button Pattern

```kotlin
Surface(
    shape = CircleShape,
    color = Color.Transparent,
    border = androidx.compose.foundation.BorderStroke(1.5.dp, iconColor),
    modifier = Modifier.size(36.dp)
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
    }
}
```

**Icon Colors by Function**:

- 🔵 Blue `#197FE6`: Date, Filter, General actions
- 🟢 Green `#10B981`: Category, Success, Add
- 🟣 Purple `#8B5CF6`: History, Special
- 🟠 Orange `#F59E0B`: Warning, Edit
- 🔴 Red `#EF4444`: Delete, Error

---

### Dropdown Menu Pattern

```kotlin
var expanded by remember { mutableStateOf(false) }

DropdownMenu(
    expanded = expanded,
    onDismissRequest = { expanded = false },
    modifier = Modifier.width(160.dp),
    shape = RoundedCornerShape(12.dp)
) {
    DropdownMenuItem(
        text = { Text("Menu Item") },
        onClick = {
            // Action
            expanded = false
        },
        leadingIcon = {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    )
}
```

---

### Status Badge Pattern

```kotlin
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Usage
StatusBadge(
    text = "Aman",
    color = Color(0xFF10B981)
)
```

---

### Loading Indicator Pattern

```kotlin
@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading...")
            }
        }
    }
}
```

---

### Empty State Pattern

```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        if (description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}
```

---

### Search Bar Component

```kotlin
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Cari...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = Color(0xFF197FE6)
        ),
        singleLine = true
    )
}

// Usage
SearchBar(
    query = searchQuery,
    onQueryChange = { searchQuery = it },
    placeholder = "Cari produk...",
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

---

### Filter Chips Component

```kotlin
@Composable
fun FilterChips(
    options: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelectionChange(index) },
                label = {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (selectedIndex == index)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF197FE6),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color(0xFF64748B)
                ),
                border = if (selectedIndex == index) null else
                    androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            )
        }
    }
}

// Usage
FilterChips(
    options = listOf("Semua", "Minuman", "Makanan", "Snack"),
    selectedIndex = selectedCategory,
    onSelectionChange = { selectedCategory = it },
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

---

## 🎯 Component Best Practices

### 1. Props Organization

```kotlin
@Composable
fun MyComponent(
    // Required props first
    title: String,

    // Optional props with defaults
    subtitle: String = "",

    // Callbacks last
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
)
```

### 2. State Hoisting

Component tidak boleh manage state sendiri yang berpengaruh ke parent:

```kotlin
// ❌ Wrong - managing state internally
@Composable
fun MyCard() {
    var isExpanded by remember { mutableStateOf(false) }
    // ...
}

// ✅ Correct - state hoisted to parent
@Composable
fun MyCard(
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit
)
```

### 3. Composition vs Inheritance

Gunakan composition untuk reusability:

```kotlin
// ✅ Good - composable components
@Composable
fun InfoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(...) {
        Column {
            Text(title)
            content()
        }
    }
}
```

### 4. Preview Annotations

Selalu sertakan `@Preview` untuk testing:

```kotlin
@Preview(showBackground = true)
@Composable
fun MaterialCardPreview() {
    MaterialCard(
        material = Material(
            id = "1",
            nama = "Tepung Terigu",
            satuan = "kg",
            stokSaatIni = 25.5,
            batasMinimum = 10.0,
            hargaPerSatuan = 12000.0
        )
    )
}
```

---

## 📝 Notes

- Semua components mengikuti Material Design 3 guidelines
- Consistent spacing: 8dp, 12dp, 16dp, 20dp, 24dp
- Color palette dari theme, bukan hardcoded (kecuali untuk specific brand colors)
- Accessibility: ContentDescription untuk semua Icon
- Performance: Avoid unnecessary recomposition
- Testing: Preview untuk semua components
