package com.komputerkit.aplikasimonitoringkelas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipsRow(
    items: List<String>,
    selectedItems: Set<String>,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.filterNotNull().forEach { item ->
            val safeItem = item.ifEmpty { " " }
            
            androidx.compose.material3.FilterChip(
                selectedItems.contains(safeItem),
                { onSelectionChange(safeItem) },
                modifier = Modifier,
                enabled = true,
                label = {
                    Text(
                        text = if (safeItem.isEmpty()) " " else safeItem,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = Color(0xFF424242),
                    selectedContainerColor = Color(0xFF2196F3),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}