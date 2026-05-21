package com.jarvis.assistant.domain.model

data class ModelInfo(
    val id: String,
    val displayName: String,
    val provider: String,
    val maxTokens: Int = 4096,
    val supportsStreaming: Boolean = true
)
