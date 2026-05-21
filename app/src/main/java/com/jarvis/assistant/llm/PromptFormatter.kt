package com.jarvis.assistant.llm

import com.jarvis.assistant.domain.model.Message
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptFormatter @Inject constructor() {

    fun formatMessages(messages: List<Message>, architecture: String = "llama"): String {
        return when {
            architecture.contains("llama", ignoreCase = true) -> formatLlama(messages)
            architecture.contains("mistral", ignoreCase = true) -> formatMistral(messages)
            architecture.contains("phi", ignoreCase = true) -> formatPhi(messages)
            architecture.contains("falcon", ignoreCase = true) -> formatFalcon(messages)
            else -> formatLlama(messages)
        }
    }

    private fun formatLlama(messages: List<Message>): String {
        val sb = StringBuilder()
        sb.append("<s>")
        messages.forEach { msg ->
            when (msg.role.lowercase()) {
                "system" -> sb.append("[INST] <<SYS>>\n${msg.content}\n<</SYS>>\n\n")
                "user" -> sb.append("[INST] ${msg.content} [/INST]")
                "assistant" -> sb.append(" ${msg.content} </s><s>")
                else -> sb.append("[INST] ${msg.content} [/INST]")
            }
        }
        return sb.toString()
    }

    private fun formatMistral(messages: List<Message>): String {
        val sb = StringBuilder()
        messages.forEach { msg ->
            when (msg.role.lowercase()) {
                "user" -> sb.append("[INST] ${msg.content} [/INST]")
                "assistant" -> sb.append(" ${msg.content}")
                "system" -> sb.append("<s>${msg.content}</s>\n")
                else -> sb.append("[INST] ${msg.content} [/INST]")
            }
        }
        return sb.toString()
    }

    private fun formatPhi(messages: List<Message>): String {
        val sb = StringBuilder()
        messages.forEach { msg ->
            when (msg.role.lowercase()) {
                "user" -> sb.append("Instruct: ${msg.content}\n")
                "assistant" -> sb.append("Output: ${msg.content}\n")
                "system" -> sb.append("System: ${msg.content}\n")
                else -> sb.append("Instruct: ${msg.content}\n")
            }
        }
        sb.append("Output: ")
        return sb.toString()
    }

    private fun formatFalcon(messages: List<Message>): String {
        val sb = StringBuilder()
        messages.forEach { msg ->
            when (msg.role.lowercase()) {
                "user" -> sb.append("User: ${msg.content}\n")
                "assistant" -> sb.append("Falcon: ${msg.content}\n")
                "system" -> sb.append("System: ${msg.content}\n")
                else -> sb.append("User: ${msg.content}\n")
            }
        }
        sb.append("Falcon: ")
        return sb.toString()
    }

    fun formatMessage(role: String, content: String): String {
        return "$role: $content\n"
    }
}
