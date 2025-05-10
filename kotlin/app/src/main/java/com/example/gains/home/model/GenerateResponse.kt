package com.example.gains.home.model

import kotlinx.serialization.Serializable
@Serializable
data class GeneratedWorkoutRoutine(
    val message: String,
    val schedule: List<GeneratedWorkoutDay>
)

@Serializable
data class GeneratedWorkoutDay(
    val created_at: String,
    val exercise_sets: List<ExerciseDetail>
)