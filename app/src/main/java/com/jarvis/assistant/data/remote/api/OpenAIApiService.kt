package com.jarvis.assistant.data.remote.api

import com.jarvis.assistant.data.remote.dto.OpenAIRequest
import com.jarvis.assistant.data.remote.dto.OpenAIResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApiService {

    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun sendMessage(
        @Header("Authorization") authHeader: String,
        @Body request: OpenAIRequest
    ): Response<OpenAIResponse>

    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun sendMessageStream(
        @Header("Authorization") authHeader: String,
        @Body request: OpenAIRequest
    ): Response<ResponseBody>
}
