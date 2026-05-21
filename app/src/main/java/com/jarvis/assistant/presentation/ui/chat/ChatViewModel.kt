package com.jarvis.assistant.presentation.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.assistant.data.local.db.entity.ConversationEntity
import com.jarvis.assistant.data.local.prefs.SecurePreferencesManager
import com.jarvis.assistant.domain.model.AIConfig
import com.jarvis.assistant.domain.model.Message
import com.jarvis.assistant.domain.usecase.GetConversationHistoryUseCase
import com.jarvis.assistant.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getConversationHistoryUseCase: GetConversationHistoryUseCase,
    private val prefs: SecurePreferencesManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentConversationId = MutableStateFlow<Long?>(null)
    val currentConversationId: StateFlow<Long?> = _currentConversationId.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _conversations = MutableStateFlow<List<ConversationEntity>>(emptyList())
    val conversations: StateFlow<List<ConversationEntity>> = _conversations.asStateFlow()

    private var lastAssistantMessageId: Long? = null

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            getConversationHistoryUseCase.getAllConversations().collect { convos ->
                _conversations.value = convos
            }
        }
    }

    fun loadMessages(conversationId: Long) {
        _currentConversationId.value = conversationId
        viewModelScope.launch {
            getConversationHistoryUseCase.getMessages(conversationId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun createNewConversation() {
        viewModelScope.launch {
            val id = com.jarvis.assistant.data.repository.ChatRepositoryImpl::class.java
                .let {
                    val providerId = prefs.getActiveProvider() ?: "openai"
                    val modelId = prefs.getSelectedModel(providerId) ?: "gpt-4o"
                    // Create new conversation
                    0L // Placeholder - will be replaced after send
                }
            // For simplicity, create on first send
            _currentConversationId.value = null
            _messages.value = emptyList()
        }
    }

    fun sendMessage(text: String) {
        val conversationId = _currentConversationId.value
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Add user message immediately
            val currentMessages = _messages.value.toMutableList()
            val userMessage = Message(
                role = "user",
                content = text
            )
            currentMessages.add(userMessage)
            _messages.value = currentMessages

            // Add placeholder assistant message
            val assistantMessage = Message(
                role = "assistant",
                content = "",
                isStreaming = true
            )
            currentMessages.add(assistantMessage)
            _messages.value = currentMessages

            _isStreaming.value = true

            val providerId = prefs.getActiveProvider() ?: "openai"
            val apiKey = prefs.getApiKey(providerId) ?: ""
            val modelId = prefs.getSelectedModel(providerId) ?: "gpt-4o"

            val config = AIConfig(
                apiKey = apiKey,
                model = modelId
            )

            val activeConversationId = conversationId ?: 0L // Will create via repo

            sendMessageUseCase.sendWithHistory(activeConversationId, text, _messages.value, config)
                .collect { result ->
                    result.onSuccess { response ->
                        _streamingText.value = response
                        // Update the last assistant message
                        val msgs = _messages.value.toMutableList()
                        if (msgs.isNotEmpty()) {
                            val lastIndex = msgs.lastIndex
                            msgs[lastIndex] = msgs[lastIndex].copy(
                                content = response,
                                isStreaming = result.isSuccess
                            )
                            _messages.value = msgs
                        }
                    }
                    result.onFailure { e ->
                        _error.value = e.message ?: "An error occurred"
                    }
                }

            _isStreaming.value = false
            _isLoading.value = false
            _streamingText.value = ""
        }
    }

    fun clearError() {
        _error.value = null
    }
}
