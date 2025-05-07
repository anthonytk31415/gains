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
import com.example.gains.home.model.ExerciseMappings
import com.example.gains.home.model.WorkoutRoutine
import com.example.gains.home.network.WorkoutApi
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedWorkoutScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    var workoutRoutine by remember { mutableStateOf<WorkoutRoutine?>(null) }
    var editableWorkout by remember { mutableStateOf<MutableList<EditableWorkoutDay>>(mutableListOf()) }
    val userId = "1";

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedWorkout = WorkoutApi.getWorkout(userId)
            workoutRoutine = fetchedWorkout
            editableWorkout = fetchedWorkout.schedule.mapIndexed { index, day ->
                val exercisesRaw = day.exercise_sets ?: day.exercises ?: emptyList()
                EditableWorkoutDay(
                    day = "Day ${index + 1}",
                    exercises = exercisesRaw.map { ex ->
                        EditableExercise(
                            exerciseId = ex.exercise_id,
                            sets = ex.sets,
                            reps = ex.reps,
                            weight = ex.weights
                        )
                    }.toMutableList()
                )
            }.toMutableList()
        }
    }

    if (workoutRoutine == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(editableWorkout) { day ->
                var expanded by remember { mutableStateOf(false) }
                var selectedExerciseName by remember { mutableStateOf("") }
                var sets by remember { mutableStateOf("") }
                var reps by remember { mutableStateOf("") }
                var weight by remember { mutableStateOf("") }
                var dropdownExpanded by remember { mutableStateOf(false) }
                val exerciseNames = ExerciseMappings.nameToId.keys.toList()

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
                                    text = "• ${ExerciseMappings.getExerciseName(exercise.exerciseId)} - ${exercise.sets} sets, ${exercise.reps} reps",
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

                        Button(onClick = { expanded = !expanded }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Exercise")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Exercise")
                        }

                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Dropdown for Exercise Name
                            ExposedDropdownMenuBox(
                                expanded = dropdownExpanded,
                                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = selectedExerciseName,
                                    onValueChange = {},
                                    label = { Text("Select Exercise") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                                    },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false }
                                ) {
                                    exerciseNames.forEach { name ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                selectedExerciseName = name
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

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
                                    val exerciseId = ExerciseMappings.getExerciseId(selectedExerciseName)
                                    if (exerciseId != -1 && sets.isNotBlank() && reps.isNotBlank()) {
                                        val newExercise = EditableExercise(
                                            exerciseId = exerciseId,
                                            sets = sets.toIntOrNull() ?: 0,
                                            reps = reps.toIntOrNull() ?: 0,
                                            weight = weight.toFloatOrNull() ?: 0f
                                        )
                                        val updatedExercises = day.exercises.toMutableList()
                                        updatedExercises.add(newExercise)
                                        val updatedDay = day.copy(exercises = updatedExercises)
                                        editableWorkout = editableWorkout.map {
                                            if (it.day == day.day) updatedDay else it
                                        }.toMutableList()

                                        // Reset form
                                        expanded = false
                                        selectedExerciseName = ""
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
            }
        }
    }
}

