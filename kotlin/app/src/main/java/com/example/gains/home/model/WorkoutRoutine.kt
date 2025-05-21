package com.example.gains.home.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRoutine(
    val schedule: List<WorkoutDay>
) {
    companion object
}

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
    val exercise_set_id: Int? = null,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val is_done: Boolean
)