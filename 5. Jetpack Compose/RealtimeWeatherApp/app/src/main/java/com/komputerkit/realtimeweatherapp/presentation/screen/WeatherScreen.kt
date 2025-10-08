package com.komputerkit.realtimeweatherapp.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.komputerkit.realtimeweatherapp.presentation.components.*
import com.komputerkit.realtimeweatherapp.presentation.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Weather App",
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { query ->
                    viewModel.searchWeather(query)
                },
                isLoading = uiState.isLoading,
                placeholder = "Enter city name (e.g., London, Jakarta)"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Content based on UI state
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        message = "Fetching weather for ${uiState.searchQuery}..."
                    )
                }
                
                uiState.hasError -> {
                    ErrorMessage(
                        message = uiState.errorMessage ?: "Unknown error occurred",
                        onRetry = { 
                            if (searchQuery.isNotEmpty()) {
                                viewModel.searchWeather(searchQuery)
                            }
                        },
                        onDismiss = { viewModel.clearError() }
                    )
                }
                
                uiState.hasData -> {
                    uiState.weatherData?.let { weatherData ->
                        WeatherCard(weatherData = weatherData)
                    }
                }
                
                uiState.isInitialState -> {
                    WelcomeMessage()
                }
                
                else -> {
                    // Fallback for any unexpected state
                    WelcomeMessage()
                }
            }
        }
    }
}

@Composable
private fun WelcomeMessage(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌤️",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome to Weather App",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter a city name to get current weather information",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}