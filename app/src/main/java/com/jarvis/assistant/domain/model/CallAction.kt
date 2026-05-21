package com.jarvis.assistant.domain.model

sealed class CallAction {
    object Accept : CallAction()
    object Reject : CallAction()
    object ToggleSpeaker : CallAction()
    object ToggleMute : CallAction()
}
