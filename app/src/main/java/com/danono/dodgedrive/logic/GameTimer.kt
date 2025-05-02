package com.danono.dodgedrive.logic

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameTimer(private val scope: CoroutineScope, private val onTick: () -> Unit) {

    private lateinit var timerJob: Job
    private var timerOn: Boolean = false

    /**
     * Starts the game timer with the specified delay between ticks
     * @param delayMillis Milliseconds between each timer tick
     */
    fun start(delayMillis: Long) {
        if (!timerOn) {
            timerOn = true

            timerJob = scope.launch {
                while (timerOn) {
                    onTick()
                    delay(delayMillis)
                    Log.d(
                        "Game Timer",
                        "Timer tick on thread: ${Thread.currentThread().name}"
                    )
                }
            }
        }
    }

    // Stops the timer if it's running
    fun stop() {
        if (timerOn) {
            timerOn = false
            if (::timerJob.isInitialized) {
                timerJob.cancel()
            }
        }
    }

    // Check if the timer is currently running
    fun isRunning(): Boolean {
        return timerOn
    }
}