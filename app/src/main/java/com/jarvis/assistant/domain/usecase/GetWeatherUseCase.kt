package com.jarvis.assistant.domain.usecase

import com.jarvis.assistant.domain.model.WeatherResult
import com.jarvis.assistant.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Flow<Result<WeatherResult>> {
        return weatherRepository.getWeather(lat, lon)
    }

    suspend fun getCached(): WeatherResult? {
        return weatherRepository.getCachedWeather()
    }
}
