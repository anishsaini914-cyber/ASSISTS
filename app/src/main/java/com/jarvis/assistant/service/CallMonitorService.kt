package com.jarvis.assistant.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import com.jarvis.assistant.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallMonitorService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private var telephonyCallback: CallStateCallback? = null

    companion object {
        const val TAG = "CallMonitorService"
        const val ACTION_CALL_STATE_CHANGED = "com.jarvis.assistant.CALL_STATE_CHANGED"
        const val EXTRA_STATE = "state"
        const val EXTRA_NUMBER = "number"
        const val STATE_RINGING = "ringing"
        const val STATE_IDLE = "idle"
        const val STATE_OFFHOOK = "offhook"

        fun start(context: Context) {
            val intent = Intent(context, CallMonitorService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, CallMonitorService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerCallListener()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        unregisterCallListener()
        super.onDestroy()
    }

    private fun registerCallListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback = CallStateCallback()
            telephonyManager.registerTelephonyCallback(mainExecutor, telephonyCallback!!)
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.listen(
                phoneStateListener,
                TelephonyManager.LISTEN_CALL_STATE
            )
        }
    }

    private fun unregisterCallListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback?.let { telephonyManager.unregisterTelephonyCallback(it) }
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.listen(phoneStateListener, TelephonyManager.LISTEN_NONE)
        }
    }

    @Suppress("DEPRECATION")
    private val phoneStateListener = object : android.telephony.PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            handleCallState(state, phoneNumber)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    inner class CallStateCallback : TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            handleCallState(state, null)
        }
    }

    private fun handleCallState(state: Int, phoneNumber: String?) {
        val stateStr = when (state) {
            TelephonyManager.CALL_STATE_IDLE -> STATE_IDLE
            TelephonyManager.CALL_STATE_RINGING -> STATE_RINGING
            TelephonyManager.CALL_STATE_OFFHOOK -> STATE_OFFHOOK
            else -> "unknown"
        }

        Log.d(TAG, "Call state changed: $stateStr, number: $phoneNumber")

        val intent = Intent(ACTION_CALL_STATE_CHANGED).apply {
            putExtra(EXTRA_STATE, stateStr)
            putExtra(EXTRA_NUMBER, phoneNumber ?: "")
        }
        sendBroadcast(intent)
    }
}
