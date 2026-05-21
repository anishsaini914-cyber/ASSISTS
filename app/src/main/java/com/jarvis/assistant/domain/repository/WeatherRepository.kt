package com.jarvis.assistant.domain.repository

import com.jarvis.assistant.domain.model.WeatherResult
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeather(lat: Double, lon: Double): Flow<Result<WeatherResult>>
    suspend fun getCachedWeather(): WeatherResult?
    suspend fun cacheWeather(weather: WeatherResult, lat: Double, lon: Double)
}
