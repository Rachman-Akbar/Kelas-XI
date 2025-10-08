package com.komputerkit.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ButtonConfig(
    val text: String,
    val backgroundColor: Color,
    val textColor: Color = Color.White
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorButton(
    config: ButtonConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(70.dp)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = config.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = config.text,
                color = config.textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

object CalculatorColors {
    val NumberButton = Color(0xFF4DB6AC) // Teal/Hijau kebiruan
    val OperatorButton = Color(0xFFFF9800) // Orange
    val ClearButton = Color(0xFFF44336) // Red
    val ParenthesesButton = Color(0xFF9E9E9E) // Gray
    val Background = Color(0xFF121212)
    val DisplayBackground = Color(0xFF1E1E1E)
    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFB3B3B3)
}

@Composable
fun getButtonConfig(text: String): ButtonConfig {
    return when (text) {
        "C", "AC" -> ButtonConfig(text, CalculatorColors.ClearButton)
        "(", ")" -> ButtonConfig(text, CalculatorColors.ParenthesesButton)
        "+", "-", "×", "÷", "=" -> ButtonConfig(text, CalculatorColors.OperatorButton)
        else -> ButtonConfig(text, CalculatorColors.NumberButton) // Numbers and decimal point
    }
}