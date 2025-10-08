package com.komputerkit.realtimeweatherapp.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.realtimeweatherapp.data.model.WeatherResponse

@Stable
@Composable
fun WeatherCard(
    weatherData: WeatherResponse,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Location Header
                LocationHeader(
                    locationName = weatherData.location?.fullLocationName ?: "Unknown Location"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Main Temperature Display
                MainTemperatureDisplay(
                    temperature = weatherData.current?.safeTemperatureC ?: "N/A",
                    condition = weatherData.current?.condition?.safeText ?: "Unknown"
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Weather Details Grid
                WeatherDetailsGrid(weatherData = weatherData)
            }
        }
    }
}

@Composable
private fun LocationHeader(
    locationName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = locationName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MainTemperatureDisplay(
    temperature: String,
    condition: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = temperature,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = condition,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeatherDetailsGrid(
    weatherData: WeatherResponse,
    modifier: Modifier = Modifier
) {
    val current = weatherData.current
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherDetailItem(
            icon = Icons.Default.Info,
            label = "Humidity",
            value = current?.safeHumidity ?: "N/A"
        )
        
        WeatherDetailItem(
            icon = Icons.Default.Settings,
            label = "Wind",
            value = current?.safeWindSpeed ?: "N/A"
        )
        
        WeatherDetailItem(
            icon = Icons.Default.Star,
            label = "Pressure",
            value = current?.safePressure ?: "N/A"
        )
    }
}

@Composable
private fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}