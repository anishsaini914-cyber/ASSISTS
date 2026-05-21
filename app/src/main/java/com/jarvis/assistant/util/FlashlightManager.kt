package com.jarvis.assistant.util

import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log

class FlashlightManager(private val context: Context) {

    companion object {
        const val TAG = "FlashlightManager"
    }

    private val cameraManager: CameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private var cameraId: String? = null
    private var isFlashOn = false

    init {
        cameraId = getCameraId()
    }

    private fun getCameraId(): String? {
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                if (hasFlash) return id
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to get camera ID", e)
        }
        return null
    }

    fun isAvailable(): Boolean = cameraId != null

    fun isFlashOn(): Boolean = isFlashOn

    fun toggleFlashlight(): Boolean {
        return if (isFlashOn) {
            turnOffFlashlight()
        } else {
            turnOnFlashlight()
        }
    }

    fun turnOnFlashlight(): Boolean {
        if (!hasCameraPermission()) return false
        val id = cameraId ?: return false
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(id, true)
                isFlashOn = true
                true
            } else {
                Log.w(TAG, "Flashlight requires Android M+")
                false
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to turn on flashlight", e)
            false
        } catch (e: SecurityException) {
            Log.e(TAG, "Camera permission denied", e)
            false
        }
    }

    fun turnOffFlashlight(): Boolean {
        val id = cameraId ?: return false
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(id, false)
                isFlashOn = false
                true
            } else {
                false
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to turn off flashlight", e)
            false
        }
    }

    private fun hasCameraPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(Manifest.permission.CAMERA) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun release() {
        if (isFlashOn) {
            turnOffFlashlight()
        }
    }
}
