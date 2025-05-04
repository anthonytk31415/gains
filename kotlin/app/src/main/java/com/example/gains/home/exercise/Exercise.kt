package com.example.gains.home.exercise

data class Exercise(
    val name: String,
    val imageRes: Int? = null,
    val videoUrl: String? = null,
    val instructions: String,
    val breathingTips: String,
    val commonMistakes: String
)