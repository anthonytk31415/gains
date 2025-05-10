package com.example.gains.home.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRoutine(
    val schedule: List<WorkoutDay>
)

@Serializable
data class WorkoutDay(
    val workout_id: Int? = null,
    val execution_date: String? = null,
    val created_at: String,
    val exercise_sets: List<ExerciseDetail>? = null,
    val exercises: List<ExerciseDetail>? = null
)

@Serializable
data class ExerciseDetail(
    val exercise_id: Int,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    var is_done: Boolean = false
)
