package com.jarvis.assistant.domain.model

data class Message(
    val id: Long = 0,
    val conversationId: Long = 0,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)
