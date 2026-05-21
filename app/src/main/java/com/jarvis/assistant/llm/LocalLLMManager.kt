package com.jarvis.assistant.llm

import com.jarvis.assistant.data.local.db.dao.ModelRegistryDao
import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import com.jarvis.assistant.domain.model.LocalLLMConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLLMManager @Inject constructor(
    private val inferenceEngine: LocalInferenceEngine,
    private val modelRegistryDao: ModelRegistryDao
) {

    private var loadedModelInfo: ModelMetadataEntity? = null

    fun isNativeAvailable(): Boolean = inferenceEngine.isNativeAvailable()

    suspend fun loadModel(modelPath: String): Boolean {
        val success = inferenceEngine.loadModel(modelPath)
        if (success) {
            loadedModelInfo = modelRegistryDao.getActiveModel()
        }
        return success
    }

    fun unloadModel() {
        inferenceEngine.unloadModel()
        loadedModelInfo = null
    }

    fun generateResponse(prompt: String, config: LocalLLMConfig): Flow<String> = callbackFlow {
        if (!isNativeAvailable()) {
            send("Local LLM inference is not available on this device. Use a cloud AI provider instead.")
            close()
            return@callbackFlow
        }

        // Format prompt with chat template
        val formattedPrompt = "[INST] $prompt [/INST]"

        inferenceEngine.generateResponse(
            prompt = formattedPrompt,
            maxTokens = config.maxTokens,
            temperature = config.temperature,
            topP = config.topP
        ).collect { token ->
            trySend(token)
        }
        close()
    }

    fun cancelGeneration() {
        inferenceEngine.cancelGeneration()
    }

    fun getLoadedModelInfo(): ModelMetadataEntity? = loadedModelInfo
}
