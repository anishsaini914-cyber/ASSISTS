package com.jarvis.assistant.domain.repository

import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.AIError
import com.jarvis.assistant.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(
        messages: List<Message>,
        config: AIConfig
    ): Flow<Result<String>>

    suspend fun saveMessage(message: Message): Long
    fun getMessagesByConversation(conversationId: Long): Flow<List<Message>>
    suspend fun getMessagesSync(conversationId: Long): List<Message>
    suspend fun createConversation(title: String, providerId: String, modelId: String): Long
    fun getAllConversations(): Flow<List<com.jarvis.assistant.data.local.db.entity.ConversationEntity>>
    suspend fun deleteConversation(id: Long)
    suspend fun updateMessageContent(id: Long, content: String)
}
