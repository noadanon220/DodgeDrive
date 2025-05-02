package com.danono.dodgedrive.logic

import com.danono.dodgedrive.model.Position
import com.danono.dodgedrive.utilities.Constants

class GameManager {
    var carPosition = Constants.Game.CAR_START_POSITION
        private set

    private var lives = Constants.Game.INITIAL_LIVES
    private val rockPositions = mutableListOf<Position>()
    private val MAX_ROCKS = Constants.Game.MAX_ROCK_STREAMS
    private var rockAddCounter = 0

    fun getRockPositions(): List<Position> = rockPositions.toList()
    fun getLives(): Int = lives
    fun isGameOver(): Boolean = lives <= 0

    fun addNewRocks() {
        if (rockPositions.size < MAX_ROCKS) {
            rockAddCounter++
            if (rockAddCounter >= Constants.Game.ROCK_ADD_DELAY) {
                rockAddCounter = 0
                val occupiedColumns = rockPositions
                    .filter { it.row == 0 }
                    .map { it.col }
                    .toSet()

                val availableColumns = (0 until Constants.Game.BOARD_COLS)
                    .filter { it !in occupiedColumns }

                if (availableColumns.isNotEmpty()) {
                    val randomColumn = availableColumns.random()
                    rockPositions.add(Position(0, randomColumn))
                }
            }
        }
    }

    fun moveRocksDown(): Boolean {
        val updatedPositions = mutableListOf<Position>()
        var collision = false

        for (position in rockPositions) {
            val newRow = position.row + 1
            if (newRow == Constants.Game.BOARD_ROWS - 1 && position.col == carPosition) {
                collision = true
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

    fun moveLeft() {
        if (carPosition > 0) carPosition--
    }

    fun moveRight() {
        if (carPosition < Constants.Game.BOARD_COLS - 1) carPosition++
    }
}
