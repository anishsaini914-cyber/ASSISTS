package com.jarvis.assistant.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AgentRouterApiService {

    @POST("chat/completions")
    suspend fun sendMessage(
        @Header("Authorization") authHeader: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ResponseBody>

    @POST("chat/completions")
    suspend fun sendMessageStream(
        @Header("Authorization") authHeader: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ResponseBody>
}
