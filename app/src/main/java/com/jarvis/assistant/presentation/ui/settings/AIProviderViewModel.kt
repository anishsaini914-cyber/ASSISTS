package com.jarvis.assistant.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.assistant.ai.AIProviderFactory
import com.jarvis.assistant.data.local.prefs.SecurePreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIProviderViewModel @Inject constructor(
    private val prefs: SecurePreferencesManager,
    private val providerFactory: AIProviderFactory
) : ViewModel() {

    fun getApiKey(providerId: String): String? = prefs.getApiKey(providerId)

    fun getEndpoint(providerId: String): String? = prefs.getEndpoint(providerId)

    fun getSelectedModel(providerId: String): String? = prefs.getSelectedModel(providerId)

    fun saveApiKey(providerId: String, key: String) {
        prefs.saveApiKey(providerId, key)
    }

    fun saveSelectedModel(provider: String, model: String) {
        prefs.saveSelectedModel(provider, model)
    }

    suspend fun validateConnection(providerId: String, apiKey: String): Boolean {
        val provider = providerFactory.getProvider(providerId) ?: return false
        return provider.validateApiKey(apiKey)
    }
}
