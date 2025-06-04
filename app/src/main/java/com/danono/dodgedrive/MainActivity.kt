package com.danono.dodgedrive

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.danono.dodgedrive.logic.GameManager
import com.danono.dodgedrive.logic.GameTimer
import com.danono.dodgedrive.logic.HeartCollectionResult
import com.danono.dodgedrive.utilities.Constants
import com.danono.dodgedrive.utilities.SignalManager
import com.danono.dodgedrive.utilities.SoundManager
import com.danono.dodgedrive.utilities.TiltDetector
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlin.arrayOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.widget.TextView
import com.danono.dodgedrive.data.PrefsManager
import com.danono.dodgedrive.interfaces.TiltCallback
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), TiltCallback {

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_LAYOUT_board: Array<Array<AppCompatImageView>>
    private lateinit var main_LAYOUT_car_row: Array<AppCompatImageView>
    private lateinit var main_FAB_left: ExtendedFloatingActionButton
    private lateinit var main_FAB_right: ExtendedFloatingActionButton
    private lateinit var gameTimer: GameTimer
    private lateinit var gameManager: GameManager
    private lateinit var tiltDetector: TiltDetector

    private lateinit var score_FRAME_map: FrameLayout
    private lateinit var score_FRAME_records: FrameLayout
    private lateinit var main_TXT_score: TextView
    private var gameSpeed: Long = Constants.Timer.DELAY

    private var carPosition = 1
    private var isSensorMode = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0
    private var finalScore: Int = 0
    private var finalDistance: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize sound manager
        SoundManager.init(this)

        // Apply padding to avoid overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        gameManager = GameManager(this)
        findViews()
        initViews()
        updateUI()
        initGame()

        // Initialize the game timer with lifecycleScope
        gameTimer = GameTimer(lifecycleScope) {
            gameManager.tickGameProgress() // increment distance
            gameManager.addNewRocks()
            gameManager.addNewCoins()
            gameManager.addNewHearts()
            val collision = gameManager.moveRocksDown()
            gameManager.moveCoinsDown()
            val heartResult = gameManager.moveHeartsDown()

            if (collision) {
                updateHearts()

                if (gameManager.isGameOver()) {
                    gameTimer.stop()
                    finalScore = gameManager.getScore()
                    finalDistance = gameManager.getDistance() // store distance
                    SignalManager.getInstance().vibrate()
                    SignalManager.getInstance().toast("Game Over")
                    locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    SignalManager.getInstance().vibrate()
                    SignalManager.getInstance().toast("Ouch! You hit a rock!")
                }
            }

            when (heartResult) {
                HeartCollectionResult.COLLECTED -> {
                    updateHearts()
                    SignalManager.getInstance().toast("Extra life!")
                }
                HeartCollectionResult.MAX_LIVES -> {
                    SignalManager.getInstance().toast("You have enough life.")
                }
                HeartCollectionResult.NONE -> {
                    // Do nothing
                }
            }

            updateUI()
        }

        // Determine game speed based on mode
        val gameMode = intent.getStringExtra(MenuActivity.EXTRA_GAME_MODE)
        gameSpeed = when (gameMode) {
            MenuActivity.GAME_MODE_FAST -> 500 // fast
            MenuActivity.GAME_MODE_SLOW -> Constants.Timer.DELAY // slow/default
            else -> Constants.Timer.DELAY
        }

        // Initialize sensor mode if selected
        isSensorMode = gameMode == MenuActivity.GAME_MODE_SENSORS
        if (isSensorMode) {
            tiltDetector = TiltDetector(this, this)
            main_FAB_left.visibility = View.GONE
            main_FAB_right.visibility = View.GONE
        }

        // Start the game loop
        gameTimer.start(gameSpeed)
    }

    override fun onResume() {
        super.onResume()
        if (isSensorMode) {
            tiltDetector.start()
        }
        SoundManager.start()
    }

    override fun onPause() {
        super.onPause()
        if (isSensorMode) {
            tiltDetector.stop()
        }
        SoundManager.pause()
    }

    override fun tiltX() {
        // X-axis tilt controls left/right movement
        val x = tiltDetector.tiltCounterX
        if (x > 0) {
            gameManager.moveRight()
        } else {
            gameManager.moveLeft()
        }
        carPosition = gameManager.carPosition
        updateUI()
    }

    override fun tiltY() {
        // Y-axis tilt is not used for movement
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLat = location.latitude
                    currentLon = location.longitude
                }
                launchPlayerInfoActivity()
            }
            .addOnFailureListener {
                launchPlayerInfoActivity()
            }
    }

    private fun launchPlayerInfoActivity() {
        val playerName = "Guest"
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        savePlayerInfo(finalScore, currentLat, currentLon)

        val intent = Intent(this, PlayerInfoActivity::class.java)
        intent.putExtra("EXTRA_SCORE", finalScore)
        intent.putExtra("EXTRA_DISTANCE", finalDistance)
        intent.putExtra("EXTRA_LAT", currentLat)
        intent.putExtra("EXTRA_LON", currentLon)
        startActivity(intent)
        finish()
    }

    private fun updateHearts() {
        val lives = gameManager.getLives()
        for (i in main_IMG_hearts.indices) {
            val reverseIndex = main_IMG_hearts.size - 1 - i
            main_IMG_hearts[reverseIndex].visibility =
                if (i < lives) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun savePlayerInfo(score: Int, lat: Double, lon: Double) {
        val sharedPref = getSharedPreferences("PlayerPrefs", MODE_PRIVATE)
        sharedPref.edit().apply {
            putInt("score", score)
            putInt("distance", finalDistance)
            putFloat("lat", lat.toFloat())
            putFloat("lon", lon.toFloat())
            apply()
        }
    }

    private fun initViews() {
        main_FAB_right.setOnClickListener {
            gameManager.moveRight()
            carPosition = gameManager.carPosition
            updateUI()
        }

        main_FAB_left.setOnClickListener {
            gameManager.moveLeft()
            carPosition = gameManager.carPosition
            updateUI()
        }
    }

    private fun findViews() {
        main_TXT_score = findViewById(R.id.main_TXT_score)

        main_FAB_right = findViewById(R.id.main_FAB_right)
        main_FAB_left = findViewById(R.id.main_FAB_left)

        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )

        main_LAYOUT_board = arrayOf(
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_0_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_0_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_0_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_0_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_0_4)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_1_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_1_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_1_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_1_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_1_4)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_2_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_2_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_2_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_2_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_2_4)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_3_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_3_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_3_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_3_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_3_4)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_4_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_4_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_4_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_4_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_4_4)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_5_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_5_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_5_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_5_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_5_4)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_6_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_6_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_6_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_6_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_6_4)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_7_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_7_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_7_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_7_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_7_4)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_8_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_8_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_8_2),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_8_3),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_8_4)
            )
        )

        main_LAYOUT_car_row = arrayOf(
            findViewById<AppCompatImageView>(R.id.main_IMG_car_0),
            findViewById<AppCompatImageView>(R.id.main_IMG_car_1),
            findViewById<AppCompatImageView>(R.id.main_IMG_car_2),
            findViewById<AppCompatImageView>(R.id.main_IMG_car_3),
            findViewById<AppCompatImageView>(R.id.main_IMG_car_4)
        )
    }

    private fun updateUI() {
        // Clear all cells first
        for (row in main_LAYOUT_board.indices) {
            for (col in main_LAYOUT_board[row].indices) {
                main_LAYOUT_board[row][col].apply {
                    visibility = View.INVISIBLE
                    setImageResource(R.drawable.rock) // Reset to default image
                }
            }
        }

        drawCar()
        drawRocks()
        drawCoins()
        drawHearts()
        main_TXT_score.text = "Score: ${gameManager.getScore()}"
    }

    private fun drawCar() {
        for (i in main_LAYOUT_car_row.indices) {
            main_LAYOUT_car_row[i].visibility =
                if (i == carPosition) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun drawRocks() {
        val rockPositions = gameManager.getRockPositions()
        for (position in rockPositions) {
            main_LAYOUT_board[position.row][position.col].apply {
                setImageResource(R.drawable.rock)
                visibility = View.VISIBLE
            }
        }
    }

    private fun drawCoins() {
        val coinPositions = gameManager.getCoinPositions()
        for (position in coinPositions) {
            main_LAYOUT_board[position.row][position.col].apply {
                setImageResource(R.drawable.coin)
                visibility = View.VISIBLE
            }
        }
    }

    private fun drawHearts() {
        val heartPositions = gameManager.getHeartPositions()
        for (position in heartPositions) {
            main_LAYOUT_board[position.row][position.col].apply {
                setImageResource(R.drawable.heart)
                visibility = View.VISIBLE
            }
        }
    }

    private fun initGame() {
        // Hide all rocks initially
        for (row in 0 until Constants.Game.BOARD_ROWS) {
            for (col in 0 until Constants.Game.BOARD_COLS) {
                main_LAYOUT_board[row][col].visibility = View.INVISIBLE
            }
        }

        // Show only one car (middle position by default)
        for (i in main_LAYOUT_car_row.indices) {
            main_LAYOUT_car_row[i].visibility =
                if (i == carPosition) View.VISIBLE else View.INVISIBLE
        }

        // Show all hearts initially
        for (heart in main_IMG_hearts) {
            heart.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameTimer.stop()
        SoundManager.stop()
    }

    private fun restartGame() {
        gameTimer.stop()
        gameManager.resetGame()
        carPosition = gameManager.carPosition
        updateHearts()
        updateUI()
        gameTimer.start(Constants.Timer.DELAY)
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLastLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}






