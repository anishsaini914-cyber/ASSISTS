package com.jarvis.assistant.domain.model

sealed class VoiceCommand {
    data class OpenApp(val packageName: String) : VoiceCommand()
    data class Call(val contact: String) : VoiceCommand()
    object EndCall : VoiceCommand()
    object AnswerCall : VoiceCommand()
    object SpeakerOn : VoiceCommand()
    object SpeakerOff : VoiceCommand()
    object MuteCall : VoiceCommand()
    object UnmuteCall : VoiceCommand()
    object WhoIsCalling : VoiceCommand()
    data class SearchWeb(val query: String) : VoiceCommand()
    data class GetWeather(val location: String?) : VoiceCommand()
    object FlashlightOn : VoiceCommand()
    object FlashlightOff : VoiceCommand()
    object ReadNotifications : VoiceCommand()
    data class SetAlarm(val hour: Int, val minute: Int) : VoiceCommand()
    object OpenSettings : VoiceCommand()
    object CheckBattery : VoiceCommand()
    data class OpenWhatsApp(val contact: String?) : VoiceCommand()
    data class Custom(val text: String) : VoiceCommand()
}
