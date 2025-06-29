package com.danono.dodgedrive.logic

import android.content.Context
import com.danono.dodgedrive.model.Position
import com.danono.dodgedrive.utilities.Constants
import com.danono.dodgedrive.utilities.SoundManager

class GameManager(private val context: Context) {
    var carPosition = Constants.Game.CAR_START_POSITION
        private set

    private var lives = Constants.Game.INITIAL_LIVES
    private val rockPositions = mutableListOf<Position>()
    private val MAX_ROCKS = Constants.Game.MAX_ROCK_STREAMS
    private var rockAddCounter = 0

    private val coinPositions = mutableListOf<Position>()
    private var coinAddCounter = 0
    private var score = 0

    private val heartPositions = mutableListOf<Position>()
    private var heartAddCounter = 0
    private val HEART_ADD_DELAY = Constants.Game.COIN_ADD_DELAY * 3 // Hearts appear less frequently than coins

    private var distance: Int = 0
    fun getDistance(): Int = distance

    fun getRockPositions(): List<Position> = rockPositions.toList()
    fun getCoinPositions(): List<Position> = coinPositions.toList()
    fun getHeartPositions(): List<Position> = heartPositions.toList()
    fun getLives(): Int = lives
    fun isGameOver(): Boolean = lives <= 0
    fun getScore(): Int = score

    // Called every game tick to increment distance
    fun tickGameProgress() {
        distance++
    }

    fun addNewRocks() {
        if (rockPositions.size < MAX_ROCKS) {
            rockAddCounter++
            if (rockAddCounter >= Constants.Game.ROCK_ADD_DELAY) {
                rockAddCounter = 0

                // Block columns that already contain rocks, coins, or hearts anywhere on board
                val blockedColumns = (rockPositions + coinPositions + heartPositions)
                    .map { it.col }
                    .toSet()

                val availableColumns = (0 until Constants.Game.BOARD_COLS)
                    .filter { it !in blockedColumns }

                if (availableColumns.isNotEmpty()) {
                    val randomColumn = availableColumns.random()
                    rockPositions.add(Position(0, randomColumn))
                }
            }
        }
    }

    fun addNewCoins() {
        if (coinPositions.size < MAX_ROCKS) {
            coinAddCounter++
            if (coinAddCounter >= Constants.Game.COIN_ADD_DELAY) {
                coinAddCounter = 0

                // Block columns that already contain coins, rocks, or hearts anywhere on board
                val blockedColumns = (coinPositions + rockPositions + heartPositions)
                    .map { it.col }
                    .toSet()

                val availableColumns = (0 until Constants.Game.BOARD_COLS)
                    .filter { it !in blockedColumns }

                if (availableColumns.isNotEmpty()) {
                    val randomColumn = availableColumns.random()
                    coinPositions.add(Position(0, randomColumn))
                }
            }
        }
    }

    fun addNewHearts() {
        heartAddCounter++
        if (heartAddCounter >= HEART_ADD_DELAY) {
            heartAddCounter = 0

            // Find a random column for the heart
            val randomColumn = (0 until Constants.Game.BOARD_COLS).random()
            heartPositions.add(Position(0, randomColumn))
        }
    }

    // Moves all rocks down and checks for collision with the car
    fun moveRocksDown(): Boolean {
        val updatedPositions = mutableListOf<Position>()
        var collision = false

        for (position in rockPositions) {
            val newRow = position.row + 1

            // Skip if rock moves into a cell that has a coin, heart, or would move to same position as a coin/heart
            val objectAtNewPosition = (coinPositions + heartPositions).any { 
                (it.row == newRow && it.col == position.col) || 
                (it.row == position.row + 1 && it.col == position.col)
            }
            if (objectAtNewPosition) {
                continue
            }

            // Check for collision only if rock reaches the car's row
            if (newRow == Constants.Game.BOARD_ROWS - 1 && position.col == carPosition) {
                collision = true
                SoundManager.playRockCollision(context)
                continue
            }

            if (newRow < Constants.Game.BOARD_ROWS) {
                updatedPositions.add(Position(newRow, position.col))
            }
        }

        rockPositions.clear()
        rockPositions.addAll(updatedPositions)

        if (collision) {
            lives--
        }

        return collision
    }

    // Moves all coins down and checks for collection by the car
    fun moveCoinsDown(): Boolean {
        val updatedPositions = mutableListOf<Position>()
        var collected = false

        for (position in coinPositions) {
            val newRow = position.row + 1
            
            // Skip if coin would move into a cell that has a rock/heart or would move to same position as a rock/heart
            val objectAtNewPosition = (rockPositions + heartPositions).any { 
                (it.row == newRow && it.col == position.col) || 
                (it.row == position.row + 1 && it.col == position.col)
            }
            if (objectAtNewPosition) {
                continue
            }

            if (newRow == Constants.Game.BOARD_ROWS - 1 && position.col == carPosition) {
                collected = true
                score += 10
                SoundManager.playCoinCollection(context)
                continue
            }
            if (newRow < Constants.Game.BOARD_ROWS) {
                updatedPositions.add(Position(newRow, position.col))
            }
        }

        coinPositions.clear()
        coinPositions.addAll(updatedPositions)

        return collected
    }

    // Moves all hearts down and checks for collection by the car
    fun moveHeartsDown(): HeartCollectionResult {
        val updatedPositions = mutableListOf<Position>()
        var result = HeartCollectionResult.NONE

        for (position in heartPositions) {
            val newRow = position.row + 1

            if (newRow == Constants.Game.BOARD_ROWS - 1 && position.col == carPosition) {
                if (lives < Constants.Game.INITIAL_LIVES) {
                    lives++
                    SoundManager.playHeartCollection(context)
                    result = HeartCollectionResult.COLLECTED
                } else {
                    result = HeartCollectionResult.MAX_LIVES
                }
                continue
            }
            if (newRow < Constants.Game.BOARD_ROWS) {
                updatedPositions.add(Position(newRow, position.col))
            }
        }

        heartPositions.clear()
        heartPositions.addAll(updatedPositions)

        return result
    }

    fun moveLeft() {
        if (carPosition > 0) carPosition--
    }

    fun moveRight() {
        if (carPosition < Constants.Game.BOARD_COLS - 1) carPosition++
    }

    fun resetGame() {
        lives = Constants.Game.INITIAL_LIVES
        rockPositions.clear()
        coinPositions.clear()
        heartPositions.clear()
        rockAddCounter = 0
        coinAddCounter = 0
        heartAddCounter = 0
        carPosition = Constants.Game.CAR_START_POSITION
        score = 0
    }
}

enum class HeartCollectionResult {
    NONE,
    COLLECTED,
    MAX_LIVES
}
