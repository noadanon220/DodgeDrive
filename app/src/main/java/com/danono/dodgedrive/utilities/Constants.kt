package com.danono.dodgedrive.utilities

class Constants {

        object Timer {
            const val DELAY = 1500L // Original timer delay
        }

        // Add a new object for game constants
        object Game {
            const val GAME_SPEED = 1000L // Game update speed (same as Timer.DELAY)
            const val INITIAL_LIVES = 3
            const val MAX_ROCK_STREAMS = 3
            const val FIRST_UPGRADE_THRESHOLD = 15 // When to add second rock
            const val SECOND_UPGRADE_THRESHOLD = 30 // When to add third rock
        }
}