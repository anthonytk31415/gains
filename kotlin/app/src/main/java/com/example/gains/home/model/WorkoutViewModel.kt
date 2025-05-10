package com.example.gains.home.model

import androidx.lifecycle.ViewModel
import com.example.gains.home.model.WorkoutRoutine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkoutViewModel : ViewModel() {
    private val _generatedWorkout = MutableStateFlow<WorkoutRoutine?>(null)
    val generatedWorkout: StateFlow<WorkoutRoutine?> = _generatedWorkout

    fun setWorkout(workout: WorkoutRoutine) {
        _generatedWorkout.value = workout
    }
}