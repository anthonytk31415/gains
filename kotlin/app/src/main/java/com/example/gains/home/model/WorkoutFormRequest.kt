package com.example.gains.home.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutFormRequest(
//    val dob: String,
//    val height: Float,
//    val weight: Float,
    val gender: String,
    val location: String,
    val experience: String,
    val workout_days: String,
    val muscle_focus: String,
    val goal: String,
//    val age: String
)