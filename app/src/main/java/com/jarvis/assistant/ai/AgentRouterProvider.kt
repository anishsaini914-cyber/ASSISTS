package com.jarvis.assistant.ai

import com.jarvis.assistant.data.remote.api.AgentRouterApiService
import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.AIError
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.model.ModelInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRouterProvider @Inject constructor(
    private val apiService: AgentRouterApiService
) : AIProvider {

    override val id = "agentrouter"
    override val displayName = "AgentRouter"

    private val supportedModels = listOf(
        ModelInfo("default", "Default", "agentrouter")
    )

    override suspend fun sendMessage(
        messages: List<Message>,
        config: AIConfig
    ): Flow<Result<String>> = flow {
        val requestMap = mapOf<String, Any>(
            "model" to config.model,
            "messages" to messages.map { mapOf("role" to it.role, "content" to it.content) },
            "temperature" to config.temperature.toDouble(),
            "max_tokens" to config.maxTokens,
            "stream" to config.streamingEnabled
        )

        try {
            val authHeader = "Bearer ${config.apiKey}"
            val response = if (config.streamingEnabled) {
                apiService.sendMessageStream(authHeader, requestMap)
            } else {
                apiService.sendMessage(authHeader, requestMap)
            }

            if (response.isSuccessful) {
                val body = response.body()?.string() ?: ""
                // Simple non-streaming support
                try {
                    val json = org.json.JSONObject(body)
                    val content = json.optJSONArray("choices")?.optJSONObject(0)
                        ?.optJSONObject("message")?.optString("content", body) ?: body
                    emit(Result.success(content))
                } catch (_: Exception) {
                    emit(Result.success(body))
                }
            } else {
                emit(Result.failure(AIError.Unknown("AgentRouter error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(AIError.NetworkError(e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun validateApiKey(key: String): Boolean {
        return try {
            val response = apiService.sendMessage(
                "Bearer $key",
                mapOf("model" to "default", "messages" to emptyList<Map<String, String>>())
            )
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    override fun getSupportedModels(): List<ModelInfo> = supportedModels
}
