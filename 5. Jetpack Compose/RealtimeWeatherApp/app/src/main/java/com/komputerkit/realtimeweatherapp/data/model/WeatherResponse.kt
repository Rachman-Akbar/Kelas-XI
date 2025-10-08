package com.komputerkit.realtimeweatherapp.data.model

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

@Stable
data class WeatherResponse(
    @SerializedName("location") val location: Location? = null,
    @SerializedName("current") val current: Current? = null
)

@Stable
data class Location(
    @SerializedName("name") val name: String? = null,
    @SerializedName("region") val region: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lon") val lon: Double? = null,
    @SerializedName("tz_id") val tzId: String? = null,
    @SerializedName("localtime_epoch") val localtimeEpoch: Long? = null,
    @SerializedName("localtime") val localtime: String? = null
) {
    val fullLocationName: String
        get() = listOfNotNull(name, region, country)
            .joinToString(", ")
            .takeIf { it.isNotEmpty() } ?: "Unknown Location"
}

@Stable
data class Current(
    @SerializedName("last_updated_epoch") val lastUpdatedEpoch: Long? = null,
    @SerializedName("last_updated") val lastUpdated: String? = null,
    @SerializedName("temp_c") val tempC: Double? = null,
    @SerializedName("temp_f") val tempF: Double? = null,
    @SerializedName("is_day") val isDay: Int? = null,
    @SerializedName("condition") val condition: Condition? = null,
    @SerializedName("wind_mph") val windMph: Double? = null,
    @SerializedName("wind_kph") val windKph: Double? = null,
    @SerializedName("wind_degree") val windDegree: Int? = null,
    @SerializedName("wind_dir") val windDir: String? = null,
    @SerializedName("pressure_mb") val pressureMb: Double? = null,
    @SerializedName("pressure_in") val pressureIn: Double? = null,
    @SerializedName("precip_mm") val precipMm: Double? = null,
    @SerializedName("precip_in") val precipIn: Double? = null,
    @SerializedName("humidity") val humidity: Int? = null,
    @SerializedName("cloud") val cloud: Int? = null,
    @SerializedName("feelslike_c") val feelslikeC: Double? = null,
    @SerializedName("feelslike_f") val feelslikeF: Double? = null,
    @SerializedName("vis_km") val visKm: Double? = null,
    @SerializedName("vis_miles") val visMiles: Double? = null,
    @SerializedName("uv") val uv: Double? = null,
    @SerializedName("gust_mph") val gustMph: Double? = null,
    @SerializedName("gust_kph") val gustKph: Double? = null
) {
    val safeTemperatureC: String
        get() = tempC?.toInt()?.toString()?.plus("°C") ?: "N/A"
    
    val safeHumidity: String
        get() = humidity?.toString()?.plus("%") ?: "N/A"
    
    val safeWindSpeed: String
        get() = windKph?.toInt()?.toString()?.plus(" km/h") ?: "N/A"
    
    val safePressure: String
        get() = pressureMb?.toInt()?.toString()?.plus(" mb") ?: "N/A"
}

@Stable
data class Condition(
    @SerializedName("text") val text: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("code") val code: Int? = null
) {
    val safeText: String
        get() = text?.takeIf { it.isNotEmpty() } ?: "Unknown"
    
    val httpIcon: String?
        get() = icon?.replace("//", "https://")
}