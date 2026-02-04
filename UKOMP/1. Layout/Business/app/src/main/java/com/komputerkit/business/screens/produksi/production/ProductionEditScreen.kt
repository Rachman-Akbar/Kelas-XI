package com.komputerkit.business.screens.produksi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class
data class MaterialRequirement(
    val name: String,
    val required: Int,
    val stock: Int,
    val isSufficient: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionEditScreen(
    productionId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var selectedProduct by remember { mutableStateOf("Sepatu Nike Air Max") }
    var targetQuantity by remember { mutableStateOf("150") }
    var selectedPriority by remember { mutableStateOf("High") }
    var selectedDate by remember { mutableStateOf("2024-01-15") }
    var selectedTime by remember { mutableStateOf("08:00") }
    var selectedShift by remember { mutableStateOf("Shift Pagi") }
    var selectedPIC by remember { mutableStateOf("Budi Santoso") }
    var selectedStatus by remember { mutableStateOf("Pending") }

    val products = listOf("Sepatu Nike Air Max", "Sepatu Adidas Ultraboost", "Sepatu Puma RS-X")
    val priorities = listOf("Low", "Medium", "High")
    val shifts = listOf("Shift Pagi", "Shift Siang", "Shift Malam")
    val pics = listOf("Budi Santoso", "Siti Nurhaliza", "Ahmad Wijaya")
    val statuses = listOf("Pending", "In Progress", "Completed")

    var expandedProduct by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedShift by remember { mutableStateOf(false) }
    var expandedPIC by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

    val materials = listOf(
        MaterialRequirement("Kulit Sintetis Premium", 80, 120, true),
        MaterialRequirement("Sol Karet EVA", 150, 300, true),
        MaterialRequirement("Benang Jahit Nilon", 45, 50, false),
        MaterialRequirement("Lem Sepatu Industri", 25, 30, false)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Production Schedule",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E293B),
                    navigationIconContentColor = Color(0xFF1E293B)
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Save as draft */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF197FE6)
                        )
                    ) {
                        Text("Save Draft", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = onSaveSuccess,
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF197FE6)
                        )
                    ) {
                        Text("Update Schedule", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Production Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Production Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )

                    // Product Selection
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormFieldLabel("Product Name")
                        ExposedDropdownMenuBox(
                            expanded = expandedProduct,
                            onExpandedChange = { expandedProduct = !expandedProduct }
                        ) {
                            OutlinedTextField(
                                value = selectedProduct,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        if (expandedProduct) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedProduct,
                                onDismissRequest = { expandedProduct = false }
                            ) {
                                products.forEach { product ->
                                    DropdownMenuItem(
                                        text = { Text(product) },
                                        onClick = {
                                            selectedProduct = product
                                            expandedProduct = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Target Quantity & Priority Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FormFieldLabel("Target Quantity")
                            OutlinedTextField(
                                value = targetQuantity,
                                onValueChange = { targetQuantity = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                suffix = { Text("pcs", color = Color(0xFF64748B)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FormFieldLabel("Priority")
                            ExposedDropdownMenuBox(
                                expanded = expandedPriority,
                                onExpandedChange = { expandedPriority = !expandedPriority }
                            ) {
                                OutlinedTextField(
                                    value = selectedPriority,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            if (expandedPriority) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF197FE6),
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedPriority,
                                    onDismissRequest = { expandedPriority = false }
                                ) {
                                    priorities.forEach { priority ->
                                        DropdownMenuItem(
                                            text = { Text(priority) },
                                            onClick = {
                                                selectedPriority = priority
                                                expandedPriority = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Date & Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FormFieldLabel("Production Date")
                            OutlinedTextField(
                                value = selectedDate,
                                onValueChange = { selectedDate = it },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FormFieldLabel("Start Time")
                            OutlinedTextField(
                                value = selectedTime,
                                onValueChange = { selectedTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }
                    }

                    // Shift & PIC Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FormFieldLabel("Shift")
                            ExposedDropdownMenuBox(
                                expanded = expandedShift,
                                onExpandedChange = { expandedShift = !expandedShift }
                            ) {
                                OutlinedTextField(
                                    value = selectedShift,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            if (expandedShift) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF197FE6),
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedShift,
                                    onDismissRequest = { expandedShift = false }
                                ) {
                                    shifts.forEach { shift ->
                                        DropdownMenuItem(
                                            text = { Text(shift) },
                                            onClick = {
                                                selectedShift = shift
                                                expandedShift = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FormFieldLabel("PIC (Person In Charge)")
                            ExposedDropdownMenuBox(
                                expanded = expandedPIC,
                                onExpandedChange = { expandedPIC = !expandedPIC }
                            ) {
                                OutlinedTextField(
                                    value = selectedPIC,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            if (expandedPIC) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF197FE6),
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedPIC,
                                    onDismissRequest = { expandedPIC = false }
                                ) {
                                    pics.forEach { pic ->
                                        DropdownMenuItem(
                                            text = { Text(pic) },
                                            onClick = {
                                                selectedPIC = pic
                                                expandedPIC = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Status Field
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormFieldLabel("Status")
                        ExposedDropdownMenuBox(
                            expanded = expandedStatus,
                            onExpandedChange = { expandedStatus = !expandedStatus }
                        ) {
                            OutlinedTextField(
                                value = selectedStatus,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        if (expandedStatus) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF197FE6),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStatus,
                                onDismissRequest = { expandedStatus = false }
                            ) {
                                statuses.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status) },
                                        onClick = {
                                            selectedStatus = status
                                            expandedStatus = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Material Requirements Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    Modifier.padding(16.dp), Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Material Requirements",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF22C55E)
                                )
                                Text(
                                    "Auto-Check Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF22C55E)
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        materials.forEach { material ->
                            MaterialItemEdit(material)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MaterialItemEdit(material: MaterialRequirement) {
        val borderColor = if (material.isSufficient) Color(0xFFE2E8F0) else Color(0xFFFECDD3)
        val iconColor = if (material.isSufficient) Color(0xFF22C55E) else Color(0xFFF59E0B)
        val icon = if (material.isSufficient) Icons.Default.Done else Icons.Default.Warning

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = material.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${material.required} pcs required",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Text(
                text = "${material.stock} pcs",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (material.isSufficient) Color(0xFF22C55E) else Color(0xFFF59E0B)
            )
        }
    }
