package com.jarvis.assistant.data.repository

import com.jarvis.assistant.data.local.db.dao.ModelRegistryDao
import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import com.jarvis.assistant.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRepositoryImpl @Inject constructor(
    private val modelRegistryDao: ModelRegistryDao
) : ModelRepository {

    override fun getAllModels(): Flow<List<ModelMetadataEntity>> {
        return modelRegistryDao.getAllModels()
    }

    override suspend fun getAllModelsSync(): List<ModelMetadataEntity> {
        return modelRegistryDao.getAllModelsSync()
    }

    override suspend fun insertModel(model: ModelMetadataEntity): Long {
        return modelRegistryDao.insert(model)
    }

    override suspend fun deleteModel(id: Long) {
        modelRegistryDao.deleteById(id)
    }

    override suspend fun setActiveModel(id: Long) {
        modelRegistryDao.deactivateAll()
        modelRegistryDao.setActive(id)
    }

    override suspend fun getActiveModel(): ModelMetadataEntity? {
        return modelRegistryDao.getActiveModel()
    }

    override fun getActiveModelFlow(): Flow<ModelMetadataEntity?> {
        return modelRegistryDao.getActiveModelFlow()
    }

    override suspend fun getModelById(id: Long): ModelMetadataEntity? {
        return modelRegistryDao.getById(id)
    }
}
