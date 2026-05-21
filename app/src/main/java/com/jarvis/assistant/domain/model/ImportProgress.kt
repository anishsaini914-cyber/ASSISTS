package com.jarvis.assistant.domain.model

data class ImportProgress(
    val percentage: Int = 0,
    val stage: String = "Starting",
    val isComplete: Boolean = false,
    val error: String? = null
)
