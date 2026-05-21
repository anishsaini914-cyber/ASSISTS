package com.jarvis.assistant.domain.usecase

import com.jarvis.assistant.domain.model.VoiceCommand
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParseVoiceCommandUseCase @Inject constructor() {

    operator fun invoke(rawSpeechText: String): VoiceCommand {
        val text = rawSpeechText.trim().lowercase()

        // Hindi command mapping
        val hindiMap = mapOf(
            "call uthao" to "answer call",
            "call cut karo" to "end call",
            "speaker pe uthao" to "speaker on",
            "mute karo" to "mute call",
            "unmute karo" to "unmute call",
            "kaun call kar raha hai" to "who is calling",
            "mausam kaisa hai" to "weather"
        )

        val mappedText = hindiMap.entries
            .firstOrNull { (key) -> text.contains(key) }
            ?.value ?: text

        return when {
            // Open app
            mappedText.contains("open ") -> {
                val appName = mappedText.substringAfter("open ").trim()
                VoiceCommand.OpenApp(appName)
            }
            // Call
            mappedText.contains("call ") && !mappedText.contains("end call") -> {
                val contact = mappedText.substringAfter("call ").trim()
                VoiceCommand.Call(contact)
            }
            mappedText.contains("call") && mappedText.contains("answer") -> VoiceCommand.AnswerCall
            mappedText.contains("end call") || mappedText.contains("hung up") -> VoiceCommand.EndCall
            mappedText.contains("answer call") -> VoiceCommand.AnswerCall
            mappedText.contains("speaker on") -> VoiceCommand.SpeakerOn
            mappedText.contains("speaker off") -> VoiceCommand.SpeakerOff
            mappedText.contains("mute call") || mappedText.contains("mute karo") -> VoiceCommand.MuteCall
            mappedText.contains("unmute") -> VoiceCommand.UnmuteCall
            mappedText.contains("who is calling") -> VoiceCommand.WhoIsCalling
            // Search
            mappedText.contains("search") || mappedText.contains("google") -> {
                val query = mappedText.substringAfter("search ").substringAfter("google ")
                    .ifEmpty { "" }
                VoiceCommand.SearchWeb(query)
            }
            // Weather
            mappedText.contains("weather") || mappedText.contains("mausam") -> {
                // Try to extract location
                val location = extractLocation(mappedText)
                VoiceCommand.GetWeather(location)
            }
            // Flashlight
            mappedText.contains("flashlight on") || mappedText.contains("torch on") -> VoiceCommand.FlashlightOn
            mappedText.contains("flashlight off") || mappedText.contains("torch off") -> VoiceCommand.FlashlightOff
            // Notifications
            mappedText.contains("notification") || mappedText.contains("read notification") -> VoiceCommand.ReadNotifications
            // Alarm
            mappedText.contains("alarm") || mappedText.contains("set alarm") -> {
                val time = extractTime(mappedText)
                VoiceCommand.SetAlarm(time.first, time.second)
            }
            // Settings
            mappedText.contains("open settings") || mappedText.contains("settings") -> VoiceCommand.OpenSettings
            // Battery
            mappedText.contains("battery") -> VoiceCommand.CheckBattery
            // WhatsApp
            mappedText.contains("whatsapp") -> {
                val contact = extractWhatsAppContact(mappedText)
                VoiceCommand.OpenWhatsApp(contact)
            }
            // Default
            else -> VoiceCommand.Custom(rawSpeechText)
        }
    }

    private fun extractLocation(text: String): String? {
        val patterns = listOf(
            "weather in (\\w+)",
            "weather of (\\w+)",
            "ka mausam"
        )
        for (pattern in patterns) {
            val matcher = Pattern.compile(pattern).matcher(text)
            if (matcher.find()) return matcher.group(1)
        }
        return null
    }

    private fun extractTime(text: String): Pair<Int, Int> {
        val timePattern = Pattern.compile("(\\d{1,2})\\s*(:|\\s)*(\\d{0,2})").matcher(text)
        return if (timePattern.find()) {
            val hour = timePattern.group(1).toIntOrNull() ?: 7
            val minute = timePattern.group(3)?.toIntOrNull() ?: 0
            Pair(hour, minute)
        } else {
            Pair(7, 0)
        }
    }

    private fun extractWhatsAppContact(text: String): String? {
        val patterns = listOf(
            "whatsapp (\\w+)",
            "whatsapp pe (\\w+)"
        )
        for (pattern in patterns) {
            val matcher = Pattern.compile(pattern).matcher(text)
            if (matcher.find()) return matcher.group(1)
        }
        return null
    }
}
