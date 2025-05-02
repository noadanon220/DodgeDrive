package com.danono.carrace

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.danono.carrace.logic.GameManager
import com.danono.carrace.logic.GameTimer
import com.danono.carrace.utilities.Constants
import com.danono.carrace.utilities.SignalManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlin.arrayOf

class MainActivity : AppCompatActivity() {

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_LAYOUT_board: Array<Array<AppCompatImageView>>
    private lateinit var main_LAYOUT_car_row: Array<AppCompatImageView>
    private lateinit var main_FAB_left: ExtendedFloatingActionButton
    private lateinit var main_FAB_right: ExtendedFloatingActionButton

    private lateinit var gameTimer: GameTimer
    private lateinit var gameManager: GameManager

    private var carPosition = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        gameManager = GameManager()
        findViews()
        initViews()
        updateUI()
        initGame()
        // Initialize the game timer with lifecycleScope
        gameTimer = GameTimer(lifecycleScope) {
            // This is the onTick callback that will run on every timer tick
            gameManager.addNewRocks()
            val collision = gameManager.moveRocksDown()

            if (collision) {
                updateHearts()
                if (gameManager.isGameOver()) {
                    gameTimer.stop()
                    SignalManager.getInstance().vibrate()
                    SignalManager.getInstance().toast("Game Over")
                } else {
                    SignalManager.getInstance().vibrate()
                    SignalManager.getInstance().toast("Ouch! You hit a rock!")
                    updateHearts()
                }
            }




            updateUI()
        }

        // Start the timer with the delay from Constants
        gameTimer.start(Constants.Timer.DELAY)
    }

    private fun updateHearts() {
        val lives = gameManager.getLives()

        // Update the visibility of heart images based on remaining lives
        for (i in main_IMG_hearts.indices) {
            // Show heart if the index is less than the number of lives
            // Hide heart if the index is equal to or greater than the number of lives
            main_IMG_hearts[i].visibility = if (i < lives) View.VISIBLE else View.INVISIBLE
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
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_0_2)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_1_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_1_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_1_2)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_2_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_2_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_2_2)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_3_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_3_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_3_2)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_4_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_4_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_4_2)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_5_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_5_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_5_2)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_6_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_6_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_6_2)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_7_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_7_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_7_2)
            ),
            arrayOf(
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_8_0),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_8_1),
                findViewById<AppCompatImageView>(R.id.main_IMG_cell_8_2)
            )
        )

        main_LAYOUT_car_row = arrayOf(
            findViewById<AppCompatImageView>(R.id.main_IMG_car_0),
            findViewById<AppCompatImageView>(R.id.main_IMG_car_1),
            findViewById<AppCompatImageView>(R.id.main_IMG_car_2)
        )
    }


    private fun updateUI() {
        drawCar()
        drawRocks()
    }

    private fun drawCar() {
        // Hide all cars
        for (carView in main_LAYOUT_car_row) {
            carView.visibility = View.INVISIBLE
        }
        // Show only the current car position
        main_LAYOUT_car_row[carPosition].visibility = View.VISIBLE
    }

    private fun drawRocks() {
        // This would typically be driven by your GameManager logic
        // For example, if gameManager has a method to get the rock positions:
        val rockPositions = gameManager.getRockPositions()

        // Clear all previous rocks
        for (row in main_LAYOUT_board.indices) {
            for (col in main_LAYOUT_board[row].indices) {
                main_LAYOUT_board[row][col].visibility = View.INVISIBLE
            }
        }

        // Set visible only the cells that should have rocks
        for (position in rockPositions) {
            main_LAYOUT_board[position.row][position.col].visibility = View.VISIBLE
        }
    }

    private fun initGame() {
        // Hide all rocks initially
        for (row in 0 until 9) {
            for (col in 0 until 3) {
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

    override fun onPause() {
        super.onPause()
        // Pause the game when the activity is paused
        gameTimer.stop()
    }

    override fun onResume() {
        super.onResume()
        // Resume the game when the activity is resumed
        if (!gameManager.isGameOver()) {
            gameTimer.start(Constants.Timer.DELAY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Not strictly necessary with lifecycleScope,
        // but good practice to explicitly stop the timer
        gameTimer.stop()
    }


}// END






