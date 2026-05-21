package com.jarvis.assistant.service

import android.annotation.SuppressLint
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.jarvis.assistant.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("OverrideAbstract")
class JarvisNotificationListenerService : NotificationListenerService() {

    companion object {
        const val TAG = "NotificationListener"
        var cachedNotifications: MutableList<NotificationData> = mutableListOf()

        data class NotificationData(
            val packageName: String,
            val title: String?,
            val text: String?,
            val timestamp: Long,
            val appName: String?
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString()

        Log.d(TAG, "Notification from ${sbn.packageName}: $title")

        val data = NotificationData(
            packageName = sbn.packageName,
            title = title,
            text = text,
            timestamp = sbn.postTime,
            appName = getAppName(sbn.packageName)
        )

        cachedNotifications.add(0, data)
        if (cachedNotifications.size > 100) {
            cachedNotifications.removeAt(cachedNotifications.lastIndex)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        cachedNotifications.removeAll { it.packageName == sbn.packageName }
    }

    private fun getAppName(packageName: String): String? {
        return try {
            val packageManager = packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }
}
