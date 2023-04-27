package com.volio.vn.callscreen.helpers

import android.content.Context
import android.media.AudioManager


object CallMicHelper {
    fun enabledMic(context: Context) {
        context.getSystemService(AudioManager::class.java).isMicrophoneMute = true
    }

    fun disable(context: Context) {
        context.getSystemService(AudioManager::class.java).isMicrophoneMute = false
    }
}