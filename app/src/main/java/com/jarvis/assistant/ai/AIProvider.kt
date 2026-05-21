package com.jarvis.assistant.ai

import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.model.ModelInfo
import kotlinx.coroutines.flow.Flow

interface AIProvider {
    val id: String
    val displayName: String

    suspend fun sendMessage(
        messages: List<Message>,
        config: AIConfig
    ): Flow<Result<String>>

    suspend fun validateApiKey(key: String): Boolean

    fun getSupportedModels(): List<ModelInfo>
}
