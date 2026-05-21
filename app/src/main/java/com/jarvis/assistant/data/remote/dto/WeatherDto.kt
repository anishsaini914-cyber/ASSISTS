package com.jarvis.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherApiResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("current_weather") val currentWeather: CurrentWeather?,
    val hourly: HourlyData?,
    @SerializedName("timezone") val timezone: String?
)

data class CurrentWeather(
    val temperature: Float,
    @SerializedName("weathercode") val weatherCode: Int,
    @SerializedName("windspeed") val windSpeed: Float,
    @SerializedName("is_day") val isDay: Int
)

data class HourlyData(
    val time: List<String>?,
    @SerializedName("temperature_2m") val temperature2m: List<Float>?,
    @SerializedName("weathercode") val weatherCode: List<Int>?
)
