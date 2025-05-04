package com.example.gains.home.model

data class EditableWorkoutDay(
    val day: String,
    val exercises: MutableList<EditableExercise>
)

data class EditableExercise(
    val name: String,
    var sets: Int,
    var reps: String,
    var weight: String
)