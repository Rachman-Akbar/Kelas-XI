package com.example.kishaapp.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val SEARCH = "search"
    const val CATEGORIES = "categories"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDERS = "orders"
    const val ORDER_DETAIL = "orders/{orderId}"
    const val PROFILE = "profile"

    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val EMAIL_VERIFICATION = "email_verification"

    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val SELLER_DASHBOARD = "seller_dashboard"
    const val ADD_EDIT_PRODUCT = "add_edit_product?productId={productId}"

    fun productDetail(productId: String): String = "product_detail/$productId"
    fun orderDetail(orderId: String): String = "orders/$orderId"

    fun addEditProduct(productId: String? = null): String {
        return if (productId.isNullOrBlank()) "add_edit_product" else "add_edit_product?productId=$productId"
    }
}
