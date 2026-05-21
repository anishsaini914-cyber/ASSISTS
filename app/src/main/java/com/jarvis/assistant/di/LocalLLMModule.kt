package com.jarvis.assistant.di

import com.jarvis.assistant.llm.ChatMemoryBridge
import com.jarvis.assistant.llm.GGUFLoader
import com.jarvis.assistant.llm.LocalInferenceEngine
import com.jarvis.assistant.llm.LocalLLMManager
import com.jarvis.assistant.llm.PromptFormatter
import com.jarvis.assistant.llm.TokenStreamingSystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalLLMModule {

    @Provides
    @Singleton
    fun provideLocalInferenceEngine(): LocalInferenceEngine {
        return LocalInferenceEngine()
    }

    @Provides
    @Singleton
    fun provideGGUFLoader(): GGUFLoader {
        return GGUFLoader()
    }

    @Provides
    @Singleton
    fun provideChatMemoryBridge(): ChatMemoryBridge {
        return ChatMemoryBridge()
    }

    @Provides
    @Singleton
    fun providePromptFormatter(): PromptFormatter {
        return PromptFormatter()
    }

    @Provides
    @Singleton
    fun provideTokenStreamingSystem(): TokenStreamingSystem {
        return TokenStreamingSystem()
    }
}
