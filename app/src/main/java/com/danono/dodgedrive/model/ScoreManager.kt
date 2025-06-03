package com.danono.dodgedrive.model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

object ScoreManager {
    private const val PREFS_NAME = "score_prefs"
    private const val SCORES_KEY = "top_scores"
    private const val MAX_SCORES = 10
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    private val scores = mutableListOf<ScoreRecord>()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadScores()
    }

    fun addScore(score: ScoreRecord) {
        scores.add(score)
        scores.sortByDescending { it.score }
        if (scores.size > MAX_SCORES) {
            scores.removeAt(scores.lastIndex)
        }
        saveScores()
    }

    fun getTopScores(): List<ScoreRecord> {
        return scores.toList()
    }

    fun clearScores() {
        scores.clear()
        saveScores()
    }

    private fun saveScores() {
        val json = gson.toJson(scores)
        sharedPreferences.edit().putString(SCORES_KEY, json).apply()
    }

    private fun loadScores() {
        val json = sharedPreferences.getString(SCORES_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<ScoreRecord>>() {}.type
            val savedScores: List<ScoreRecord> = gson.fromJson(json, type)
            scores.clear()
            scores.addAll(savedScores)
        }
    }
}
