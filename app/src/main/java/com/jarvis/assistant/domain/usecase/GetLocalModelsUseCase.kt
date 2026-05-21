package com.jarvis.assistant.domain.usecase

import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import com.jarvis.assistant.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocalModelsUseCase @Inject constructor(
    private val modelRepository: ModelRepository
) {
    operator fun invoke(): Flow<List<ModelMetadataEntity>> {
        return modelRepository.getAllModels()
    }

    suspend fun getActiveModel(): ModelMetadataEntity? {
        return modelRepository.getActiveModel()
    }
}
