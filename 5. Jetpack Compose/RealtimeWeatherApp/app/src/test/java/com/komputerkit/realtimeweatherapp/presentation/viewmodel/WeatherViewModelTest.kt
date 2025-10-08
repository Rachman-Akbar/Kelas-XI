package com.komputerkit.realtimeweatherapp.presentation.viewmodel

import app.cash.turbine.test
import com.komputerkit.realtimeweatherapp.data.model.*
import com.komputerkit.realtimeweatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @Mock
    private lateinit var weatherRepository: WeatherRepository

    private lateinit var viewModel: WeatherViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(weatherRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isInitialState)
            assertFalse(initialState.isLoading)
            assertNull(initialState.weatherData)
            assertNull(initialState.errorMessage)
            assertEquals("", initialState.searchQuery)
        }
    }

    @Test
    fun `searchWeather should update loading state correctly`() = runTest {
        val mockWeatherResponse = createMockWeatherResponse()
        whenever(weatherRepository.validateLocation("London")).thenReturn(null)
        whenever(weatherRepository.getCurrentWeather("London"))
            .thenReturn(WeatherResult.success(mockWeatherResponse))

        viewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            assertTrue(initialState.isInitialState)

            // Trigger search
            viewModel.searchWeather("London")
            
            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals("London", loadingState.searchQuery)
            assertTrue(loadingState.hasSearched)

            // Success state
            testDispatcher.scheduler.advanceUntilIdle()
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertNotNull(successState.weatherData)
            assertEquals(mockWeatherResponse, successState.weatherData)
            assertNull(successState.errorMessage)
        }
    }

    @Test
    fun `searchWeather should handle validation error`() = runTest {
        val errorMessage = "Location cannot be empty"
        whenever(weatherRepository.validateLocation("")).thenReturn(errorMessage)

        viewModel.uiState.test {
            // Initial state
            awaitItem()

            // Trigger search with empty string
            viewModel.searchWeather("")
            
            // Error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.errorMessage)
            assertNull(errorState.weatherData)
        }
    }

    @Test
    fun `searchWeather should handle network error`() = runTest {
        val errorMessage = "Network error occurred"
        whenever(weatherRepository.validateLocation("London")).thenReturn(null)
        whenever(weatherRepository.getCurrentWeather("London"))
            .thenReturn(WeatherResult.error(RuntimeException("Network error"), errorMessage))

        viewModel.uiState.test {
            // Initial state
            awaitItem()

            // Trigger search
            viewModel.searchWeather("London")
            
            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Error state
            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.errorMessage)
            assertNull(errorState.weatherData)
        }
    }

    @Test
    fun `clearError should remove error message`() = runTest {
        // Set up error state first
        val errorMessage = "Test error"
        whenever(weatherRepository.validateLocation("")).thenReturn(errorMessage)

        viewModel.searchWeather("")
        
        viewModel.uiState.test {
            val errorState = awaitItem()
            assertNotNull(errorState.errorMessage)

            // Clear error
            viewModel.clearError()
            
            val clearedState = awaitItem()
            assertNull(clearedState.errorMessage)
        }
    }

    @Test
    fun `clearData should reset to initial state`() = runTest {
        // Set up some state first
        val mockWeatherResponse = createMockWeatherResponse()
        whenever(weatherRepository.validateLocation("London")).thenReturn(null)
        whenever(weatherRepository.getCurrentWeather("London"))
            .thenReturn(WeatherResult.success(mockWeatherResponse))

        viewModel.searchWeather("London")
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.uiState.test {
            // Skip to current state
            val currentState = expectMostRecentItem()
            assertNotNull(currentState.weatherData)

            // Clear data
            viewModel.clearData()
            
            val clearedState = awaitItem()
            assertTrue(clearedState.isInitialState)
            assertNull(clearedState.weatherData)
            assertNull(clearedState.errorMessage)
            assertEquals("", clearedState.searchQuery)
        }
    }

    private fun createMockWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            location = Location(
                name = "London",
                region = "City of London, Greater London",
                country = "United Kingdom",
                lat = 51.52,
                lon = -0.11,
                tzId = "Europe/London",
                localtimeEpoch = 1699123200,
                localtime = "2023-11-04 12:00"
            ),
            current = Current(
                lastUpdatedEpoch = 1699123200,
                lastUpdated = "2023-11-04 12:00",
                tempC = 15.0,
                tempF = 59.0,
                isDay = 1,
                condition = Condition(
                    text = "Partly cloudy",
                    icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                    code = 1003
                ),
                windMph = 6.9,
                windKph = 11.2,
                windDegree = 240,
                windDir = "WSW",
                pressureMb = 1025.0,
                pressureIn = 30.27,
                precipMm = 0.0,
                precipIn = 0.0,
                humidity = 72,
                cloud = 25,
                feelslikeC = 15.0,
                feelslikeF = 59.0,
                visKm = 10.0,
                visMiles = 6.0,
                uv = 4.0,
                gustMph = 8.5,
                gustKph = 13.7
            )
        )
    }
}