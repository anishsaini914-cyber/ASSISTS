package com.jarvis.assistant.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelRegistryDao {

    @Query("SELECT * FROM model_metadata ORDER BY importedAt DESC")
    fun getAllModels(): Flow<List<ModelMetadataEntity>>

    @Query("SELECT * FROM model_metadata ORDER BY importedAt DESC")
    suspend fun getAllModelsSync(): List<ModelMetadataEntity>

    @Query("SELECT * FROM model_metadata WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveModel(): ModelMetadataEntity?

    @Query("SELECT * FROM model_metadata WHERE isActive = 1 LIMIT 1")
    fun getActiveModelFlow(): Flow<ModelMetadataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: ModelMetadataEntity): Long

    @Query("DELETE FROM model_metadata WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE model_metadata SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE model_metadata SET isActive = 1 WHERE id = :id")
    suspend fun setActive(id: Long)

    @Query("SELECT * FROM model_metadata WHERE id = :id")
    suspend fun getById(id: Long): ModelMetadataEntity?

    @Query("SELECT COUNT(*) FROM model_metadata")
    suspend fun getCount(): Int
}
