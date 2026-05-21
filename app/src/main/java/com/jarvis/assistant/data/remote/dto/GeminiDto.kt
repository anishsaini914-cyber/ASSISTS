package com.jarvis.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiGenerationConfig(
    val temperature: Float = 0.7f,
    @SerializedName("maxOutputTokens") val maxOutputTokens: Int = 2048,
    @SerializedName("topP") val topP: Float = 0.9f,
    @SerializedName("stopSequences") val stopSequences: List<String>? = null
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?,
    @SerializedName("promptFeedback") val promptFeedback: GeminiPromptFeedback?
)

data class GeminiCandidate(
    val content: GeminiContent?,
    @SerializedName("finishReason") val finishReason: String?,
    val index: Int?,
    val safetyRatings: List<GeminiSafetyRating>?
)

data class GeminiPromptFeedback(
    val safetyRatings: List<GeminiSafetyRating>?
)

data class GeminiSafetyRating(
    val category: String,
    val probability: String
)

data class GeminiErrorResponse(
    val error: GeminiErrorDetail?
)

data class GeminiErrorDetail(
    val code: Int,
    val message: String,
    val status: String
)
