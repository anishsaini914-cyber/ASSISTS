package com.jarvis.assistant.data.repository

import com.jarvis.assistant.ai.AIProviderFactory
import com.jarvis.assistant.data.local.db.dao.ConversationDao
import com.jarvis.assistant.data.local.db.dao.MessageDao
import com.jarvis.assistant.data.local.db.entity.ConversationEntity
import com.jarvis.assistant.data.local.db.entity.MessageEntity
import com.jarvis.assistant.data.local.prefs.SecurePreferencesManager
import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.AIError
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val prefs: SecurePreferencesManager,
    private val providerFactory: AIProviderFactory
) : ChatRepository {

    override suspend fun sendMessage(
        messages: List<Message>,
        config: AIConfig
    ): Flow<Result<String>> {
        val provider = providerFactory.getProvider(prefs.getActiveProvider() ?: "openai")
            ?: throw AIError.Unknown("No AI provider available")
        
        val configWithKeys = config.copy(
            apiKey = prefs.getApiKey(prefs.getActiveProvider() ?: "openai") ?: config.apiKey
        )
        
        return provider.sendMessage(messages, configWithKeys)
    }

    override suspend fun saveMessage(message: Message): Long {
        return messageDao.insert(
            MessageEntity(
                conversationId = message.conversationId,
                role = message.role,
                content = message.content,
                timestamp = message.timestamp,
                isStreaming = message.isStreaming
            )
        )
    }

    override fun getMessagesByConversation(conversationId: Long): Flow<List<Message>> {
        return messageDao.getMessagesByConversation(conversationId).map { entities ->
            entities.map { entity ->
                Message(
                    id = entity.id,
                    conversationId = entity.conversationId,
                    role = entity.role,
                    content = entity.content,
                    timestamp = entity.timestamp,
                    isStreaming = entity.isStreaming
                )
            }
        }
    }

    override suspend fun getMessagesSync(conversationId: Long): List<Message> {
        return messageDao.getMessagesByConversationSync(conversationId).map { entity ->
            Message(
                id = entity.id,
                conversationId = entity.conversationId,
                role = entity.role,
                content = entity.content,
                timestamp = entity.timestamp,
                isStreaming = entity.isStreaming
            )
        }
    }

    override suspend fun createConversation(
        title: String,
        providerId: String,
        modelId: String
    ): Long {
        return conversationDao.insert(
            ConversationEntity(
                title = title,
                providerId = providerId,
                modelId = modelId
            )
        )
    }

    override fun getAllConversations(): Flow<List<ConversationEntity>> {
        return conversationDao.getAllConversations()
    }

    override suspend fun deleteConversation(id: Long) {
        conversationDao.deleteById(id)
    }

    override suspend fun updateMessageContent(id: Long, content: String) {
        messageDao.updateContent(id, content)
    }
}
