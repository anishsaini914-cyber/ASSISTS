package com.jarvis.assistant.ai

import com.jarvis.assistant.data.remote.api.GeminiApiService
import com.jarvis.assistant.data.remote.dto.GeminiContent
import com.jarvis.assistant.data.remote.dto.GeminiGenerationConfig
import com.jarvis.assistant.data.remote.dto.GeminiPart
import com.jarvis.assistant.data.remote.dto.GeminiRequest
import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.AIError
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.model.ModelInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiProvider @Inject constructor(
    private val apiService: GeminiApiService
) : AIProvider {

    override val id = "gemini"
    override val displayName = "Gemini"

    private val supportedModels = listOf(
        ModelInfo("gemini-1.5-pro", "Gemini 1.5 Pro", "gemini"),
        ModelInfo("gemini-1.5-flash", "Gemini 1.5 Flash", "gemini")
    )

    override suspend fun sendMessage(
        messages: List<Message>,
        config: AIConfig
    ): Flow<Result<String>> = flow {
        val parts = messages.map { message ->
            GeminiPart(message.content)
        }

        val geminiContents = listOf(
            GeminiContent(
                role = "user",
                parts = parts
            )
        )

        val request = GeminiRequest(
            contents = geminiContents,
            generationConfig = GeminiGenerationConfig(
                temperature = config.temperature,
                maxOutputTokens = config.maxTokens,
                topP = config.topP
            )
        )

        try {
            if (config.streamingEnabled) {
                val response = apiService.streamGenerateContent(config.model, config.apiKey, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val reader = BufferedReader(InputStreamReader(body.byteStream()))
                        val sb = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            val currentLine = line ?: continue
                            if (currentLine.startsWith("data: ")) {
                                val data = currentLine.removePrefix("data: ")
                                try {
                                    val json = JSONObject(data)
                                    val candidates = json.optJSONArray("candidates")
                                    if (candidates != null && candidates.length() > 0) {
                                        val content = candidates.getJSONObject(0)
                                            .optJSONObject("content")
                                        val partsArr = content?.optJSONArray("parts")
                                        if (partsArr != null && partsArr.length() > 0) {
                                            val text = partsArr.getJSONObject(0).optString("text", "")
                                            sb.append(text)
                                            emit(Result.success(sb.toString()))
                                        }
                                    }
                                } catch (_: Exception) {
                                    // Skip malformed JSON
                                }
                            }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    emit(Result.failure(AIError.Unknown("Gemini error: $errorBody")))
                }
            } else {
                val response = apiService.generateContent(config.model, config.apiKey, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    val text = body?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()
                        ?.text ?: ""
                    emit(Result.success(text))
                } else {
                    emit(Result.failure(AIError.Unknown("Gemini error: ${response.code()}")))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(AIError.NetworkError(e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun validateApiKey(key: String): Boolean {
        return try {
            val response = apiService.generateContent(
                "gemini-1.5-flash", key,
                GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart("test"))))
                )
            )
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    override fun getSupportedModels(): List<ModelInfo> = supportedModels
}
