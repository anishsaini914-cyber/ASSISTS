package com.jarvis.assistant.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.jarvis.assistant.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        const val TAG = "JarvisAccessibility"
        var isServiceEnabled = false

        // Call action results
        var lastCallActionResult: Boolean? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isServiceEnabled = true

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_CLICKED

            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            notificationTimeout = 100

            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        serviceInfo = info
        Log.d(TAG, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.d(TAG, "Window changed: ${event.packageName} - ${event.className}")
            }
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                Log.d(TAG, "Notification event: ${event.text}")
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceEnabled = false
    }

    fun answerCall(): Boolean {
        return try {
            val rootNode = rootInActiveWindow ?: return false
            val answerButton = findButtonWithText(rootNode, listOf("Answer", "Accept", "answer", "accept"))
            answerButton?.let {
                it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                lastCallActionResult = true
                return true
            }
            lastCallActionResult = false
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to answer call", e)
            lastCallActionResult = false
            false
        }
    }

    fun endCall(): Boolean {
        return try {
            val rootNode = rootInActiveWindow ?: return false
            val endButton = findButtonWithText(rootNode, listOf("End", "End call", "Decline", "Reject"))
            endButton?.let {
                it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                lastCallActionResult = true
                return true
            }
            lastCallActionResult = false
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to end call", e)
            lastCallActionResult = false
            false
        }
    }

    private fun findButtonWithText(
        node: AccessibilityNodeInfo,
        texts: List<String>,
        depth: Int = 0
    ): AccessibilityNodeInfo? {
        if (depth > 10) return null

        val nodeText = node.text?.toString()?.lowercase() ?: ""
        val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""

        if (texts.any { nodeText.contains(it.lowercase()) || contentDesc.contains(it.lowercase()) }) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findButtonWithText(child, texts, depth + 1)
            if (result != null) {
                child.recycle()
                return result
            }
            child.recycle()
        }
        return null
    }
}
