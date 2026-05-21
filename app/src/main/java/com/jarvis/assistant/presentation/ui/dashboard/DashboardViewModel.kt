package com.jarvis.assistant.presentation.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.assistant.data.local.db.entity.ConversationEntity
import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import com.jarvis.assistant.data.local.prefs.SecurePreferencesManager
import com.jarvis.assistant.domain.model.WeatherResult
import com.jarvis.assistant.domain.repository.ChatRepository
import com.jarvis.assistant.domain.repository.ModelRepository
import com.jarvis.assistant.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository,
    private val prefs: SecurePreferencesManager
) : ViewModel() {

    private val _weather = MutableStateFlow<WeatherResult?>(null)
    val weather: StateFlow<WeatherResult?> = _weather.asStateFlow()

    private val _activeModel = MutableStateFlow<ModelMetadataEntity?>(null)
    val activeModel: StateFlow<ModelMetadataEntity?> = _activeModel.asStateFlow()

    private val _recentConversations = MutableStateFlow<List<ConversationEntity>>(emptyList())
    val recentConversations: StateFlow<List<ConversationEntity>> = _recentConversations.asStateFlow()

    init {
        loadWeather()
        loadActiveModel()
        loadRecentConversations()
    }

    private fun loadWeather() {
        val location = prefs.getWeatherLocation()
        if (location.first != null && location.second != null) {
            viewModelScope.launch {
                getWeatherUseCase(location.first!!, location.second!!).collect { result ->
                    result.onSuccess { _weather.value = it }
                    result.onFailure { _weather.value = getWeatherUseCase.getCached() }
                }
            }
        }
    }

    private fun loadActiveModel() {
        viewModelScope.launch {
            modelRepository.getActiveModelFlow().collect { model ->
                _activeModel.value = model
            }
        }
    }

    private fun loadRecentConversations() {
        viewModelScope.launch {
            chatRepository.getAllConversations().collect { conversations ->
                _recentConversations.value = conversations.take(5)
            }
        }
    }
}
