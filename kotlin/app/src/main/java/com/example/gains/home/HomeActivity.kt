package com.example.gains.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gains.home.model.EditableExercise
import com.example.gains.home.model.ExerciseDetail
import com.example.gains.home.model.WorkoutRoutine
import com.example.gains.home.network.WorkoutApi
import com.example.gains.home.exercise.*
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val exercises = remember { loadExercisesFromJson(context) }
    val nameToId = remember { exercises.associate { it.name to it.id } }

    val coroutineScope = rememberCoroutineScope()
    var workoutRoutine by remember { mutableStateOf<WorkoutRoutine?>(null) }
    var expandedDayIndex by remember { mutableStateOf<Int?>(null) }



    LaunchedEffect(Unit) {
        coroutineScope.launch {
            workoutRoutine = WorkoutApi.getWorkout()

        }
    }

    val toDoDays = workoutRoutine?.schedule?.filter { day ->
        day.exercises.any { !it.isDone }
    } ?: emptyList()

    val doneDays = workoutRoutine?.schedule?.filter { day ->
        day.exercises.all { it.isDone }
    } ?: emptyList()

    LaunchedEffect(workoutRoutine) {
        println("Workout Routine: $workoutRoutine")
        println("ToDo Days: $toDoDays")
        println("Done Days: $doneDays")
    }

    if (workoutRoutine == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "To Do this week",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Text(
                    text = "Workout Plan: ${workoutRoutine?.focus ?: "Loading..."}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            toDoDays.forEachIndexed { index, day ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                expandedDayIndex = if (expandedDayIndex == index) null else index
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(day.day, style = MaterialTheme.typography.titleLarge)

                            if (expandedDayIndex == index) {
                                Spacer(modifier = Modifier.height(8.dp))
                                day.exercises.forEachIndexed { exIndex, ex ->
                                    val id = nameToId[ex.name]
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = id != null) {
                                                id?.let {
                                                    navController.navigate("exerciseDetail/$it")
                                                }
                                            }
                                    ) {
                                        Checkbox(
                                            checked = ex.isDone,
                                            onCheckedChange = {
                                                workoutRoutine = workoutRoutine?.copy(
                                                    schedule = workoutRoutine!!.schedule.map { d ->
                                                        if (d.day == day.day) {
                                                            d.copy(
                                                                exercises = d.exercises.mapIndexed { i, e ->
                                                                    if (i == exIndex) e.copy(isDone = it) else e
                                                                }
                                                            )
                                                        } else d
                                                    }
                                                )
                                            }
                                        )
                                        Text(
                                            text = "${ex.name} - ${ex.sets} sets, ${ex.reps}",
                                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                                        )
                                        IconButton(onClick = {
                                            workoutRoutine = workoutRoutine?.copy(
                                                schedule = workoutRoutine!!.schedule.map { d ->
                                                    if (d.day == day.day) {
                                                        d.copy(
                                                            exercises = d.exercises.toMutableList()
                                                                .also {
                                                                    it.removeAt(exIndex)
                                                                })
                                                    } else d
                                                }
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Remove Exercise"
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                AddExercise(day.day) { newExercise ->
                                    workoutRoutine = workoutRoutine?.copy(
                                        schedule = workoutRoutine!!.schedule.map { d ->
                                            if (d.day == day.day) {
                                                d.copy(
                                                    exercises = d.exercises.toMutableList().also {
                                                        it.add(
                                                            ExerciseDetail(
                                                                name = newExercise.name,
                                                                sets = newExercise.sets,
                                                                reps = newExercise.reps,
                                                                weight = newExercise.weight ?: ""
                                                            )
                                                        )
                                                    })
                                            } else d
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            workoutRoutine?.let {
                                println("post")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text("Save Progress")
                }
            }

            item {
                Text(
                    text = "Done this week",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            doneDays.forEach { day ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${day.day} ✅", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            day.exercises.forEach { ex ->
                                Text("• ${ex.name} - ${ex.sets} sets, ${ex.reps}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExercise(day: String, onAdd: (EditableExercise) -> Unit) {
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