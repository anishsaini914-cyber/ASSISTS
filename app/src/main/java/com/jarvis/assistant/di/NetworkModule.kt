package com.jarvis.assistant.di

import com.jarvis.assistant.data.remote.api.AgentRouterApiService
import com.jarvis.assistant.data.remote.api.GeminiApiService
import com.jarvis.assistant.data.remote.api.OpenAIApiService
import com.jarvis.assistant.data.remote.api.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenAIApiService(client: OkHttpClient): OpenAIApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiApiService(client: OkHttpClient): GeminiApiService {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAgentRouterApiService(client: OkHttpClient): AgentRouterApiService {
        return Retrofit.Builder()
            .baseUrl("https://agentrouter.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AgentRouterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(client: OkHttpClient): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
