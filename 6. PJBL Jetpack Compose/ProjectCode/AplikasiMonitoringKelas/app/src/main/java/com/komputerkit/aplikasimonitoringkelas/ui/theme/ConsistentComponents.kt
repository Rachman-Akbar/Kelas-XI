package com.komputerkit.aplikasimonitoringkelas.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Modern Color Palette - Soft Blue & White Theme
object ModernColors {
    val PrimaryBlue = Color(0xFF2196F3) // Soft Blue
    val PrimaryBlueDark = Color(0xFF1976D2)
    val LightBlue = Color(0xFFE3F2FD)
    val MediumBlue = Color(0xFFBBDEFB)
    val AccentBlue = Color(0xFF03A9F4)

    val BackgroundWhite = Color(0xFFFAFAFA)
    val CardWhite = Color(0xFFFFFFFF)
    val TextWhite = Color(0xFFFFFFFF)
    val DividerGray = Color(0xFFE0E0E0)
    val BorderGray = Color(0xFFE0E0E0)
    val LightGray = Color(0xFFF5F5F5)

    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
    val TextHint = Color(0xFF9E9E9E)

    val SuccessGreen = Color(0xFF4CAF50)
    val WarningAmber = Color(0xFFFFC107)
    val InfoBlue = Color(0xFF2196F3)
    val ErrorRed = Color(0xFFF44336)
}

/**
 * Standard Page Title Bar - konsisten untuk semua halaman
 */
@Composable
fun StandardPageTitle(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ModernColors.LightBlue,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ModernColors.PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ModernColors.PrimaryBlueDark
            )
        }
    }
}

/**
 * Standard Dashboard Menu Card - untuk home screen
 */
@Composable
fun StandardDashboardCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = ModernColors.CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ModernColors.LightBlue,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(32.dp),
                        tint = ModernColors.PrimaryBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = ModernColors.TextPrimary,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = ModernColors.TextSecondary,
                maxLines = 2
            )
        }
    }
}

/**
 * Standard Statistic Card - untuk menampilkan angka statistik
 */
@Composable
fun StandardStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = ModernColors.CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ModernColors.LightBlue,
                modifier = Modifier.size(44.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = ModernColors.PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = ModernColors.PrimaryBlue
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = ModernColors.TextSecondary
                )
            }
        }
    }
}

/**
 * Standard Data Card - untuk menampilkan data list item
 */
@Composable
fun StandardDataCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ModernColors.CardWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

/**
 * Standard Filter Dropdown - konsisten untuk semua filter
 */
@Composable
fun StandardFilterButton(
    label: String,
    selectedValue: String?,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = ModernColors.LightBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = ModernColors.PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = ModernColors.TextSecondary
                    )
                    Text(
                        text = selectedValue ?: "Pilih",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = ModernColors.PrimaryBlue
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = ModernColors.PrimaryBlue
            )
        }
    }
}

/**
 * Standard Status Badge - untuk menampilkan status
 */
@Composable
fun StandardStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (status.lowercase()) {
        "disetujui", "approved", "hadir", "present" -> ModernColors.SuccessGreen to "Hadir"
        "dijadwalkan" -> ModernColors.SuccessGreen to "Dijadwalkan"
        "tidak_hadir", "not_present", "alpha" -> ModernColors.TextSecondary to "Tidak Hadir"
        "telat" -> ModernColors.WarningAmber to "Terlambat"
        "izin" -> ModernColors.WarningAmber to "Izin"
        "sakit" -> ModernColors.WarningAmber to "Sakit"
        "pending", "menunggu" -> ModernColors.WarningAmber to "Menunggu"
        "ditolak", "rejected" -> ModernColors.ErrorRed to "Ditolak"
        else -> ModernColors.InfoBlue to status
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Text(
            text = label.uppercase(),
            fontWeight = FontWeight.SemiBold,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

/**
 * Standard Section Header - untuk pemisah section
 */
@Composable
fun StandardSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = ModernColors.TextPrimary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

/**
 * Standard Info Card - untuk menampilkan info banner
 */
@Composable
fun StandardInfoCard(
    message: String,
    icon: ImageVector = Icons.Default.Info,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ModernColors.LightBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ModernColors.PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = ModernColors.TextPrimary
            )
        }
    }
}

/**
 * Modern Filter Dialog - dropdown filter yang lebih cantik
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernFilterDialog(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    android.util.Log.d("ModernFilterDialog", "Title: $title, Options count: ${options.size}, Selected: $selectedOption")
    options.forEach { opt ->
        android.util.Log.d("ModernFilterDialog", "Option: $opt")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ModernColors.PrimaryBlue
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (options.isEmpty()) {
                    Text(
                        text = "Tidak ada opsi tersedia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ModernColors.TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    options.forEach { option ->
                        val isSelected = option == selectedOption
                        Card(
                            onClick = {
                                android.util.Log.d("ModernFilterDialog", "Option clicked: $option")
                                onSelect(option)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) ModernColors.LightBlue else ModernColors.CardWhite
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isSelected) 2.dp else 0.dp
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                                1.dp,
                                ModernColors.DividerGray
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) ModernColors.PrimaryBlue else ModernColors.TextPrimary
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = ModernColors.PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = ModernColors.CardWhite,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    )
}

/**
 * Modern FAB - untuk entri data (khusus guru)
 */
@Composable
fun ModernFAB(
    onClick: () -> Unit,
    icon: ImageVector = Icons.Default.Add,
    text: String = "",
    modifier: Modifier = Modifier
) {
    if (text.isEmpty()) {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = ModernColors.PrimaryBlue,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Add",
                modifier = Modifier.size(24.dp)
            )
        }
    } else {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = ModernColors.PrimaryBlue,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Empty State Component - ketika tidak ada data
 */
@Composable
fun ModernEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    onResetFilters: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = ModernColors.LightBlue,
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ModernColors.PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ModernColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = ModernColors.TextSecondary,
            textAlign = TextAlign.Center
        )

        onResetFilters?.let {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = it,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ModernColors.PrimaryBlue
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Filter")
            }
        }
    }
}

/**
 * Modern Filter Button - button untuk membuka filter dialog
 */
@Composable
fun ModernFilterButton(
    label: String,
    icon: ImageVector = Icons.Default.FilterList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = ModernColors.LightBlue,
            contentColor = ModernColors.PrimaryBlue
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
