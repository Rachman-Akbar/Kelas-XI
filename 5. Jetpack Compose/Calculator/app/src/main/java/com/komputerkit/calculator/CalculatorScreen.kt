package com.komputerkit.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    val equation by viewModel.equation.observeAsState("")
    val result by viewModel.result.observeAsState("0")
    
    // 4x5 grid layout for calculator buttons
    val buttons = listOf(
        listOf("AC", "C", "(", ")", "÷"),
        listOf("7", "8", "9", "×", ""),
        listOf("4", "5", "6", "-", ""),
        listOf("1", "2", "3", "+", ""),
        listOf("0", ".", "", "=", "")
    )
    
    val flatButtons = buttons.flatten().filter { it.isNotEmpty() }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalculatorColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    CalculatorColors.DisplayBackground,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(24.dp)
        ) {
            // Equation Display
            Text(
                text = if (equation.isEmpty()) "0" else equation,
                color = CalculatorColors.TextSecondary,
                fontSize = 20.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Result Display
            Text(
                text = result,
                color = CalculatorColors.TextPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Buttons Grid - Custom layout for calculator
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row 1: AC, C, (, ), ÷
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("AC", "C", "(", ")", "÷").forEach { buttonText ->
                    CalculatorButton(
                        config = getButtonConfig(buttonText),
                        onClick = { viewModel.onButtonClick(buttonText) }
                    )
                }
            }
            
            // Row 2: 7, 8, 9, ×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("7", "8", "9", "×").forEach { buttonText ->
                    CalculatorButton(
                        config = getButtonConfig(buttonText),
                        onClick = { viewModel.onButtonClick(buttonText) }
                    )
                }
                // Empty space for 5th column
                Spacer(modifier = Modifier.size(70.dp))
            }
            
            // Row 3: 4, 5, 6, -
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("4", "5", "6", "-").forEach { buttonText ->
                    CalculatorButton(
                        config = getButtonConfig(buttonText),
                        onClick = { viewModel.onButtonClick(buttonText) }
                    )
                }
                // Empty space for 5th column
                Spacer(modifier = Modifier.size(70.dp))
            }
            
            // Row 4: 1, 2, 3, +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("1", "2", "3", "+").forEach { buttonText ->
                    CalculatorButton(
                        config = getButtonConfig(buttonText),
                        onClick = { viewModel.onButtonClick(buttonText) }
                    )
                }
                // Empty space for 5th column
                Spacer(modifier = Modifier.size(70.dp))
            }
            
            // Row 5: 0, ., =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("0", ".", "=").forEach { buttonText ->
                    CalculatorButton(
                        config = getButtonConfig(buttonText),
                        onClick = { viewModel.onButtonClick(buttonText) }
                    )
                }
                // Empty spaces for 4th and 5th column
                Spacer(modifier = Modifier.size(70.dp))
                Spacer(modifier = Modifier.size(70.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}