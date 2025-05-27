package com.danono.dodgedrive.interfaces

import com.danono.dodgedrive.model.ScoreRecord

interface ScoreCallback {
    fun onScoreClicked(score: ScoreRecord, position: Int)
}