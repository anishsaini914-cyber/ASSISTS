package com.jarvis.assistant.domain.model

data class WeatherResult(
    val temperature: Float,
    val weatherCode: Int,
    val windSpeed: Float,
    val isDay: Boolean,
    val description: String
) {
    companion object {
        fun getWeatherDescription(code: Int): String = when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Mainly clear"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rainy"
            71, 73, 75 -> "Snowy"
            80, 81, 82 -> "Rain showers"
            95 -> "Thunderstorm"
            96, 99 -> "Thunderstorm with hail"
            else -> "Unknown"
        }
    }
}
