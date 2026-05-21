package com.jarvis.assistant.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

class BatteryHelper(private val context: Context) {

    data class BatteryInfo(
        val level: Int,
        val isCharging: Boolean,
        val chargingMethod: String,
        val temperature: Float,
        val voltage: Int,
        val health: String,
        val technology: String
    ) {
        val healthDescription: String
            get() = when (health) {
                "good" -> "Good"
                "overheat" -> "Overheating"
                "dead" -> "Dead"
                "over_voltage" -> "Over Voltage"
                "failure" -> "Failure"
                else -> "Unknown"
            }

        val temperatureCelsius: String
            get() = "%.1f°C".format(temperature / 10f)

        val summary: String
            get() = buildString {
                append("Battery at $level%")
                if (isCharging) append(", charging ($chargingMethod)")
                append(", ${temperatureCelsius}")
            }
    }

    fun getBatteryInfo(): BatteryInfo {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }

        intent?.let { batteryIntent ->
            val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = if (level != -1 && scale != -1) {
                (level * 100) / scale
            } else -1

            val status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val chargingMethod = when {
                chargePlug == BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                chargePlug == BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
                else -> "Not charging"
            }

            val temperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            val voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            val tech = batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

            val healthCode = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            val health = when (healthCode) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "over_voltage"
                BatteryManager.BATTERY_HEALTH_FAILURE -> "failure"
                else -> "unknown"
            }

            return BatteryInfo(
                level = batteryPct,
                isCharging = isCharging,
                chargingMethod = chargingMethod,
                temperature = temperature.toFloat(),
                voltage = voltage,
                health = health,
                technology = tech
            )
        }

        return BatteryInfo(
            level = -1,
            isCharging = false,
            chargingMethod = "Unknown",
            temperature = 0f,
            voltage = 0,
            health = "unknown",
            technology = "Unknown"
        )
    }
}
