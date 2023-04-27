package com.volio.vn.b1_project.ui.call

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.os.PowerManager
import android.telecom.Call
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.volio.vn.b1_project.R
import com.volio.vn.callscreen.extensions.getStateCompat
import com.volio.vn.callscreen.extensions.isOreoMr1Plus
import com.volio.vn.callscreen.extensions.isOreoPlus
import com.volio.vn.callscreen.helpers.CallManager
import com.volio.vn.callscreen.helpers.CallManagerListener
import com.volio.vn.callscreen.helpers.CallVibrateManager
import com.volio.vn.callscreen.helpers.adjustAudio
import com.volio.vn.callscreen.helpers.getCallContact
import com.volio.vn.callscreen.models.AudioRoute

private const val TAG = "CallActivity"

class CallActivity : AppCompatActivity() {

    private var screenOnWakeLock: PowerManager.WakeLock? = null
    val callCallback = object : CallManagerListener {
        override fun onStateChanged() {
            val call = CallManager.getPrimaryCall()
            call?.let {
                getCallContact(this@CallActivity, it) { contact ->
                    textName.text = contact.name
                    Log.e(TAG, "Contact: ${contact.toString()}")
                }
            }

            when (CallManager.getPrimaryCall().getStateCompat()) {
                Call.STATE_RINGING -> {

                }

                Call.STATE_ACTIVE -> {

                }

                Call.STATE_DISCONNECTED -> {
                    Log.e(TAG, "STATE_DISCONNECTED")
                    finish()
                }

                Call.STATE_CONNECTING, Call.STATE_DIALING -> {

                }

                Call.STATE_SELECT_PHONE_ACCOUNT -> {

                }
            }
        }

        override fun onAudioStateChanged(audioState: AudioRoute) {
        }

        override fun onPrimaryCallChanged(call: Call) {
        }
    }
    val textName by lazy { findViewById<TextView>(R.id.text_view_name) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        val btnAccept = findViewById<Button>(R.id.btn_accept)
        val btnIgnore = findViewById<Button>(R.id.btn_ignore)
        val btnMute = findViewById<Button>(R.id.btn_mute)
        val btnHold = findViewById<Button>(R.id.btn_hold)
        val btnUnHold = findViewById<Button>(R.id.btn_un_hold)
        addLockScreenFlags()

        // CallVibrateManager.enableVibrate(this)
        CallVibrateManager.vibrateWithPatten(this)

        CallManager.addListener(callCallback)

        btnAccept.setOnClickListener {
            CallManager.accept()
            CallVibrateManager.cancelVibrate(this)
        }

        btnIgnore.setOnClickListener {
            CallManager.reject()
            CallVibrateManager.cancelVibrate(this)
        }

        btnMute.setOnClickListener {
            adjustAudio(this, setMute = true)
            CallVibrateManager.cancelVibrate(this)
        }

        btnHold.setOnClickListener {
            CallManager.hold()
        }

        btnUnHold.setOnClickListener {
            CallManager.unHold()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        CallVibrateManager.cancelVibrate(this)
        adjustAudio(this, setMute = false)
    }

    @SuppressLint("NewApi")
    private fun addLockScreenFlags() {
        if (isOreoMr1Plus()) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        if (isOreoPlus()) {
            (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).requestDismissKeyguard(
                this, null
            )
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }

        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            screenOnWakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK, "com.simplemobiletools.dialer.pro:full_wake_lock"
            )
            screenOnWakeLock!!.acquire(5 * 1000L)
        } catch (e: Exception) {
        }
    }


}