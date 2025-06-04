package com.danono.dodgedrive

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.danono.dodgedrive.model.ScoreManager
import com.danono.dodgedrive.model.ScoreRecord
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDateTime

class PlayerInfoActivity : AppCompatActivity() {

    private lateinit var userinfo_ET_text: TextInputEditText
    private lateinit var userinfo_BTN_saveRecord: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_info)

        userinfo_ET_text = findViewById(R.id.userinfo_ET_text)
        userinfo_BTN_saveRecord = findViewById(R.id.userinfo_BTN_saveRecord)

        val score = intent.getIntExtra("EXTRA_SCORE", 0)
        val distance = intent.getIntExtra("EXTRA_DISTANCE", 0)
        val lat = intent.getDoubleExtra("EXTRA_LAT", 0.0)
        val lon = intent.getDoubleExtra("EXTRA_LON", 0.0)

        findViewById<TextView>(R.id.palyrinfo_TXT_score).text = "Score"
        findViewById<TextView>(R.id.playerinfo_TXT_scorevalue).text = score.toString()

        findViewById<TextView>(R.id.palyrinfo_TXT_miles).text = "Distance (miles)"
        findViewById<TextView>(R.id.playerinfo_TXT_milesvalue).text = String.format("%.2f", distance * 0.05)

        Toast.makeText(this, getString(R.string.enter_name_toast), Toast.LENGTH_SHORT).show()

        userinfo_BTN_saveRecord.setOnClickListener {
            val name = userinfo_ET_text.text.toString().trim()
            if (name.isNotEmpty()) {
                val record = ScoreRecord.Builder()
                    .playerName(name)
                    .score(score)
                    .date(LocalDateTime.now())
                    .location(lat, lon)
                    .distance(distance)
                    .build()

                ScoreManager.addScore(record)

                val intent = Intent(this, ScoreboardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(ScoreboardActivity.EXTRA_PLAYER_NAME, name)
                    putExtra(ScoreboardActivity.EXTRA_SCORE, score)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
