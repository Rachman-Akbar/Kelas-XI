package com.komputerkit.business.screens.bahan_baku

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestockMaterialScreen(
    currentRoute: String = "",
    onNavigate: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    var selectedMaterial by remember { mutableStateOf("") }
    var selectedSupplier by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf("2023-10-27") }
    var invoiceNumber by remember { mutableStateOf("") }
    
    val totalPrice = remember(quantity, pricePerUnit) {
        val qty = quantity.toDoubleOrNull() ?: 0.0
        val price = pricePerUnit.toDoubleOrNull() ?: 0.0
        qty * price
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF197FE6)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back"
                        )
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    Text(
                        text = "Restock Material",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Box(modifier = Modifier.width(80.dp)) // Spacer for symmetry
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 96.dp) // Space for bottom button
        ) {
            // Form Sections
            Spacer(modifier = Modifier.height(16.dp))
            
            // Material Details Section
            FormSectionHeader(title = "Material Details")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField(
                label = "Material",
                value = selectedMaterial,
                onValueChange = { selectedMaterial = it },
                placeholder = "Select material...",
                isDropdown = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField(
                label = "Arrival Date",
                value = arrivalDate,
                onValueChange = { arrivalDate = it },
                trailingIcon = Icons.Default.CalendarToday
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Pricing & Quantity Section
            FormSectionHeader(title = "Pricing & Quantity")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    FormField(
                        label = "Quantity",
                        value = quantity,
                        onValueChange = { quantity = it },
                        placeholder = "0"
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FormField(
                        label = "Price / Unit",
                        value = pricePerUnit,
                        onValueChange = { pricePerUnit = it },
                        placeholder = "0.00",
                        prefix = "$"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField(
                label = "Total Price (Calculated)",
                value = String.format("%.2f", totalPrice),
                onValueChange = {},
                prefix = "$",
                enabled = false
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logistics Section
            FormSectionHeader(title = "Logistics")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField(
                label = "Supplier",
                value = selectedSupplier,
                onValueChange = { selectedSupplier = it },
                placeholder = "Select supplier...",
                isDropdown = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField(
                label = "Invoice Number",
                value = invoiceNumber,
                onValueChange = { invoiceNumber = it },
                placeholder = "INV-000000",
                isError = invoiceNumber.isEmpty(),
                errorMessage = "Invoice number is required for restock"
            )
        }
        
        // Bottom Action Button (Fixed)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF197FE6)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Save Restock Entry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp)) // iPhone indicator spacer
            }
        }
    }
}

@Composable
private fun FormSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isDropdown: Boolean = false,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    prefix: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ) 
            },
            leadingIcon = if (prefix != null) {
                { Text(prefix, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) }
            } else null,
            trailingIcon = if (isDropdown) {
                { Icon(Icons.Default.ArrowDropDown, null) }
            } else if (trailingIcon != null) {
                { Icon(trailingIcon, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) }
            } else null,
            shape = RoundedCornerShape(16.dp),
            enabled = enabled,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF197FE6),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            singleLine = true
        )
        
        if (isError && errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
