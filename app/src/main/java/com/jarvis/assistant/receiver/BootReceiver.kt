package com.jarvis.assistant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jarvis.assistant.service.WakeWordService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "BootReceiver"
        const val PREF_AUTO_START = "pref_auto_start_wake_word"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed received")

            // Check if auto-start is enabled in preferences
            val prefs = context.getSharedPreferences("jarvis_settings", Context.MODE_PRIVATE)
            val autoStartEnabled = prefs.getBoolean(PREF_AUTO_START, false)

            if (autoStartEnabled) {
                Log.d(TAG, "Auto-starting wake word service")
                WakeWordService.start(context)
            }
        }
    }
}
