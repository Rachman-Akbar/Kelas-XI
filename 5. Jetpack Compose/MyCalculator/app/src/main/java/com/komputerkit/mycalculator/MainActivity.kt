package com.komputerkit.mycalculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.komputerkit.mycalculator.ui.theme.MyCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {,m,,
            MyCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    var firstNumber by remember { mutableStateOf(TextFieldValue("")) }
    var secondNumber by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TextField pertama
        OutlinedTextField(
            value = firstNumber,
            onValueChange = { firstNumber = it },
            label = { Text("Angka Pertama") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        // TextField kedua
        OutlinedTextField(
            value = secondNumber,
            onValueChange = { secondNumber = it },
            label = { Text("Angka Kedua") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        // Row berisi tombol operasi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Tombol Tambah
            Button(
                onClick = {
                    performOperation(
                        firstNumber.text,
                        secondNumber.text,
                        "tambah",
                        context
                    )
                }
            ) {
                Text("+")
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Tombol Kurang
            Button(
                onClick = {
                    performOperation(
                        firstNumber.text,
                        secondNumber.text,
                        "kurang",
                        context
                    )
                }
            ) {
                Text("-")
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Tombol Kali
            Button(
                onClick = {
                    performOperation(
                        firstNumber.text,
                        secondNumber.text,
                        "kali",
                        context
                    )
                }
            ) {
                Text("×")
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Tombol Bagi
            Button(
                onClick = {
                    performOperation(
                        firstNumber.text,
                        secondNumber.text,
                        "bagi",
                        context
                    )
                }
            ) {
                Text("÷")
            }
        }
    }
}

fun performOperation(
    firstStr: String,
    secondStr: String,
    operation: String,
    context: android.content.Context
) {
    try {
        // Mengubah string menjadi integer
        val firstNum = firstStr.toInt()
        val secondNum = secondStr.toInt()
        
        // Melakukan operasi sesuai pilihan
        val result = when (operation) {
            "tambah" -> firstNum + secondNum
            "kurang" -> firstNum - secondNum
            "kali" -> firstNum * secondNum
            "bagi" -> {
                if (secondNum != 0) {
                    firstNum / secondNum
                } else {
                    Toast.makeText(context, "Tidak bisa membagi dengan nol!", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            else -> 0
        }
        
        // Menampilkan hasil dalam Toast
        val operationSymbol = when (operation) {
            "tambah" -> "+"
            "kurang" -> "-"
            "kali" -> "×"
            "bagi" -> "÷"
            else -> ""
        }
        
        Toast.makeText(
            context,
            "$firstNum $operationSymbol $secondNum = $result",
            Toast.LENGTH_LONG
        ).show()
        
    } catch (e: NumberFormatException) {
        Toast.makeText(
            context,
            "Masukkan angka yang valid!",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    MyCalculatorTheme {
        CalculatorApp()
    }
}