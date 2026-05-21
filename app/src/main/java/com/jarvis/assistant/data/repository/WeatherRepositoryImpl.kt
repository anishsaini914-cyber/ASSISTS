package com.jarvis.assistant.data.repository

import com.jarvis.assistant.data.remote.api.WeatherApiService
import com.jarvis.assistant.domain.model.WeatherResult
import com.jarvis.assistant.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApiService
) : WeatherRepository {

    private var cachedWeather: WeatherResult? = null

    override suspend fun getWeather(lat: Double, lon: Double): Flow<Result<WeatherResult>> = flow {
        try {
            val response = weatherApi.getForecast(lat, lon)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.currentWeather != null) {
                    val current = body.currentWeather
                    val result = WeatherResult(
                        temperature = current.temperature,
                        weatherCode = current.weatherCode,
                        windSpeed = current.windSpeed,
                        isDay = current.isDay == 1,
                        description = WeatherResult.getWeatherDescription(current.weatherCode)
                    )
                    cachedWeather = result
                    emit(Result.success(result))
                } else {
                    emit(Result.failure(Exception("No weather data available")))
                }
            } else {
                emit(Result.failure(Exception("Weather API error: ${response.code()}")))
            }
        } catch (e: Exception) {
            cachedWeather?.let { emit(Result.success(it)) }
                ?: emit(Result.failure(e))
        }
    }

    override suspend fun getCachedWeather(): WeatherResult? = cachedWeather

    override suspend fun cacheWeather(weather: WeatherResult, lat: Double, lon: Double) {
        cachedWeather = weather
    }
}
