package com.jarvis.assistant.domain.repository

import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun getAllModels(): Flow<List<ModelMetadataEntity>>
    suspend fun getAllModelsSync(): List<ModelMetadataEntity>
    suspend fun insertModel(model: ModelMetadataEntity): Long
    suspend fun deleteModel(id: Long)
    suspend fun setActiveModel(id: Long)
    suspend fun getActiveModel(): ModelMetadataEntity?
    fun getActiveModelFlow(): Flow<ModelMetadataEntity?>
    suspend fun getModelById(id: Long): ModelMetadataEntity?
}
