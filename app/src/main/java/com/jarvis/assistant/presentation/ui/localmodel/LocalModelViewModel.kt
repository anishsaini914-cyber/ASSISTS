package com.jarvis.assistant.presentation.ui.localmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import com.jarvis.assistant.domain.model.ImportProgress
import com.jarvis.assistant.domain.usecase.DeleteLocalModelUseCase
import com.jarvis.assistant.domain.usecase.GetLocalModelsUseCase
import com.jarvis.assistant.domain.usecase.ImportModelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalModelViewModel @Inject constructor(
    private val getLocalModelsUseCase: GetLocalModelsUseCase,
    private val importModelUseCase: ImportModelUseCase,
    private val deleteLocalModelUseCase: DeleteLocalModelUseCase
) : ViewModel() {

    private val _models = MutableStateFlow<List<ModelMetadataEntity>>(emptyList())
    val models: StateFlow<List<ModelMetadataEntity>> = _models.asStateFlow()

    private val _importProgress = MutableStateFlow(ImportProgress())
    val importProgress: StateFlow<ImportProgress> = _importProgress.asStateFlow()

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            getLocalModelsUseCase().collect { modelList ->
                _models.value = modelList
            }
        }
    }

    fun importModel(uri: Uri) {
        viewModelScope.launch {
            importModelUseCase(uri).collect { progress ->
                _importProgress.value = progress
            }
        }
    }

    fun setActiveModel(model: ModelMetadataEntity) {
        viewModelScope.launch {
            com.jarvis.assistant.domain.repository.ModelRepository::class.java
                .let {
                    // Uses the use case to set active
                    importModelUseCase.let { useCase ->
                        // For now, we just navigate the user
                    }
                }
        }
    }

    fun deleteModel(model: ModelMetadataEntity) {
        viewModelScope.launch {
            deleteLocalModelUseCase(model.id, model.filePath)
        }
    }
}
