package com.komputerkit.realtimeweatherapp.presentation.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.realtimeweatherapp.data.model.WeatherResponse
import com.komputerkit.realtimeweatherapp.data.model.WeatherResult
import com.komputerkit.realtimeweatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class WeatherUiState(
    val weatherData: WeatherResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val hasSearched: Boolean = false
) {
    val isInitialState: Boolean
        get() = weatherData == null && !isLoading && errorMessage == null && !hasSearched
    
    val hasData: Boolean
        get() = weatherData != null
    
    val hasError: Boolean
        get() = errorMessage != null
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null
    
    fun searchWeather(location: String) {
        // Cancel previous search if still running
        searchJob?.cancel()
        
        val trimmedLocation = location.trim()
        
        // Validate input
        val validationError = weatherRepository.validateLocation(trimmedLocation)
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = validationError,
                isLoading = false,
                searchQuery = trimmedLocation
            )
            return
        }
        
        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                searchQuery = trimmedLocation,
                hasSearched = true
            )
            
            // Add small delay to prevent too many rapid requests
            delay(300)
            
            val result = weatherRepository.getCurrentWeather(trimmedLocation)
            when (result) {
                is WeatherResult.Loading -> {
                    // Already in loading state
                }
                is WeatherResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        weatherData = result.data,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                is WeatherResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        weatherData = null,
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun clearData() {
        _uiState.value = WeatherUiState()
    }
    
    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}