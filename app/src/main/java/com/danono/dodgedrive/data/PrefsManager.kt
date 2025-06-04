package com.danono.dodgedrive.data

import android.content.Context
import android.content.SharedPreferences
import com.danono.dodgedrive.model.ScoreRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("DodgeDrivePrefs", Context.MODE_PRIVATE)

    private val gson = Gson()
    private val SCORES_KEY = "HIGH_SCORES"

    // Save a new score to the list of high scores
    fun saveScore(newScore: ScoreRecord) {
        val currentList = getTopScores().toMutableList()
        currentList.add(newScore)

        // Sort by score descending, and keep only top 10
        val sortedList = currentList.sortedByDescending { it.score }.take(10)

        val json = gson.toJson(sortedList)
        prefs.edit().putString(SCORES_KEY, json).apply()
    }

    // Retrieve the top 10 high scores
    fun getTopScores(): List<ScoreRecord> {
        val json = prefs.getString(SCORES_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<ScoreRecord>>() {}.type
        return gson.fromJson(json, type)
    }

    // Clear all scores
    fun clearScores() {
        prefs.edit().remove(SCORES_KEY).apply()
    }
}
