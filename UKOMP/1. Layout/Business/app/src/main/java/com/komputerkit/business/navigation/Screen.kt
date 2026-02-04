package com.komputerkit.business.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    
    // Main - Bottom Nav Routes
    data object Dashboard : Screen("dashboard")
    data object BahanBaku : Screen("bahan_baku")
    data object ProductList : Screen("product_list")
    data object Production : Screen("production")
    
    // Additional Routes
    data object Profile : Screen("profile")
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    
    // Material (Bahan Baku) Sub-routes
    data object MaterialList : Screen("material_list")
    data object MaterialHistory : Screen("material_history")
    data object RestockMaterial : Screen("restock_material")
    data object MaterialSpecificHistory : Screen("material_specific_history/{materialId}") {
        fun createRoute(materialId: String) = "material_specific_history/$materialId"
    }
    
    // Production Sub-routes
    data object ProductionList : Screen("production_list")
    data object ProductionHistory : Screen("production_history")
    data object ProductionForm : Screen("production_form")
    data object ProductionDetail : Screen("production_detail/{productionId}") {
        fun createRoute(productionId: String) = "production_detail/$productionId"
    }
    data object ProductionEdit : Screen("production_edit/{productionId}") {
        fun createRoute(productionId: String) = "production_edit/$productionId"
    }
    
    // Product Sub-routes
    data object ProductComposition : Screen("product_composition/{productId}") {
        fun createRoute(productId: String) = "product_composition/$productId"
    }
    data object RestockProduct : Screen("restock_product/{productId}") {
        fun createRoute(productId: String) = "restock_product/$productId"
    }
    data object AllProductHistory : Screen("all_product_history")
    
    // Sales (Penjualan)
    data object SalesDashboard : Screen("sales_dashboard")
    data object SalesPOS : Screen("sales_pos")
    data object CustomerList : Screen("customer_list")
    data object CustomerForm : Screen("customer_form")
    data object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: String) = "customer_detail/$customerId"
    }
    data object SalesCheckout : Screen("sales_checkout")
    data object SalesHistory : Screen("sales_history")
    data object SalesDetail : Screen("sales_detail/{transactionId}") {
        fun createRoute(transactionId: String) = "sales_detail/$transactionId"
    }
    data object PreorderList : Screen("preorder_list")
    data object PreorderDetail : Screen("preorder_detail/{preorderId}") {
        fun createRoute(preorderId: String) = "preorder_detail/$preorderId"
    }
    data object PreorderHistory : Screen("preorder_history")
    
    // Finance (Keuangan)
    data object FinanceDashboard : Screen("finance_dashboard")
    data object HppBepAnalysis : Screen("hpp_bep_analysis")
    data object TransactionForm : Screen("transaction_form")
    data object TransactionHistory : Screen("transaction_history")
    data object DebtManagement : Screen("debt_management")
    data object DebtReceivable : Screen("debt_receivable")
    data object DebtReceivableHistory : Screen("debt_receivable_history")
    data object DebtForm : Screen("debt_form")
    
    // Material Detail
    data object MaterialDetail : Screen("material_detail/{materialId}") {
        fun createRoute(materialId: String) = "material_detail/$materialId"
    }
}
