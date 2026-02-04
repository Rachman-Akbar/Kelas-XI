package com.komputerkit.business.screens.produksi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ProductionFormScreen(
    productionId: String? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit = {}
) {
    var selectedProduct by remember { mutableStateOf("") }
    var targetQuantity by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("medium") }
    var dateTime by remember { mutableStateOf("") }
    var shift by remember { mutableStateOf("morning") }
    var pic by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    val isEdit = productionId != null

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
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = "Create Production Schedule",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section: Basic Info
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Product Dropdown
                    FormFieldLabel("Product")
                    OutlinedTextField(
                        value = selectedProduct,
                        onValueChange = { selectedProduct = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Select product") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF197FE6),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        singleLine = true
                    )
                    
                    // Target Quantity & Priority
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormFieldLabel("Target Quantity")
                            OutlinedTextField(
                                value = targetQuantity,
                                onValueChange = { targetQuantity = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                singleLine = true
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            FormFieldLabel("Priority")
                            OutlinedTextField(
                                value = priority.capitalize(),
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                singleLine = true
                            )
                        }
                    }
                }
                
                // Section: Schedule Details
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormFieldLabel("Date & Time")
                    OutlinedTextField(
                        value = dateTime,
                        onValueChange = { dateTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("YYYY-MM-DD HH:MM") },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF197FE6),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormFieldLabel("Shift")
                            OutlinedTextField(
                                value = when(shift) {
                                    "morning" -> "Morning (06:00 - 14:00)"
                                    "afternoon" -> "Afternoon (14:00 - 22:00)"
                                    else -> "Night (22:00 - 06:00)"
                                },
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                singleLine = true
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            FormFieldLabel("PIC")
                            OutlinedTextField(
                                value = pic,
                                onValueChange = { pic = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Assign PIC") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                singleLine = true
                            )
                        }
                    }
                }
                
                // Section: Material Requirements
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF197FE6).copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFF197FE6).copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Inventory2,
                                    contentDescription = null,
                                    tint = Color(0xFF197FE6),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Material Requirements",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFFEF3C7),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFFFEF3C7)
                                )
                            ) {
                                Text(
                                    text = "Auto-Check Active",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    color = Color(0xFFA16207)
                                )
                            }
                        }
                        
                        // Material Items
                        MaterialItem(
                            name = "Aluminum Alloy 6061",
                            required = "250kg",
                            stock = "1,200kg",
                            isAvailable = true
                        )
                        
                        MaterialItem(
                            name = "Heat Dissipation Paste",
                            required = "15L",
                            stock = "8L",
                            isAvailable = false,
                            isWarning = true
                        )
                        
                        MaterialItem(
                            name = "Packing Inserts",
                            required = "500pcs",
                            stock = "5,000pcs",
                            isAvailable = true
                        )
                        
                        Text(
                            text = "* Material shortage detected. Production may be delayed if stock is not replenished.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = Color(0xFFC2410C),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                // Section: Notes
                Column {
                    FormFieldLabel("Notes & Special Instructions")
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp),
                        placeholder = { Text("Add details about machinery, safety protocols or specific assembly steps...") },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF197FE6),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        maxLines = 5
                    )
                }
            }
            
            // Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Save as draft */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "Save Draft",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    
                    Button(
                        onClick = onSaveSuccess,
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF197FE6)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Publish Schedule",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun FormFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun MaterialItem(
    name: String,
    required: String,
    stock: String,
    isAvailable: Boolean,
    isWarning: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (isWarning) 2.dp else 1.dp,
            if (isWarning) Color(0xFFFED7AA) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    text = "Req: $required | Stock: $stock",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = if (isWarning) 
                        Color(0xFFEA580C) 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = if (isWarning) FontWeight.Bold else FontWeight.Normal
                )
            }
            
            Icon(
                imageVector = if (isAvailable && !isWarning) 
                    Icons.Default.CheckCircle 
                else 
                    Icons.Default.Warning,
                contentDescription = null,
                tint = if (isAvailable && !isWarning) 
                    Color(0xFF22C55E) 
                else 
                    Color(0xFFF97316),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
