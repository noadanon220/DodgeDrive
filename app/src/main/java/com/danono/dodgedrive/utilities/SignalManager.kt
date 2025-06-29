package com.danono.dodgedrive.utilities

import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

class SignalManager private constructor(context: Context) {

    private val appContext: Context = context.applicationContext

    companion object {
        @Volatile
        private var instance: SignalManager? = null

        fun init(context: Context): SignalManager {
            return instance ?: synchronized(this) {
                instance ?: SignalManager(context).also { instance = it }
            }
        }

        fun getInstance(): SignalManager {
            return instance ?: throw IllegalStateException(
                "SignalManager must be initialized by calling init(context) before use."
            )
        }
    }

    fun toast(text: String) {
        Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
    }

    fun vibrate() {
        val vibrator: Vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    appContext.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                appContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val SOSPattern = longArrayOf(
                0,
                200,
                100,
                200,
                100,
                200,
                300,
                500,
                100,
                500,
                100,
                500,
                300,
                200,
                100,
                200,
                100,
                200
            )

            val waveFormVibrationEffect =
                VibrationEffect.createWaveform(SOSPattern, -1)

            vibrator.vibrate(waveFormVibrationEffect)
        } else {
            vibrator.vibrate(500)
        }
    }
}
