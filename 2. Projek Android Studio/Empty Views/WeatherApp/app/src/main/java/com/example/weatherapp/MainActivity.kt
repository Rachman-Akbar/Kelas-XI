package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.weatherapp.animations.WeatherAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var editLocation: TextInputEditText
    private lateinit var buttonSearch: MaterialButton
    private lateinit var textLocation: TextView
    private lateinit var imageWeatherIcon: ImageView
    private lateinit var imageBackground: ImageView
    private lateinit var textTemperature: TextView
    private lateinit var textCondition: TextView
    private lateinit var textFeelsLike: TextView
    private lateinit var textWindSpeed: TextView
    private lateinit var textHumidity: TextView
    private lateinit var textVisibility: TextView
    private lateinit var textPressure: TextView
    private lateinit var cardWeatherMain: CardView
    private lateinit var loadingCard: CardView
    private lateinit var weatherAnimationView: WeatherAnimationView

    private val client = OkHttpClient()
    private val apiKey = "8299fa09a4a6cc7e7847c4612328cd17"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
        
        // Load default weather for Jakarta
        fetchWeather("Jakarta")
    }

    private fun initializeViews() {
        editLocation = findViewById(R.id.editLocation)
        buttonSearch = findViewById(R.id.buttonSearch)
        textLocation = findViewById(R.id.textLocation)
        imageWeatherIcon = findViewById(R.id.imageWeatherIcon)
        imageBackground = findViewById(R.id.imageBackground)
        textTemperature = findViewById(R.id.textTemperature)
        textCondition = findViewById(R.id.textCondition)
        textFeelsLike = findViewById(R.id.textFeelsLike)
        textWindSpeed = findViewById(R.id.textWindSpeed)
        textHumidity = findViewById(R.id.textHumidity)
        textVisibility = findViewById(R.id.textVisibility)
        textPressure = findViewById(R.id.textPressure)
        cardWeatherMain = findViewById(R.id.cardWeatherMain)
        loadingCard = findViewById(R.id.loadingCard)
        weatherAnimationView = findViewById(R.id.weatherAnimationView)
    }

    private fun setupClickListeners() {
        buttonSearch.setOnClickListener {
            val location = editLocation.text.toString().trim()
            if (location.isNotEmpty()) {
                fetchWeather(location)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        editLocation.setOnEditorActionListener { _, _, _ ->
            val location = editLocation.text.toString().trim()
            if (location.isNotEmpty()) {
                fetchWeather(location)
                true
            } else {
                false
            }
        }
    }

    private fun fetchWeather(city: String) {
        showLoading(true)
        
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"
        
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showLoading(false)
                    Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                
                runOnUiThread {
                    showLoading(false)
                    
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody)
                            updateUI(jsonObject)
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "Error parsing weather data", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val errorMessage = if (responseBody != null) {
                            try {
                                val errorJson = JSONObject(responseBody)
                                errorJson.getString("message")
                            } catch (e: Exception) {
                                "City not found"
                            }
                        } else {
                            "Unknown error occurred"
                        }
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun updateUI(jsonObject: JSONObject) {
        try {
            // Location information
            val cityName = jsonObject.getString("name")
            val country = jsonObject.getJSONObject("sys").getString("country")
            textLocation.text = "$cityName, $country"

            // Main weather data
            val main = jsonObject.getJSONObject("main")
            val temp = main.getDouble("temp").toInt()
            val feelsLike = main.getDouble("feels_like").toInt()
            val humidity = main.getInt("humidity")
            val pressure = main.getInt("pressure")

            // Weather condition
            val weatherArray = jsonObject.getJSONArray("weather")
            val weather = weatherArray.getJSONObject(0)
            val condition = weather.getString("main").lowercase()
            val description = weather.getString("description")

            // Wind data
            val wind = jsonObject.getJSONObject("wind")
            val windSpeed = wind.getDouble("speed")

            // Visibility (convert from meters to kilometers)
            val visibility = jsonObject.getInt("visibility") / 1000.0

            // Update UI elements
            textTemperature.text = "${temp}°"
            textCondition.text = description.replaceFirstChar { it.uppercase() }
            textFeelsLike.text = "Feels like ${feelsLike}°C"
            textWindSpeed.text = "${windSpeed.toInt()} km/h"
            textHumidity.text = "${humidity}%"
            textVisibility.text = "${visibility.toInt()} km"
            textPressure.text = "${pressure} hPa"

            // Update weather icon and background based on condition
            updateWeatherVisuals(condition)
            
            // Show weather card with animation
            cardWeatherMain.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .start()

            // Clear input field after successful search
            editLocation.text?.clear()

        } catch (e: Exception) {
            Toast.makeText(this, "Error displaying weather data", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateWeatherVisuals(condition: String) {
        val (iconRes, backgroundRes, animationType) = when {
            condition.contains("clear") -> Triple(R.drawable.ic_clear, R.drawable.bg_clear, "clear")
            condition.contains("cloud") -> Triple(R.drawable.ic_clouds, R.drawable.bg_clouds, "clouds")
            condition.contains("rain") || condition.contains("drizzle") -> Triple(R.drawable.ic_rain, R.drawable.bg_rain, "rain")
            condition.contains("snow") -> Triple(R.drawable.ic_snow, R.drawable.bg_snow, "snow")
            condition.contains("thunder") || condition.contains("storm") -> Triple(R.drawable.ic_thunder, R.drawable.bg_thunder, "thunder")
            condition.contains("mist") || condition.contains("fog") || condition.contains("haze") -> Triple(R.drawable.ic_clouds, R.drawable.bg_clouds, "clouds")
            else -> Triple(R.drawable.ic_clear, R.drawable.bg_clear, "clear")
        }

        // Update weather icon with scale animation
        imageWeatherIcon.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(200)
            .withEndAction {
                imageWeatherIcon.setImageResource(iconRes)
                imageWeatherIcon.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start()
            }
            .start()

        // Update background with fade transition
        imageBackground.animate()
            .alpha(0.7f)
            .setDuration(300)
            .withEndAction {
                imageBackground.setImageResource(backgroundRes)
                imageBackground.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .start()
            }
            .start()

        // Update weather animation
        weatherAnimationView.setWeatherAnimation(animationType)
    }

    private fun showLoading(show: Boolean) {
        loadingCard.visibility = if (show) View.VISIBLE else View.GONE
        
        if (show) {
            // Hide main card while loading
            cardWeatherMain.animate()
                .alpha(0.5f)
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(300)
                .start()
            
            // Show loading with animation
            loadingCard.alpha = 0f
            loadingCard.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        } else {
            // Hide loading
            loadingCard.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    loadingCard.visibility = View.GONE
                }
                .start()
        }
    }
}
