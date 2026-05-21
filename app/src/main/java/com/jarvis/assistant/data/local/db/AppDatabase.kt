package com.jarvis.assistant.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jarvis.assistant.data.local.db.dao.ConversationDao
import com.jarvis.assistant.data.local.db.dao.MessageDao
import com.jarvis.assistant.data.local.db.dao.ModelRegistryDao
import com.jarvis.assistant.data.local.db.entity.ConversationEntity
import com.jarvis.assistant.data.local.db.entity.MessageEntity
import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        ModelMetadataEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun modelRegistryDao(): ModelRegistryDao
}
