package com.jarvis.assistant.domain.usecase

import com.jarvis.assistant.domain.repository.ChatRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(conversationId: Long) {
        chatRepository.deleteConversation(conversationId)
    }
}
