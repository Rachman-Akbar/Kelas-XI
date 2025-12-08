package com.komputerkit.aplikasimonitoringkelas.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.focusable
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Minimal helper composables and utils used by screens. These are lightweight wrappers
// to satisfy compile-time references and delegate to Material3 primitives.

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    allowManualInput: Boolean = false
) {
    val context = LocalContext.current
    val cal = Calendar.getInstance()
    // Try to prefill calendar from selectedDate if possible
    try {
        val parts = selectedDate.split("-")
        if (parts.size == 3) {
            val y = parts[0].toIntOrNull()
            val m = parts[1].toIntOrNull()
            val d = parts[2].toIntOrNull()
            if (y != null && m != null && d != null) {
                cal.set(y, m - 1, d)
            }
        }
    } catch (_: Exception) { }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val dpd = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // month is 0-based
            val sel = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(sel)
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = { newValue ->
            // Accept manual input when allowed, otherwise ignore text changes
            if (allowManualInput) onDateSelected(newValue)
        },
        label = { Text(label) },
        modifier = modifier
            .padding(0.dp)
            .then(
                if (!allowManualInput) Modifier.clickable(interactionSource = interactionSource) { dpd.show() }
                else Modifier
            )
            .focusable(),
        readOnly = !allowManualInput,
        trailingIcon = {
            IconButton(onClick = { dpd.show() }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pilih tanggal")
            }
        }
    )
}

@Composable
fun ModernCard(containerColor: Color = MaterialTheme.colorScheme.surface, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor),
        modifier = Modifier
    ) {
        content()
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = value ?: "-", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor, text) = when (status) {
        "approved" -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "Disetujui"
        )
        "pending" -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Menunggu"
        )
        "rejected" -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Ditolak"
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            status
        )
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ModernFloatingActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
    ) {
        icon()
    }
}

@Composable
fun DateFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

object Utils {
    fun getStatusText(status: String?): String {
        return when (status) {
            "approved" -> "Disetujui"
            "pending" -> "Menunggu"
            "rejected" -> "Ditolak"
            "hadir" -> "Hadir"
            "tidak_hadir" -> "Tidak Hadir"
            else -> status ?: "-"
        }
    }

    fun formatDate(date: String?): String = date ?: ""
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor, text) = when (status) {
        "approved" -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "Disetujui"
        )
        "pending" -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Menunggu"
        )
        "rejected" -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Ditolak"
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            status
        )
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Helper: format date strings (attempt parse yyyy-MM-dd or ISO format, fallback to raw string)
fun formatDate(input: String): String {
    if (input.isBlank()) return "-"
    return try {
        // Try ISO format first (2025-10-22T00:00:00.000Z)
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val parsed = isoFormat.parse(input)
        if (parsed != null) {
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            return outputFormat.format(parsed)
        }
        
        // Try yyyy-MM-dd format
        val parse = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val d: Date = parse.parse(input) ?: return input
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        fmt.format(d)
    } catch (e: Exception) {
        // If all parsing fails, try to extract just the date part
        if (input.contains("T")) {
            input.substringBefore("T")
        } else {
            input
        }
    }
}
 
