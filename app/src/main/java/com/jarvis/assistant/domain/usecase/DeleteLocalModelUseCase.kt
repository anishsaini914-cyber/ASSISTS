package com.jarvis.assistant.domain.usecase

import com.jarvis.assistant.domain.repository.ModelRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteLocalModelUseCase @Inject constructor(
    private val modelRepository: ModelRepository
) {
    suspend operator fun invoke(modelId: Long, filePath: String?) {
        modelRepository.deleteModel(modelId)
        if (filePath != null) {
            try {
                File(filePath).delete()
            } catch (_: Exception) {
                // File may already be deleted
            }
        }
    }
}
