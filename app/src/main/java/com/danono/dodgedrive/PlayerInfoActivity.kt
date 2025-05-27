package com.danono.dodgedrive

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.danono.dodgedrive.databinding.ActivityPlayerInfoBinding
import com.danono.dodgedrive.model.ScoreManager
import com.danono.dodgedrive.model.ScoreRecord
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class PlayerInfoActivity : AppCompatActivity() {

    private lateinit var userinfo_ET_text: TextInputEditText
    private lateinit var userinfo_BTN_saveRecord: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_info)

        userinfo_ET_text = findViewById(R.id.userinfo_ET_text)
        userinfo_BTN_saveRecord = findViewById(R.id.userinfo_BTN_saveRecord)

        val score = intent.getIntExtra("EXTRA_SCORE", 0)
        val lat = intent.getDoubleExtra("EXTRA_LAT", 0.0)
        val lon = intent.getDoubleExtra("EXTRA_LON", 0.0)

        findViewById<TextView>(R.id.palyrinfo_TXT_score).text = "Score"
        findViewById<TextView>(R.id.palyrinfo_TXT_miles).text = "Miles"

        findViewById<TextView>(R.id.playerinfo_TXT_scorevalue).text = score.toString()
        findViewById<TextView>(R.id.playerinfo_TXT_milesvalue).text = "Saved"

        userinfo_BTN_saveRecord.setOnClickListener {
            val name = userinfo_ET_text.text.toString().trim()
            if (name.isNotEmpty()) {
                val record = ScoreRecord.Builder()
                    .playerName(name)
                    .score(score)
                    .location(lat, lon)
                    .build()

                ScoreManager.addScore(record)

                // Save to SharedPreferences
                val sharedPref = getSharedPreferences("PlayerPrefs", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()

                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                editor.putString("player_name", name)
                editor.putInt("player_score", score)
                editor.putString("player_date", date)
                editor.putString("player_lat", lat.toString())
                editor.putString("player_lon", lon.toString())
                editor.apply()  // Save the changes

                // Go to scoreboard screen
                val intent = Intent(this, ScoreboardActivity::class.java)
                intent.putExtra(ScoreboardActivity.EXTRA_PLAYER_NAME, name)
                intent.putExtra(ScoreboardActivity.EXTRA_SCORE, score)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
