package com.volio.vn.callscreen.helpers

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings


object CallVibrateManager {

    fun enableVibrateWhenRinging(context: Context) {
        Settings.System.putInt(context.contentResolver, "vibrate_when_ringing", 1)
    }

    fun disableVibrateWhenRinging(context: Context) {
        Settings.System.putInt(context.contentResolver, "vibrate_when_ringing", 0)
    }

    fun cancelVibrate(context: Context) {
        val vibrateManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrateManager.cancel()
    }

    fun vibrateWithPatten(context: Context) {
        val vibrateManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if(vibrateManager.hasVibrator()){
            vibrateManager.cancel()
            val vibrationWaveFormDurationPattern =
                longArrayOf(0, 10, 200, 20, 700, 30, 300, 40, 50, 10)
            //val vibratePattern = longArrayOf(DELAY.toLong(), VIBRATE.toLong(), SLEEP.toLong())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrateManager.vibrate(
                    VibrationEffect.createWaveform(
                        vibrationWaveFormDurationPattern,
                        0
                    )
                )

            } else {

                vibrateManager.vibrate(vibrationWaveFormDurationPattern, 0)

            }
        }
    }
}