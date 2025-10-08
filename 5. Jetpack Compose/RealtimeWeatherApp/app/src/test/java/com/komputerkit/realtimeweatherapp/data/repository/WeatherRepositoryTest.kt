package com.komputerkit.realtimeweatherapp.data.repository

import com.komputerkit.realtimeweatherapp.data.model.*
import com.komputerkit.realtimeweatherapp.data.network.WeatherApiService
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

class WeatherRepositoryTest {

    @Mock
    private lateinit var weatherApiService: WeatherApiService

    private lateinit var repository: WeatherRepository
    private val testApiKey = "test_api_key"

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = WeatherRepository(weatherApiService, testApiKey)
    }

    @Test
    fun `getCurrentWeather should return success when API call succeeds`() = runTest {
        val mockResponse = createMockWeatherResponse()
        val successResponse = Response.success(mockResponse)
        
        whenever(weatherApiService.getCurrentWeather(testApiKey, "London", "no"))
            .thenReturn(successResponse)

        val result = repository.getCurrentWeather("London")

        assertTrue(result.isSuccess())
        assertEquals(mockResponse, result.dataOrNull())
    }

    @Test
    fun `getCurrentWeather should return error for empty location`() = runTest {
        val result = repository.getCurrentWeather("")

        assertTrue(result.isError())
        assertTrue(result is WeatherResult.Error)
        assertEquals("Please enter a valid city name", (result as WeatherResult.Error).message)
    }

    @Test
    fun `getCurrentWeather should return error for blank location`() = runTest {
        val result = repository.getCurrentWeather("   ")

        assertTrue(result.isError())
        assertTrue(result is WeatherResult.Error)
        assertEquals("Please enter a valid city name", (result as WeatherResult.Error).message)
    }

    @Test
    fun `getCurrentWeather should handle 404 error correctly`() = runTest {
        val errorResponse = Response.error<WeatherResponse>(
            404, 
            "Not Found".toResponseBody()
        )
        
        whenever(weatherApiService.getCurrentWeather(testApiKey, "NonExistentCity", "no"))
            .thenReturn(errorResponse)

        val result = repository.getCurrentWeather("NonExistentCity")

        assertTrue(result.isError())
        assertTrue(result is WeatherResult.Error)
        assertEquals("Location not found. Please try a different city name.", (result as WeatherResult.Error).message)
    }

    @Test
    fun `getCurrentWeather should handle 401 error correctly`() = runTest {
        val errorResponse = Response.error<WeatherResponse>(
            401, 
            "Unauthorized".toResponseBody()
        )
        
        whenever(weatherApiService.getCurrentWeather(testApiKey, "London", "no"))
            .thenReturn(errorResponse)

        val result = repository.getCurrentWeather("London")

        assertTrue(result.isError())
        assertTrue(result is WeatherResult.Error)
        assertEquals("API key is invalid. Please check your configuration.", (result as WeatherResult.Error).message)
    }

    @Test
    fun `getCurrentWeather should handle network exception`() = runTest {
        whenever(weatherApiService.getCurrentWeather(testApiKey, "London", "no"))
            .thenThrow(UnknownHostException("Unable to resolve host"))

        val result = repository.getCurrentWeather("London")

        assertTrue(result.isError())
        assertTrue(result is WeatherResult.Error)
        assertEquals("No internet connection. Please check your network.", (result as WeatherResult.Error).message)
    }

    @Test
    fun `getCurrentWeather should handle IOException`() = runTest {
        whenever(weatherApiService.getCurrentWeather(testApiKey, "London", "no"))
            .thenThrow(IOException("Network error"))

        val result = repository.getCurrentWeather("London")

        assertTrue(result.isError())
        assertTrue(result is WeatherResult.Error)
        assertEquals("Network error occurred. Please try again.", (result as WeatherResult.Error).message)
    }

    @Test
    fun `getCurrentWeather should handle null response body`() = runTest {
        val nullResponse = Response.success<WeatherResponse>(null)
        
        whenever(weatherApiService.getCurrentWeather(testApiKey, "London", "no"))
            .thenReturn(nullResponse)

        val result = repository.getCurrentWeather("London")

        assertTrue(result.isError())
        assertTrue(result is WeatherResult.Error)
        assertEquals("No weather data found for this location", (result as WeatherResult.Error).message)
    }

    @Test
    fun `validateLocation should return null for valid location`() {
        val result = repository.validateLocation("London")
        assertNull(result)
    }

    @Test
    fun `validateLocation should return error for empty location`() {
        val result = repository.validateLocation("")
        assertEquals("Location cannot be empty", result)
    }

    @Test
    fun `validateLocation should return error for blank location`() {
        val result = repository.validateLocation("   ")
        assertEquals("Location cannot be empty", result)
    }

    @Test
    fun `validateLocation should return error for short location`() {
        val result = repository.validateLocation("A")
        assertEquals("Location must be at least 2 characters", result)
    }

    @Test
    fun `validateLocation should return error for too long location`() {
        val longLocation = "A".repeat(101)
        val result = repository.validateLocation(longLocation)
        assertEquals("Location name is too long", result)
    }

    @Test
    fun `validateLocation should return error for numeric only input`() {
        val result = repository.validateLocation("12345")
        assertEquals("Please enter a valid city name", result)
    }

    @Test
    fun `validateLocation should accept location with numbers and letters`() {
        val result = repository.validateLocation("New York 123")
        assertNull(result)
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