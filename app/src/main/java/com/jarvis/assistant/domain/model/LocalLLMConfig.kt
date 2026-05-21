package com.jarvis.assistant.domain.model

data class LocalLLMConfig(
    val threads: Int = 4,
    val contextLength: Int = 2048,
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val maxTokens: Int = 512,
    val streamingEnabled: Boolean = true,
    val lowMemoryMode: Boolean = false,
    val gpuLayers: Int = 0
)
