package com.komputerkit.mvvm_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.komputerkit.mvvm_demo.ui.theme.MVVM_DemoTheme
import com.komputerkit.mvvm_demo.view.HomePage
import com.komputerkit.mvvm_demo.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVVM_DemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Create ViewModel instance using viewModel() function
                    val homeViewModel: HomeViewModel = viewModel()
                    
                    // Pass the ViewModel to HomePage composable
                    HomePage(
                        viewModel = homeViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}