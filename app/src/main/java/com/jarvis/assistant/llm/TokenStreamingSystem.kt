package com.jarvis.assistant.llm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStreamingSystem @Inject constructor() {

    private val _tokens = MutableSharedFlow<String>(replay = 0)
    val tokens: Flow<String> = _tokens.asSharedFlow()

    private val _isStreaming = MutableSharedFlow<Boolean>(replay = 1)
    val isStreaming: Flow<Boolean> = _isStreaming.asSharedFlow()

    private var currentBuffer = StringBuilder()
    private var streamingActive = false

    fun startStreaming() {
        streamingActive = true
        currentBuffer = StringBuilder()
        _isStreaming.tryEmit(true)
    }

    suspend fun emitToken(token: String) {
        if (!streamingActive) return
        currentBuffer.append(token)
        _tokens.emit(token)
    }

    fun stopStreaming(): String {
        streamingActive = false
        _isStreaming.tryEmit(false)
        return currentBuffer.toString()
    }

    fun isStreamingActive(): Boolean = streamingActive

    fun cancelStreaming() {
        streamingActive = false
        currentBuffer = StringBuilder()
        _isStreaming.tryEmit(false)
    }

    fun getCurrentBuffer(): String = currentBuffer.toString()

    fun getTokenCount(): Int = currentBuffer.length
}
