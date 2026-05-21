package com.jarvis.assistant.domain.usecase

import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        conversationId: Long,
        content: String,
        config: AIConfig
    ): Flow<Result<String>> {
        val userMessage = Message(
            conversationId = conversationId,
            role = "user",
            content = content
        )
        chatRepository.saveMessage(userMessage)

        val messages = chatRepository.getMessagesSync(conversationId)
        return chatRepository.sendMessage(messages, config)
    }

    suspend fun sendWithHistory(
        conversationId: Long,
        content: String,
        messages: List<Message>,
        config: AIConfig
    ): Flow<Result<String>> {
        val userMessage = Message(
            conversationId = conversationId,
            role = "user",
            content = content
        )
        chatRepository.saveMessage(userMessage)

        return chatRepository.sendMessage(messages + userMessage, config)
    }
}
