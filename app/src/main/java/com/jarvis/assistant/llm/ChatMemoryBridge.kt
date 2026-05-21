package com.jarvis.assistant.llm

import com.jarvis.assistant.domain.model.Message
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatMemoryBridge @Inject constructor() {

    private var contextWindow: MutableList<Message> = mutableListOf()
    private var maxContextTokens: Int = 2048

    fun setMaxContextTokens(tokens: Int) {
        maxContextTokens = tokens
    }

    fun addMessage(message: Message) {
        contextWindow.add(message)
        trimContextWindowIfNeeded()
    }

    fun addMessages(messages: List<Message>) {
        contextWindow.addAll(messages)
        trimContextWindowIfNeeded()
    }

    fun getContextMessages(): List<Message> = contextWindow.toList()

    fun getContextWindow(): String {
        return contextWindow.joinToString("\n") { "${it.role}: ${it.content}" }
    }

    fun clear() {
        contextWindow.clear()
    }

    fun removeLast() {
        if (contextWindow.isNotEmpty()) {
            contextWindow.removeAt(contextWindow.lastIndex)
        }
    }

    fun getApproximateTokenCount(): Int {
        return contextWindow.sumOf { it.content.length / 4 + 4 }
    }

    private fun trimContextWindowIfNeeded() {
        while (getApproximateTokenCount() > maxContextTokens && contextWindow.isNotEmpty()) {
            // Remove oldest non-system messages first
            val firstNonSystemIndex = contextWindow.indexOfFirst { it.role != "system" }
            if (firstNonSystemIndex >= 0) {
                contextWindow.removeAt(firstNonSystemIndex)
            } else {
                break
            }
        }
    }

    fun getHistory(): List<Message> = contextWindow.toList()
}
