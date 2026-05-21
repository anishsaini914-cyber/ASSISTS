package com.jarvis.assistant.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionHelper {

    data class PermissionItem(
        val permission: String,
        val label: String,
        val description: String,
        val iconResId: Int = 0,
        val isDangerous: Boolean = true
    ) {
        val isGranted: Boolean get() = false // Placeholder, checked at runtime
    }

    fun getAllRequiredPermissions(): List<PermissionItem> {
        val permissions = mutableListOf(
            PermissionItem(
                Manifest.permission.RECORD_AUDIO,
                "Microphone",
                "Required for voice commands and wake word detection",
                isDangerous = true
            ),
            PermissionItem(
                Manifest.permission.INTERNET,
                "Internet",
                "Required for AI provider API calls and weather",
                isDangerous = false
            ),
            PermissionItem(
                Manifest.permission.ACCESS_NETWORK_STATE,
                "Network State",
                "Required to check network connectivity",
                isDangerous = false
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(
                PermissionItem(
                    Manifest.permission.POST_NOTIFICATIONS,
                    "Notifications",
                    "Required for foreground services and alerts",
                    isDangerous = true
                )
            )
        }

        permissions.addAll(
            listOf(
                PermissionItem(
                    Manifest.permission.CAMERA,
                    "Camera",
                    "Required for flashlight control",
                    isDangerous = true
                ),
                PermissionItem(
                    Manifest.permission.READ_PHONE_STATE,
                    "Phone State",
                    "Required to detect incoming calls",
                    isDangerous = true
                ),
                PermissionItem(
                    Manifest.permission.ANSWER_PHONE_CALLS,
                    "Answer Calls",
                    "Required to answer calls via voice commands",
                    isDangerous = true
                ),
                PermissionItem(
                    Manifest.permission.CALL_PHONE,
                    "Call Phone",
                    "Required to make calls via voice commands",
                    isDangerous = true
                ),
                PermissionItem(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    "Location",
                    "Required for weather feature",
                    isDangerous = true
                ),
                PermissionItem(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    "Precise Location",
                    "Required for accurate weather",
                    isDangerous = true
                ),
                PermissionItem(
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    "Boot Completed",
                    "Required for auto-start after reboot",
                    isDangerous = false
                ),
                PermissionItem(
                    Manifest.permission.FOREGROUND_SERVICE,
                    "Foreground Service",
                    "Required for persistent background services",
                    isDangerous = false
                )
            )
        )

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            permissions.add(
                PermissionItem(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "Storage",
                    "Required for importing local models",
                    isDangerous = true
                )
            )
        }

        return permissions
    }

    fun getRuntimePermissions(): List<String> {
        return getAllRequiredPermissions()
            .filter { it.isDangerous }
            .map { it.permission }
    }

    fun areAllRuntimePermissionsGranted(context: Context): Boolean {
        return getRuntimePermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getOverlayPermissionIntent(context: Context): Intent? {
        return if (!Settings.canDrawOverlays(context)) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
        } else null
    }

    fun isOverlayPermissionGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isNotificationListenerGranted(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(context.packageName) == true
    }

    fun getNotificationListenerIntent(context: Context): Intent {
        return Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    }

    fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains("${context.packageName}/${serviceClass.name}") == true
    }

    fun getAccessibilitySettingsIntent(): Intent {
        return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    }

    fun isBatteryOptimizationExempted(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else true
    }

    fun getBatteryOptimizationIntent(context: Context): Intent {
        return Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.parse("package:${context.packageName}")
        )
    }
}
