package com.volio.vn.callscreen.helpers

import android.content.Context
import android.media.AudioManager

object CallSpeakerHelper {

    fun turnOnExternalSpeaker(context: Context) {
        context.getSystemService(AudioManager::class.java).apply {
            isSpeakerphoneOn = true
            mode = AudioManager.MODE_IN_CALL
        }
    }

    fun turnOffExternalSpeaker(context: Context) {
        context.getSystemService(AudioManager::class.java).apply {
            isSpeakerphoneOn = false
            mode = AudioManager.MODE_NORMAL
        }
    }
}