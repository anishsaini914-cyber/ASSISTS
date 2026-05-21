package com.jarvis.assistant.domain.usecase

import com.jarvis.assistant.data.local.prefs.SecurePreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SwitchProviderUseCase @Inject constructor(
    private val prefs: SecurePreferencesManager
) {
    operator fun invoke(providerId: String) {
        prefs.saveActiveProvider(providerId)
    }
}
