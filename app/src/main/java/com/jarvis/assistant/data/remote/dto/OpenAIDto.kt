package com.jarvis.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    val temperature: Float = 0.7f,
    @SerializedName("max_tokens") val maxTokens: Int = 2048,
    @SerializedName("top_p") val topP: Float = 0.9f,
    val stream: Boolean = true
)

data class OpenAIMessage(
    val role: String,
    val content: String
)

data class OpenAIResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<OpenAIChoice>,
    val usage: OpenAIUsage?
)

data class OpenAIChoice(
    val index: Int,
    val delta: OpenAIMessage?,
    val message: OpenAIMessage?,
    @SerializedName("finish_reason") val finishReason: String?
)

data class OpenAIUsage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)

data class OpenAIErrorResponse(
    val error: OpenAIErrorDetail?
)

data class OpenAIErrorDetail(
    val message: String,
    val type: String,
    val code: String?
)
