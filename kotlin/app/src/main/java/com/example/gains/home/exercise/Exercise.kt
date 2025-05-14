package com.example.gains.home.exercise

import android.content.Context
import com.example.gains.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Exercise(
    val id: Int,
    val name: String,
    val imageResName: String,
    val videoUrl: String,
    val instructions: String,
    val breathingTips: String,
    val commonMistakes: String,
    val focusAreas: List<String> = emptyList(),
    val imageRes: Int = 0
)

fun loadExercisesFromJson(context: Context): List<Exercise> {
    val inputStream = context.resources.openRawResource(R.raw.exercises)
    val json = inputStream.bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Exercise>>() {}.type
    return Gson().fromJson(json, type)
}