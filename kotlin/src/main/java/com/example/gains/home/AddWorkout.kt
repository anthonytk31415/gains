package com.example.gains.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gains.home.model.EditableExercise
import com.example.gains.home.model.EditableWorkoutDay
import com.example.gains.home.model.WorkoutRoutine
import com.example.gains.home.network.WorkoutApi
import kotlinx.coroutines.launch

@Composable
fun GeneratedWorkoutScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    var workoutRoutine by remember { mutableStateOf<WorkoutRoutine?>(null) }
    var editableWorkout by remember { mutableStateOf<MutableList<EditableWorkoutDay>>(mutableListOf()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedWorkout = WorkoutApi.getWorkout() // Your GET API Call
            workoutRoutine = fetchedWorkout
            editableWorkout = fetchedWorkout.schedule.map { day ->
                EditableWorkoutDay(
                    day = day.day,
                    exercises = day.exercises.map { ex ->
                        EditableExercise(
                            name = ex.name,
                            sets = ex.sets,
                            reps = ex.reps,
                            weight = ex.weight // Initially no weight
                        )
                    }.toMutableList()
                )
            }.toMutableList()
        }
    }

    if (workoutRoutine == null) {
        // Loading state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(editableWorkout) { day ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = day.day, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        day.exercises.forEachIndexed { index, exercise ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "• ${exercise.name} - ${exercise.sets} sets, ${exercise.reps} reps",
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    val updatedExercises = day.exercises.toMutableList()
                                    updatedExercises.removeAt(index)
                                    val updatedDay = day.copy(exercises = updatedExercises)
                                    editableWorkout = editableWorkout.map {
                                        if (it.day == day.day) updatedDay else it
                                    }.toMutableList()
                                }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Remove Exercise")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AddExerciseSection(day.day) { newExercise ->
                            val updatedExercises = day.exercises.toMutableList()
                            updatedExercises.add(newExercise)
                            val updatedDay = day.copy(exercises = updatedExercises)
                            editableWorkout = editableWorkout.map {
                                if (it.day == day.day) updatedDay else it
                            }.toMutableList()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExerciseSection(day: String, onAdd: (EditableExercise) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    Column {
        Button(onClick = { expanded = !expanded }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Exercise")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Exercise")
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = selectedExercise,
                onValueChange = { selectedExercise = it },
                label = { Text("Exercise Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = sets,
                onValueChange = { sets = it },
                label = { Text("Sets") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text("Reps") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (selectedExercise.isNotBlank() && sets.isNotBlank() && reps.isNotBlank()) {
                        onAdd(
                            EditableExercise(
                                name = selectedExercise,
                                sets = sets.toIntOrNull() ?: 0,
                                reps = reps,
                                weight = weight
                            )
                        )
                        // Reset fields after adding
                        expanded = false
                        selectedExercise = ""
                        sets = ""
                        reps = ""
                        weight = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Confirm")
            }
        }
    }
}