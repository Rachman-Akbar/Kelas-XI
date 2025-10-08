package com.komputerkit.easyshop.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.easyshop.data.repository.AuthRepository
import com.komputerkit.easyshop.data.repository.CartRepository
import com.komputerkit.easyshop.data.repository.FavoriteRepository
import com.komputerkit.easyshop.data.repository.HomeRepository
import com.komputerkit.easyshop.data.repository.OrderRepository
import com.komputerkit.easyshop.data.repository.ProductRepository
import com.komputerkit.easyshop.ui.viewmodel.AuthViewModel
import com.komputerkit.easyshop.ui.viewmodel.CartViewModel
import com.komputerkit.easyshop.ui.viewmodel.FavoriteViewModel
import com.komputerkit.easyshop.ui.viewmodel.HomeViewModel
import com.komputerkit.easyshop.ui.viewmodel.OrderViewModel
import com.komputerkit.easyshop.ui.viewmodel.ProductDetailViewModel

object AppModule {
    
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    
    private val authRepository: AuthRepository by lazy {
        AuthRepository(firebaseAuth, firestore)
    }
    
    private val homeRepository: HomeRepository by lazy {
        HomeRepository(firestore)
    }
    
    private val productRepository: ProductRepository by lazy {
        ProductRepository(firestore)
    }
    
    private val cartRepository: CartRepository by lazy {
        CartRepository(firestore, firebaseAuth)
    }
    
    private val orderRepository: OrderRepository by lazy {
        OrderRepository(firestore, firebaseAuth)
    }
    
    private val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepository(firestore, firebaseAuth)
    }
    
    val authViewModel: AuthViewModel by lazy {
        AuthViewModel(authRepository)
    }
    
    val homeViewModel: HomeViewModel by lazy {
        HomeViewModel(homeRepository, productRepository, firebaseAuth)
    }
    
    val cartViewModel: CartViewModel by lazy {
        CartViewModel(cartRepository)
    }
    
    val productDetailViewModel: ProductDetailViewModel by lazy {
        ProductDetailViewModel(productRepository)
    }
    
    val orderViewModel: OrderViewModel by lazy {
        OrderViewModel(orderRepository, productRepository)
    }
    
    val favoriteViewModel: FavoriteViewModel by lazy {
        FavoriteViewModel(favoriteRepository)
    }
}