package com.danono.dodgedrive

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.danono.dodgedrive.utilities.SoundManager
import kotlin.jvm.java
import com.danono.dodgedrive.R

class MenuActivity : AppCompatActivity() {

    private lateinit var menu_BTN_slow: MaterialButton
    private lateinit var menu_BTN_fast: MaterialButton
    private lateinit var menu_BTN_sensors: MaterialButton
    private lateinit var menu_BTN_scoreboard: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Initialize sound manager
        SoundManager.init(this)

        findViews()
        initViews()
    }

    override fun onResume() {
        super.onResume()
        SoundManager.start()
    }

    override fun onPause() {
        super.onPause()
        SoundManager.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.stop()
    }

    private fun findViews() {
        menu_BTN_slow = findViewById(R.id.menu_BTN_slow)
        menu_BTN_fast = findViewById(R.id.menu_BTN_fast)
        menu_BTN_sensors = findViewById(R.id.menu_BTN_sensors)
        menu_BTN_scoreboard = findViewById(R.id.menu_BTN_scoreboard)

    }

    private fun initViews() {
        menu_BTN_slow.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_GAME_MODE, GAME_MODE_SLOW)
            })
        }

        menu_BTN_fast.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_GAME_MODE, GAME_MODE_FAST)
            })
        }

        menu_BTN_sensors.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_GAME_MODE, GAME_MODE_SENSORS)
            })
        }

        menu_BTN_scoreboard.setOnClickListener {
            startActivity(Intent(this, ScoreboardActivity::class.java))
        }



    }

    companion object {
        const val EXTRA_GAME_MODE = "EXTRA_GAME_MODE"
        const val GAME_MODE_SLOW = "GAME_MODE_SLOW"
        const val GAME_MODE_FAST = "GAME_MODE_FAST"
        const val GAME_MODE_SENSORS = "GAME_MODE_SENSORS"
    }
}