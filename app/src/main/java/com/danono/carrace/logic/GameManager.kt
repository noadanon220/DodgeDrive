package com.danono.carrace.logic

import com.danono.carrace.model.Position


class GameManager {
    // Car position (0, 1, or 2 for the lanes)
    var carPosition = 1
        private set

    // Lives remaining
    private var lives = 3

    // List to store active rock positions on the board
    private val rockPositions = mutableListOf<Position>()

    // Max number of rocks that can be active at once
    private val MAX_ROCKS = 3

    // Counter to track when to add the next rock
    private var rockAddCounter = 0

    // How many ticks to wait between adding rocks
    private val ROCK_ADD_DELAY = 2

    // Get a copy of current rock positions
    fun getRockPositions(): List<Position> = rockPositions.toList()

    // Get remaining lives
    fun getLives(): Int = lives

    // Check if game is over
    fun isGameOver(): Boolean = lives <= 0

    // Add new rocks with controlled timing
    fun addNewRocks() {
        // Only add a new rock if we have fewer than MAX_ROCKS
        if (rockPositions.size < MAX_ROCKS) {
            rockAddCounter++

            // Only add a new rock after waiting ROCK_ADD_DELAY ticks
            if (rockAddCounter >= ROCK_ADD_DELAY) {
                // Reset counter
                rockAddCounter = 0

                // Choose a random lane that doesn't already have a rock in the top row
                val occupiedColumns = rockPositions
                    .filter { it.row == 0 }
                    .map { it.col }
                    .toSet()

                val availableColumns = (0..2).filter { it !in occupiedColumns }

                // Only add a rock if there's an available column
                if (availableColumns.isNotEmpty()) {
                    val randomColumn = availableColumns.random()
                    rockPositions.add(Position(0, randomColumn))
                }
            }
        }
    }

    // Move all rocks down by one row and check for collisions
    fun moveRocksDown(): Boolean {
        val updatedPositions = mutableListOf<Position>()
        var collision = false

        for (position in rockPositions) {
            val newRow = position.row + 1

            // Check if this rock hits the car
            if (newRow == 8 && position.col == carPosition) {
                collision = true
                // Don't add this rock to updated positions (it "crashed" with car)
                continue
            }

            // Keep the rock if it's still on the board
            if (newRow < 9) {
                updatedPositions.add(Position(newRow, position.col))
            }
            // If rock reaches the bottom, it just disappears
            // (we don't add it back to the top)
        }

        // Update the rock positions
        rockPositions.clear()
        rockPositions.addAll(updatedPositions)

        // If collision occurred, reduce lives
        if (collision) {
            lives--
        }

        return collision
    }

    // Move car left
    fun moveLeft() {
        if (carPosition > 0) carPosition--
    }

    // Move car right
    fun moveRight() {
        if (carPosition < 2) carPosition++
    }
}
