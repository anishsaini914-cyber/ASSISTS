package com.jarvis.assistant.domain.usecase

import android.content.Context
import android.net.Uri
import com.jarvis.assistant.data.local.db.dao.ModelRegistryDao
import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import com.jarvis.assistant.domain.model.ImportProgress
import com.jarvis.assistant.llm.GGUFLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportModelUseCase @Inject constructor(
    private val context: Context,
    private val modelRegistryDao: ModelRegistryDao,
    private val ggufLoader: GGUFLoader
) {
    operator fun invoke(uri: Uri): Flow<ImportProgress> = flow {
        emit(ImportProgress(0, "Validating file", false))

        val modelsDir = File(context.getExternalFilesDir("models"), "")
        if (!modelsDir.exists()) modelsDir.mkdirs()

        val fileName = getFileName(uri) ?: "model_${System.currentTimeMillis()}.gguf"
        val destFile = File(modelsDir, fileName)

        emit(ImportProgress(10, "Starting copy", false))

        val bytesCopied = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(8192)
                    var total = 0L
                    var bytesRead: Int
                    var lastProgress = 10
                    val totalSize = context.contentResolver.openFileDescriptor(uri, "r")?.statSize ?: -1L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        total += bytesRead
                        if (totalSize > 0) {
                            val progress = ((total.toFloat() / totalSize) * 80 + 10).toInt()
                            if (progress > lastProgress) {
                                lastProgress = progress
                                emit(ImportProgress(progress, "Copying", false))
                            }
                        }
                    }
                    total
                }
            } ?: throw Exception("Failed to open input stream")
        }

        emit(ImportProgress(90, "Parsing metadata", false))

        val isValid = withContext(Dispatchers.IO) {
            ggufLoader.validateMagicBytes(destFile)
        }
        if (!isValid) {
            destFile.delete()
            emit(ImportProgress(0, "Invalid model file", true, "Not a valid GGUF/GGML file"))
            return@flow
        }

        val metadata = withContext(Dispatchers.IO) {
            ggufLoader.parseMetadata(destFile)
        }

        val entity = ModelMetadataEntity(
            fileName = fileName,
            filePath = destFile.absolutePath,
            fileSize = bytesCopied,
            architecture = metadata.architecture,
            quantization = metadata.quantization,
            contextLength = metadata.contextLength,
            parameterCount = metadata.parameterCount,
            ramEstimateMb = metadata.ramEstimateMb
        )

        modelRegistryDao.insert(entity)
        emit(ImportProgress(100, "Complete", true))
    }

    private fun getFileName(uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            if (nameIndex >= 0) it.getString(nameIndex) else null
        }
    }
}
