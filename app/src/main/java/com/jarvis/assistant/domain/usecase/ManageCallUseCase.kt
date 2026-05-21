package com.jarvis.assistant.domain.usecase

import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat
import com.jarvis.assistant.domain.model.CallAction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageCallUseCase @Inject constructor(
    private val context: Context
) {
    operator fun invoke(action: CallAction) {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

        when (action) {
            CallAction.Accept -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    telecomManager?.acceptRingingCall()
                }
            }
            CallAction.Reject -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    telecomManager?.endCall()
                }
            }
            CallAction.ToggleSpeaker -> {
                audioManager?.isSpeakerphoneOn = !(audioManager.isSpeakerphoneOn)
            }
            CallAction.ToggleMute -> {
                audioManager?.isMicrophoneMute = !(audioManager.isMicrophoneMute)
            }
        }
    }
}
