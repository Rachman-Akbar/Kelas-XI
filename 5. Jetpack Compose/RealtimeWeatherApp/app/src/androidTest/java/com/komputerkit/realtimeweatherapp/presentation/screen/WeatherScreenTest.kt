package com.komputerkit.realtimeweatherapp.presentation.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.komputerkit.realtimeweatherapp.data.model.*
import com.komputerkit.realtimeweatherapp.presentation.viewmodel.WeatherUiState
import com.komputerkit.realtimeweatherapp.presentation.viewmodel.WeatherViewModel
import com.komputerkit.realtimeweatherapp.ui.theme.RealtimeWeatherAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class WeatherScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weatherScreen_initialState_showsWelcomeMessage() {
        val mockViewModel = mock(WeatherViewModel::class.java)
        val initialState = WeatherUiState()
        
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(initialState))

        composeTestRule.setContent {
            RealtimeWeatherAppTheme {
                WeatherScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule
            .onNodeWithText("Welcome to Weather App")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Enter a city name to get current weather information")
            .assertIsDisplayed()
    }

    @Test
    fun weatherScreen_loadingState_showsLoadingIndicator() {
        val mockViewModel = mock(WeatherViewModel::class.java)
        val loadingState = WeatherUiState(
            isLoading = true,
            searchQuery = "London",
            hasSearched = true
        )
        
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(loadingState))

        composeTestRule.setContent {
            RealtimeWeatherAppTheme {
                WeatherScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule
            .onNodeWithText("Fetching weather for London...")
            .assertIsDisplayed()
        
        // Check that circular progress indicator is shown
        composeTestRule
            .onNode(hasTestTag("loading_indicator") or hasContentDescription("Loading"))
            .assertExists()
    }

    @Test
    fun weatherScreen_errorState_showsErrorMessage() {
        val mockViewModel = mock(WeatherViewModel::class.java)
        val errorState = WeatherUiState(
            errorMessage = "Location not found. Please try a different city name.",
            hasSearched = true
        )
        
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(errorState))

        composeTestRule.setContent {
            RealtimeWeatherAppTheme {
                WeatherScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule
            .onNodeWithText("Error")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Location not found. Please try a different city name.")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Retry")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Dismiss")
            .assertIsDisplayed()
    }

    @Test
    fun weatherScreen_successState_showsWeatherData() {
        val mockViewModel = mock(WeatherViewModel::class.java)
        val mockWeatherData = createMockWeatherResponse()
        val successState = WeatherUiState(
            weatherData = mockWeatherData,
            hasSearched = true
        )
        
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(successState))

        composeTestRule.setContent {
            RealtimeWeatherAppTheme {
                WeatherScreen(viewModel = mockViewModel)
            }
        }

        // Check location is displayed
        composeTestRule
            .onNodeWithText("London, City of London, Greater London, United Kingdom")
            .assertIsDisplayed()
        
        // Check temperature is displayed
        composeTestRule
            .onNodeWithText("15°C")
            .assertIsDisplayed()
        
        // Check condition is displayed
        composeTestRule
            .onNodeWithText("Partly cloudy")
            .assertIsDisplayed()
        
        // Check weather details
        composeTestRule
            .onNodeWithText("Humidity")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("72%")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Wind")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("11 km/h")
            .assertIsDisplayed()
    }

    @Test
    fun weatherScreen_searchBar_isInteractable() {
        val mockViewModel = mock(WeatherViewModel::class.java)
        val initialState = WeatherUiState()
        
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(initialState))

        composeTestRule.setContent {
            RealtimeWeatherAppTheme {
                WeatherScreen(viewModel = mockViewModel)
            }
        }

        // Find the search field
        val searchField = composeTestRule.onNodeWithText("Enter city name (e.g., London, Jakarta)")
        searchField.assertIsDisplayed()
        
        // Type in the search field
        searchField.performTextInput("London")
        
        // Verify text was entered (this would be more complex in a real test with proper ViewModel interaction)
    }

    @Test
    fun weatherScreen_hasCorrectTopAppBar() {
        val mockViewModel = mock(WeatherViewModel::class.java)
        val initialState = WeatherUiState()
        
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(initialState))

        composeTestRule.setContent {
            RealtimeWeatherAppTheme {
                WeatherScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule
            .onNodeWithText("Weather App")
            .assertIsDisplayed()
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