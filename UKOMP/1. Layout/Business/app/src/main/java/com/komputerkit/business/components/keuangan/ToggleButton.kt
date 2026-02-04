package com.komputerkit.business.components.keuangan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FinanceToggleButton(
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
    option1: ToggleOption,
    option2: ToggleOption,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5E7EB)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Option 1 Button
            Button(
                onClick = { onSelectedChange(0) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedIndex == 0) Color.White else Color.Transparent,
                    contentColor = if (selectedIndex == 0) option1.selectedColor else Color(0xFF6B7280)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (selectedIndex == 0) 2.dp else 0.dp
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (option1.showDot) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (selectedIndex == 0) option1.selectedColor else Color(0xFF9CA3AF))
                        )
                    }
                    Text(
                        text = option1.text,
                        fontSize = 14.sp,
                        fontWeight = if (selectedIndex == 0) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
            
            // Option 2 Button
            Button(
                onClick = { onSelectedChange(1) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedIndex == 1) Color.White else Color.Transparent,
                    contentColor = if (selectedIndex == 1) option2.selectedColor else Color(0xFF6B7280)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (selectedIndex == 1) 2.dp else 0.dp
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (option2.showDot) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (selectedIndex == 1) option2.selectedColor else Color(0xFF9CA3AF))
                        )
                    }
                    Text(
                        text = option2.text,
                        fontSize = 14.sp,
                        fontWeight = if (selectedIndex == 1) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

data class ToggleOption(
    val text: String,
    val selectedColor: Color,
    val showDot: Boolean = false
)
