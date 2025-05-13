package com.example.gains.home.model

data class EditableWorkoutDay(
    val day: String,
    val exercises: MutableList<EditableExercise>
)

data class EditableExercise(
    val exerciseId: Int,
    var sets: Int,
    var reps: Int,
    var weight: Float,
    var is_done: Boolean = false
)
