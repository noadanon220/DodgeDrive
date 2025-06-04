package com.danono.dodgedrive.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.danono.dodgedrive.interfaces.TiltCallback
import kotlin.math.abs

class TiltDetector(context: Context, private var tiltCallback: TiltCallback?) {

    private val sensorManager = context
        .getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensor = sensorManager
        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor

    private lateinit var sensorEventListener: SensorEventListener

    var tiltCounterX: Int = 0
        private set

    var tiltCounterY: Int = 0
        private set

    private var timestamp: Long = 0L
    private val TILT_THRESHOLD = 2.0f
    private val TILT_DELAY = 200L // Reduced delay for more responsive controls

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                calculateTilt(x)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // pass
            }
        }
    }

    private fun calculateTilt(x: Float) {
        if (System.currentTimeMillis() - timestamp >= TILT_DELAY) {
            timestamp = System.currentTimeMillis()
            if (abs(x) >= TILT_THRESHOLD) {
                tiltCounterX = if (x > 0) 1 else -1
                tiltCallback?.tiltX()
            } else {
                tiltCounterX = 0
            }
        }
    }

    fun start() {
        sensorManager.registerListener(
            sensorEventListener,
            sensor,
            SensorManager.SENSOR_DELAY_GAME // Using game mode for better responsiveness
        )
    }

    fun stop() {
        sensorManager.unregisterListener(
            sensorEventListener,
            sensor
        )
    }
}