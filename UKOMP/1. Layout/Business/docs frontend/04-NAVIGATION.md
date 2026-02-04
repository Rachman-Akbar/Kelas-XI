# 🧭 Navigation Architecture

Dokumentasi sistem navigasi dalam aplikasi SI-UMKM Mobile menggunakan Jetpack Navigation Compose.

---

## 📐 Navigation Structure

### Route Definition (Screen.kt)

```kotlin
sealed class Screen(val route: String) {
    // Auth
    data object Splash : Screen("splash")
    data object Login : Screen("login")

    // Produksi Module
    data object ProductionDashboard : Screen("production_dashboard")
    data object MaterialList : Screen("material_list")
    data object ProductList : Screen("product_list")
    // ... more routes
}
```

**Pattern**: Sealed class dengan data objects untuk type-safe navigation

### Navigation Host (AppNavigator.kt)

```kotlin
@Composable
fun AppNavigator(
    navController: NavHostController,
    userRole: String
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Composable routes
        composable(Screen.Splash.route) { SplashScreen(...) }
        composable(Screen.Login.route) { LoginScreen(...) }
        // ...
    }
}
```

---

## 🎯 Route Registry

### Authentication Routes

| Route    | Screen       | Access |
| -------- | ------------ | ------ |
| `splash` | SplashScreen | Public |
| `login`  | LoginScreen  | Public |

### Produksi Module Routes

| Route                              | Screen                    | Access                          |
| ---------------------------------- | ------------------------- | ------------------------------- |
| `production_dashboard`             | ProductionDashboardScreen | Produksi Only                   |
| `material_list`                    | MaterialListScreen        | Produksi Only                   |
| `material_restock/{materialId}`    | RestockMaterialScreen     | Produksi Only                   |
| `material_history/{materialId}`    | MaterialHistoryScreen     | Produksi Only                   |
| `product_list`                     | ProductListScreen         | Produksi, (Penjualan read-only) |
| `product_form`                     | ProductFormScreen         | Produksi Only                   |
| `product_composition/{productId}`  | ProductCompositionScreen  | Produksi Only                   |
| `production_list`                  | ProductionListScreen      | Produksi Only                   |
| `production_form`                  | ProductionFormScreen      | Produksi Only                   |
| `production_detail/{productionId}` | ProductionDetailScreen    | Produksi Only                   |
| `production_history`               | ProductionHistoryScreen   | Produksi Only                   |

### Keuangan Module Routes

| Route                   | Screen                      | Access        |
| ----------------------- | --------------------------- | ------------- |
| `finance_dashboard`     | FinanceDashboardScreen      | Keuangan Only |
| `hpp_bep_analysis`      | HppBepAnalysisScreen        | Keuangan Only |
| `debt_receivable`       | DebtReceivableScreen        | Keuangan Only |
| `debt_form`             | DebtFormScreen              | Keuangan Only |
| `debt_history/{debtId}` | DebtReceivableHistoryScreen | Keuangan Only |
| `transaction_history`   | TransactionHistoryScreen    | Keuangan Only |
| `transaction_form`      | TransactionFormScreen       | Keuangan Only |

### Penjualan Module Routes

| Route                          | Screen               | Access         |
| ------------------------------ | -------------------- | -------------- |
| `sales_dashboard`              | SalesDashboardScreen | Penjualan Only |
| `sales_pos`                    | SalesPOSScreen       | Penjualan Only |
| `sales_checkout`               | SalesCheckoutScreen  | Penjualan Only |
| `sales_history`                | SalesHistoryScreen   | Penjualan Only |
| `sales_detail/{transactionId}` | SalesDetailScreen    | Penjualan Only |
| `customer_list`                | CustomerListScreen   | Penjualan Only |
| `customer_form`                | CustomerFormScreen   | Penjualan Only |
| `customer_detail/{customerId}` | CustomerDetailScreen | Penjualan Only |
| `product_catalog`              | SalesCatalogScreen   | Penjualan Only |

### Shared Routes

| Route     | Screen        | Access    |
| --------- | ------------- | --------- |
| `profile` | ProfileScreen | All Roles |

---

## 🎨 Bottom Navigation Configuration

### Implementation (AppBottomNavigation.kt)

```kotlin
@Composable
fun AppBottomNavigation(
    currentRoute: String,
    userRole: String,
    onNavigate: (String) -> Unit
) {
    val items = when (userRole) {
        "Produksi" -> listOf(
            BottomNavItem(
                route = Screen.ProductionDashboard.route,
                icon = Icons.Default.Home,
                label = "Home"
            ),
            BottomNavItem(
                route = Screen.MaterialList.route,
                icon = ImageVector.vectorResource(R.drawable.ic_material),
                label = "Bahan Baku"
            ),
            BottomNavItem(
                route = Screen.ProductList.route,
                icon = Icons.Default.ShoppingBag,
                label = "Produk"
            ),
            BottomNavItem(
                route = Screen.ProductionList.route,
                icon = ImageVector.vectorResource(R.drawable.ic_production),
                label = "Produksi"
            )
        )
        "Keuangan" -> listOf(
            BottomNavItem(
                route = Screen.FinanceDashboard.route,
                icon = Icons.Default.Home,
                label = "Home"
            ),
            BottomNavItem(
                route = Screen.HppBepAnalysis.route,
                icon = Icons.Default.Analytics,
                label = "HPP-BEP"
            ),
            BottomNavItem(
                route = Screen.DebtReceivable.route,
                icon = Icons.Default.AccountBalance,
                label = "Utang"
            ),
            BottomNavItem(
                route = Screen.TransactionHistory.route,
                icon = Icons.Default.Receipt,
                label = "Transaksi"
            )
        )
        "Penjualan" -> listOf(
            BottomNavItem(
                route = Screen.SalesDashboard.route,
                icon = Icons.Default.Home,
                label = "Home"
            ),
            BottomNavItem(
                route = Screen.SalesPOS.route,
                icon = Icons.Default.PointOfSale,
                label = "Kasir"
            ),
            BottomNavItem(
                route = Screen.CustomerList.route,
                icon = Icons.Default.People,
                label = "Pelanggan"
            )
        )
        else -> emptyList()
    }

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, null) },
                label = { Text(item.label) }
            )
        }
    }
}
```

### Role-Based Navigation Items

**Produksi (4 items)**:

1. 🏠 Home → `production_dashboard`
2. 📦 Bahan Baku → `material_list`
3. 📋 Produk → `product_list`
4. 🏭 Produksi → `production_list`

**Keuangan (4 items)**:

1. 🏠 Home → `finance_dashboard`
2. 📊 HPP-BEP → `hpp_bep_analysis`
3. 💰 Utang → `debt_receivable`
4. 🧾 Transaksi → `transaction_history`

**Penjualan (3 items)**:

1. 🏠 Home → `sales_dashboard`
2. 💳 Kasir → `sales_pos`
3. 👥 Pelanggan → `customer_list`

---

## 🔗 Navigation Helpers

### Navigate with Parameters

```kotlin
// Definition in Screen.kt
data object ProductionDetail : Screen("production_detail/{productionId}") {
    fun createRoute(productionId: String) = "production_detail/$productionId"
}

// Usage in composable
onItemClick = { production ->
    navController.navigate(
        Screen.ProductionDetail.createRoute(production.id)
    )
}

// Retrieve parameter in destination
composable(
    route = Screen.ProductionDetail.route,
    arguments = listOf(
        navArgument("productionId") { type = NavType.StringType }
    )
) { backStackEntry ->
    val productionId = backStackEntry.arguments?.getString("productionId")
    ProductionDetailScreen(
        productionId = productionId ?: "",
        onNavigateBack = { navController.popBackStack() }
    )
}
```

### Navigate with PopUpTo

```kotlin
// Clear back stack to login
navController.navigate(Screen.Login.route) {
    popUpTo(0) { inclusive = true }
}

// Navigate within bottom nav (preserve state)
navController.navigate(route) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
```

### Back Navigation

```kotlin
// Simple back
navController.popBackStack()

// Back to specific destination
navController.popBackStack(
    route = Screen.ProductionDashboard.route,
    inclusive = false
)

// Back with result (using SavedStateHandle)
// In destination screen
val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
savedStateHandle?.set("result_key", resultValue)
navController.popBackStack()

// In previous screen
val result = navController.currentBackStackEntry
    ?.savedStateHandle
    ?.getLiveData<String>("result_key")
    ?.observe(lifecycleOwner) { value ->
        // Handle result
    }
```

---

## 🌊 Navigation Flows

### Login Flow

```
SplashScreen (auto after 2s)
    ↓
    navigate(Screen.Login.route) {
        popUpTo(0) { inclusive = true }
    }
    ↓
LoginScreen
    ↓
    [User selects role]
    ↓
    when (role) {
        "Produksi" -> navigate(Screen.ProductionDashboard.route)
        "Keuangan" -> navigate(Screen.FinanceDashboard.route)
        "Penjualan" -> navigate(Screen.SalesDashboard.route)
    } + popUpTo(0) { inclusive = true }
    ↓
Dashboard Screen
```

### Master-Detail Flow

```
MaterialListScreen
    ↓
    navigate(Screen.MaterialRestock.createRoute(materialId))
    ↓
RestockMaterialScreen
    ↓
    [Save success]
    ↓
    popBackStack()
    ↓
MaterialListScreen (updated)
```

### Form Flow with Result

```
ProductionListScreen
    ↓
    navigate(Screen.ProductionForm.route)
    ↓
ProductionFormScreen
    ↓
    [Save success]
    ↓
    savedStateHandle.set("refresh", true)
    popBackStack()
    ↓
ProductionListScreen
    ↓
    observe "refresh" key
    ↓
    reload data
```

### Multi-Step Flow (POS)

```
SalesPOSScreen
    ↓
    navigate(Screen.SalesCheckout.route) + pass cart data
    ↓
SalesCheckoutScreen
    ↓
    [Process payment success]
    ↓
    navigate(Screen.SalesDashboard.route) {
        popUpTo(Screen.SalesDashboard.route) {
            inclusive = false
        }
    }
    ↓
SalesDashboardScreen (cart cleared)
```

---

## 🔐 Navigation Guards

### Role-Based Access Control

```kotlin
@Composable
fun AppNavigator(
    navController: NavHostController,
    userRole: String
) {
    NavHost(...) {
        // Public routes
        composable(Screen.Splash.route) { ... }
        composable(Screen.Login.route) { ... }

        // Protected routes with role check
        composable(Screen.ProductionDashboard.route) {
            if (userRole == "Produksi") {
                ProductionDashboardScreen(...)
            } else {
                // Redirect to unauthorized or login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}
```

### Authentication Check

```kotlin
// In MainActivity or root composable
val isAuthenticated by remember { mutableStateOf(checkAuth()) }

if (isAuthenticated) {
    AppNavigator(...)
} else {
    LoginScreen(...)
}
```

---

## 🎭 Navigation State Management

### Current Route Tracking

```kotlin
val navController = rememberNavController()
val currentRoute by navController.currentBackStackEntryAsState()
    .collectAsState()
    .value?.destination?.route

Scaffold(
    bottomBar = {
        AppBottomNavigation(
            currentRoute = currentRoute ?: "",
            userRole = userRole,
            onNavigate = { route ->
                navController.navigate(route) { ... }
            }
        )
    }
) { ... }
```

### Deep Link Support (Future)

```kotlin
composable(
    route = Screen.ProductionDetail.route,
    deepLinks = listOf(
        navDeepLink {
            uriPattern = "si-umkm://production/detail/{productionId}"
        }
    )
) { ... }
```

### Handling System Back Button

```kotlin
BackHandler(enabled = shouldInterceptBack) {
    // Custom back behavior
    if (hasUnsavedChanges) {
        showDiscardDialog = true
    } else {
        navController.popBackStack()
    }
}
```

---

## 📊 Navigation Graph Visualization

```
┌─────────────────┐
│  SplashScreen   │
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│  LoginScreen    │
└────────┬────────┘
         │
    ┌────┴────┬──────────┐
    ↓         ↓          ↓
┌───────┐ ┌───────┐ ┌────────┐
│Prod.  │ │Keu.   │ │Penj.   │
│Dash   │ │Dash   │ │Dash    │
└───┬───┘ └───┬───┘ └───┬────┘
    │         │          │
┌───┴───────┐ │      ┌───┴──────┐
│  4-tab    │ │      │  3-tab   │
│  bottom   │ │      │  bottom  │
│  nav      │ │      │  nav     │
└───────────┘ │      └──────────┘
              │
         ┌────┴─────┐
         │  4-tab   │
         │  bottom  │
         │  nav     │
         └──────────┘
```

---

## 🛠️ Navigation Best Practices

### 1. Single Source of Truth

```kotlin
// ❌ BAD: Hardcoded strings
navController.navigate("production_dashboard")

// ✅ GOOD: Use Screen sealed class
navController.navigate(Screen.ProductionDashboard.route)
```

### 2. Type-Safe Parameters

```kotlin
// ❌ BAD: Manual string concatenation
navController.navigate("production_detail/$productionId")

// ✅ GOOD: Use createRoute helper
navController.navigate(
    Screen.ProductionDetail.createRoute(productionId)
)
```

### 3. Proper Back Stack Management

```kotlin
// ❌ BAD: Creating multiple instances
navController.navigate(route)

// ✅ GOOD: Single top + restore state
navController.navigate(route) {
    launchSingleTop = true
    restoreState = true
    popUpTo(startDestination) { saveState = true }
}
```

### 4. State Preservation

```kotlin
// Bottom nav navigation should preserve state
navController.navigate(route) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
```

### 5. Clear Navigation on Logout

```kotlin
// Logout action
navController.navigate(Screen.Login.route) {
    popUpTo(0) { inclusive = true }
}
// This clears entire back stack
```

---

## 🧪 Navigation Testing

### Test Navigation Events

```kotlin
@Test
fun testLoginNavigatesToDashboard() {
    val navController = TestNavHostController(context)
    navController.setGraph(R.navigation.app_nav_graph)

    // Perform login
    loginViewModel.login("Produksi")

    // Verify navigation
    assertEquals(
        Screen.ProductionDashboard.route,
        navController.currentDestination?.route
    )
}
```

---

## 📝 Navigation Changelog

### Version 1.0

- Initial navigation setup
- Role-based bottom nav
- 3 main modules (Produksi, Keuangan, Penjualan)
- Basic master-detail flows

### Future Enhancements

- [ ] Deep linking support
- [ ] Tablet dual-pane navigation
- [ ] Navigation animations
- [ ] Breadcrumb navigation (tablet)
- [ ] Quick navigation shortcuts
- [ ] Recently visited screens

---

## 🔍 Debugging Navigation

### Log Current Route

```kotlin
LaunchedEffect(navController) {
    navController.addOnDestinationChangedListener { _, destination, _ ->
        Log.d("Navigation", "Current: ${destination.route}")
    }
}
```

### Inspect Back Stack

```kotlin
val backStack = navController.currentBackStack.value
backStack.forEach { entry ->
    Log.d("BackStack", "Route: ${entry.destination.route}")
}
```

---

## 📚 References

- [Navigation Compose Documentation](https://developer.android.com/jetpack/compose/navigation)
- [Bottom Navigation with Navigation Compose](https://developer.android.com/jetpack/compose/navigation#bottom-nav)
- [Type Safety in Navigation Compose](https://developer.android.com/guide/navigation/navigation-kotlin-dsl)
