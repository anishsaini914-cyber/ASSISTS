package com.jarvis.assistant.presentation.ui.voice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.assistant.domain.model.VoiceCommand
import com.jarvis.assistant.domain.usecase.ManageCallUseCase
import com.jarvis.assistant.domain.usecase.ParseVoiceCommandUseCase
import com.jarvis.assistant.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceAssistantViewModel @Inject constructor(
    private val parseVoiceCommandUseCase: ParseVoiceCommandUseCase,
    private val manageCallUseCase: ManageCallUseCase,
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _isListening = MutableLiveData(false)
    val isListening: LiveData<Boolean> = _isListening

    private val _processedText = MutableLiveData<String>("")
    val processedText: LiveData<String> = _processedText

    fun setListening(listening: Boolean) {
        _isListening.value = listening
    }

    fun processVoiceInput(text: String) {
        val command = parseVoiceCommandUseCase(text)
        executeCommand(command)
    }

    private fun executeCommand(command: VoiceCommand) {
        viewModelScope.launch {
            when (command) {
                is VoiceCommand.FlashlightOn -> {
                    // Requires CAMERA permission - handled via FlashlightManager
                }
                is VoiceCommand.FlashlightOff -> {}
                is VoiceCommand.AnswerCall -> manageCallUseCase(com.jarvis.assistant.domain.model.CallAction.Accept)
                is VoiceCommand.EndCall -> manageCallUseCase(com.jarvis.assistant.domain.model.CallAction.Reject)
                is VoiceCommand.SpeakerOn -> manageCallUseCase(com.jarvis.assistant.domain.model.CallAction.ToggleSpeaker)
                is VoiceCommand.SpeakerOff -> manageCallUseCase(com.jarvis.assistant.domain.model.CallAction.ToggleSpeaker)
                is VoiceCommand.MuteCall -> manageCallUseCase(com.jarvis.assistant.domain.model.CallAction.ToggleMute)
                is VoiceCommand.UnmuteCall -> manageCallUseCase(com.jarvis.assistant.domain.model.CallAction.ToggleMute)
                is VoiceCommand.GetWeather -> {
                    if (command.location != null) {
                        // Would need geocoding for location
                        _processedText.value = "Fetching weather for ${command.location}"
                    } else {
                        _processedText.value = "Please specify a location for weather"
                    }
                }
                is VoiceCommand.SearchWeb -> {
                    _processedText.value = "Searching for: ${command.query}"
                }
                is VoiceCommand.OpenApp -> {
                    _processedText.value = "Opening app: ${command.packageName}"
                }
                is VoiceCommand.Call -> {
                    _processedText.value = "Calling: ${command.contact}"
                }
                is VoiceCommand.Custom -> {
                    _processedText.value = "Command received: ${command.text}"
                }
                else -> {
                    _processedText.value = "Command: ${command.javaClass.simpleName}"
                }
            }
        }
    }
}
