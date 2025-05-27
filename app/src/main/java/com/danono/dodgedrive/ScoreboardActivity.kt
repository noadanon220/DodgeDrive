package com.danono.dodgedrive

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.danono.dodgedrive.fragments.ScoreListFragment
import com.danono.dodgedrive.fragments.ScoreMapFragment

class ScoreboardActivity : AppCompatActivity() {
    private lateinit var scoreboard_FRAME_list: FrameLayout
    private lateinit var scoreboard_FRAME_map: FrameLayout

    private lateinit var scoreListFragment: ScoreListFragment
    private lateinit var scoreMapFragment: ScoreMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        findViews()

        // Try to get data from intent first
        var playerName = intent.getStringExtra(EXTRA_PLAYER_NAME)
        var score = intent.getIntExtra(EXTRA_SCORE, -1)
        var lat: Double? = null
        var lon: Double? = null
        var date: String? = null

        // If not passed in intent or called directly, fallback to SharedPreferences
        if (playerName.isNullOrEmpty() || score == -1) {
            val sharedPref = getSharedPreferences("PlayerPrefs", Context.MODE_PRIVATE)
            playerName = sharedPref.getString("player_name", "Unknown")
            score = sharedPref.getInt("player_score", 0)
            date = sharedPref.getString("player_date", "")
            lat = sharedPref.getString("player_lat", "0.0")?.toDoubleOrNull()
            lon = sharedPref.getString("player_lon", "0.0")?.toDoubleOrNull()
        }

        initFragments(playerName ?: "Unknown", score, lat, lon, date)
    }

    private fun findViews() {
        scoreboard_FRAME_list = findViewById(R.id.scoreboard_FRAME_list)
        scoreboard_FRAME_map = findViewById(R.id.scoreboard_FRAME_map)
    }

    private fun initFragments(
        playerName: String,
        score: Int,
        lat: Double?,
        lon: Double?,
        date: String?
    ) {
        scoreListFragment = ScoreListFragment().apply {
            arguments = Bundle().apply {
                putString(ScoreListFragment.ARG_PLAYER_NAME, playerName)
                putInt(ScoreListFragment.ARG_SCORE, score)
                putString(ScoreListFragment.ARG_DATE, date)
            }
        }

        scoreMapFragment = ScoreMapFragment().apply {
            arguments = Bundle().apply {
                if (lat != null && lon != null) {
                    putDouble("latitude", lat)
                    putDouble("longitude", lon)
                }
            }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.scoreboard_FRAME_list, scoreListFragment)
            .add(R.id.scoreboard_FRAME_map, scoreMapFragment)
            .commit()
    }

    fun showLocation(latitude: Double, longitude: Double) {
        scoreMapFragment.showLocation(latitude, longitude)
    }

    companion object {
        const val EXTRA_PLAYER_NAME = "EXTRA_PLAYER_NAME"
        const val EXTRA_SCORE = "EXTRA_SCORE"
    }
}
