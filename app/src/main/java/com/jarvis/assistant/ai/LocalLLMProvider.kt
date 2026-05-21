package com.jarvis.assistant.ai

import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.AIError
import com.jarvis.assistant.domain.model.LocalLLMConfig
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.model.ModelInfo
import com.jarvis.assistant.llm.LocalLLMManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLLMProvider @Inject constructor(
    private val localLLMManager: LocalLLMManager
) : AIProvider {

    override val id = "local"
    override val displayName = "Local LLM"

    private val supportedModels = listOf(
        ModelInfo("local", "Local Model", "local")
    )

    override suspend fun sendMessage(
        messages: List<Message>,
        config: AIConfig
    ): Flow<Result<String>> = flow {
        if (!localLLMManager.isNativeAvailable()) {
            emit(Result.failure(AIError.Unknown(
                "Local LLM inference is not available on this device. Use a cloud AI provider instead."
            )))
            return@flow
        }

        val prompt = messages.joinToString("\n") { "${it.role}: ${it.content}" }
        val llmConfig = LocalLLMConfig(
            temperature = config.temperature,
            maxTokens = config.maxTokens,
            topP = config.topP
        )

        localLLMManager.generateResponse(prompt, llmConfig).collect { token ->
            emit(Result.success(token))
        }
    }

    override suspend fun validateApiKey(key: String): Boolean = localLLMManager.isNativeAvailable()

    override fun getSupportedModels(): List<ModelInfo> = supportedModels
}
