package com.jarvis.assistant.llm

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalInferenceEngine @Inject constructor() {

    companion object {
        private const val TAG = "LocalInferenceEngine"

        init {
            try {
                System.loadLibrary("llama-jni")
                Log.i(TAG, "Native library loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                Log.w(TAG, "Native library not available: ${e.message}")
            }
        }
    }

    private var isLoaded = false
    private var isCancelled = false

    // Native JNI functions (implemented in C++ stub)
    private external fun nativeInit(modelPath: String): Boolean
    private external fun nativeGenerate(prompt: String, maxTokens: Int, temperature: Float, topP: Float): String?
    private external fun nativeIsAvailable(): Boolean
    private external fun nativeUnload()
    private external fun nativeTokenCount(text: String): Int
    private external fun nativeCancel()
    private external fun nativeIsGenerating(): Boolean
    private external fun nativeSetThreads(threads: Int)
    private external fun nativeGetVersion(): String

    fun isNativeAvailable(): Boolean {
        return try {
            nativeIsAvailable()
        } catch (_: UnsatisfiedLinkError) {
            false
        }
    }

    suspend fun loadModel(modelPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(modelPath)
            if (!file.exists()) {
                Log.e(TAG, "Model file not found: $modelPath")
                return@withContext false
            }
            isLoaded = nativeInit(modelPath)
            Log.i(TAG, "Model loaded: $isLoaded")
            isLoaded
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load model: ${e.message}")
            isLoaded = false
            false
        }
    }

    fun unloadModel() {
        try {
            nativeUnload()
        } catch (_: Exception) {}
        isLoaded = false
    }

    fun generateResponse(
        prompt: String,
        maxTokens: Int = 512,
        temperature: Float = 0.7f,
        topP: Float = 0.9f
    ): Flow<String> = callbackFlow {
        if (!isLoaded) {
            close()
            return@callbackFlow
        }

        isCancelled = false
        try {
            // For the placeholder, emit a single response
            val result = nativeGenerate(prompt, maxTokens, temperature, topP)
            if (result != null && !isCancelled) {
                trySend(result)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Generation failed: ${e.message}")
        }
        close()
    }.also {
        isCancelled = false
    }

    fun cancelGeneration() {
        isCancelled = true
        try {
            nativeCancel()
        } catch (_: Exception) {}
    }

    fun isGenerating(): Boolean {
        return try {
            nativeIsGenerating()
        } catch (_: Exception) {
            false
        }
    }

    fun getVersion(): String {
        return try {
            nativeGetVersion()
        } catch (_: Exception) {
            "0.0.0 (placeholder)"
        }
    }

    fun getTokenCount(text: String): Int {
        return try {
            nativeTokenCount(text)
        } catch (_: Exception) {
            text.length / 4
        }
    }
}
