package com.jarvis.assistant.ai

import com.jarvis.assistant.data.remote.api.OpenAIApiService
import com.jarvis.assistant.data.remote.dto.OpenAIMessage
import com.jarvis.assistant.data.remote.dto.OpenAIRequest
import com.jarvis.assistant.data.remote.dto.OpenAIResponse
import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.AIError
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.model.ModelInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIProvider @Inject constructor(
    private val apiService: OpenAIApiService
) : AIProvider {

    override val id = "openai"
    override val displayName = "OpenAI"

    private val supportedModels = listOf(
        ModelInfo("gpt-4o", "GPT-4o", "openai"),
        ModelInfo("gpt-4-turbo", "GPT-4 Turbo", "openai"),
        ModelInfo("gpt-3.5-turbo", "GPT-3.5 Turbo", "openai")
    )

    override suspend fun sendMessage(
        messages: List<Message>,
        config: AIConfig
    ): Flow<Result<String>> = flow {
        val apiMessages = messages.map { OpenAIMessage(it.role, it.content) }
        if (config.systemPrompt.isNotEmpty()) {
            apiMessages.toMutableList().add(0, OpenAIMessage("system", config.systemPrompt))
        }

        val request = OpenAIRequest(
            model = config.model,
            messages = apiMessages,
            temperature = config.temperature,
            maxTokens = config.maxTokens,
            topP = config.topP,
            stream = config.streamingEnabled
        )

        try {
            val authHeader = "Bearer ${config.apiKey}"
            if (config.streamingEnabled) {
                val response = apiService.sendMessageStream(authHeader, request)
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
                                if (data == "[DONE]") break
                                try {
                                    val json = JSONObject(data)
                                    val choices = json.optJSONArray("choices")
                                    if (choices != null && choices.length() > 0) {
                                        val delta = choices.getJSONObject(0).optJSONObject("delta")
                                        val content = delta?.optString("content", "") ?: ""
                                        sb.append(content)
                                        emit(Result.success(sb.toString()))
                                    }
                                } catch (_: Exception) {
                                    // Skip malformed JSON lines
                                }
                            }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    emit(Result.failure(AIError.Unknown("OpenAI error: $errorBody")))
                }
            } else {
                val response = apiService.sendMessage(authHeader, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.choices?.firstOrNull()?.message?.content ?: ""
                    emit(Result.success(content))
                } else {
                    emit(Result.failure(AIError.Unknown("OpenAI error: ${response.code()}")))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(AIError.NetworkError(e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun validateApiKey(key: String): Boolean {
        return try {
            val response = apiService.sendMessage(
                "Bearer $key",
                OpenAIRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(OpenAIMessage("user", "test")),
                    maxTokens = 1,
                    stream = false
                )
            )
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    override fun getSupportedModels(): List<ModelInfo> = supportedModels
}
