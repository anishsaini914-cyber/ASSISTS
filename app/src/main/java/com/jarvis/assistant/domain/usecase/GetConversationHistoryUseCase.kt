package com.jarvis.assistant.domain.usecase

import com.jarvis.assistant.data.local.db.entity.ConversationEntity
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetConversationHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    fun getAllConversations(): Flow<List<ConversationEntity>> {
        return chatRepository.getAllConversations()
    }

    fun getMessages(conversationId: Long): Flow<List<Message>> {
        return chatRepository.getMessagesByConversation(conversationId)
    }
}
