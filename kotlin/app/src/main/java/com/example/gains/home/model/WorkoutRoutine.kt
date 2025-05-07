package com.example.gains.home.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRoutine(
    val creation_date: String,
    val schedule: List<WorkoutDay>
)

@Serializable
data class WorkoutDay(
    val workout_id: Int,
    val exercise_sets: List<ExerciseDetail>? = null,
    val exercises: List<ExerciseDetail>? = null
)

@Serializable
data class ExerciseDetail(
    val exercise_id: Int,
    val sets: Int,
    val reps: Int,
    val weights: Float,
    var isDone: Boolean = false
)
