package com.jarvis.assistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_metadata")
data class ModelMetadataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val architecture: String = "",
    val quantization: String = "",
    val contextLength: Int = 2048,
    val parameterCount: Long = 0,
    val ramEstimateMb: Int = 0,
    val importedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
)
