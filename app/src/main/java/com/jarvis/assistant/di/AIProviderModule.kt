package com.jarvis.assistant.di

import com.jarvis.assistant.ai.AgentRouterProvider
import com.jarvis.assistant.ai.AIProviderFactory
import com.jarvis.assistant.ai.GeminiProvider
import com.jarvis.assistant.ai.LocalLLMProvider
import com.jarvis.assistant.ai.OpenAIProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIProviderModule {

    @Provides
    @Singleton
    fun provideAIProviderFactory(
        openAIProvider: OpenAIProvider,
        geminiProvider: GeminiProvider,
        agentRouterProvider: AgentRouterProvider,
        localLLMProvider: LocalLLMProvider
    ): AIProviderFactory {
        return AIProviderFactory(
            openAIProvider,
            geminiProvider,
            agentRouterProvider,
            localLLMProvider
        )
    }
}
