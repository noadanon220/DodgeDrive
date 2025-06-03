package com.danono.dodgedrive.model

import java.time.LocalDateTime

data class ScoreRecord(
    val playerName: String,
    val score: Int,
    val date: LocalDateTime,
    val latitude: Double,
    val longitude: Double,
    val distance: Int = 0
) {
    class Builder(
        var playerName: String = "",
        var score: Int = 0,
        var date: LocalDateTime = LocalDateTime.now(),
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var distance: Int = 0
    ) {
        fun playerName(name: String) = apply { this.playerName = name }
        fun score(score: Int) = apply { this.score = score }
        fun date(date: LocalDateTime) = apply { this.date = date }
        fun location(lat: Double, lon: Double) = apply {
            this.latitude = lat
            this.longitude = lon
        }
        fun distance(distance: Int) = apply { this.distance = distance }

        fun build() = ScoreRecord(
            playerName,
            score,
            date,
            latitude,
            longitude,
            distance
        )
    }
}
