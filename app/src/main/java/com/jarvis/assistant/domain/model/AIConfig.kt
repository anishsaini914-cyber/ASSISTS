package com.jarvis.assistant.domain.model

data class AIConfig(
    val apiKey: String = "",
    val model: String = "gpt-4o",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val topP: Float = 0.9f,
    val systemPrompt: String = "You are JARVIS, an advanced AI assistant. Be helpful, concise, and professional.",
    val streamingEnabled: Boolean = true
)
