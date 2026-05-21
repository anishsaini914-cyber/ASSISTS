package com.jarvis.assistant.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferencesManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences
) {

    companion object {
        private const val KEY_OPENAI_API_KEY = "openai_api_key"
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_AGENTROUTER_API_KEY = "agentrouter_api_key"
        private const val KEY_AGENTROUTER_ENDPOINT = "agentrouter_endpoint"
        private const val KEY_ACTIVE_PROVIDER = "active_provider"
        private const val KEY_SELECTED_MODEL = "selected_model"
        private const val KEY_WAKE_WORD = "wake_word"
        private const val KEY_WAKE_WORD_ENABLED = "wake_word_enabled"
        private const val KEY_TTS_PITCH = "tts_pitch"
        private const val KEY_TTS_SPEED = "tts_speed"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_OVERLAY_X = "overlay_x"
        private const val KEY_OVERLAY_Y = "overlay_y"
        private const val KEY_WEATHER_LAT = "weather_lat"
        private const val KEY_WEATHER_LON = "weather_lon"
        private const val KEY_WEATHER_CITY = "weather_city"
    }

    fun saveApiKey(provider: String, key: String) {
        val keyName = when (provider.lowercase()) {
            "openai" -> KEY_OPENAI_API_KEY
            "gemini" -> KEY_GEMINI_API_KEY
            "agentrouter" -> KEY_AGENTROUTER_API_KEY
            else -> "${provider}_api_key"
        }
        encryptedPrefs.edit().putString(keyName, key).apply()
    }

    fun getApiKey(provider: String): String? {
        val keyName = when (provider.lowercase()) {
            "openai" -> KEY_OPENAI_API_KEY
            "gemini" -> KEY_GEMINI_API_KEY
            "agentrouter" -> KEY_AGENTROUTER_API_KEY
            else -> "${provider}_api_key"
        }
        return encryptedPrefs.getString(keyName, null)
    }

    fun saveEndpoint(provider: String, url: String) {
        if (provider.lowercase() == "agentrouter") {
            encryptedPrefs.edit().putString(KEY_AGENTROUTER_ENDPOINT, url).apply()
        }
    }

    fun getEndpoint(provider: String): String? {
        return if (provider.lowercase() == "agentrouter") {
            encryptedPrefs.getString(KEY_AGENTROUTER_ENDPOINT, null)
        } else null
    }

    fun saveSelectedModel(provider: String, model: String) {
        encryptedPrefs.edit().putString("${provider}_model", model).apply()
    }

    fun getSelectedModel(provider: String): String? {
        return encryptedPrefs.getString("${provider}_model", null)
    }

    fun saveActiveProvider(providerId: String) {
        encryptedPrefs.edit().putString(KEY_ACTIVE_PROVIDER, providerId).apply()
    }

    fun getActiveProvider(): String? {
        return encryptedPrefs.getString(KEY_ACTIVE_PROVIDER, "openai")
    }

    fun saveWakeWord(phrase: String) {
        encryptedPrefs.edit().putString(KEY_WAKE_WORD, phrase).apply()
    }

    fun getWakeWord(): String {
        return encryptedPrefs.getString(KEY_WAKE_WORD, "Hey Jarvis") ?: "Hey Jarvis"
    }

    fun setWakeWordEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean(KEY_WAKE_WORD_ENABLED, enabled).apply()
    }

    fun isWakeWordEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_WAKE_WORD_ENABLED, false)
    }

    fun isFirstLaunch(): Boolean {
        return encryptedPrefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        encryptedPrefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun saveOverlayPosition(x: Int, y: Int) {
        encryptedPrefs.edit()
            .putInt(KEY_OVERLAY_X, x)
            .putInt(KEY_OVERLAY_Y, y)
            .apply()
    }

    fun getOverlayPosition(): Pair<Int, Int>? {
        val x = encryptedPrefs.getInt(KEY_OVERLAY_X, -1)
        val y = encryptedPrefs.getInt(KEY_OVERLAY_Y, -1)
        return if (x >= 0 && y >= 0) Pair(x, y) else null
    }

    fun saveWeatherLocation(lat: Double, lon: Double, city: String) {
        encryptedPrefs.edit()
            .putString(KEY_WEATHER_LAT, lat.toString())
            .putString(KEY_WEATHER_LON, lon.toString())
            .putString(KEY_WEATHER_CITY, city)
            .apply()
    }

    fun getWeatherLocation(): Triple<Double?, Double?, String?> {
        val lat = encryptedPrefs.getString(KEY_WEATHER_LAT, null)?.toDoubleOrNull()
        val lon = encryptedPrefs.getString(KEY_WEATHER_LON, null)?.toDoubleOrNull()
        val city = encryptedPrefs.getString(KEY_WEATHER_CITY, null)
        return Triple(lat, lon, city)
    }

    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
    }
}
