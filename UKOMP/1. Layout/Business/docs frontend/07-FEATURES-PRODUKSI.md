# 🏭 FITUR ROLE PRODUKSI

Dokumentasi lengkap fitur-fitur yang tersedia untuk role Produksi dalam aplikasi SI-UMKM Mobile.

---

## 📊 1. DASHBOARD PRODUKSI

### 1.1 Overview Statistics

**Komponen**: `ProductionDashboardScreen`

**Metrics Ditampilkan**:

```kotlin
data class ProductionStats(
    val totalMaterials: Int,           // Total jenis bahan baku
    val lowStockMaterials: Int,        // Bahan < min stock
    val totalProducts: Int,            // Total jenis produk
    val productionToday: Int,          // Produksi hari ini
    val productionThisWeek: Int,       // Produksi minggu ini
    val productionThisMonth: Int       // Produksi bulan ini
)
```

**Layout**:

- Row dengan 2 kolom
- Setiap stat card dengan:
  - Ikon representatif
  - Angka besar (24sp)
  - Label (12sp)
  - Background putih + rounded 12dp

**Business Logic**:

```kotlin
// Count materials with stock < minStock
lowStockMaterials = materials.count { it.stock < it.minStock }

// Count production by date range
productionToday = productions.count {
    it.date.isToday() && it.status == "Completed"
}
```

### 1.2 Quick Access Menu

**Actions**:

1. **Tambah Bahan Baku** → `material_form`
2. **Tambah Produk** → `product_form`
3. **Jadwalkan Produksi** → `production_form`
4. **Lihat Riwayat** → `production_history`

**Design Pattern**:

```kotlin
LazyVerticalGrid(columns = GridCells.Fixed(2)) {
    items(quickActions) { action ->
        QuickActionCard(
            icon = action.icon,
            title = action.title,
            onClick = { navController.navigate(action.route) }
        )
    }
}
```

### 1.3 Material Alert Section

**Tampilan**:

- List material dengan status "Menipis"
- Sorted by urgency: (stock / minStock) ratio ascending
- Show top 5 only

**Indicator**:

```kotlin
when {
    stock == 0 -> Color.Error + "Habis"
    stock < minStock * 0.3 -> Color.Error + "Kritis"
    stock < minStock -> Color.Warning + "Menipis"
    else -> Color.Success + "Aman"
}
```

**Action Buttons**:

- "Restock" → Navigate to `material_restock/{materialId}`
- "Lihat Semua" → Navigate to `material_list`

---

## 📦 2. MANAJEMEN BAHAN BAKU

### 2.1 Material List

**Komponen**: `MaterialListScreen`

**Features**:

#### Search & Filter

```kotlin
var searchQuery by remember { mutableStateOf("") }
var selectedCategory by remember { mutableStateOf("Semua") }

val filteredMaterials = materials.filter { material ->
    material.name.contains(searchQuery, ignoreCase = true) &&
    (selectedCategory == "Semua" || material.category == selectedCategory)
}
```

**Categories**:

- Semua
- Tepung
- Gula
- Susu
- Kopi
- Lainnya

#### Stock Status Badge

```kotlin
@Composable
fun StockStatusBadge(stock: Int, minStock: Int) {
    val (color, text) = when {
        stock == 0 -> Color.Error to "Habis"
        stock < minStock * 0.3 -> Color.Error to "Kritis"
        stock < minStock -> Color.Warning to "Menipis"
        else -> Color.Success to "Aman"
    }

    Badge(
        containerColor = color,
        contentColor = Color.White
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall)
    }
}
```

#### Card Actions

**Three-dot menu**:

1. **Edit** → `material_form/{materialId}?mode=edit`
2. **Restock** → `material_restock/{materialId}`
3. **Lihat Riwayat** → `material_history/{materialId}`
4. **Hapus** → Show confirmation dialog

**Validation**:

```kotlin
fun deleteMaterial(materialId: String): Result {
    // Check if used in any product composition
    val isUsed = products.any { product ->
        product.materials.any { it.materialId == materialId }
    }

    if (isUsed) {
        return Result.Error("Material sedang digunakan di produk")
    }

    // Safe to delete
    materialRepository.delete(materialId)
    return Result.Success
}
```

### 2.2 Material Restock

**Komponen**: `RestockMaterialScreen`

**Form Fields**:

```kotlin
data class RestockForm(
    val materialId: String,           // Read-only (from route)
    val materialName: String,          // Read-only (from DB)
    val currentStock: Float,           // Read-only (displayed)
    val restockQuantity: Float,        // User input (required)
    val supplier: String,              // User input (optional)
    val cost: Double,                  // User input (optional)
    val notes: String                  // User input (optional)
)
```

**Business Logic**:

```kotlin
suspend fun processRestock(form: RestockForm): Result {
    // 1. Validate quantity
    if (form.restockQuantity <= 0) {
        return Result.Error("Jumlah harus lebih dari 0")
    }

    // 2. Update stock
    val material = materialRepository.getById(form.materialId)
    material.stock += form.restockQuantity
    materialRepository.update(material)

    // 3. Create history record
    val history = MaterialHistory(
        id = generateId(),
        materialId = form.materialId,
        type = "Restock",
        quantity = form.restockQuantity,
        beforeStock = form.currentStock,
        afterStock = material.stock,
        supplier = form.supplier,
        cost = form.cost,
        notes = form.notes,
        timestamp = System.currentTimeMillis()
    )
    historyRepository.insert(history)

    // 4. Create transaction (if cost provided)
    if (form.cost > 0) {
        val transaction = Transaction(
            id = generateId(),
            type = "Pengeluaran",
            category = "Bahan Baku",
            title = "Restock ${form.materialName}",
            amount = form.cost,
            date = Date(),
            notes = "Quantity: ${form.restockQuantity} ${material.unit}"
        )
        transactionRepository.insert(transaction)
    }

    return Result.Success
}
```

**UI Enhancements**:

- Show preview: "Stock setelah restock: X unit"
- Show estimated cost if supplier has default price
- Quick presets: +10, +50, +100 buttons

### 2.3 Material History

**Komponen**: `MaterialHistoryScreen`

**Display**:

```kotlin
LazyColumn {
    items(historyList.groupBy { it.date.toDateString() }) { (date, histories) ->
        // Date header
        Text(
            text = date,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp)
        )

        // History items for this date
        histories.forEach { history ->
            MaterialHistoryCard(
                type = history.type,           // Restock / Usage / Adjustment
                quantity = history.quantity,    // ±X unit
                beforeStock = history.beforeStock,
                afterStock = history.afterStock,
                relatedInfo = history.getRelatedInfo(),  // e.g., "Produksi #123"
                timestamp = history.timestamp
            )
        }
    }
}
```

**History Types**:

1. **Restock**: Hijau, ikon ⬆️
2. **Usage** (dari produksi): Merah, ikon ⬇️
3. **Adjustment**: Biru, ikon ✏️

**Card Format**:

```
┌─────────────────────────────────┐
│ ⬆️ Restock                      │
│ +50 kg                          │
│ 100 kg → 150 kg                 │
│                                  │
│ Supplier: Toko ABC              │
│ Cost: Rp 500.000                │
│ 10:30 WIB                       │
└─────────────────────────────────┘
```

---

## 📋 3. MANAJEMEN PRODUK

### 3.1 Product List

**Komponen**: `ProductListScreen`

**Features**:

#### Search & Category Filter

```kotlin
val categories = listOf("Semua", "Minuman", "Makanan", "Snack")
var selectedCategory by remember { mutableStateOf("Semua") }
var searchQuery by remember { mutableStateOf("") }

val filteredProducts = products.filter { product ->
    product.name.contains(searchQuery, ignoreCase = true) &&
    (selectedCategory == "Semua" || product.category == selectedCategory)
}
```

#### Product Card Layout

```kotlin
@Composable
fun ProductCard(product: Product) {
    Card {
        Row {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray)
            ) {
                Icon(Icons.Default.ShoppingBag, null)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("Stock: ${product.stock} ${product.unit}")
                Text(
                    "Rp ${product.sellingPrice.formatCurrency()}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // HPP & margin
                Row {
                    Text("HPP: Rp ${product.hpp.formatCurrency()}", fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Margin: ${product.margin}%", fontSize = 12.sp, color = Color.Green)
                }
            }

            // Actions
            IconButton(onClick = { /* menu */ }) {
                Icon(Icons.Default.MoreVert, null)
            }
        }
    }
}
```

#### Product Actions

**Three-dot menu**:

1. **Edit** → `product_form/{productId}?mode=edit`
2. **Komposisi** → `product_composition/{productId}`
3. **Adjustment Stock** → Show dialog untuk koreksi manual
4. **Hapus** → Confirmation dialog

**Delete Validation**:

```kotlin
fun deleteProduct(productId: String): Result {
    // Check if used in any pending/ongoing production
    val isUsed = productions.any { production ->
        production.productId == productId &&
        production.status in listOf("Planned", "InProgress")
    }

    if (isUsed) {
        return Result.Error("Produk sedang dalam jadwal produksi")
    }

    productRepository.delete(productId)
    return Result.Success
}
```

### 3.2 Product Form (Add/Edit)

**Form Fields**:

```kotlin
data class ProductForm(
    val name: String,                      // Required
    val category: String,                  // Required (dropdown)
    val description: String,               // Optional
    val unit: String,                      // Required (e.g., "pcs", "porsi")
    val sellingPrice: Double,              // Required
    val materials: List<MaterialUsage>,    // Required (min 1)
    val laborCost: Double,                 // Optional (default 0)
    val overheadCost: Double               // Optional (default 0)
)

data class MaterialUsage(
    val materialId: String,
    val materialName: String,
    val quantity: Float,
    val unit: String,
    val pricePerUnit: Double
)
```

**Material Composition Builder**:

```kotlin
@Composable
fun MaterialCompositionBuilder(
    materials: List<MaterialUsage>,
    onMaterialsChange: (List<MaterialUsage>) -> Unit
) {
    Column {
        Text("Komposisi Bahan", style = MaterialTheme.typography.titleMedium)

        materials.forEachIndexed { index, material ->
            Row {
                Text("${material.materialName}")
                Spacer(Modifier.weight(1f))

                // Quantity input
                OutlinedTextField(
                    value = material.quantity.toString(),
                    onValueChange = { /* update */ },
                    modifier = Modifier.width(80.dp),
                    suffix = { Text(material.unit) }
                )

                // Remove button
                IconButton(onClick = {
                    onMaterialsChange(materials.removeAt(index))
                }) {
                    Icon(Icons.Default.Delete, null)
                }
            }
        }

        // Add material button
        OutlinedButton(onClick = { showMaterialPicker = true }) {
            Icon(Icons.Default.Add, null)
            Text("Tambah Bahan")
        }
    }
}
```

**HPP Auto-Calculate**:

```kotlin
fun calculateHPP(form: ProductForm): Double {
    val materialCost = form.materials.sumOf {
        it.quantity * it.pricePerUnit
    }
    val laborCost = form.laborCost
    val overheadCost = form.overheadCost

    return materialCost + laborCost + overheadCost
}

fun calculateMargin(hpp: Double, sellingPrice: Double): Double {
    return ((sellingPrice - hpp) / sellingPrice) * 100
}
```

**Real-time Preview**:

```
┌─────────────────────────────────┐
│ 📊 Ringkasan Biaya              │
├─────────────────────────────────┤
│ Biaya Bahan:  Rp 12.000         │
│ Biaya Tenaga: Rp  2.000         │
│ Overhead:     Rp  1.000         │
├─────────────────────────────────┤
│ Total HPP:    Rp 15.000         │
│ Harga Jual:   Rp 25.000         │
│ Margin:       40.0%             │
└─────────────────────────────────┘
```

### 3.3 Product Composition Viewer

**Komponen**: `ProductCompositionScreen`

**Display**:

```kotlin
Column {
    // Product header
    ProductHeader(product)

    // Material breakdown table
    Text("Komposisi Bahan", style = MaterialTheme.typography.titleMedium)

    LazyColumn {
        items(product.materials) { material ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(material.name, fontWeight = FontWeight.Medium)
                    Text(
                        "${material.quantity} ${material.unit}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    "Rp ${(material.quantity * material.pricePerUnit).formatCurrency()}",
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()
        }
    }

    // Cost summary
    CostSummaryCard(
        materialCost = product.materials.sumOf { it.cost },
        laborCost = product.laborCost,
        overheadCost = product.overheadCost,
        totalHPP = product.hpp,
        sellingPrice = product.sellingPrice,
        margin = product.margin
    )

    // Action: Edit composition
    Button(onClick = { navigateToEdit() }) {
        Text("Edit Komposisi")
    }
}
```

---

## 🏭 4. MANAJEMEN PRODUKSI

### 4.1 Production Schedule List

**Komponen**: `ProductionListScreen`

**Features**:

#### Status Filter Tabs

```kotlin
val tabs = listOf("Semua", "Planned", "InProgress", "Completed", "Cancelled")
var selectedTab by remember { mutableStateOf("Semua") }

TabRow(selectedTabIndex = tabs.indexOf(selectedTab)) {
    tabs.forEach { tab ->
        Tab(
            selected = selectedTab == tab,
            onClick = { selectedTab = tab },
            text = { Text(tab) }
        )
    }
}
```

#### Date Range Filter

```kotlin
Row {
    OutlinedButton(onClick = { showStartDatePicker = true }) {
        Text(startDate.formatDate())
    }
    Text(" - ", modifier = Modifier.padding(horizontal = 8.dp))
    OutlinedButton(onClick = { showEndDatePicker = true }) {
        Text(endDate.formatDate())
    }
}

val filteredProductions = productions.filter { production ->
    production.date in startDate..endDate &&
    (selectedTab == "Semua" || production.status == selectedTab)
}
```

#### Production Card

```kotlin
@Composable
fun ProductionCard(production: Production) {
    Card {
        Column {
            // Header
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        production.productName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        production.date.formatDate(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Status badge
                StatusBadge(status = production.status)
            }

            Spacer(Modifier.height(8.dp))

            // Details
            Row {
                InfoChip(icon = Icons.Default.Numbers, text = "${production.quantity} ${production.unit}")
                Spacer(Modifier.width(8.dp))
                InfoChip(icon = Icons.Default.Person, text = production.operator)
            }

            // Progress (for InProgress status)
            if (production.status == "InProgress") {
                LinearProgressIndicator(
                    progress = production.progress / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${production.progress}% selesai", fontSize = 12.sp)
            }

            // Actions
            Row(horizontalArrangement = Arrangement.End) {
                if (production.status == "Planned") {
                    TextButton(onClick = { startProduction(production.id) }) {
                        Text("Mulai")
                    }
                }
                if (production.status == "InProgress") {
                    TextButton(onClick = { completeProduction(production.id) }) {
                        Text("Selesai")
                    }
                }
                TextButton(onClick = { navigateToDetail(production.id) }) {
                    Text("Detail")
                }
            }
        }
    }
}
```

### 4.2 Production Form (Schedule)

**Form Fields**:

```kotlin
data class ProductionForm(
    val productId: String,             // Required (dropdown/search)
    val productName: String,           // Auto-filled
    val quantity: Int,                 // Required (>0)
    val date: Date,                    // Required
    val operator: String,              // Required
    val notes: String,                 // Optional
    val status: String = "Planned"     // Default
)
```

**Validation**:

```kotlin
suspend fun validateProduction(form: ProductionForm): ValidationResult {
    val errors = mutableListOf<String>()

    // 1. Check product exists
    val product = productRepository.getById(form.productId)
        ?: return ValidationResult.Error("Produk tidak ditemukan")

    // 2. Check material availability
    product.materials.forEach { material ->
        val required = material.quantity * form.quantity
        val available = materialRepository.getById(material.materialId).stock

        if (required > available) {
            errors.add("${material.name}: butuh $required ${material.unit}, tersedia $available")
        }
    }

    if (errors.isNotEmpty()) {
        return ValidationResult.Error("Bahan tidak cukup:\n${errors.joinToString("\n")}")
    }

    // 3. Check date not in past
    if (form.date < Date()) {
        return ValidationResult.Error("Tanggal tidak boleh di masa lalu")
    }

    return ValidationResult.Success
}
```

**Material Availability Preview**:

```kotlin
@Composable
fun MaterialAvailabilityPreview(
    productId: String,
    quantity: Int
) {
    val product = remember { productRepository.getById(productId) }

    Column {
        Text("Kebutuhan Bahan:", fontWeight = FontWeight.Bold)

        product.materials.forEach { material ->
            val required = material.quantity * quantity
            val available = materialRepository.getById(material.materialId).stock
            val sufficient = available >= required

            Row {
                Text(material.name)
                Spacer(Modifier.weight(1f))
                Text(
                    "$required / $available ${material.unit}",
                    color = if (sufficient) Color.Green else Color.Red
                )
                Icon(
                    if (sufficient) Icons.Default.CheckCircle else Icons.Default.Error,
                    null,
                    tint = if (sufficient) Color.Green else Color.Red
                )
            }
        }
    }
}
```

### 4.3 Production Detail & Status Update

**Komponen**: `ProductionDetailScreen`

**Display**:

```kotlin
Column {
    // Production header
    ProductionHeader(production)

    // Product info
    ProductInfoCard(production.product)

    // Material usage breakdown
    Text("Bahan yang Digunakan", style = MaterialTheme.typography.titleMedium)
    LazyColumn {
        items(production.materials) { material ->
            MaterialUsageRow(
                name = material.name,
                quantityPerUnit = material.quantity,
                totalQuantity = material.quantity * production.quantity,
                unit = material.unit,
                totalCost = material.quantity * production.quantity * material.pricePerUnit
            )
        }
    }

    // Cost summary
    ProductionCostSummary(
        materialCost = production.totalMaterialCost,
        laborCost = production.laborCost,
        totalCost = production.totalCost
    )

    // Status timeline
    ProductionTimeline(
        created = production.createdAt,
        started = production.startedAt,
        completed = production.completedAt,
        currentStatus = production.status
    )

    // Action buttons
    ActionButtons(production)
}
```

**Status Update Logic**:

```kotlin
suspend fun startProduction(productionId: String): Result {
    val production = productionRepository.getById(productionId)

    // Validate status
    if (production.status != "Planned") {
        return Result.Error("Hanya produksi berstatus Planned yang bisa dimulai")
    }

    // Reserve materials (reduce stock)
    production.materials.forEach { material ->
        val required = material.quantity * production.quantity
        materialRepository.reduceStock(material.materialId, required)

        // Create material history
        materialHistoryRepository.insert(
            MaterialHistory(
                materialId = material.materialId,
                type = "Usage",
                quantity = -required,
                relatedType = "Production",
                relatedId = productionId,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Update production status
    production.status = "InProgress"
    production.startedAt = System.currentTimeMillis()
    productionRepository.update(production)

    return Result.Success
}

suspend fun completeProduction(productionId: String): Result {
    val production = productionRepository.getById(productionId)

    // Validate status
    if (production.status != "InProgress") {
        return Result.Error("Hanya produksi InProgress yang bisa diselesaikan")
    }

    // Add product stock
    productRepository.addStock(
        production.productId,
        production.quantity
    )

    // Update production status
    production.status = "Completed"
    production.completedAt = System.currentTimeMillis()
    productionRepository.update(production)

    return Result.Success
}
```

### 4.4 Production History

**Komponen**: `ProductionHistoryScreen`

**Filters**:

- Date range
- Product
- Operator
- Status

**Display**:

```kotlin
LazyColumn {
    items(filteredProductions.groupBy { it.date.toMonthYear() }) { (month, productions) ->
        // Month header
        SectionHeader(month)

        // Productions in this month
        productions.forEach { production ->
            ProductionHistoryCard(
                production = production,
                onViewDetail = { navigateToDetail(production.id) }
            )
        }

        // Month summary
        MonthSummary(
            totalProductions = productions.size,
            totalQuantity = productions.sumOf { it.quantity },
            totalCost = productions.sumOf { it.totalCost }
        )
    }
}
```

---

## 📊 5. ANALYTICS & REPORTS

### 5.1 Production Efficiency

```kotlin
fun calculateProductionEfficiency(
    startDate: Date,
    endDate: Date
): ProductionEfficiency {
    val productions = productionRepository.getByDateRange(startDate, endDate)

    val completed = productions.filter { it.status == "Completed" }
    val planned = productions.count()

    val avgCompletionTime = completed.map { production ->
        (production.completedAt - production.startedAt) / 1000 / 60  // minutes
    }.average()

    return ProductionEfficiency(
        completionRate = (completed.size.toFloat() / planned) * 100,
        avgCompletionTime = avgCompletionTime,
        totalOutput = completed.sumOf { it.quantity },
        onTimeRate = calculateOnTimeRate(completed)
    )
}
```

### 5.2 Material Usage Report

```kotlin
fun generateMaterialUsageReport(month: Int, year: Int): List<MaterialUsageReport> {
    val productions = productionRepository.getByMonth(month, year)
        .filter { it.status == "Completed" }

    val materialUsage = mutableMapOf<String, Float>()

    productions.forEach { production ->
        production.materials.forEach { material ->
            val totalUsed = material.quantity * production.quantity
            materialUsage[material.materialId] =
                materialUsage.getOrDefault(material.materialId, 0f) + totalUsed
        }
    }

    return materialUsage.map { (materialId, quantity) ->
        val material = materialRepository.getById(materialId)
        MaterialUsageReport(
            materialName = material.name,
            totalUsed = quantity,
            unit = material.unit,
            totalCost = quantity * material.pricePerUnit,
            percentageOfTotal = (quantity / materialUsage.values.sum()) * 100
        )
    }.sortedByDescending { it.totalCost }
}
```

---

## 🔔 6. NOTIFICATIONS & ALERTS

### 6.1 Low Stock Alert

```kotlin
fun checkLowStockAlerts(): List<Alert> {
    return materials
        .filter { it.stock < it.minStock }
        .map { material ->
            Alert(
                type = "LowStock",
                severity = when {
                    material.stock == 0f -> "Critical"
                    material.stock < material.minStock * 0.3 -> "High"
                    else -> "Medium"
                },
                title = "${material.name} menipis",
                message = "Stock: ${material.stock} ${material.unit} (Min: ${material.minStock})",
                action = "Restock",
                actionRoute = Screen.MaterialRestock.createRoute(material.id)
            )
        }
}
```

### 6.2 Production Schedule Reminder

```kotlin
fun checkProductionSchedule(): List<Alert> {
    val today = Date()
    val tomorrow = Date().addDays(1)

    return productions
        .filter {
            it.status == "Planned" &&
            it.date in today..tomorrow
        }
        .map { production ->
            Alert(
                type = "ProductionReminder",
                severity = "Medium",
                title = "Produksi dijadwalkan",
                message = "${production.productName} (${production.quantity} ${production.unit})",
                action = "Lihat Detail",
                actionRoute = Screen.ProductionDetail.createRoute(production.id)
            )
        }
}
```

---

## 📝 7. DATA PERSISTENCE

### 7.1 Local Database Schema

```kotlin
// Room Database Entities

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val stock: Float,
    val unit: String,
    val minStock: Float,
    val pricePerUnit: Double,
    val supplier: String?,
    val lastRestock: Long?
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String?,
    val unit: String,
    val stock: Int,
    val sellingPrice: Double,
    val laborCost: Double,
    val overheadCost: Double
)

@Entity(
    tableName = "product_materials",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProductMaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val materialId: String,
    val quantity: Float
)

@Entity(tableName = "productions")
data class ProductionEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val quantity: Int,
    val date: Long,
    val operator: String,
    val status: String,  // Planned, InProgress, Completed, Cancelled
    val notes: String?,
    val createdAt: Long,
    val startedAt: Long?,
    val completedAt: Long?
)
```

---

## 🎯 8. BEST PRACTICES

### 8.1 Material Management

- ✅ Set realistic min stock levels (3-7 days usage)
- ✅ Regular stock audits (weekly)
- ✅ Multiple suppliers for critical materials
- ✅ FIFO (First In First Out) for perishables

### 8.2 Product Costing

- ✅ Update material prices regularly
- ✅ Include all costs (labor, overhead)
- ✅ Review margins quarterly
- ✅ Consider market pricing

### 8.3 Production Planning

- ✅ Plan based on demand forecasts
- ✅ Allow buffer time for delays
- ✅ Schedule high-demand products early in week
- ✅ Batch similar products to optimize setup time

---

## 📚 Future Enhancements

- [ ] Barcode scanning untuk material & produk
- [ ] Batch production dengan auto-splitting
- [ ] Production templates (copy from history)
- [ ] Material expiry tracking
- [ ] Supplier management & purchase orders
- [ ] Production quality control checklist
- [ ] Machine/equipment tracking
- [ ] Multi-warehouse support
