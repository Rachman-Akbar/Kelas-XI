package com.komputerkit.easyshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.komputerkit.easyshop.di.AppModule
import com.komputerkit.easyshop.navigation.AppNavigation
import com.komputerkit.easyshop.ui.theme.EasyShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        authViewModel = AppModule.authViewModel,
                        homeViewModel = AppModule.homeViewModel,
                        cartViewModel = AppModule.cartViewModel,
                        productDetailViewModel = AppModule.productDetailViewModel,
                        orderViewModel = AppModule.orderViewModel,
                        favoriteViewModel = AppModule.favoriteViewModel
                    )
                }
            }
        }
    }
}