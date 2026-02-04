# 💰 FITUR ROLE KEUANGAN

Dokumentasi lengkap fitur-fitur yang tersedia untuk role Keuangan dalam aplikasi SI-UMKM Mobile.

---

## 📊 1. DASHBOARD KEUANGAN

### 1.1 Financial Overview

**Komponen**: `FinanceDashboardScreen`

**Metrics Ditampilkan**:

```kotlin
data class FinancialStats(
    val totalSaldo: Double,              // Current balance
    val pemasukanToday: Double,          // Income today
    val pengeluaranToday: Double,        // Expense today
    val pemasukanMonth: Double,          // Income this month
    val pengeluaranMonth: Double,        // Expense this month
    val netProfitMonth: Double,          // Net profit/loss this month
    val totalUtang: Double,              // Total debt
    val totalPiutang: Double             // Total receivable
)
```

**Balance Visibility Toggle**:

```kotlin
var saldoVisible by remember { mutableStateOf(false) }

Row {
    Text(
        text = if (saldoVisible) {
            "Rp ${totalSaldo.formatCurrency()}"
        } else {
            "Rp ••••••••"
        },
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )

    IconButton(onClick = { saldoVisible = !saldoVisible }) {
        Icon(
            if (saldoVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = null
        )
    }
}
```

### 1.2 Income vs Expense Chart

**Chart Type**: Stacked Bar Chart (Daily/Weekly/Monthly)

```kotlin
@Composable
fun IncomeExpenseChart(
    period: Period,  // Daily, Weekly, Monthly
    data: List<ChartData>
) {
    val maxValue = data.maxOf { maxOf(it.income, it.expense) }

    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        data.forEachIndexed { index, item ->
            val x = index * barWidth

            // Income bar (green)
            drawRect(
                color = Color(0xFF10B981),
                topLeft = Offset(x, calculateY(item.income, maxValue)),
                size = Size(barWidth * 0.4f, calculateHeight(item.income, maxValue))
            )

            // Expense bar (red)
            drawRect(
                color = Color(0xFFEF4444),
                topLeft = Offset(x + barWidth * 0.5f, calculateY(item.expense, maxValue)),
                size = Size(barWidth * 0.4f, calculateHeight(item.expense, maxValue))
            )
        }
    }
}
```

### 1.3 Recent Transactions

**Display**: Last 5 transactions (mix of income & expense)

```kotlin
@Composable
fun RecentTransactionList(transactions: List<Transaction>) {
    Column {
        Text(
            "Transaksi Terakhir",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        transactions.take(5).forEach { transaction ->
            TransactionItem(
                title = transaction.title,
                category = transaction.category,
                amount = transaction.amount,
                type = transaction.type,  // Pemasukan / Pengeluaran
                date = transaction.date,
                onClick = { navigateToDetail(transaction.id) }
            )
        }

        TextButton(
            onClick = { navigateToTransactionHistory() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Lihat Semua")
        }
    }
}

@Composable
fun TransactionItem(
    title: String,
    category: String,
    amount: Double,
    type: String,
    date: Date
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Icon based on category
        CategoryIcon(category)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Row {
                Chip(category)
                Spacer(Modifier.width(8.dp))
                Text(
                    date.formatTime(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Text(
            "${if (type == "Pemasukan") "+" else "-"} Rp ${amount.formatCurrency()}",
            fontWeight = FontWeight.Bold,
            color = if (type == "Pemasukan") Color(0xFF10B981) else Color(0xFFEF4444)
        )
    }
}
```

### 1.4 Quick Actions

**Actions**:

1. **Tambah Pemasukan** → `transaction_form?type=pemasukan`
2. **Tambah Pengeluaran** → `transaction_form?type=pengeluaran`
3. **Analisis HPP-BEP** → `hpp_bep_analysis`
4. **Lihat Utang** → `debt_receivable?tab=utang`

---

## 📈 2. ANALISIS HPP & BEP

### 2.1 HPP (Harga Pokok Penjualan) Analysis

**Komponen**: `HppBepAnalysisScreen` - Tab HPP

**Purpose**: Menghitung biaya produksi sesungguhnya untuk setiap produk

**Formula**:

```kotlin
HPP = Material Cost + Labor Cost + Overhead Cost

Material Cost = Σ(quantity × price per unit) untuk semua bahan
Labor Cost = Biaya tenaga kerja per unit
Overhead Cost = Biaya operasional per unit (listrik, sewa, dll)
```

**Implementation**:

```kotlin
@Composable
fun HPPAnalysisTab() {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Column {
        // Product selector
        ProductDropdown(
            products = products,
            selectedProduct = selectedProduct,
            onProductSelect = { selectedProduct = it }
        )

        selectedProduct?.let { product ->
            // Material breakdown
            Card {
                Text("Rincian Biaya Bahan", fontWeight = FontWeight.Bold)

                product.materials.forEach { material ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(material.name)
                            Text(
                                "${material.quantity} ${material.unit}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            "Rp ${(material.quantity * material.pricePerUnit).formatCurrency()}",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Divider(Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Biaya Bahan", fontWeight = FontWeight.Bold)
                    Text(
                        "Rp ${product.materialCost.formatCurrency()}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Labor & overhead
            Card {
                CostRow("Biaya Tenaga Kerja", product.laborCost)
                CostRow("Biaya Overhead", product.overheadCost)
            }

            // HPP summary
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("HPP (Harga Pokok Penjualan)", fontWeight = FontWeight.Bold)
                    Text(
                        "Rp ${product.hpp.formatCurrency()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Pricing analysis
            Card {
                Column {
                    Text("Analisis Harga Jual", fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("HPP")
                        Text("Rp ${product.hpp.formatCurrency()}")
                    }

                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Harga Jual Saat Ini")
                        Text("Rp ${product.sellingPrice.formatCurrency()}")
                    }

                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Profit per Unit")
                        Text(
                            "Rp ${(product.sellingPrice - product.hpp).formatCurrency()}",
                            color = if (product.sellingPrice > product.hpp)
                                Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Margin", fontWeight = FontWeight.Bold)
                        Text(
                            "${product.margin}%",
                            fontWeight = FontWeight.Bold,
                            color = when {
                                product.margin >= 40 -> Color(0xFF10B981)
                                product.margin >= 20 -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }
                        )
                    }

                    // Margin indicator
                    LinearProgressIndicator(
                        progress = (product.margin / 100f).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth(),
                        color = when {
                            product.margin >= 40 -> Color(0xFF10B981)
                            product.margin >= 20 -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        }
                    )
                }
            }

            // Recommendations
            if (product.margin < 20) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF3C7)
                    )
                ) {
                    Row {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFF59E0B))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Margin Rendah", fontWeight = FontWeight.Bold)
                            Text(
                                "Margin kurang dari 20%. Pertimbangkan untuk:\n" +
                                "• Menaikkan harga jual\n" +
                                "• Mencari bahan lebih murah\n" +
                                "• Mengoptimalkan proses produksi",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
```

**Margin Classification**:

- 🟢 **Excellent**: ≥40% (Hijau)
- 🟡 **Good**: 20-39% (Kuning)
- 🔴 **Low**: <20% (Merah)

### 2.2 BEP (Break Even Point) Analysis

**Komponen**: `HppBepAnalysisScreen` - Tab BEP

**Purpose**: Menghitung titik impas (berapa unit harus terjual untuk balik modal)

**Formula**:

```kotlin
BEP (units) = Fixed Costs / (Selling Price per Unit - Variable Cost per Unit)

BEP (Rupiah) = BEP (units) × Selling Price per Unit

Margin of Safety = ((Actual Sales - BEP Sales) / Actual Sales) × 100%
```

**Implementation**:

```kotlin
@Composable
fun BEPAnalysisTab() {
    var fixedCosts by remember { mutableStateOf("") }
    var variableCostPerUnit by remember { mutableStateOf("") }
    var sellingPricePerUnit by remember { mutableStateOf("") }
    var actualSalesUnits by remember { mutableStateOf("") }

    val bepResult = remember(fixedCosts, variableCostPerUnit, sellingPricePerUnit) {
        calculateBEP(
            fixedCosts = fixedCosts.toDoubleOrNull() ?: 0.0,
            variableCost = variableCostPerUnit.toDoubleOrNull() ?: 0.0,
            sellingPrice = sellingPricePerUnit.toDoubleOrNull() ?: 0.0
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Analisis Titik Impas (BEP)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // Input form
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Input Data", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = fixedCosts,
                    onValueChange = { fixedCosts = it },
                    label = { Text("Biaya Tetap (Fixed Costs)") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text("Sewa, gaji tetap, utilitas, dll (per bulan)")
                    }
                )

                OutlinedTextField(
                    value = variableCostPerUnit,
                    onValueChange = { variableCostPerUnit = it },
                    label = { Text("Biaya Variabel per Unit") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text("Bahan baku, tenaga kerja langsung per unit")
                    }
                )

                OutlinedTextField(
                    value = sellingPricePerUnit,
                    onValueChange = { sellingPricePerUnit = it },
                    label = { Text("Harga Jual per Unit") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = actualSalesUnits,
                    onValueChange = { actualSalesUnits = it },
                    label = { Text("Penjualan Aktual (opsional)") },
                    suffix = { Text("unit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text("Untuk menghitung Margin of Safety")
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Results
        if (bepResult.isValid) {
            // BEP in units
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("BEP (Unit)", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "${bepResult.bepUnits.roundToInt()} unit",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Harus menjual ${bepResult.bepUnits.roundToInt()} unit untuk mencapai titik impas",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // BEP in currency
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("BEP (Rupiah)", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "Rp ${bepResult.bepRupiah.formatCurrency()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "Total penjualan yang harus dicapai",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Contribution margin
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Contribution Margin per Unit", fontSize = 12.sp)
                        Text(
                            "Rp ${bepResult.contributionMargin.formatCurrency()}",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("CM Ratio", fontSize = 12.sp)
                        Text(
                            "${bepResult.cmRatio}%",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Margin of Safety (if actual sales provided)
            actualSalesUnits.toIntOrNull()?.let { actualUnits ->
                val marginOfSafety = calculateMarginOfSafety(
                    actualSales = actualUnits,
                    bepUnits = bepResult.bepUnits
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            marginOfSafety >= 30 -> Color(0xFFD1FAE5)
                            marginOfSafety >= 10 -> Color(0xFFFEF3C7)
                            else -> Color(0xFFFEE2E2)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Margin of Safety", fontWeight = FontWeight.Bold)

                        Text(
                            "${marginOfSafety}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                marginOfSafety >= 30 -> Color(0xFF10B981)
                                marginOfSafety >= 10 -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }
                        )

                        Text(
                            when {
                                marginOfSafety >= 30 -> "✅ Sangat Aman"
                                marginOfSafety >= 10 -> "⚠️ Cukup Aman"
                                marginOfSafety > 0 -> "🔴 Risiko Tinggi"
                                else -> "❌ Di Bawah BEP"
                            },
                            fontSize = 12.sp
                        )

                        if (marginOfSafety > 0) {
                            Text(
                                "Penjualan dapat turun ${marginOfSafety}% sebelum mencapai BEP",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                "Penjualan saat ini belum mencapai BEP. Perlu ${bepResult.bepUnits.roundToInt() - actualUnits} unit lagi",
                                fontSize = 12.sp,
                                color = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Visualization
            BEPChart(
                fixedCosts = fixedCosts.toDouble(),
                bepUnits = bepResult.bepUnits.roundToInt(),
                sellingPrice = sellingPricePerUnit.toDouble(),
                variableCost = variableCostPerUnit.toDouble(),
                actualSales = actualSalesUnits.toIntOrNull()
            )

            // Scenario analysis
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Analisis Skenario", fontWeight = FontWeight.Bold)

                    // What if: increase price by 10%
                    val newPriceScenario = sellingPricePerUnit.toDouble() * 1.1
                    val newBEP = calculateBEP(
                        fixedCosts = fixedCosts.toDouble(),
                        variableCost = variableCostPerUnit.toDouble(),
                        sellingPrice = newPriceScenario
                    )

                    ScenarioRow(
                        scenario = "Jika harga naik 10%",
                        result = "BEP: ${newBEP.bepUnits.roundToInt()} unit",
                        impact = "Turun ${(bepResult.bepUnits - newBEP.bepUnits).roundToInt()} unit",
                        isPositive = true
                    )

                    // What if: reduce variable cost by 10%
                    val newCostScenario = variableCostPerUnit.toDouble() * 0.9
                    val newBEP2 = calculateBEP(
                        fixedCosts = fixedCosts.toDouble(),
                        variableCost = newCostScenario,
                        sellingPrice = sellingPricePerUnit.toDouble()
                    )

                    ScenarioRow(
                        scenario = "Jika biaya variabel turun 10%",
                        result = "BEP: ${newBEP2.bepUnits.roundToInt()} unit",
                        impact = "Turun ${(bepResult.bepUnits - newBEP2.bepUnits).roundToInt()} unit",
                        isPositive = true
                    )
                }
            }
        } else {
            // Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F4F6)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Masukkan data untuk melihat analisis BEP",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

data class BEPResult(
    val bepUnits: Double,
    val bepRupiah: Double,
    val contributionMargin: Double,
    val cmRatio: Double,
    val isValid: Boolean
)

fun calculateBEP(
    fixedCosts: Double,
    variableCost: Double,
    sellingPrice: Double
): BEPResult {
    if (sellingPrice <= variableCost || sellingPrice == 0.0) {
        return BEPResult(0.0, 0.0, 0.0, 0.0, false)
    }

    val contributionMargin = sellingPrice - variableCost
    val cmRatio = (contributionMargin / sellingPrice) * 100
    val bepUnits = fixedCosts / contributionMargin
    val bepRupiah = bepUnits * sellingPrice

    return BEPResult(
        bepUnits = bepUnits,
        bepRupiah = bepRupiah,
        contributionMargin = contributionMargin,
        cmRatio = cmRatio.roundToDecimal(2),
        isValid = true
    )
}

fun calculateMarginOfSafety(actualSales: Int, bepUnits: Double): Double {
    return ((actualSales - bepUnits) / actualSales) * 100
}
```

**BEP Chart Visualization**:

```kotlin
@Composable
fun BEPChart(
    fixedCosts: Double,
    bepUnits: Int,
    sellingPrice: Double,
    variableCost: Double,
    actualSales: Int?
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
    ) {
        val maxUnits = (bepUnits * 1.5).toInt()

        // Draw grid
        drawGridLines()

        // Draw fixed cost line (horizontal)
        drawLine(
            color = Color.Gray,
            start = Offset(0f, calculateY(fixedCosts)),
            end = Offset(size.width, calculateY(fixedCosts)),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )

        // Draw total cost line (fixed + variable)
        val costPath = Path().apply {
            moveTo(0f, calculateY(fixedCosts))
            lineTo(
                size.width,
                calculateY(fixedCosts + (variableCost * maxUnits))
            )
        }
        drawPath(
            path = costPath,
            color = Color.Red,
            style = Stroke(width = 3f)
        )

        // Draw revenue line
        val revenuePath = Path().apply {
            moveTo(0f, calculateY(0.0))
            lineTo(
                size.width,
                calculateY(sellingPrice * maxUnits)
            )
        }
        drawPath(
            path = revenuePath,
            color = Color.Green,
            style = Stroke(width = 3f)
        )

        // Mark BEP point
        val bepX = (bepUnits.toFloat() / maxUnits) * size.width
        val bepY = calculateY(sellingPrice * bepUnits)

        drawCircle(
            color = Color.Blue,
            radius = 8f,
            center = Offset(bepX, bepY)
        )

        // Mark actual sales (if provided)
        actualSales?.let {
            val actualX = (it.toFloat() / maxUnits) * size.width
            val actualY = calculateY(sellingPrice * it)

            drawLine(
                color = Color.Blue,
                start = Offset(actualX, 0f),
                end = Offset(actualX, size.height),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        }
    }
}
```

---

## 💳 3. MANAJEMEN TRANSAKSI

### 3.1 Transaction History

**Komponen**: `TransactionHistoryScreen`

**Features**:

#### Filter & Sort

```kotlin
data class TransactionFilter(
    val type: String,         // Semua, Pemasukan, Pengeluaran
    val category: String,     // Semua, Bahan Baku, Penjualan, Operasional, dll
    val dateRange: DateRange, // Today, Week, Month, Custom
    val minAmount: Double?,
    val maxAmount: Double?
)

val filteredTransactions = transactions.filter { transaction ->
    (filter.type == "Semua" || transaction.type == filter.type) &&
    (filter.category == "Semua" || transaction.category == filter.category) &&
    transaction.date in filter.dateRange &&
    (filter.minAmount == null || transaction.amount >= filter.minAmount) &&
    (filter.maxAmount == null || transaction.amount <= filter.maxAmount)
}
```

**Categories**:

**Pemasukan**:

- Penjualan
- Piutang Diterima
- Lain-lain

**Pengeluaran**:

- Bahan Baku
- Gaji
- Sewa
- Utilitas (Listrik, Air)
- Pemasaran
- Operasional
- Utang Dibayar
- Lain-lain

#### Transaction List Display

```kotlin
LazyColumn {
    items(transactions.groupBy { it.date.toDateString() }) { (date, txs) ->
        // Date header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3F4F6))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(date, fontWeight = FontWeight.Bold)

            // Daily summary
            val dailyIncome = txs.filter { it.type == "Pemasukan" }.sumOf { it.amount }
            val dailyExpense = txs.filter { it.type == "Pengeluaran" }.sumOf { it.amount }

            Text(
                "${if (dailyIncome > dailyExpense) "+" else "-"} Rp ${abs(dailyIncome - dailyExpense).formatCurrency()}",
                fontWeight = FontWeight.Bold,
                color = if (dailyIncome > dailyExpense) Color(0xFF10B981) else Color(0xFFEF4444)
            )
        }

        // Transactions for this date
        txs.forEach { transaction ->
            TransactionCard(
                transaction = transaction,
                onClick = { navigateToDetail(transaction.id) }
            )
        }
    }
}
```

#### Summary Stats

```kotlin
@Composable
fun TransactionSummary(transactions: List<Transaction>) {
    val totalIncome = transactions.filter { it.type == "Pemasukan" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "Pengeluaran" }.sumOf { it.amount }
    val netProfit = totalIncome - totalExpense

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryCard(
            title = "Pemasukan",
            amount = totalIncome,
            color = Color(0xFF10B981)
        )
        SummaryCard(
            title = "Pengeluaran",
            amount = totalExpense,
            color = Color(0xFFEF4444)
        )
        SummaryCard(
            title = "Net",
            amount = netProfit,
            color = if (netProfit >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
        )
    }
}
```

### 3.2 Transaction Form

**Komponen**: `TransactionFormScreen`

**Form Fields**:

```kotlin
data class TransactionForm(
    val type: String,              // Pemasukan / Pengeluaran (pre-selected from route)
    val title: String,             // Required
    val category: String,          // Required (dropdown based on type)
    val amount: Double,            // Required (>0)
    val date: Date,                // Required (default today)
    val time: Time,                // Required (default now)
    val paymentMethod: String,     // Required (Cash, Transfer, E-Wallet)
    val notes: String,             // Optional
    val attachments: List<String>  // Optional (future: receipt images)
)
```

**Validation**:

```kotlin
fun validateTransaction(form: TransactionForm): ValidationResult {
    val errors = mutableListOf<String>()

    if (form.title.isBlank()) {
        errors.add("Judul tidak boleh kosong")
    }

    if (form.category.isBlank()) {
        errors.add("Kategori harus dipilih")
    }

    if (form.amount <= 0) {
        errors.add("Jumlah harus lebih dari 0")
    }

    if (form.paymentMethod.isBlank()) {
        errors.add("Metode pembayaran harus dipilih")
    }

    return if (errors.isEmpty()) {
        ValidationResult.Success
    } else {
        ValidationResult.Error(errors.joinToString("\n"))
    }
}
```

**Business Logic**:

```kotlin
suspend fun saveTransaction(form: TransactionForm): Result {
    // 1. Validate
    val validation = validateTransaction(form)
    if (validation is ValidationResult.Error) {
        return Result.Error(validation.message)
    }

    // 2. Create transaction
    val transaction = Transaction(
        id = generateId(),
        type = form.type,
        title = form.title,
        category = form.category,
        amount = form.amount,
        date = form.date,
        time = form.time,
        paymentMethod = form.paymentMethod,
        notes = form.notes,
        createdAt = System.currentTimeMillis()
    )

    transactionRepository.insert(transaction)

    // 3. Update balance
    val currentBalance = settingsRepository.getBalance()
    val newBalance = if (form.type == "Pemasukan") {
        currentBalance + form.amount
    } else {
        currentBalance - form.amount
    }
    settingsRepository.updateBalance(newBalance)

    return Result.Success
}
```

---

## 💰 4. MANAJEMEN UTANG & PIUTANG

### 4.1 Debt & Receivable List

**Komponen**: `DebtReceivableScreen`

**Tab Structure**:

1. **Utang** (Debt - money we owe)
2. **Piutang** (Receivable - money owed to us)

**Features**:

#### Status Filters

```kotlin
val statuses = listOf("Semua", "Belum Lunas", "Lunas", "Jatuh Tempo")
var selectedStatus by remember { mutableStateOf("Semua") }

val filteredDebts = debts.filter { debt ->
    when (selectedStatus) {
        "Semua" -> true
        "Belum Lunas" -> debt.remainingAmount > 0
        "Lunas" -> debt.remainingAmount == 0.0
        "Jatuh Tempo" -> debt.dueDate < Date() && debt.remainingAmount > 0
        else -> true
    }
}
```

#### Debt/Receivable Card

```kotlin
@Composable
fun DebtCard(debt: Debt, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when {
                debt.remainingAmount == 0.0 -> Color(0xFFD1FAE5)  // Lunas
                debt.dueDate < Date() -> Color(0xFFFEE2E2)         // Overdue
                else -> Color.White
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        debt.creditorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        debt.description,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Status badge
                Badge(
                    containerColor = when {
                        debt.remainingAmount == 0.0 -> Color(0xFF10B981)
                        debt.dueDate < Date() -> Color(0xFFEF4444)
                        else -> Color(0xFFF59E0B)
                    }
                ) {
                    Text(
                        when {
                            debt.remainingAmount == 0.0 -> "Lunas"
                            debt.dueDate < Date() -> "Jatuh Tempo"
                            else -> "Aktif"
                        },
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Amount info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "Rp ${debt.totalAmount.formatCurrency()}",
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text("Terbayar", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "Rp ${debt.paidAmount.formatCurrency()}",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981)
                    )
                }

                Column {
                    Text("Sisa", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "Rp ${debt.remainingAmount.formatCurrency()}",
                        fontWeight = FontWeight.Bold,
                        color = if (debt.remainingAmount > 0) Color(0xFFEF4444) else Color(0xFF10B981)
                    )
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = (debt.paidAmount / debt.totalAmount).toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFF10B981)
            )

            // Due date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (debt.dueDate < Date()) Color.Red else Color.Gray
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Jatuh tempo: ${debt.dueDate.formatDate()}",
                    fontSize = 12.sp,
                    color = if (debt.dueDate < Date()) Color.Red else Color.Gray
                )

                if (debt.dueDate < Date() && debt.remainingAmount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "(${debt.dueDate.getDaysUntilNow()} hari terlewat)",
                        fontSize = 12.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
```

#### Summary Stats

```kotlin
@Composable
fun DebtSummary(debts: List<Debt>) {
    val totalDebt = debts.sumOf { it.totalAmount }
    val totalPaid = debts.sumOf { it.paidAmount }
    val totalRemaining = debts.sumOf { it.remainingAmount }
    val overdueCount = debts.count { it.dueDate < Date() && it.remainingAmount > 0 }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Utang", fontSize = 12.sp, color = Color.Gray)
            Text(
                "Rp ${totalDebt.formatCurrency()}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sisa", fontSize = 12.sp, color = Color.Gray)
            Text(
                "Rp ${totalRemaining.formatCurrency()}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFEF4444)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Jatuh Tempo", fontSize = 12.sp, color = Color.Gray)
            Text(
                "$overdueCount",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (overdueCount > 0) Color(0xFFEF4444) else Color(0xFF10B981)
            )
        }
    }
}
```

### 4.2 Debt Form (Add/Edit)

**Form Fields**:

```kotlin
data class DebtForm(
    val type: String,          // Utang / Piutang
    val creditorName: String,  // Nama kreditor (untuk utang) / debitor (untuk piutang)
    val totalAmount: Double,   // Total jumlah
    val dueDate: Date,         // Tanggal jatuh tempo
    val description: String,   // Deskripsi
    val category: String       // Optional: kategori utang/piutang
)
```

### 4.3 Payment History

**Komponen**: `DebtReceivableHistoryScreen`

**Display**:

```kotlin
Column {
    // Debt header
    DebtHeaderCard(debt)

    // Payment history timeline
    Text("Riwayat Pembayaran", style = MaterialTheme.typography.titleMedium)

    LazyColumn {
        items(debt.payments) { payment ->
            PaymentCard(
                amount = payment.amount,
                date = payment.date,
                method = payment.method,
                notes = payment.notes
            )
        }
    }

    // Add payment button (if not fully paid)
    if (debt.remainingAmount > 0) {
        Button(
            onClick = { showPaymentDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bayar")
        }
    }
}
```

**Payment Dialog**:

```kotlin
@Composable
fun PaymentDialog(
    debt: Debt,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, method: String, notes: String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bayar ${if (debt.type == "Utang") "Utang" else "Piutang"}") },
        text = {
            Column {
                Text(
                    "Sisa: Rp ${debt.remainingAmount.formatCurrency()}",
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Jumlah Pembayaran") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        Text("Max: Rp ${debt.remainingAmount.formatCurrency()}")
                    }
                )

                // Quick amount buttons
                Row {
                    OutlinedButton(
                        onClick = { amount = (debt.remainingAmount / 2).toString() }
                    ) {
                        Text("50%")
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { amount = debt.remainingAmount.toString() }
                    ) {
                        Text("Lunas")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Payment method
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Metode Pembayaran") }
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan (opsional)") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    if (amountDouble > 0 && amountDouble <= debt.remainingAmount) {
                        onConfirm(amountDouble, paymentMethod, notes)
                    }
                }
            ) {
                Text("Bayar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
```

**Payment Processing**:

```kotlin
suspend fun processPayment(
    debtId: String,
    amount: Double,
    method: String,
    notes: String
): Result {
    val debt = debtRepository.getById(debtId)

    // Validate amount
    if (amount <= 0 || amount > debt.remainingAmount) {
        return Result.Error("Jumlah pembayaran tidak valid")
    }

    // Create payment record
    val payment = Payment(
        id = generateId(),
        debtId = debtId,
        amount = amount,
        method = method,
        notes = notes,
        date = Date(),
        timestamp = System.currentTimeMillis()
    )

    paymentRepository.insert(payment)

    // Update debt
    debt.paidAmount += amount
    debt.remainingAmount -= amount
    debtRepository.update(debt)

    // Create transaction record
    val transaction = Transaction(
        id = generateId(),
        type = if (debt.type == "Utang") "Pengeluaran" else "Pemasukan",
        category = if (debt.type == "Utang") "Utang Dibayar" else "Piutang Diterima",
        title = "Pembayaran ${debt.type}: ${debt.creditorName}",
        amount = amount,
        date = Date(),
        paymentMethod = method,
        notes = notes
    )

    transactionRepository.insert(transaction)

    // Update balance
    val currentBalance = settingsRepository.getBalance()
    val newBalance = if (debt.type == "Utang") {
        currentBalance - amount
    } else {
        currentBalance + amount
    }
    settingsRepository.updateBalance(newBalance)

    return Result.Success
}
```

---

## 📊 5. FINANCIAL REPORTS

### 5.1 Monthly Report

```kotlin
data class MonthlyReport(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val netProfit: Double,
    val incomeByCategory: Map<String, Double>,
    val expenseByCategory: Map<String, Double>,
    val transactionCount: Int,
    val avgTransactionAmount: Double,
    val topExpenseCategory: String,
    val topIncomeSource: String
)

fun generateMonthlyReport(month: Int, year: Int): MonthlyReport {
    val transactions = transactionRepository.getByMonth(month, year)

    val incomeTransactions = transactions.filter { it.type == "Pemasukan" }
    val expenseTransactions = transactions.filter { it.type == "Pengeluaran" }

    val totalIncome = incomeTransactions.sumOf { it.amount }
    val totalExpense = expenseTransactions.sumOf { it.amount }

    val incomeByCategory = incomeTransactions
        .groupBy { it.category }
        .mapValues { it.value.sumOf { tx -> tx.amount } }

    val expenseByCategory = expenseTransactions
        .groupBy { it.category }
        .mapValues { it.value.sumOf { tx -> tx.amount } }

    return MonthlyReport(
        month = month,
        year = year,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        netProfit = totalIncome - totalExpense,
        incomeByCategory = incomeByCategory,
        expenseByCategory = expenseByCategory,
        transactionCount = transactions.size,
        avgTransactionAmount = transactions.map { it.amount }.average(),
        topExpenseCategory = expenseByCategory.maxByOrNull { it.value }?.key ?: "-",
        topIncomeSource = incomeByCategory.maxByOrNull { it.value }?.key ?: "-"
    )
}
```

### 5.2 Cash Flow Statement

```kotlin
data class CashFlowStatement(
    val period: DateRange,
    val operatingActivities: CashFlowCategory,
    val investingActivities: CashFlowCategory,
    val financingActivities: CashFlowCategory,
    val netCashFlow: Double,
    val openingBalance: Double,
    val closingBalance: Double
)

data class CashFlowCategory(
    val inflows: List<CashFlowItem>,
    val outflows: List<CashFlowItem>,
    val netFlow: Double
)

fun generateCashFlowStatement(startDate: Date, endDate: Date): CashFlowStatement {
    val transactions = transactionRepository.getByDateRange(startDate, endDate)

    // Categorize transactions
    val operating = transactions.filter {
        it.category in listOf("Penjualan", "Bahan Baku", "Gaji", "Utilitas", "Operasional")
    }

    val investing = transactions.filter {
        it.category in listOf("Peralatan", "Investasi")
    }

    val financing = transactions.filter {
        it.category in listOf("Utang", "Piutang", "Modal")
    }

    // ... calculate flows

    return CashFlowStatement(...)
}
```

---

## 🔔 6. FINANCIAL ALERTS

### 6.1 Low Balance Alert

```kotlin
fun checkLowBalanceAlert(): Alert? {
    val currentBalance = settingsRepository.getBalance()
    val minBalance = settingsRepository.getMinBalanceThreshold()

    return if (currentBalance < minBalance) {
        Alert(
            type = "LowBalance",
            severity = "High",
            title = "Saldo rendah",
            message = "Saldo Anda: Rp ${currentBalance.formatCurrency()}",
            action = "Lihat Transaksi",
            actionRoute = Screen.TransactionHistory.route
        )
    } else null
}
```

### 6.2 Overdue Debt Alert

```kotlin
fun checkOverdueDebts(): List<Alert> {
    return debtRepository.getAll()
        .filter { it.type == "Utang" && it.dueDate < Date() && it.remainingAmount > 0 }
        .map { debt ->
            Alert(
                type = "OverdueDebt",
                severity = "Critical",
                title = "Utang jatuh tempo",
                message = "${debt.creditorName}: Rp ${debt.remainingAmount.formatCurrency()}",
                action = "Bayar Sekarang",
                actionRoute = Screen.DebtHistory.createRoute(debt.id)
            )
        }
}
```

---

## 📝 7. BEST PRACTICES

### 7.1 Transaction Recording

- ✅ Catat semua transaksi segera setelah terjadi
- ✅ Gunakan kategori yang konsisten
- ✅ Simpan bukti transaksi (struk, invoice)
- ✅ Review transaksi harian setiap akhir hari

### 7.2 Cash Flow Management

- ✅ Maintain minimum balance buffer (1-2 bulan operating expense)
- ✅ Monitor cash flow trend weekly
- ✅ Plan for seasonal fluctuations
- ✅ Separate personal and business finances

### 7.3 Debt Management

- ✅ Pay overdue debts first
- ✅ Negotiate payment terms if needed
- ✅ Maintain good credit history
- ✅ Avoid unnecessary debt

---

## 📚 Future Enhancements

- [ ] Expense budgeting & tracking
- [ ] Profit margin analysis per product
- [ ] Tax calculation helper
- [ ] Bank account integration
- [ ] Multi-currency support
- [ ] Invoice generation
- [ ] Financial forecasting
- [ ] Export to accounting software
