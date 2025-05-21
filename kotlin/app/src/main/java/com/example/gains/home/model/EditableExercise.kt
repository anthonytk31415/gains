package com.example.gains.home.model

data class EditableWorkoutDay(
    val day: String,
    val exercises: MutableList<EditableExercise>,
    //val execution_date: String?
)

data class EditableExercise(
    val exerciseId: Int,
    val exercise_set_id: Int? = null,
    var sets: Int,
    var reps: Int,
    var weight: Float,
    val is_done: Boolean = false
)
