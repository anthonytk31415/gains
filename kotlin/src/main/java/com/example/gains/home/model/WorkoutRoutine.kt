package com.example.gains.home.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRoutine(
    val days_per_week: Int,
    val focus: String,
    val experience_level: String,
    val location: String,
    val schedule: List<WorkoutDay>
)

@Serializable
data class WorkoutDay(
    val day: String,
    val exercises: List<ExerciseDetail>
)

@Serializable
data class ExerciseDetail(
    val name: String,
    val sets: Int,
    val reps: String,
    val weight: String = "",
    var isDone: Boolean = false
)