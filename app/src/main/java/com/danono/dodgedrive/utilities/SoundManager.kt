package com.danono.dodgedrive.utilities

import android.content.Context
import android.media.MediaPlayer
import com.danono.dodgedrive.R

object SoundManager {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var rockSoundPlayer: MediaPlayer? = null
    private var coinSoundPlayer: MediaPlayer? = null
    private var heartSoundPlayer: MediaPlayer? = null

    fun init(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.bg_sound).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
        }
    }

    fun start() {
        if (!isPlaying) {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    fun pause() {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
        }
    }

    fun stop() {
        if (isPlaying) {
            mediaPlayer?.stop()
            isPlaying = false
        }
        mediaPlayer?.release()
        mediaPlayer = null
        rockSoundPlayer?.release()
        rockSoundPlayer = null
        coinSoundPlayer?.release()
        coinSoundPlayer = null
        heartSoundPlayer?.release()
        heartSoundPlayer = null
    }

    fun playRockCollision(context: Context) {
        rockSoundPlayer?.release()
        rockSoundPlayer = MediaPlayer.create(context, R.raw.rock_sound).apply {
            setVolume(1.0f, 1.0f)
            setOnCompletionListener { release() }
            start()
        }
    }

    fun playCoinCollection(context: Context) {
        coinSoundPlayer?.release()
        coinSoundPlayer = MediaPlayer.create(context, R.raw.coin_sound).apply {
            setVolume(1.0f, 1.0f)
            setOnCompletionListener { release() }
            start()
        }
    }

    fun playHeartCollection(context: Context) {
        heartSoundPlayer?.release()
        heartSoundPlayer = MediaPlayer.create(context, R.raw.heart_sound).apply {
            setVolume(1.0f, 1.0f)
            setOnCompletionListener { release() }
            start()
        }
    }
} 