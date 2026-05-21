package com.jarvis.assistant.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIProviderFactory @Inject constructor(
    private val openAIProvider: OpenAIProvider,
    private val geminiProvider: GeminiProvider,
    private val agentRouterProvider: AgentRouterProvider,
    private val localLLMProvider: LocalLLMProvider
) {
    fun getProvider(providerId: String): AIProvider? {
        return when (providerId.lowercase()) {
            "openai" -> openAIProvider
            "gemini" -> geminiProvider
            "agentrouter" -> agentRouterProvider
            "local" -> localLLMProvider
            else -> null
        }
    }

    fun getAllProviders(): List<AIProvider> {
        return listOf(openAIProvider, geminiProvider, agentRouterProvider, localLLMProvider)
    }
}
