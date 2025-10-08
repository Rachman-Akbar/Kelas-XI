package com.komputerkit.cgpaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.komputerkit.cgpaapp.ui.screens.CGPACalculatorScreen
import com.komputerkit.cgpaapp.ui.theme.CgpaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CgpaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CGPACalculatorScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CGPACalculatorPreview() {
    CgpaAppTheme {
        CGPACalculatorScreen()
    }
}